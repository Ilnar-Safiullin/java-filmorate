package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private static final int GENRE_MIN = 1;
    private static final int GENRE_MAX = 6;

    private static final String FIND_ALL_FILMS = "SELECT * FROM films";
    private static final String FIND_BY_ID_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_FILM = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_FILM = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?,
            mpa_id = ? WHERE film_id = ?""";
    private static final String GET_LIKES_BY_ID_FILM =
            "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String INSERT_ADD_LIKE_FILM = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_FILM = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_FILMS = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
            COUNT(l.user_id) AS likes_count
            FROM
                films f
            LEFT JOIN
                likes l ON f.film_id = l.film_id
            GROUP BY
                f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id
            ORDER BY
                likes_count DESC
            LIMIT ?""";
    private static final String INSERT_IN_FILM_GENRE = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";


    public FilmDbStorage(JdbcTemplate jdbc, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbc = jdbc;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film addFilm(Film film) {
        Integer generatedId = insert(INSERT_FILM,
                true,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(generatedId);
        updateMpaName(film);
        updateGenre(film);
        insertInFilmGenreTable(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbc.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        try {
            Film film = jdbc.queryForObject(FIND_BY_ID_FILM, new Object[]{id}, new FilmRowMapper());
            if (film == null) {
                throw new NotFoundException("Фильм c таким id не найден: " + id);
            }

            film.setLikes(getLikesForFilm(film));
            film.setGenres(updateGenre(film));
            updateMpaName(film);

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм c таким id не найден: " + id);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbc.query(FIND_ALL_FILMS, new FilmRowMapper());
        for (Film film : films) {
            film.setLikes(getLikesForFilm(film));
            film.setGenres(updateGenre(film));
            updateMpaName(film);
        }
        return films;
    }

    private Set<Integer> getLikesForFilm(Film film) {
        List<Integer> likes = jdbc.queryForList(GET_LIKES_BY_ID_FILM, new Object[]{film.getId()}, Integer.class);
        return new HashSet<>(likes);
    }

    private Integer insert(String query, boolean expectGeneratedKey, Object... params) {
        if (expectGeneratedKey) {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } else {
            int rowsCreated = jdbc.update(query, params);
            if (rowsCreated == 0) {
                throw new InternalServerException("Не удалось обновить данные");
            }
            return rowsCreated;
        }
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        Integer generatedId = insert(INSERT_ADD_LIKE_FILM,
                false,
                filmId,
                userId
        );
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        jdbc.update(DELETE_LIKE_FILM, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = jdbc.query(GET_POPULAR_FILMS, new FilmRowMapper(), count);
        return films;
    }

    private void updateMpaName(Film film) {
        Integer mpaId = film.getMpa().getId();
        String mpaName = mpaDbStorage.getNameForMpaId(mpaId);
        film.getMpa().setName(mpaName);
    }

    private Set<Genre> updateGenre(Film film) {
        String genreName;
        Set<Genre> genres = new HashSet<>();
        if (film.getGenres().size() > 0) {
            genres = film.getGenres();
            for (Genre genre : genres) {
                if (genre.getId() > GENRE_MAX || genre.getId() < GENRE_MIN) {
                    throw new NotFoundException("Не верный id Genre");
                }
                genreName = genreDbStorage.getNameForGenreId(genre.getId());
                genre.setName(genreName);
            }
        } else {
            genres = genreDbStorage.findGenreByFilmId(film.getId());
        }
        return genres;
    }

    private void insertInFilmGenreTable(Film film) {
        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            insert(INSERT_IN_FILM_GENRE,
                    false,
                    film.getId(),
                    genre.getId()
            );
        }
    }
}
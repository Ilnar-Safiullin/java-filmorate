package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

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

    private static final String FIND_ALL_FILMS = "SELECT * FROM films";
    private static final String FIND_BY_ID_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_FILM = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_FILM = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?
            """;
    private static final String INSERT_ADD_LIKE_FILM = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_FILM = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_FILMS = "SELECT * FROM films AS f JOIN ON likes AS l ON f.film_id = l.film_id" +
            "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC";


    public FilmDbStorage(JdbcTemplate jdbc, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbc = jdbc;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film addFilm(Film film) {
        Integer generatedId = insert(INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(generatedId);
        updateMpaName(film);
        updateGenreName(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbc.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId()
        );
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_FILM, new Object[]{id}, new FilmRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм c таким id не найден: " + id);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbc.query(FIND_ALL_FILMS, new BeanPropertyRowMapper<>(Film.class));
    }

    @Override
    public void deleteFilm(int id) {
    }

    private Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    private void updateGenres(Integer filmId, Set<Integer> genreIds) {
        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", filmId);
        if (genreIds != null && !genreIds.isEmpty()) {
            jdbc.batchUpdate(
                    "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)",
                    genreIds.stream()
                            .map(genreId -> new Object[]{filmId, genreId})
                            .toList()
            );
        }
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        Integer generatedId = insert(INSERT_ADD_LIKE_FILM,
                filmId,
                userId
        );
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        jdbc.update(DELETE_LIKE_FILM, filmId, userId);
    }

    public List<Film> get10PopularFilms() {
        return jdbc.query(GET_POPULAR_FILMS, new BeanPropertyRowMapper<>(Film.class));
    }

    private void updateMpaName(Film film) {
        Integer mpaId = film.getMpa().getId();
        String mpaName = mpaDbStorage.getNameForMpaId(mpaId);
        film.getMpa().setName(mpaName);
    }

    private void updateGenreName(Film film) {
        String genreName;
        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            if (genre.getId() > 6 || genre.getId() < 1) {
                throw new NotFoundException("Не верный id Genre");
            }
            genreName = genreDbStorage.getNameForGenreId(genre.getId());
            genre.setName(genreName);
        }
    }


    private Set<Integer> getGenresForFilm(Integer filmId) {
        return new HashSet<>(jdbc.queryForList("SELECT genre_id FROM film_genre WHERE film_id = ?",
                Integer.class, filmId));
    }

    private Integer getMpaIdForFilm(Integer filmId) {
        return jdbc.queryForObject("SELECT mpa_id FROM films WHERE film_id = ?",
                Integer.class, filmId);
    }
}
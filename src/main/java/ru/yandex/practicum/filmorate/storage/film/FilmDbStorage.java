package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    private static final String FIND_BY_ID_FILM =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                    "g.genre_id, g.name AS genreName, fg.genre_id AS chekgenre " +
                    "FROM films f " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "WHERE f.film_id = ?";
    private static final String INSERT_FILM = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_FILM = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?,
            mpa_id = ? WHERE film_id = ?""";
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
    private static final String GET_ALL_FILMS =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                    "g.genre_id, g.name AS genreName, fg.genre_id AS chekgenre " +
                    "FROM films f " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN mpa_rating m ON f.mpa_id = m.mpa_id";


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
        insertInFilmGenreTable(film);

        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Map<Integer, Film> filmMap = new HashMap<>();

        jdbc.query(GET_ALL_FILMS, (resultSet) -> {
            int filmId = resultSet.getInt("film_id");
            Film film = filmMap.get(filmId);

            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(resultSet.getString("name"));
                film.setDescription(resultSet.getString("description"));
                java.sql.Date releaseDate = resultSet.getDate("release_date");
                film.setReleaseDate(releaseDate.toLocalDate());
                film.setDuration(resultSet.getInt("duration"));
                film.setGenres(new HashSet<>());
                film.setMpa(new Mpa());
                filmMap.put(filmId, film);
            }

            Integer haveGenre = resultSet.getInt("chekgenre");

            if (haveGenre != null) {
                Genre genre = new Genre();
                genre.setId(resultSet.getInt("genre_id"));
                genre.setName(resultSet.getString("genreName"));
                film.getGenres().add(genre);
            }

            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getInt("mpa_id"));
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
        });

        return new ArrayList<>(filmMap.values());
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
        Map<Integer, Film> filmMap = new HashMap<>();

        jdbc.query(FIND_BY_ID_FILM, resultSet -> {
            Integer filmId = resultSet.getObject("film_id", Integer.class);
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(resultSet.getString("name"));
                film.setDescription(resultSet.getString("description"));
                film.setReleaseDate(resultSet.getObject("release_date", LocalDate.class));
                film.setDuration(resultSet.getObject("duration", Integer.class));
                film.setGenres(new LinkedHashSet<>());
                film.setLikes(new HashSet<>());
                film.setMpa(new Mpa());
                filmMap.put(filmId, film);
            }

            Integer haveGenre = resultSet.getObject("genre_id", Integer.class);

            if (haveGenre != null) {
                Genre genre = new Genre();
                genre.setId(resultSet.getInt("genre_id"));
                genre.setName(resultSet.getString("genreName"));
                film.getGenres().add(genre); // Добавляем друга в коллекцию
            }

            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getInt("mpa_id"));
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
        }, id);

        if (filmMap.isEmpty()) {
            throw new NotFoundException("Такого фильма не существует");
        }
        return filmMap.get(id);
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
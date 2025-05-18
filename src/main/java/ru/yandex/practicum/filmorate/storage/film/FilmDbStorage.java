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

import java.sql.PreparedStatement;
import java.sql.Statement;
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
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = jdbc.query(GET_ALL_FILMS, new FilmRowMapper());
        if (films.isEmpty()) {
            throw new NotFoundException("Фильмов пока нет");
        }
        return films;
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
        List<Film> films = jdbc.query(FIND_BY_ID_FILM, new FilmRowMapper(), id);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с таким айди не найден: " + id);
        }
        return films.getFirst();
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
}
package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
public class GenreDbStorage {
    protected final JdbcTemplate jdbc;
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_ALL_GENRE_BY_FILM_ID =
            "SELECT g.genre_id, g.name FROM genres g JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                    "WHERE fg.film_id = ? ORDER BY genre_id";
    private static final String DELETE_GENRES_FOR_FILM_SQL = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_IN_FILM_GENRE = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";


    public Genre getGenreById(int id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, new Object[]{id}, new GenreRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre c таким id не найден: " + id);
        }
    }

    public Collection<Genre> getAllGenre() {
        return jdbc.query(FIND_ALL_QUERY, new GenreRowMapper());
    }

    public Set<Genre> getGenresForFilm(Film film) {
        List<Genre> genres = jdbc.query(FIND_ALL_GENRE_BY_FILM_ID, new GenreRowMapper(), film.getId());
        return new TreeSet<>(genres);
    }

    public void insertInFilmGenreTable(Film film) {
        Set<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        jdbc.update(DELETE_GENRES_FOR_FILM_SQL, film.getId());
        if (genreIds.isEmpty()) {
            return;
        }
        List<Object[]> batch = genreIds.stream()
                .map(genreId -> new Object[]{film.getId(), genreId})
                .toList();
        jdbc.batchUpdate(INSERT_IN_FILM_GENRE, batch);
    }
}

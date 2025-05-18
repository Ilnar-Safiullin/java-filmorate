package ru.yandex.practicum.filmorate.storage.likes;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.List;

@AllArgsConstructor
@Repository
public class LikeDbStorage {
    private final JdbcTemplate jdbc;

    private static final String INSERT_ADD_LIKE_FILM = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_FILM = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR_FILMS = """
            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, 
            g.genre_id, g.name AS genreName, fg.genre_id AS chekgenre,
            COUNT(l.user_id) AS likes_count
            FROM
                films f
            LEFT JOIN film_genre fg ON f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            LEFT JOIN mpa_rating m ON f.mpa_id = m.mpa_id      
            LEFT JOIN
                likes l ON f.film_id = l.film_id
            GROUP BY
                f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, g.genre_id
            ORDER BY
                likes_count DESC
            LIMIT ?""";


    public void addLikeFilm(Integer filmId, Integer userId) {
        jdbc.update(INSERT_ADD_LIKE_FILM,
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
}

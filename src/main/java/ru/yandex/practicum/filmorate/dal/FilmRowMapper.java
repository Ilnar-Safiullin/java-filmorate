package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Component
public class FilmRowMapper implements RowMapper<Film> {
    Map<Integer, Film> filmMap = new HashMap<>();

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
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

        return film;
    }
}

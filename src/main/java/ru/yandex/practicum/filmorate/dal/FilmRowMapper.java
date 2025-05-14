package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getObject("film_id", Integer.class),
                rs.getString("name"),
                rs.getString("description"),
                rs.getObject("release_date", LocalDate.class),
                rs.getObject("duration", Integer.class),
                Collections.singleton(rs.getObject("genres", Genre.class)),
                rs.getObject("mpa", Mpa.class)
        );
    }
}

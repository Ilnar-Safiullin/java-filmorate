package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@AllArgsConstructor
@Repository
public class MpaDbStorage {
    protected final JdbcTemplate jdbc;

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_rating ORDER BY mpa_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_rating WHERE mpa_id = ?";
    private static final String FIND_NAME_BY_ID_QUERY = "SELECT name FROM mpa_rating WHERE mpa_id = ?";


    public Mpa getMpaById(int id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, new Object[]{id}, new MpaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa c таким id не найден: " + id);
        }
    }

    public Collection<Mpa> getAllMpa() {
        return jdbc.query(FIND_ALL_QUERY, new MpaRowMapper());
    }

    public void updateMpaName(Film film) {
        Integer mpaId = film.getMpa().getId();
        String mpaName = jdbc.queryForObject(FIND_NAME_BY_ID_QUERY, new Object[]{mpaId}, String.class);
        film.getMpa().setName(mpaName);
    }
}
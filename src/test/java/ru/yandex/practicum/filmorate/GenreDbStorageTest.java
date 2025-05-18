/*
package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class, FilmDbStorage.class, FilmRowMapper.class})
public class GenreDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;
    private GenreDbStorage genreDbStorage;

    private Film film;
    private Film film2;


    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM friendship");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM likes");
        jdbc.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        jdbc.update("DELETE FROM films");
        jdbc.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");

        film = new Film();
        film.setName("filmName");
        film.setDuration(100);
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        Genre genre = new Genre();
        Genre genre2 = new Genre();
        genre.setId(1);
        genre2.setId(2);
        film.setGenres(Set.of(genre, genre2));
        filmDbStorage.addFilm(film);

        film2 = new Film();
        film2.setName("filmName2");
        film2.setDuration(200);
        film2.setDescription("description2");
        film2.setReleaseDate(LocalDate.now());
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);
        filmDbStorage.addFilm(film2);
    }

    @Test
    public void testFindGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);
        assertThat(genre)
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void testFilmGenres() {
        genreDbStorage.insertInFilmGenreTable(film);
        Set<Genre> filmGenresBeforeAdding = genreDbStorage.getGenresForFilm(film);
        assertThat(filmGenresBeforeAdding.size()).isEqualTo(2);
    }

    @Test
    public void testGetAllGenres() {
        Collection<Genre> genres = genreDbStorage.getAllGenre();
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    public void testInsertInFilm_genre() {
        Genre genre5 = new Genre();
        genre5.setId(5);
        Film film3 = new Film();
        film3.setGenres(Set.of(genre5));
        genreDbStorage.insertInFilmGenreTable(film3);
        assertThat(film3.getGenres()).isEqualTo(genreDbStorage.getGenresForFilm(film3));
    }
}

 */
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, LikeDbStorage.class, UserDbStorage.class})
public class LikeDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;

    private Film film;
    private Film film2;
    private User user;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM friendship");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM likes");
        jdbc.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        jdbc.update("DELETE FROM films");
        jdbc.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");

        user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());
        userDbStorage.addUser(user);

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
    public void testGetPopular() {
        likeDbStorage.addLikeFilm(film2.getId(), user.getId());
        Collection<Film> mostLiked = likeDbStorage.getPopularFilms(1);
        assertThat(mostLiked.size()).isEqualTo(1);

        List<String> names = mostLiked.stream()
                .map(Film::getName)
                .toList();
        assertThat(names.contains("filmName2")).isTrue();
    }

    @Test
    public void testDeleteLike() {
        likeDbStorage.addLikeFilm(film2.getId(), user.getId());
        String selectQuery = "SELECT * FROM likes";
        List<Map<String, Object>> rows = jdbc.queryForList(selectQuery);
        assertThat(rows.size()).isEqualTo(1);

        likeDbStorage.deleteLikeFilm(film2.getId(), user.getId());
        List<Map<String, Object>> rowsAfter = jdbc.queryForList(selectQuery);
        assertThat(rowsAfter.isEmpty()).isTrue();
    }
}
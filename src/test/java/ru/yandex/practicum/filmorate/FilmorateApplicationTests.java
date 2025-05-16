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
import ru.yandex.practicum.filmorate.dal.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmDbStorage.class, FilmRowMapper.class,
        MpaDbStorage.class, GenreDbStorage.class, MpaRowMapper.class, GenreRowMapper.class})
class FilmorateApplicationTests {

    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    private User user;
    private User user2;
    private User user3;
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

        user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());
        userDbStorage.addUser(user);

        user2 = new User();
        user2.setEmail("test2@mail.ru");
        user2.setLogin("login2");
        user2.setName("name2");
        user2.setBirthday(LocalDate.now());
        userDbStorage.addUser(user2);

        user3 = new User();
        user3.setEmail("test3@mail.ru");
        user3.setLogin("login3");
        user3.setName("name3");
        user3.setBirthday(LocalDate.now());
        userDbStorage.addUser(user3);

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
    public void testFindUserById() {
        User user1 = userDbStorage.getUserById(1);

        assertThat(user1)
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void testFindAllUsers() {
        Collection<User> allUsers = userDbStorage.getAllUsers();
        assertThat(allUsers.size()).isEqualTo(3);

        List<String> emails = allUsers.stream()
                .map(User::getEmail)
                .toList();
        assertThat(emails.contains("test@mail.ru")).isTrue();
    }

    @Test
    public void testUpdateUser() {
        user.setName("Updated name");
        userDbStorage.updateUser(user);
        Collection<User> allUsers = userDbStorage.getAllUsers();

        assertThat(allUsers.size()).isEqualTo(3);

        List<String> names = allUsers.stream()
                .map(User::getName)
                .toList();
        assertThat(names.contains("Updated name")).isTrue();
    }

    @Test
    public void testAddFriend() {
        userDbStorage.addFriend(1, 2);
        Collection<User> friends = userDbStorage.getFriendsForUser(1);
        assertThat(friends.size()).isEqualTo(1);

        List<Integer> ids = friends.stream()
                .map(User::getId)
                .toList();
        assertThat(ids.contains(2)).isTrue();

        Collection<User> user2Friends = userDbStorage.getFriendsForUser(2);
        assertThat(user2Friends.isEmpty()).isTrue();
    }

    @Test
    public void testDeleteFriend() {
        userDbStorage.addFriend(1, 2);
        Collection<User> friends = userDbStorage.getFriendsForUser(1);
        assertThat(friends.size()).isEqualTo(1);

        List<Integer> ids = friends.stream()
                .map(User::getId)
                .toList();
        assertThat(ids.contains(2)).isTrue();
        userDbStorage.removeFriend(1, 2);

        Collection<User> user1Friends = userDbStorage.getFriendsForUser(1);
        assertThat(user1Friends.isEmpty()).isTrue();
    }

    @Test
    public void testFindFilmById() {
        Film film1 = filmDbStorage.getFilmById(1);
        assertThat(film1)
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void testFindAllFilms() {
        Collection<Film> allFilms = filmDbStorage.getAllFilms();
        assertThat(allFilms.size()).isEqualTo(2);

        List<String> names = allFilms.stream()
                .map(Film::getName)
                .toList();
        assertThat(names.contains("filmName")).isTrue();
    }

    @Test
    public void testUpdateFilm() {
        film.setName("Updated name");
        filmDbStorage.updateFilm(film);
        Collection<Film> allFilms = filmDbStorage.getAllFilms();
        assertThat(allFilms.size()).isEqualTo(2);

        List<String> names = allFilms.stream()
                .map(Film::getName)
                .toList();
        assertThat(names.contains("Updated name")).isTrue();
    }

    @Test
    public void testAddLike() {
        filmDbStorage.addLikeFilm(2, 1);
        Collection<Film> mostLiked = filmDbStorage.getPopularFilms(1);
        assertThat(mostLiked.size()).isEqualTo(1);

        List<String> names = mostLiked.stream()
                .map(Film::getName)
                .toList();
        assertThat(names.contains("filmName2")).isTrue();
    }

    @Test
    public void testDeleteLike() {
        filmDbStorage.addLikeFilm(2, 1);
        String selectQuery = "SELECT * FROM likes";
        List<Map<String, Object>> rows = jdbc.queryForList(selectQuery);
        assertThat(rows.size()).isEqualTo(1);

        filmDbStorage.deleteLikeFilm(2, 1);
        List<Map<String, Object>> rowsAfter = jdbc.queryForList(selectQuery);
        assertThat(rowsAfter.isEmpty()).isTrue();
    }

    @Test
    public void testFindAllMpa() {
        Collection<Mpa> allMpa = mpaDbStorage.getAllMpa();
        assertThat(allMpa.size()).isEqualTo(5);
    }

    @Test
    public void testFindAllGenres() {
        Collection<Genre> allGenres = genreDbStorage.getAllGenre();
        assertThat(allGenres.size()).isEqualTo(6);
    }

    @Test
    public void testFindMpaById() {
        Mpa mpa = mpaDbStorage.getMpaById(1);
        assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void testFindGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);
        assertThat(genre)
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void testFilmGenres() {
        Collection<Genre> filmGenresBeforeAdding = genreDbStorage.findGenreByFilmId(1);
        assertThat(filmGenresBeforeAdding.size()).isEqualTo(2);
    }
}

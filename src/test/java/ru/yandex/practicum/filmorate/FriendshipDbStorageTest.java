package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FriendshipDbStorage.class})
public class FriendshipDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbc;
    private final FriendshipDbStorage friendshipDbStorage;

    private User user;
    private User user2;

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
    }

    @Test
    public void testAddFriend() {
        friendshipDbStorage.addFriend(user.getId(), user2.getId());
        Collection<User> friends = friendshipDbStorage.getFriendsForUserId(user.getId());
        assertThat(friends.size()).isEqualTo(1);

        List<Integer> ids = friends.stream()
                .map(User::getId)
                .toList();
        assertThat(ids.contains(2)).isTrue();

        Collection<User> user2Friends = friendshipDbStorage.getFriendsForUserId(user2.getId());
        assertThat(user2Friends.isEmpty()).isTrue();
    }

    @Test
    public void testDeleteFriend() {
        friendshipDbStorage.addFriend(user.getId(), user2.getId());
        Collection<User> friends = friendshipDbStorage.getFriendsForUserId(1);
        assertThat(friends.size()).isEqualTo(1);

        List<Integer> ids = friends.stream()
                .map(User::getId)
                .toList();
        assertThat(ids.contains(2)).isTrue();
        friendshipDbStorage.removeFriend(user.getId(), user2.getId());

        Collection<User> user1Friends = friendshipDbStorage.getFriendsForUserId(user.getId());
        assertThat(user1Friends.isEmpty()).isTrue();
    }

}
package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Repository
public class FriendshipDbStorage {
    private final JdbcTemplate jdbc;

    private static final String ADD_FRIEND_ID_QUERY = "INSERT INTO friendship(user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND_ID_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_ALL_USER_FRIENDS_QUERY = """
            SELECT * FROM users AS u
            INNER JOIN friendship AS uf ON u.user_id = uf.friend_id WHERE uf.user_id = ?""";


    public void addFriend(int userId, int friendId) {
        jdbc.update(ADD_FRIEND_ID_QUERY, userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        jdbc.update(REMOVE_FRIEND_ID_QUERY, userId, friendId);
    }

    public Set<User> getFriendsForUserId(int userId) {
        List<User> friends = jdbc.query(FIND_ALL_USER_FRIENDS_QUERY, new UserRowMapper(), userId);
        return new HashSet<>(friends);
    }
}
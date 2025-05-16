package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class UserDbStorage implements UserStorage {
    protected final JdbcTemplate jdbc;
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String ADD_FRIEND_ID_QUERY = "INSERT INTO friendship(user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND_ID_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIEND_ID_QUERY =
            "SELECT friend_id FROM friendship WHERE user_id = ?";


    public UserDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public User addUser(User user) {
        Integer generatedId = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(generatedId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbc.update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public User getUserById(int id) {
        try {
            User user = jdbc.queryForObject(FIND_BY_ID_QUERY, new Object[]{id}, new UserRowMapper());
            if (user == null) {
                throw new NotFoundException("Пользователь c таким id не найден: " + id);
            }
            user.setFriends(getFriendsId(user.getId())); //думал все перенести в UserRowMapper, чтобы там сразу обьект со всеми полями получать, но вроде как не правильно там обращаться к БД, поэтому сделал здесь
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь c таким id не найден: " + id);
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        List<User> users = jdbc.query(FIND_ALL_QUERY, new UserRowMapper());
        for (User user : users) {
            user.setFriends(getFriendsId(user.getId()));
        }
        return users;
    }

    protected Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        if (id != null) {
            return id.intValue();
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    public void addFriend(int userId, int friendId) {
        jdbc.update(ADD_FRIEND_ID_QUERY, userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        jdbc.update(REMOVE_FRIEND_ID_QUERY, userId, friendId);
    }

    public Set<Integer> getFriendsId(int userId) {
        List<Integer> friendsList = jdbc.queryForList(GET_FRIEND_ID_QUERY, new Object[]{userId}, Integer.class);
        return new HashSet<>(friendsList);
    }

    public List<User> getFriendsForUser(int userId) {
        List<Integer> friendsList = jdbc.queryForList(GET_FRIEND_ID_QUERY, new Object[]{userId}, Integer.class);
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsList) {
            friends.add(getUserById(id));
        }
        return friends;
    }
}

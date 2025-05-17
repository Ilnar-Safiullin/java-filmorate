package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Repository
public class UserDbStorage implements UserStorage {
    protected final JdbcTemplate jdbc;

    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String ADD_FRIEND_ID_QUERY = "INSERT INTO friendship(user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND_ID_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIEND_ID_QUERY =
            "SELECT friend_id FROM friendship WHERE user_id = ?";
    private static final String GET_All_USERS = "SELECT u.user_id, u.email, u.login, u.name, u.birthday, f.friend_id " +
            "FROM users u " +
            "LEFT JOIN friendship f ON u.user_id = f.user_id";
    private static final String GET_USER_BY_ID = "SELECT u.user_id, u.email, u.login, u.name, u.birthday, f.friend_id " +
            "FROM users u " +
            "LEFT JOIN friendship f ON u.user_id = f.user_id " +
            "WHERE u.user_id = ?";


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
        Map<Integer, User> userMap = new HashMap<>();

        jdbc.query(GET_USER_BY_ID, rs -> {
            Integer userId = rs.getInt("user_id");
            User user = userMap.get(userId);

            if (user == null) {
                user = new User();
                user.setId(userId);
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setName(rs.getString("name"));
                user.setBirthday(rs.getObject("birthday", LocalDate.class));
                user.setFriends(new LinkedHashSet<>());
                userMap.put(userId, user);
            }

            Integer friendId = rs.getObject("friend_id", Integer.class);
            if (friendId != null) {
                user.getFriends().add(friendId);
            }
        }, id);
        if (userMap.isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        return userMap.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        Map<Integer, User> userMap = new HashMap<>();

        jdbc.query(GET_All_USERS, (resultSet) -> {
            int userId = resultSet.getInt("user_id");
            User user = userMap.get(userId);

            if (user == null) {
                user = new User();
                user.setId(userId);
                user.setEmail(resultSet.getString("email"));
                user.setLogin(resultSet.getString("login"));
                user.setName(resultSet.getString("name"));
                java.sql.Date birthdayDate = resultSet.getDate("birthday");
                user.setBirthday(birthdayDate.toLocalDate());
                user.setFriends(new HashSet<>());
                userMap.put(userId, user);
            }

            Integer friendId = resultSet.getInt("friend_id");
            if (friendId != null) {
                user.getFriends().add(friendId); // Добавляем друга в коллекцию
            }
        });

        return new ArrayList<>(userMap.values());
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

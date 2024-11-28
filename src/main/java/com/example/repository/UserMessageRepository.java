package com.example.repository;

import com.example.model.UserMessage;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;


public class UserMessageRepository {
    private final JdbcTemplate<UserMessage> jdbcTemplate;

    public UserMessageRepository() throws NamingException {
        jdbcTemplate = new JdbcTemplate<>();
    }

    public List<UserMessage> findAll() throws SQLException {
        String sql = "SELECT * FROM user_message ORDER BY id";
        return jdbcTemplate.queryForList(sql, rs -> {
            var chatUser = new UserMessage();
            chatUser.setId(rs.getString("id"));
            chatUser.setUserId(rs.getString("user_id"));
            chatUser.setText(rs.getString("text"));
            chatUser.setTime(rs.getTimestamp("time").toLocalDateTime());
            return chatUser;
        });
    }

    public UserMessage findById(String messageId) throws SQLException {
        String sql = "SELECT * FROM user_message where id = ?";
        return jdbcTemplate.queryForList(sql, List.of(messageId), rs -> {
            var chatUser = new UserMessage();
            chatUser.setId(rs.getString("id"));
            chatUser.setUserId(rs.getString("user_id"));
            chatUser.setText(rs.getString("text"));
            chatUser.setTime(rs.getTimestamp("time").toLocalDateTime());
            return chatUser;
        }).get(0);
    }

    public int add(UserMessage userMessage) throws SQLException {
        return jdbcTemplate.update("INSERT INTO user_message (id, user_id, text) values (?, ?, ?)",
                Arrays.asList(userMessage.getId(), userMessage.getUserId(), userMessage.getText()));
    }

}

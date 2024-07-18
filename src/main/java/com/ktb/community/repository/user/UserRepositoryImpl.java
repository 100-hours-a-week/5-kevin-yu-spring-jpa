package com.ktb.community.repository.user;

import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.entity.user.User;
import com.ktb.community.entity.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public UserRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
    }

    @Override
    public User save(User user) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(user);
        Long id = jdbcInsert.executeAndReturnKey(param).longValue();

        user.setId(id);
        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users WHERE status = 'ACTIVE'";

        return template.query(sql, userRowMapper());
    }

    @Override
    public Optional<User> findById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = :user_id AND status = 'ACTIVE'";

        try {
            SqlParameterSource param = new MapSqlParameterSource().addValue("user_id", userId);
            return Optional.of(template.queryForObject(sql, param, userRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email AND status = 'ACTIVE'";

        try {
            SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
            return Optional.of(template.queryForObject(sql, param, userRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Integer countByNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM users WHERE nickname = :nickname AND status = 'ACTIVE'";

        SqlParameterSource param = new MapSqlParameterSource().addValue("nickname", nickname);
        return template.queryForObject(sql, param, Integer.class);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .nickname(rs.getString("nickname"))
                .status(UserStatus.valueOf(rs.getString("status")))
                .profileImage(rs.getString("profile_image"))
                .joinedAt(rs.getTimestamp("joined_at").toLocalDateTime())
                .lastLogin(rs.getTimestamp("last_login") != null ?
                        rs.getTimestamp("last_login").toLocalDateTime() : null)
                .deletedAt(rs.getTimestamp("deleted_at") != null ?
                        rs.getTimestamp("deleted_at").toLocalDateTime() : null)
                .role(rs.getString("role"))
                .build();
    }

    @Override
    public void modifyUserInfo(UserRequestDto dto) {
        String sql = "UPDATE users SET nickname = :nickname";
        sql += dto.getProfileImage() != null ? ", profile_image = :profile_image" : "";
        sql += " WHERE user_id = :user_id AND status = 'ACTIVE'";

        template.update(sql, new MapSqlParameterSource()
                .addValue("nickname", dto.getNickname())
                .addValue("profile_image", dto.getProfileImage())
                .addValue("user_id", dto.getId()));
    }

    @Override
    public void modifyPassword(Long userId, String password) {
        String sql = "UPDATE users SET password = :password WHERE user_id = :user_id AND status = 'ACTIVE'";

        template.update(sql, new MapSqlParameterSource()
                .addValue("password", password)
                .addValue("user_id", userId));
    }

    @Override
    public void modifyLoginTime(Long userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = :user_id AND status = 'ACTIVE'";

        template.update(sql, new MapSqlParameterSource()
                .addValue("user_id", userId));
    }

    @Override
    public void remove(Long userId) {
        String sql = "UPDATE users SET status = 'DELETED', deleted_at = NOW() WHERE user_id = :user_id AND status = 'ACTIVE'";

        template.update(sql, new MapSqlParameterSource()
                .addValue("user_id", userId));
    }
}
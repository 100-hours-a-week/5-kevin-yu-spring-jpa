package com.ktb.community.repository.user;

import com.ktb.community.dto.user.UserRequestDto;
import com.ktb.community.dto.user.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
    public UserResponseDto save(UserRequestDto dto) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(dto);
        Long id = jdbcInsert.executeAndReturnKey(param).longValue();

        return UserResponseDto.builder()
                .id(id)
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .profileImage(dto.getProfileImage())
                .build();
    }

    @Override
    public List<UserResponseDto> findAll() {
        String sql = "SELECT user_id, email, nickname, profile_image FROM users WHERE status = 'ACTIVE'";

        return template.query(sql, userRowMapper());
    }

    @Override
    public Optional<UserResponseDto> findById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = :user_id AND status = 'ACTIVE'";

        try {
            SqlParameterSource param = new MapSqlParameterSource().addValue("user_id", userId);
            return Optional.of(template.queryForObject(sql, param, userRowMapper())
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<UserResponseDto> userRowMapper() {
        return BeanPropertyRowMapper.newInstance(UserResponseDto.class);
    }

    @Override
    public void modify(UserRequestDto dto) {
        String sql = "UPDATE users SET nickname = :nickname, profile_image = :profile_image " +
                    "WHERE user_id = :user_id AND status = 'ACTIVE'";

        template.update(sql, new MapSqlParameterSource()
                .addValue("nickname", dto.getNickname())
                .addValue("profile_image", dto.getProfileImage())
                .addValue("user_id", dto.getId()));
    }

    @Override
    public void remove(Long userId) {
        String sql = "UPDATE users SET status = 'DELETED', deleted_at = NOW() WHERE user_id = :user_id";

        template.update(sql, new MapSqlParameterSource()
                .addValue("user_id", userId));
    }
}
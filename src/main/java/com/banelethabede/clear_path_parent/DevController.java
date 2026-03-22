package com.banelethabede.clear_path_parent;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile("dev") // only active in dev
public class DevController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/reset-db")
    public String resetDatabase() {
        jdbcTemplate.execute("DROP SCHEMA public CASCADE;");
        jdbcTemplate.execute("CREATE SCHEMA public;");
        return "Database reset complete";
    }
}
package com.apihub.admin.controller;

import com.apihub.common.result.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理端健康检查。
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final JdbcTemplate jdbcTemplate;

    public AdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return Result.ok(Map.of(
                "service", "api-admin",
                "status", "UP",
                "db", one != null && one == 1 ? "UP" : "DOWN"
        ));
    }
}

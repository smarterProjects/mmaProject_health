package com.health.dao;

import com.health.pojo.User;

public interface UserDao {
    User findByUsername(String username);
}

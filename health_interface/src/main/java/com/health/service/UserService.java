package com.health.service;

import com.health.pojo.User;

public interface UserService {

    /**
     * 通过用户名查询用户信息 包含角色与权限
     * @param username
     * @return
     */
    User findByUsername(String username);
}

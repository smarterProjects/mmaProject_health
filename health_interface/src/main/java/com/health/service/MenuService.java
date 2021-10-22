package com.health.service;

import com.health.pojo.Menu;

import java.util.List;

public interface MenuService {
    List<Menu> findMenus(String name);
}

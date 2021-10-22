package com.health.dao;

import com.health.pojo.Menu;

import java.util.List;

public interface MenuDao {
    List<Menu> findMenus(String name);

    List<Menu> findChildrenMenuByParentId(int parentId);
}

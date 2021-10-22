package com.health.dao;

import com.github.pagehelper.Page;
import com.health.pojo.Permission;

public interface PermissionDao {
    Page<Permission> findByCondition(String queryString);

    void add(Permission permission);

    Permission findById(int id);

    void edit(Permission permission);

    int findCountByPermissionId(int id);

    void delete(int id);
}

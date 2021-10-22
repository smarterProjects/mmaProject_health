package com.health.service;

import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.Permission;

public interface PermissionService {
    PageResult findPage(QueryPageBean queryPageBean);

    void add(Permission permission);

    Permission findById(int id);

    void edit(Permission permission);

    void delete(int id);
}

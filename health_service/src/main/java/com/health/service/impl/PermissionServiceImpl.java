package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.dao.PermissionDao;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.exception.HealthException;
import com.health.pojo.Permission;
import com.health.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Service(interfaceClass = PermissionService.class)
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //使用分页插件分页
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        Page<Permission> page = permissionDao.findByCondition(queryPageBean.getQueryString());
        return new PageResult<Permission>(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Permission permission) {
        permissionDao.add(permission);
    }

    @Override
    public Permission findById(int id) {
        return permissionDao.findById(id);
    }

    @Override
    public void edit(Permission permission) {
        permissionDao.edit(permission);
    }

    @Override
    public void delete(int id) {
        //判断该权限是否被角色使用，若使用则不能删除并报错，如果没使用则删除
        int count = permissionDao.findCountByPermissionId(id);
        if (count>0){
            throw new HealthException("该权限已被使用了，不能删除！");
        }
        //删除
        permissionDao.delete(id);
    }
}

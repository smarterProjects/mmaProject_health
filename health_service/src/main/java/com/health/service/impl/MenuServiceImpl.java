package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.dao.MenuDao;
import com.health.pojo.Menu;
import com.health.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(interfaceClass = MenuService.class)
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

    @Override
    public List<Menu> findMenus(String name) {
        List<Menu> menus = menuDao.findMenus(name);
        for (Menu menu : menus){
            int parentId = menu.getId();
            List<Menu> childrenMenus = menuDao.findChildrenMenuByParentId(parentId);
            menu.setChildren(childrenMenus);
        }
        return menus;
    }
}

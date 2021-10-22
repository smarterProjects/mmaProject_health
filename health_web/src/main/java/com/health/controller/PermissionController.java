package com.health.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.entity.Result;
import com.health.pojo.Permission;
import com.health.service.PermissionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Reference
    private PermissionService permissionService;

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult page = permissionService.findPage(queryPageBean);
        return page;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Permission permission){
        permissionService.add(permission);
        return new Result(true, MessageConstant.ADD_PERMISSION_SUCCESS);
    }

    @RequestMapping("/findById")
    public Result findById(int id){
        Permission permission = permissionService.findById(id);
        return new Result(true,MessageConstant.QUERY_PERMISSION_SUCCESS,permission);
    }


    @RequestMapping("/edit")
    public Result edit(@RequestBody Permission permission){
        permissionService.edit(permission);
        return new Result(true, MessageConstant.EDIT_PERMISSION_SUCCESS);
    }

    @RequestMapping("/delete")
    public Result delete(int id){
        try{
            permissionService.delete(id);
            return new Result(true,MessageConstant.DELETE_PERMISSION_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_PERMISSION_FAIL);
        }
    }
}

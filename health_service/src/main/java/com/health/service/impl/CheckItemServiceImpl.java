package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.dao.CheckItemDao;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.exception.HealthException;
import com.health.pojo.CheckItem;
import com.health.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 发布服务到zookeeper上，打上alibaba的service注解
 * interfaceClass：指定发布的服务的接口为CheckItemServcie类型
 */
@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        //使用分页插件的静态方法
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //是否有查询条件，有查询条件要实现模糊查询  用%拼接
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            //不为空，有查询条件
            //拼接%
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //Page时PageHelper的内置对象，该对象代表分页信息
        Page<CheckItem> page = checkItemDao.findByCondition(queryPageBean.getQueryString());
        //将page包装到pageResult中再返回

        return new PageResult<CheckItem>(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteById(int id) throws HealthException {
        //判断检查项是否被检查组使用了
        int count = checkItemDao.findCountByCheckItemId(id);
        if(count>0){
            //报错-自定义异常
            throw new HealthException("该检查项已经被使用了，不能删除！！！");
        }
        //删除
        checkItemDao.deleteById(id);

    }

    @Override
    public CheckItem findById(int id) {
        return checkItemDao.findById(id);
    }

    @Override
    public void update(CheckItem checkItem) {
        checkItemDao.update(checkItem);
    }
}

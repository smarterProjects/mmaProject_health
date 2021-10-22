package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.health.dao.SetmealDao;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.exception.HealthException;
import com.health.pojo.Setmeal;
import com.health.service.SetmealService;
import com.health.utils.QiNiuUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
public class SetmealServcieImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;

    //因为对数据库进行了多个操作，因此要加事务控制
    @Override
    @Transactional
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        //先添加套餐
        setmealDao.add(setmeal);
        //获取套餐的id
        Integer setmealId = setmeal.getId();
        //添加套餐与检查组的关系
        if (checkgroupIds!=null){
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmealId,checkgroupId);
            }
        }
    }

    @Override
    public PageResult<Setmeal> findPage(QueryPageBean queryPageBean) {
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        if (!StringUtils.isEmpty(queryPageBean.getQueryString())){
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        Page<Setmeal> page = setmealDao.findByCondition(queryPageBean.getQueryString());

        return new PageResult<>(page.getTotal(),page.getResult());
    }

    @Override
    public Setmeal findById(int id) {
        return setmealDao.findById(id);
    }

    @Override
    public List<Integer> findCheckgroupIdsBySetmealId(int id) {
        return setmealDao.findCheckgroupIdsBySetmealId(id);
    }

    @Override
    @Transactional
    public void update(Setmeal setmeal, Integer[] checkgroupIds) {
        //更新套餐信息
        setmealDao.update(setmeal);
        //删除旧关系
        setmealDao.deleteSetmealCheckGroup(setmeal.getId());
        //添加新关系
        if (checkgroupIds!=null){
            for (Integer checkgroupId : checkgroupIds) {
                setmealDao.addSetmealCheckGroup(setmeal.getId(),checkgroupId);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(int id) throws HealthException {
        //判断套餐是否被订单使用了
        int count = setmealDao.findOrderCountBySetmealId(id);
        //使用了则报错
        if(count>0){
            throw new HealthException("该套餐已经被使用了，不能删除");
        }
        //没有使用先删除套餐与检查组的关系  再删除套餐
        setmealDao.deleteSetmealCheckGroup(id);
        setmealDao.deleteById(id);
    }

    @Override
    public List<String> findImgs() {
        return setmealDao.findImgs();
    }

    @Override
    public List<Setmeal> findAll() {
        List<Setmeal> setmealList = setmealDao.findAll();
        // 拼接图片的完整路径 $.each
        for (Setmeal setmeal : setmealList) {
            setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        }
        // 生成列表静态文件
        generateSetmealList(setmealList);
        // 套餐详情页面
        generateSetmealDetails(setmealList);
        return setmealList;
    }


    @Autowired
    private FreeMarkerConfigurer freeMarkerConfig;

    @Value("D:/health_parent/health_mobile/src/main/webapp/pages")
    private String out_put_path;

    /**
     * 生成 详情页面
     * @param setmealList
     */
    private void generateSetmealDetails(List<Setmeal> setmealList){
        for (Setmeal setmeal : setmealList) {
            // 补全检查组与检查项信息
            Setmeal setmealDetail = setmealDao.findDetailById(setmeal.getId());
            // 设置完整的图片
            setmealDetail.setImg(setmeal.getImg());
            // 数据模型
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("setmeal", setmealDetail);
            String templateName = "mobile_setmeal_detail.ftl";
            String filename = String.format("%s/setmeal_%d.html",out_put_path,setmealDetail.getId());
            generateHtml(templateName,dataMap,filename);
        }
    }


    private void generateHtml(String templateName,Map<String,Object> dataMap,String filename){
        // 获取模板
        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate(templateName);
            // utf-8 不能少了。少了就中文乱码
            BufferedWriter writer =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"utf-8"));
            // 填充数据到模板
            template.process(dataMap,writer);
            // 关闭流
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成套餐列表静态页面
     * @param setmealList
     */
    private void generateSetmealList(List<Setmeal> setmealList){
        Map<String,Object> dataMap = new HashMap<String,Object>();
        // key setmealList 与模板中的变量要一致
        dataMap.put("setmealList",setmealList);
        // 输出
        String setmealListFile = out_put_path + "/mobile_setmeal.html";

        generateHtml("mobile_setmeal.ftl", dataMap, setmealListFile);
    }


    @Override
    public Setmeal findDetailById(int id) {
        return setmealDao.findDetailById(id);
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }
}

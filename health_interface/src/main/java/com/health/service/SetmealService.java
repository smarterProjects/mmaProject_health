package com.health.service;

import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.exception.HealthException;
import com.health.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    void add(Setmeal setmeal, Integer[] checkgroupIds);

    PageResult<Setmeal> findPage(QueryPageBean queryPageBean);

    Setmeal findById(int id);

    List<Integer> findCheckgroupIdsBySetmealId(int id);


    void update(Setmeal setmeal, Integer[] checkgroupIds);


    void deleteById(int id) throws HealthException;

    List<String> findImgs();

    List<Setmeal> findAll();

    Setmeal findDetailById(int id);

    List<Map<String, Object>> findSetmealCount();
}

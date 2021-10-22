package com.health.service;

import com.health.exception.HealthException;
import com.health.pojo.Order;

import java.util.Map;

/**
 * Description: No Description
 * User: Eric
 */
public interface OrderService {
    /**
     * 预约提交
     * @param paraMap
     * @return
     */
    Order submitOrder(Map<String, String> paraMap) throws HealthException;

    /**
     * 成功信息的展示
     * @param id
     * @return
     */
    Map<String, Object> findOrderDetailById(int id);
}

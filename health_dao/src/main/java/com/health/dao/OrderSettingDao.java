package com.health.dao;

import com.health.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: No Description
 * User: Eric
 */
public interface OrderSettingDao {
    /**
     * 通过日期查询预约设置信息
     * @param orderDate
     * @return
     */
    OrderSetting findByOrderDate(Date orderDate);

    /**
     * 更新可预约数
     * @param os
     */
    void updateNumber(OrderSetting os);

    /**
     * 添加预约设置
     * @param os
     */
    void add(OrderSetting os);

    /**
     * 通过月份查询预约设置信息
     * @param map
     * @return
     */
    List<Map<String, Integer>> getOrderSettingByMonth(Map<String, String> map);

    /**
     * 更新已预约人数
     */
    void editReservationsByOrderDate(OrderSetting orderSetting);

    long findCountByOrderDate(Date orderDate);
}

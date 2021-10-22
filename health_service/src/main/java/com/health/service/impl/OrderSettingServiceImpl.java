package com.health.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.health.dao.OrderSettingDao;
import com.health.exception.HealthException;
import com.health.pojo.OrderSetting;
import com.health.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Override
    @Transactional
    public void add(List<OrderSetting> orderSettings) throws HealthException {
        //遍历循环List<OrderSetting>
        for (OrderSetting orderSetting : orderSettings) {
            //通过日期查询是否已存在预约设置信息
            OrderSetting osInDb = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
            //若存在
            if (osInDb != null) {
                //       判断已预约人数是否大于要更新的可预约人数
                //       大于则报错
                if (osInDb.getReservations() > orderSetting.getNumber()) {
                    throw new HealthException(osInDb.getOrderDate() + "中，可预约数不能小于已预约数");
                } else {
                    //       小于则更新可预约人数
                    orderSettingDao.updateNumber(orderSetting);
                }
            } else {
                //若不存在，则插入预约设置
                orderSettingDao.add(orderSetting);
            }
        }
    }

    /**
     * 通过月份查询预约设置信息
     *
     * @param month
     * @return
     * List<Map<String, Integer>>中的形式如下：
     *                     { date: 1, number: 120, reservations: 1 },
     *                     { date: 3, number: 120, reservations: 1 },
     *                     { date: 4, number: 120, reservations: 120 },
     *                     { date: 6, number: 120, reservations: 1 },
     *                     { date: 8, number: 120, reservations: 1 }
     */
    @Override
    public List<Map<String, Integer>> getOrderSettingByMonth(String month) {
        //拼接开始日期和结束日期
        String startDate = month + "-01";
        String endDate = month + "-31";

        Map<String, String> map = new HashMap<String, String>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);

        return orderSettingDao.getOrderSettingByMonth(map);
    }

    /**
     * 更新预约数
     *
     * @param orderSetting
     */
    @Override
    public void editNumberByDate(OrderSetting orderSetting) throws HealthException {
        //通过日期查询是否已存在预约设置信息
        OrderSetting osInDb = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
        //若存在
        if (osInDb != null) {
            //       判断已预约人数是否大于要更新的可预约人数
            //       大于则报错
            if (osInDb.getReservations() > orderSetting.getNumber()) {
                throw new HealthException(osInDb.getOrderDate() + "中，可预约数不能小于已预约数");
            } else {
                //       小于则更新可预约人数
                orderSettingDao.updateNumber(orderSetting);
            }
        } else {
            //若不存在，则插入预约设置
            orderSettingDao.add(orderSetting);
        }
    }
}

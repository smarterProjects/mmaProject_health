package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.constant.RedisMessageConstant;
import com.health.entity.Result;
import com.health.pojo.Order;
import com.health.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    /**
     * 提交 预约
     * @param paraMap
     * @return
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> paraMap){
        // 验证码校验
        Jedis jedis = jedisPool.getResource();
        // 手机号码
        String telephone = paraMap.get("telephone");
        // 验证码的 key
        String key = RedisMessageConstant.SENDTYPE_ORDER + ":" + telephone;
        // redis中的验证码
        String codeInRedis = jedis.get(key);
        if(StringUtils.isEmpty(codeInRedis)){
            //没值 重新发送
            return new Result(false, "请重新获取验证码!");
        }
        // 前端传的验证码
        String validateCode = paraMap.get("validateCode");
        if(!codeInRedis.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        // 移除redis中的验证码，防止重复提交，
        jedis.del(key);// 测试时可注释掉
        // 设置预约的类型
        paraMap.put("orderType",Order.ORDERTYPE_WEIXIN);
        // 预约成功页面展示时需要用到id
        Order order = orderService.submitOrder(paraMap);
        return new Result(true, MessageConstant.ORDER_SUCCESS, order);
    }

    /**
     * 成功信息的展示
     */
    @GetMapping("/findById")
    public Result findById(int id){
        Map<String,Object> orderDetail = orderService.findOrderDetailById(id);
        return new Result(true, MessageConstant.ORDER_SUCCESS,orderDetail);
    }
}

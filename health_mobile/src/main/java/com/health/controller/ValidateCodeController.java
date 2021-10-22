package com.health.controller;

import com.health.constant.MessageConstant;
import com.health.constant.RedisMessageConstant;
import com.health.entity.Result;
import com.health.utils.SMSUtils;
import com.health.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 体检预约 发送验证码
     * @param telephone
     * @return
     */
    @PostMapping("/send4Order")
    public Result send4Order(String telephone){
        Jedis jedis = jedisPool.getResource();
        // 先从redis看是否存在
        String key = RedisMessageConstant.SENDTYPE_ORDER + ":" + telephone;
        String codeInRedis = jedis.get(key);
        if(null != codeInRedis){
            // 不为空，发送过了
            return new Result(false,"验证码已经发送过了，请注意查收");
        }
        // 没发送过
        // 生成验证码
        String validateCode = ValidateCodeUtils.generateValidateCode(6) + "";
        // 发送短信
        try {
            SMSUtils.sendSMS(telephone, validateCode);
            // 存入redis, 有效时间10分钟
            // 延长有效期 expire key 秒
            jedis.setex(key, 10*60, validateCode);
            jedis.close();
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    }

    /**
     * 发送登陆用的验证码
     */
    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        Jedis jedis = jedisPool.getResource();
        // 先从redis看是否存在
        String key = RedisMessageConstant.SENDTYPE_LOGIN + ":" + telephone;
        String codeInRedis = jedis.get(key);
        if(null != codeInRedis){
            // 不为空，发送过了
            return new Result(false,"验证码已经发送过了，请注意查收");
        }
        // 没发送过
        // 生成验证码
        String validateCode = ValidateCodeUtils.generateValidateCode(6) + "";
        // 发送短信
        try {
            SMSUtils.sendSMS(telephone, validateCode);
            // 存入redis, 有效时间10，15
            // 延长有效期 expire key 秒
            jedis.setex(key, 10*60, validateCode);
            jedis.close();
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    }


}

package com.health.controller;

import com.health.entity.Result;
import com.health.exception.HealthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 声明对异常的捕获
 * 全局异常处理
 *
 * 捕获的异常，遵循从小到大，即从进入小范围异常
 * 在类上加上@ControllerAdvice注解这个类就成为了全局异常处理类，通过@ExceptionHandler指定对应异常
 * 优点：将 Controller 层的异常和数据校验的异常进行统一处理，减少模板代码，减少编码量，提升扩展性和可维护性。
 *
 * 缺点：能处理 Controller 层的异常 (未使用try-catch进行捕获) 和 @Validated 校验器注解的异常，
 * 但是对于 Interceptor（拦截器）层的异常 和 Spring 框架层的异常，就无能为力了。
 *
 */
@RestControllerAdvice
public class HealthExceptionAdvice {

    /**
     * log的用法
     * info:打印日志，记录流程性的内容
     * debug:记录一些重要的数据，id,orderid,userid
     * error:记录异常的堆栈信息，代替e.printStackTrace()
     * 工作中的代码中不能出现System.out.println(),e.printStackTrace();因为调用这两个方法，会去调用硬件，人多的话，硬件受不了。
     */
    private static final Logger log = LoggerFactory.getLogger(HealthException.class);

    /**
     * ExceptionHandler:用来捕获指定类型的异常，相当于catch
     * @param e
     * @return
     */
    @ExceptionHandler(HealthException.class) //@ExceptionHandler(value = HealthException.class)注解中的value用于指定需要捕获的异常
    public Result handleHealthException(HealthException e){
        return new Result(false,e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        log.error("发生异常",e);
        return new Result(false,"操作异常，请联系管理员");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException e){
        return new Result(false, "权限不足");
    }

}

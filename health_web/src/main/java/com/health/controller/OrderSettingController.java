package com.health.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.Result;
import com.health.pojo.OrderSetting;
import com.health.service.OrderSettingService;
import com.health.utils.POIUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;


    /**
     * 批量导入预约设置
     * @param excelFile
     * @return
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile){

        try {
            //解析文件(读取文件,每行是一个String数组，每一行都存到一个List集合中)
            List<String[]> strings = POIUtils.readExcel(excelFile);
            //通过循环遍历将List<String[]>类型数据转换为List<OrderSetting>类型数据
            List<OrderSetting> orderSettings = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat(POIUtils.DATE_FORMAT);
            OrderSetting os = null;
            for (String[] arr : strings){
                //每个arr，代表一行数据
                Date orderDate = sdf.parse(arr[0]);
                int number = Integer.valueOf(arr[1]);
                os = new OrderSetting(orderDate,number);
                orderSettings.add(os);
            }
            //调用服务端
            orderSettingService.add(orderSettings);
            //返回结果给页面
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false,MessageConstant.IMPORT_ORDERSETTING_FAIL);
    }


    /**
     *
     */
    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String month){
        //调用服务端查询
        List<Map<String,Integer>> data = orderSettingService.getOrderSettingByMonth(month);
        return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,data);
    }

    @PostMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        //调用服务更新
        orderSettingService.editNumberByDate(orderSetting);
        return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
    }

}

package com.health.job;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.service.SetmealService;
import com.health.utils.QiNiuUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CleanImgJob {

    @Reference
    private SetmealService setmealService;

    /**
     * 定时清除垃圾图片
     */
    public void cleanImg(){
        //查出七牛云上的所有图片
        List<String> imgIn7Niu = QiNiuUtils.listFile();
        //查出数据库中的所有图片
        List<String> ingInDb = setmealService.findImgs();
        //七牛云中的所有图片减去数据库中的所有图片，得到的就是七牛云上的垃圾图片（即直接从imgIn7Niu上减去，剩下的还是在imgIn7Niu中）
        //相同对象才会移除
        imgIn7Niu.removeAll(ingInDb);
        //删除七牛云上的垃圾图片
        String[] strings = imgIn7Niu.toArray(new String[]{});
        QiNiuUtils.removeFiles(strings);

    }
}

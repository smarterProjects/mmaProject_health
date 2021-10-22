package com.health.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.Result;
import com.health.service.MemberService;
import com.health.service.ReportService;
import com.health.service.SetmealService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员数量折线图
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        //创建12个月的月份信息
        Calendar car = Calendar.getInstance();
        //日历减去一年
        car.add(Calendar.YEAR,-1);
        //创建月份信息集合
        List<String> months = new ArrayList<>();
        //格式化拿到的月份信息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        //遍历12个月
        for (int i = 0;i<12;i++){
            //每次加一个月
            car.add(Calendar.MONDAY,1);
            months.add(sdf.format(car.getTime()));

        }
        //调用会员服务查询
        List<Integer> memberCount = memberService.getMemberReport(months);
        //封装结果返回给页面
        Map<String,Object> map = new HashMap<>();
        map.put("months",months);
        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }

    /**
     * 套餐预约占比
     */
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        // 调用服务查询套餐预约占比
        List<Map<String,Object>> setmealCount = setmealService.findSetmealCount();
        // 抽取套餐名称集合
        List<String> setmealNames = new ArrayList<String>();
        for (Map<String, Object> map : setmealCount) {
            // {value:222,name:xx套餐}
            // null.toString()  (String)null
            String setmealName = (String)map.get("name");
            setmealNames.add(setmealName);
        }
        // 封装结果返回给页面
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("setmealNames",setmealNames);
        resultMap.put("setmealCount",setmealCount);
        return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,resultMap);
    }

    /**
     * 运营统计数据
     */
    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        Map<String,Object> reportData = reportService.getBusinessReportData();
        return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS,reportData);
    }


    /**
     * 导出运营统计数据
     */
    @GetMapping("/exportBusinessReport")
    public void exportBusinessReport(HttpServletRequest req, HttpServletResponse res){
        // 获取报表数据
        Map<String,Object> reportData = reportService.getBusinessReportData();
        // 获取导出的excel模板
        // getRealPath webapp/目录下
        String template = req.getSession().getServletContext().getRealPath("/template/report_template.xlsx");
        // 加载excel创建workbook
        // 在try(实现Closeable接口)
        try(Workbook wk = new XSSFWorkbook(template)) {
            // 获取工作表
            Sheet sht = wk.getSheetAt(0);
            // 找到单元格并赋值
            // 日期
            sht.getRow(2).getCell(5).setCellValue((String) reportData.get("reportDate"));
            // ================ 会员信息 ======================
            sht.getRow(4).getCell(5).setCellValue((Integer) reportData.get("todayNewMember"));
            sht.getRow(4).getCell(7).setCellValue((Integer) reportData.get("totalMember"));
            sht.getRow(5).getCell(5).setCellValue((Integer) reportData.get("thisWeekNewMember"));
            sht.getRow(5).getCell(7).setCellValue((Integer) reportData.get("thisMonthNewMember"));

            // ================ 预约数量 ================
            sht.getRow(7).getCell(5).setCellValue((Integer) reportData.get("todayOrderNumber"));
            sht.getRow(7).getCell(7).setCellValue((Integer) reportData.get("todayVisitsNumber"));
            sht.getRow(8).getCell(5).setCellValue((Integer) reportData.get("thisWeekOrderNumber"));
            sht.getRow(8).getCell(7).setCellValue((Integer) reportData.get("thisWeekVisitsNumber"));
            sht.getRow(9).getCell(5).setCellValue((Integer) reportData.get("thisMonthOrderNumber"));
            sht.getRow(9).getCell(7).setCellValue((Integer) reportData.get("thisMonthVisitsNumber"));

            // 热门套餐
            List<Map<String,Object>> hotSetmeal = (List<Map<String,Object>>)reportData.get("hotSetmeal");
            int rowIndex = 12;
            for (Map<String, Object> setmeal : hotSetmeal) {
                sht.getRow(rowIndex).getCell(4).setCellValue(((String) setmeal.get("name")));
                sht.getRow(rowIndex).getCell(5).setCellValue((Long)setmeal.get("setmeal_count"));
                BigDecimal proportion = (BigDecimal) setmeal.get("proportion");
                sht.getRow(rowIndex).getCell(6).setCellValue(proportion.doubleValue());
                sht.getRow(rowIndex).getCell(7).setCellValue((String)setmeal.get("remark"));
                rowIndex++;
            }
            String filename="运营数据统计.xlsx";
            filename = new String(filename.getBytes(),"ISO-8859-1");
            // 设置响应的头信息，告诉浏览器，是下载文件
            res.setHeader("Content-Disposition","attachment;filename=" + filename);
            // 告诉浏览器，我的内容体的文件格式是excel
            res.setContentType("application/vnd.ms-excel");
            wk.write(res.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

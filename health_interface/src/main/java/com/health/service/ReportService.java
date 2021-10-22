package com.health.service;

import java.util.Map;

/**
 * Description: No Description
 * User: Eric
 */
public interface ReportService {
    /**
     * 运营统计数据
     * @return
     */
    Map<String, Object> getBusinessReportData();
}

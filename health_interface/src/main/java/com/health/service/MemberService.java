package com.health.service;

import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.pojo.Member;

import java.util.List;

/**
 * Description: No Description
 * User: Eric
 */
public interface MemberService {
    /**
     * 通过手机号码查询会员
     * @param telephone
     * @return
     */
    Member findByTelephone(String telephone);

    /**
     * 添加新会员
     * @param member
     */
    void add(Member member);

    /**
     * 按月统计截止到每个月的会员总数量
     * @param months
     * @return
     */
    List<Integer> getMemberReport(List<String> months);

    PageResult<Member> findMemberByPage(QueryPageBean queryPageBean);
}

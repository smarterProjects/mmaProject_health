package com.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.entity.PageResult;
import com.health.entity.QueryPageBean;
import com.health.entity.Result;
import com.health.pojo.Member;
import com.health.service.MemberService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Reference
    private MemberService memberService;


    @RequestMapping("/findMemberByPage")
    public Result findMemberByPage(@RequestBody QueryPageBean queryPageBean){
        PageResult<Member> pageResult = memberService.findMemberByPage(queryPageBean);
        return new Result(true, MessageConstant.QUERY_MEMBER_SUCCESS,pageResult);

    }

}

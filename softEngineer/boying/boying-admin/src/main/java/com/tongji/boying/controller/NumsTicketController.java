package com.tongji.boying.controller;

import com.tongji.boying.common.api.CommonPage;
import com.tongji.boying.common.api.CommonResult;
import com.tongji.boying.dto.orderParam.TicketParam;
import com.tongji.boying.model.BoyingOrder;
import com.tongji.boying.model.BoyingTicket;
import com.tongji.boying.service.NumsTicketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = "NumsTicketController", description = "后台票管理")
@RequestMapping("/ticket")
public class NumsTicketController {

    @Autowired
    private NumsTicketService numsTicketService;

    @ApiOperation("查看票信息")
    @RequestMapping(value = "/listTickets", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<BoyingTicket>> listTickets(@Validated @RequestBody TicketParam param) {
        return CommonResult.success(CommonPage.restPage(numsTicketService.listTickets(param)));
    }
}
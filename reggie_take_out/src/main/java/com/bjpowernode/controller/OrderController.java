package com.bjpowernode.controller;

import com.bjpowernode.bean.Orders;
import com.bjpowernode.common.R;
import com.bjpowernode.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }
}

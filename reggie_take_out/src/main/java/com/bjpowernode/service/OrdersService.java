package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.bean.Orders;

public interface OrdersService extends IService<Orders> {
    //用户下单
    void submit(Orders orders);
}
package com.bjpowernode.dto;

import com.bjpowernode.bean.OrderDetail;
import com.bjpowernode.bean.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}

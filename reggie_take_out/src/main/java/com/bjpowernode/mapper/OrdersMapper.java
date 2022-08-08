package com.bjpowernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjpowernode.bean.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}

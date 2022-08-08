package com.bjpowernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjpowernode.bean.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}

package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.bean.Dish;
import com.bjpowernode.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，并插入相对应的口味数据
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品
    void updateWithFlavor(DishDto dishDto);

    //删除菜品同时需要删除菜品相关联的口味数据
    void removeWithFlavor(List<Long> ids);
}

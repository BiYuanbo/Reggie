package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.bean.Setmeal;
import com.bjpowernode.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时保存套餐和菜品的关联关系
    void saveSetmealAndDish(SetmealDto setmealDto);

    //删除套餐同时需要删除套餐和菜品的关联数据
    void removeWithDish(List<Long> ids);
}

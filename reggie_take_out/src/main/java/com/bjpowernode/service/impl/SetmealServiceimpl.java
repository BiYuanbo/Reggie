package com.bjpowernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjpowernode.bean.Setmeal;
import com.bjpowernode.bean.SetmealDish;
import com.bjpowernode.common.CustomException;
import com.bjpowernode.dto.SetmealDto;
import com.bjpowernode.mapper.SetmealMapper;
import com.bjpowernode.service.SetmealDishService;
import com.bjpowernode.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceimpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveSetmealAndDish(SetmealDto setmealDto) {
        //1.保存套餐基本信息
        setmealMapper.insert(setmealDto);
        //获取套餐id
        Long setmealId = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        //2.保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐同时需要删除套餐和菜品的关联数据(售卖中的不能删除)
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);

        Integer count = setmealMapper.selectCount(lambdaQueryWrapper);
        //如果大于0，说明在售卖中，不可删除
        if (count>0){
            //如果不能删除，则抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐中的数据
        setmealMapper.deleteBatchIds(ids);

        //删除关联表的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);
    }
}

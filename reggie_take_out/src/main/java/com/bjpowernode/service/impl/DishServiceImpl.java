package com.bjpowernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjpowernode.bean.Dish;
import com.bjpowernode.bean.DishFlavor;
import com.bjpowernode.bean.Setmeal;
import com.bjpowernode.common.CustomException;
import com.bjpowernode.dto.DishDto;
import com.bjpowernode.mapper.DishFlavorMapper;
import com.bjpowernode.mapper.DishMapper;
import com.bjpowernode.service.DishFlavorService;
import com.bjpowernode.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        dishMapper.insert(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        //保存菜品口味数据到菜品口味表dish-flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = dishMapper.selectById(id);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> flavorlist = dishFlavorService.list(queryWrapper);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(flavorlist);

        return dishDto;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        dishMapper.updateById(dishDto);

        //更新dish_flavor表基本信息
        //1.先清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //2.再添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 删除菜品同时需要删除菜品相关联的口味数据(售卖中的不能删除)
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        //查询菜品状态，确定是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        Integer count = dishMapper.selectCount(queryWrapper);
        if (count>0){
            //如果不能删除，则抛出一个业务异常
            throw new CustomException("菜品正在售卖中，不能删除");
        }

        //如果可以删除，先删除菜品中的数据
        dishMapper.deleteBatchIds(ids);

        //删除关联表的数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
    }
}

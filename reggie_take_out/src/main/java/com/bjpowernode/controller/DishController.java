package com.bjpowernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjpowernode.bean.Category;
import com.bjpowernode.bean.Dish;
import com.bjpowernode.bean.DishFlavor;
import com.bjpowernode.common.R;
import com.bjpowernode.dto.DishDto;
import com.bjpowernode.service.CategoryService;
import com.bjpowernode.service.DishFlavorService;
import com.bjpowernode.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //获取显示在页面的数据
        List<Dish> records = pageInfo.getRecords();

        /*List<DishDto> list = null;
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            //分类id
            Long categoryId = record.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category!=null){
                String categoryName = category.getName();

                dishDto.setCategoryName(categoryName);

                list.add(dishDto);
            }
        }*/

        //瑞吉外卖p61
        List<DishDto> list = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category!=null) {
                String categoryName = category.getName();

                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());


        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        if (dishDto!=null){
            return R.success(dishDto);
        }

        return R.error("未查询到相关信息");
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("菜品修改成功");
    }

    /**
     * 修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateById(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Dish::getId,ids);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        for (Dish dish : dishList) {
            dish.setStatus(status);
            dishService.updateById(dish);
        }

        return R.success("状态修改成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeWithFlavor(ids);

        return R.success("删除成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param //categoryId
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }*/

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category!=null) {
                String categoryName = category.getName();

                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //当前菜品口味的集合
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }
}

package com.bjpowernode.dto;

import com.bjpowernode.bean.Setmeal;
import com.bjpowernode.bean.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.bean.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}

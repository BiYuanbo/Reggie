package com.bjpowernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjpowernode.bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

package com.bjpowernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjpowernode.bean.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}

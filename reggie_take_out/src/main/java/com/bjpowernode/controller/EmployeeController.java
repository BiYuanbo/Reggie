package com.bjpowernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjpowernode.bean.Employee;
import com.bjpowernode.common.R;
import com.bjpowernode.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //登录功能
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee eq = employeeService.getOne(queryWrapper);

        //如果没有查询到则返回登录失败
        if (eq == null){
            return R.error("用户名不存在，登录失败");
        }

        //密码对比，若不一致则登录失败
        if (!eq.getPassword().equals(password)) {
            return R.error("密码不一致，登录失败");
        }

        //查看员工状态，如果已禁用，则登陆失败
        if (eq.getStatus() == 0){
            return R.error("账号已禁用，登录失败");
        }

        //登录成功
        request.getSession().setAttribute("employee",eq.getId());
        return R.success(eq);
    }

    //后台退出登录功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        //设置初始密码为123456，需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        /*//获取系统当前时间
        employee.setCreateTime(LocalDateTime.now());
        //信息更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        //添加
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    //员工信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.hasText(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    //根据id修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        /*employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employee.setUpdateTime(LocalDateTime.now());*/
        employeeService.updateById(employee);

        return R.success("员工信息修改成成功");
    }

    //根据id查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);

        if (employee!=null){
            return R.success(employee);
        }

        return R.error("未查询到对应员工信息");
    }
}

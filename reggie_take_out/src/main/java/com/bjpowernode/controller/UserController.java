package com.bjpowernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bjpowernode.bean.User;
import com.bjpowernode.common.R;
import com.bjpowernode.service.UserService;
import com.bjpowernode.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.hasText(phone)){
            //生成6位随机验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();

            //调用阿里云提供的短信服务API完成发送短信(此处以日志代替)
            log.info("code={}",code);

            //需要将生成的验证码保存到session
            session.setAttribute(phone,code);

            return R.success("手机验证码发送成功");
        }

        return R.error("手机验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(HttpSession session, @RequestBody Map map){
        System.out.println(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码(页面提交的验证码和Session中保存的验证码比对)
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的比对
        if (codeInSession!=null&&codeInSession.equals(code)){
            //比对成功，登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            //判断当前手机号的用户是否为新用户，如果是新用户就自动完成注册
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user",user.getId());

            return R.success(user);
        }

        return R.error("登录失败");
    }
}

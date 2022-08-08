package com.bjpowernode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
/**
 * 启动类上使用@ServletComponentScan 注解后
 * Servlet可以直接通过@WebServlet注解自动注册
 * Filter可以直接通过@WebFilter注解自动注册
 * Listener可以直接通过@WebListener 注解自动注册
 */
@ServletComponentScan
/**
 * 开启事务支持
 */
@EnableTransactionManagement
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
    }
}

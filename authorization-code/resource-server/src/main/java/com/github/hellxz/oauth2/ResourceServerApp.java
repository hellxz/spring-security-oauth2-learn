package com.github.hellxz.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 授权服务器 入口
 *
 * @author hellxz
 */
@SpringBootApplication
public class ResourceServerApp {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApp.class, args);
    }
}

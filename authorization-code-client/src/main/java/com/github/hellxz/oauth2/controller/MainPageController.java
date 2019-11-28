package com.github.hellxz.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainPageController {

    @GetMapping(value = {"/", "/index"})
    public ModelAndView index(){
        //访问主页
        return new ModelAndView("index");
    }
}

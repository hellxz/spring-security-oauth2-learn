package com.github.hellxz.oauth2.web;

import com.github.hellxz.oauth2.domain.ClientUser;
import com.github.hellxz.oauth2.service.ClientUserService;
import com.github.hellxz.oauth2.service.TokenService;
import com.github.hellxz.oauth2.utils.SecurityUtils;
import com.github.hellxz.oauth2.web.vo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;

/**
 * 页面控制器，本示例中所有controller都在这里
 */
@Controller
public class MainPageController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * 主页
     */
    @GetMapping(value = {"/", "/index"})
    public String index(){
        return "index";
    }

    /**
     * 回调
     */
    @GetMapping("/callback")
    public ModelAndView callback(@RequestParam("code") String code) throws UnsupportedEncodingException {
        String token = tokenService.getToken(code);
        if(StringUtils.isNotBlank(token)){
            securityUtils.updateToken(token);
            return new ModelAndView("redirect:/user-info");
        }
        throw new RuntimeException("请求超时");
    }

    @GetMapping("/user-info")
    public ModelAndView userInfoPage(){
        //获取当前登录的用户，这个用户对象中包含数据库中保存的token
        ClientUser currentUser = (ClientUser)securityUtils.getCurrentUser();
        //不存在需要去走一次授权码模式流程
        if(null != currentUser && StringUtils.isBlank(currentUser.getAccessToken())){
            //重定向到授权服务器进行授权, 先获取授权码
            return new ModelAndView("redirect:"+tokenService.getAuthorizeUrl());
        }

        //正常请求资源服务器，获取用户信息
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getAccessToken());
        RequestEntity<MultiValueMap<String, String>> requestEntity
                = new RequestEntity<>(headers,HttpMethod.GET, URI.create("http://localhost:8081/user/jack"));
        ResponseEntity<UserInfo> exchange = restTemplate.exchange(requestEntity, UserInfo.class);
        if(exchange.getStatusCode().is2xxSuccessful()){
            UserInfo body = exchange.getBody();
            HashMap<String, Object> map = new HashMap<>(3);
            map.put("currentLoginUsername", currentUser.getUsername());
            map.put("user", body);
            return new ModelAndView("user-info", map);
        }
        throw new RuntimeException("操作超时，请稍候重试！");
    }

}

package com.y3tu.cloud.auth.controller;


import com.y3tu.cloud.common.constant.SecurityConstants;
import com.y3tu.tool.web.base.pojo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author liuht
 * @date 2018年03月10日
 */
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    /**
     * 认证页面
     *
     * @return ModelAndView
     */
    @GetMapping("/require")
    public ModelAndView require() {
        return new ModelAndView("ftl/login");
    }

    @PostMapping("/loginSuccess")
    public R loginSuccess(Authentication authentication) {
        R response = R.ok(authentication);
        response.setMessage("登录成功");
        return response;
    }

    /**
     * 用户信息校验
     *
     * @param authentication 信息
     * @return 用户信息
     */
    @RequestMapping("/user")
    public Object user(Authentication authentication) {
        return authentication.getPrincipal();
    }

    /**
     * 清除Redis中 accesstoken refreshtoken
     *
     * @param accesstoken accesstoken
     * @return true/false
     */
    @PostMapping("/removeToken")
    @CacheEvict(value = SecurityConstants.TOKEN_USER_DETAIL, key = "#accesstoken")
    public R removeToken(String accesstoken) {
        return R.ok(consumerTokenServices.revokeToken(accesstoken));
    }
}

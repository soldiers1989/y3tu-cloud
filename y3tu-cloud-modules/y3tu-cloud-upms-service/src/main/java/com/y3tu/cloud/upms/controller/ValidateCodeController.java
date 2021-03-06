package com.y3tu.cloud.upms.controller;

import com.google.code.kaptcha.Producer;
import com.y3tu.cloud.common.constant.SecurityConstants;
import com.y3tu.cloud.upms.service.SysUserService;
import com.y3tu.tool.core.io.IOUtil;
import com.y3tu.tool.core.lang.Assert;
import com.y3tu.tool.web.base.pojo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * @author liuht
 * @date 2017/12/18
 * 验证码提供
 */
@Controller
public class ValidateCodeController {

    @Autowired(required = false)
    private Producer producer;

    @Autowired
    private SysUserService userService;

    /**
     * 创建验证码
     *
     * @throws Exception
     */
    @GetMapping(SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX + "/{randomStr}")
    public void createCode(@PathVariable String randomStr, HttpServletResponse response)
            throws Exception {
        Assert.notEmpty(randomStr, "机器码不能为空");
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        userService.saveImageCode(randomStr, text);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "JPEG", out);
        IOUtil.close(out);
    }

    /**
     * 发送手机验证码
     * 后期要加接口限制
     *
     * @param mobile 手机号
     * @return R
     */
    @ResponseBody
    @GetMapping(SecurityConstants.MOBILE_VALIDATE_CODE_URL_PREFIX + "/{mobile}")
    public R createCode(@PathVariable String mobile) {
        Assert.notEmpty(mobile, "手机号不能为空");
        return userService.sendSmsCode(mobile);
    }
}

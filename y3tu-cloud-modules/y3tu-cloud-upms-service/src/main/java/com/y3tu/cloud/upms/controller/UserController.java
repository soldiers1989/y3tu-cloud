package com.y3tu.cloud.upms.controller;

import com.y3tu.cloud.common.constant.CommonConstant;
import com.y3tu.cloud.common.vo.UserVO;
import com.y3tu.tool.web.base.controller.BaseController;

import com.y3tu.cloud.upms.model.dto.UserInfo;
import com.y3tu.cloud.upms.model.dto.UserDTO;
import com.y3tu.cloud.upms.model.entity.SysUser;
import com.y3tu.cloud.upms.model.entity.SysUserRole;
import com.y3tu.cloud.upms.service.SysUserService;
import com.baomidou.mybatisplus.plugins.Page;
import com.y3tu.tool.web.base.pojo.Query;
import com.y3tu.tool.web.base.pojo.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author liuht
 * @date 2017/10/28
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
    @Autowired
    private SysUserService userService;

    /**
     * 获取当前用户信息（角色、权限）
     * 并且异步初始化用户部门信息
     *
     * @param userVo 当前用户信息
     * @return 用户名
     */
    @GetMapping("/info")
    public R user(UserVO userVo) {
        UserInfo userInfo = userService.findUserInfo(userVo);
        return R.ok(userInfo);
    }

    /**
     * 通过ID查询当前用户信息
     *
     * @param id ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public UserVO user(@PathVariable Integer id) {
        return userService.selectUserVoById(id);
    }

    /**
     * 删除用户信息
     *
     * @param id ID
     * @return R
     */
    @DeleteMapping("/{id}")
    public R userDel(@PathVariable Integer id) {
        SysUser sysUser = userService.selectById(id);
        if (CommonConstant.ADMIN_USER_NAME.equals(sysUser.getUsername())) {
            return R.error("不允许删除超级管理员");
        }
        return R.ok(userService.deleteUserById(sysUser));
    }

    /**
     * 添加用户
     *
     * @param userDto 用户信息
     * @return success/false
     */
    @PostMapping
    public R user(@RequestBody UserDTO userDto) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setDelFlag(CommonConstant.STATUS_NORMAL);
        sysUser.setPassword(ENCODER.encode(userDto.getNewpassword1()));
        userService.insert(sysUser);

        userDto.getRole().forEach(roleId -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(sysUser.getUserId());
            userRole.setRoleId(roleId);
            userRole.insert();
        });
        return R.ok(Boolean.TRUE);
    }

    /**
     * 更新用户信息
     *
     * @param userDto 用户信息
     * @return R
     */
    @PutMapping
    public R userUpdate(@RequestBody UserDTO userDto) {
        SysUser user = userService.selectById(userDto.getUserId());
        return R.ok(userService.updateUser(userDto, user.getUsername()));
    }

    /**
     * 通过用户名查询用户及其角色信息
     *
     * @param username 用户名
     * @return UseVo 对象
     */
    @GetMapping("/findUserByUsername/{username}")
    public UserVO findUserByUsername(@PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    /**
     * 通过手机号查询用户及其角色信息
     *
     * @param mobile 手机号
     * @return UseVo 对象
     */
    @GetMapping("/findUserByMobile/{mobile}")
    public UserVO findUserByMobile(@PathVariable String mobile) {
        return userService.findUserByMobile(mobile);
    }

    /**
     * 通过OpenId查询
     *
     * @param openId openid
     * @return 对象
     */
    @GetMapping("/findUserByOpenId/{openId}")
    public UserVO findUserByOpenId(@PathVariable String openId) {
        return userService.findUserByOpenId(openId);
    }

    /**
     * 分页查询用户
     *
     * @param params 参数集
     * @param userVO 用户信息
     * @return 用户集合
     */
    @GetMapping("/userPage")
    public Page userPage(@RequestParam Map<String, Object> params, UserVO userVO) {
        return userService.selectWithRolePage(new Query(params), userVO);
    }

    /**
     * 修改个人信息
     *
     * @param userDto userDto
     * @param userVo  登录用户信息
     * @return success/false
     */
    @PutMapping("/editInfo")
    public R editInfo(@RequestBody UserDTO userDto, UserVO userVo) {
        return userService.updateUserInfo(userDto, userVo.getUsername());
    }
}

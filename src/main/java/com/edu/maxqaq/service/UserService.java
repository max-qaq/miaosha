package com.edu.maxqaq.service;

import com.edu.maxqaq.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.maxqaq.vo.LoginVo;
import com.edu.maxqaq.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author maxqaq
 * @since 2022-03-18
 */
public interface UserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    //根据cookie获取用户
    User getUserByCookie(String userTicket,HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse);

    RespBean updatePassword(String userTicket, String password,HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse);

}

package com.edu.maxqaq.service.Impl;

import com.edu.maxqaq.Exception.GlobalException;
import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.mapper.UserMapper;
import com.edu.maxqaq.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.maxqaq.utils.CookieUtil;
import com.edu.maxqaq.utils.MD5Util;
import com.edu.maxqaq.utils.UUIDUtil;
import com.edu.maxqaq.vo.LoginVo;
import com.edu.maxqaq.vo.RespBean;
import com.edu.maxqaq.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author maxqaq
 * @since 2022-03-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;


    /**
     * 登录
     * @param loginVo
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        参数校验,用注解替换
//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if (!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        User user = userMapper.selectById(mobile);
        if(null == user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if(!MD5Util.FormPasstoDBPass(password,user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
        httpServletRequest.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(httpServletRequest,httpServletResponse,"userTicket",ticket);
        return RespBean.success();
    }
}

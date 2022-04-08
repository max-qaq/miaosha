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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    RedisTemplate redisTemplate;


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
        //用户信息存入redis
        redisTemplate.opsForValue().set("user:" + ticket,user);
        //httpServletRequest.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(httpServletRequest,httpServletResponse,"userTicket",ticket);
        return RespBean.success();
    }

    @Override
    public User getUserByCookie(String userTicket,HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        if(StringUtils.isEmpty(userTicket)) return null;
        //获取user
        User user =  (User)redisTemplate.opsForValue().get("user:"+userTicket);
        if (user != null){
            //以防万一再存一次
            CookieUtil.setCookie(httpServletRequest,httpServletResponse,"userTicket",userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password,HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        User user = getUserByCookie(userTicket, httpServletRequest, httpServletResponse);
        if (null == user){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBpass(password,user.getSalt()));
        int result = userMapper.updateById(user);
        if (result == 1){
            //操作成功,删除缓存
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}

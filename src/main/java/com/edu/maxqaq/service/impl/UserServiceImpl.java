package com.edu.maxqaq.service.Impl;

import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.mapper.UserMapper;
import com.edu.maxqaq.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.maxqaq.utils.MD5Util;
import com.edu.maxqaq.utils.ValidatorUtil;
import com.edu.maxqaq.vo.LoginVo;
import com.edu.maxqaq.vo.RespBean;
import com.edu.maxqaq.vo.RespBeanEnum;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if (!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        User user = userMapper.selectById(mobile);
        if(null == user){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!MD5Util.FormPasstoDBPass(password,user.getSalt()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        return RespBean.success();
    }
}

package com.edu.maxqaq.service;

import com.edu.maxqaq.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.maxqaq.vo.LoginVo;
import com.edu.maxqaq.vo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author maxqaq
 * @since 2022-03-18
 */
public interface UserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo);
}

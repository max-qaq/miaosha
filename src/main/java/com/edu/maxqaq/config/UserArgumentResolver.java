package com.edu.maxqaq.config;

import com.edu.maxqaq.entity.User;
import com.edu.maxqaq.service.UserService;
import com.edu.maxqaq.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: miaosha
 * @description: 用户自定义参数
 * @author: max-qaq
 * @create: 2022-04-05 16:17
 **/
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //条件判断,符合条件为true才能执行下面的
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //获取request和response
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(ticket)){
            return null;
        }
        return userService.getUserByCookie(ticket,request,response);
    }
}

# 要解决的问题



**秒杀主要的问题就是要解决并发读和并发写**



-  高性能
  支持高并发的方案:**动静分离方案,热点的发现与隔离,请求削峰和分层过滤** 

-  一致性
  高并发场景要保证数据一致性,在大并发的情况下保证数据的一致性 

-  高可用
  要设计一个PLANB来兜底,防止最坏的情况 



# 实现登录功能



## 密码MD5加密



两次MD5加密,用户端加密一次传给服务端 服务端再加密传给数据库



- 客户端 明文+固定salt

- 服务端 md5 + 随机salt



# 自定义参数校验规则



1. **导入依赖**



```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
            <version>2.6.4</version>
</dependency>
```



1. 在参数上加上注解`@Valid`

1. 在对应参数的类里面添加验证 如`@NotNull @Len(min = 3)`



## 自定义注解



1. 创建自定义注解类



```java
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        //校验规则
        validatedBy = { IsMobieValidator.class }
)
public @interface IsMobile {

    boolean required() default true;//必填

    String message() default "手机号码格式错误";//报错消息

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```



1. 创建校验规则类



```java
public class IsMobieValidator implements ConstraintValidator<IsMobile , String> {
//ConstraintValidator的IsMobile是要验证定义规则的类
    private boolean required  = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        //初始化
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            //ValidatorUtil是定义了验证的工具类,就是检测字符串是否符合规则
            return ValidatorUtil.isMobile(s);
        }else{
            if(StringUtils.isEmpty(s)){
                return true;
            }else{
                ValidatorUtil.isMobile(s);
            }
        }
        return false;
    }
}
```



```java
public class ValidatorUtil {
    //手机号
    private static final Pattern mobile_pattern = Pattern.compile("^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");

    public static boolean isMobile(String mobile){
        //校验手机号
        if(StringUtils.isEmpty(mobile)) return false;
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}
```



# 全局异常处理



1. 先定义通用返回类



```java
/**
 * @program: miaosha
 * @description: 公共返回对象
 * @author: max-qaq
 * @create: 2022-03-20 21:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object obj;

    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }

    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), obj);
    }

    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.ERROR.getCode(), respBeanEnum.ERROR.getMessage(), null);
    }

    public static RespBean error(RespBeanEnum respBeanEnum , Object obj){
        return new RespBean(respBeanEnum.ERROR.getCode(), respBeanEnum.ERROR.getMessage(), obj);
    }
}
```



1. 定义通用返回枚举类



```java
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200,"成功"),
    ERROR(500,"服务端异常"),
    //登录模块
    LOGIN_ERROR(500210,"用户名或密码错误"),
    MOBILE_ERROR(500220,"手机号格式错误"),
    BIND_ERROR(500230,"参数校验异常")
    ;
    private final Integer code;
    private final String message;
}
```



1. 定义全局异常



```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;//构造传入一个异常的枚举
}
```



1. 定义全局异常处理类



```java
/**
 * @program: miaosha
 * @description:全局异常处理类
 * @author: max-qaq
 * @create: 2022-04-01 13:02
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        if (e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        }else if (e instanceof BindException){
            BindException ex = (BindException) e;
            RespBean error = RespBean.error(RespBeanEnum.BIND_ERROR);
            error.setMessage("参数绑定异常: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return error;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
```



1. 使用的时候直接throw globalException



```java
 if(null == user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
 }
```



# 存储用户登录状态 分布式Session



## 单机Session



### Cookie工具类



```java
/**
 * @program: miaosha
 * @description:CookieUtil
 * @author: max-qaq
 * @create: 2022-04-02 21:20
 **/

public class CookieUtil {
    /**
     * 得到Cookie的值, 不编码
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    /**
     * 得到Cookie的值,
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (cookieList[i].getName().equals(cookieName)) {
                    if (isDecoder) {
                        retValue = URLDecoder.decode(cookieList[i].getValue(), "UTF-8");
                    } else {
                        retValue = cookieList[i].getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * 得到Cookie的值,
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (cookieList[i].getName().equals(cookieName)) {
                    retValue = URLDecoder.decode(cookieList[i].getValue(), encodeString);
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * 设置Cookie的值 不设置生效时间默认浏览器关闭即失效,也不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }

    /**
     * 设置Cookie的值 在指定时间内生效,但不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxage) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxage, false);
    }

    /**
     * 设置Cookie的值 不设置生效时间,但编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, boolean isEncode) {
        setCookie(request, response, cookieName, cookieValue, -1, isEncode);
    }

    /**
     * 设置Cookie的值 在指定时间内生效, 编码参数
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxage, boolean isEncode) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, isEncode);
    }

    /**
     * 设置Cookie的值 在指定时间内生效, 编码参数(指定编码)
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxage, String encodeString) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, encodeString);
    }

    /**
     * 删除Cookie带cookie域名
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName) {
        doSetCookie(request, response, cookieName, "", -1, false);
    }

    /**
     * 设置Cookie的值，并使其在指定时间内生效
     *
     * @param cookieMaxage cookie生效的最大秒数
     */
    private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                          String cookieName, String cookieValue, int cookieMaxage, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0)
                cookie.setMaxAge(cookieMaxage);
            if (null != request) {// 设置域名的cookie
                String domainName = getDomainName(request);
                System.out.println(domainName);
                if (!"localhost".equals(domainName)) {
                    cookie.setDomain(domainName);
                }
            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Cookie的值，并使其在指定时间内生效
     *
     * @param cookieMaxage cookie生效的最大秒数
     */
    private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                          String cookieName, String cookieValue, int cookieMaxage, String encodeString) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else {
                cookieValue = URLEncoder.encode(cookieValue, encodeString);
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0) {
                cookie.setMaxAge(cookieMaxage);
            }
            if (null != request) {// 设置域名的cookie
                String domainName = getDomainName(request);
                System.out.println(domainName);
                if (!"localhost".equals(domainName)) {
                    cookie.setDomain(domainName);
                }
            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到cookie的域名
     */
    private static final String getDomainName(HttpServletRequest request) {
        String domainName = null;
        // 通过request对象获取访问的url地址
        String serverName = request.getRequestURL().toString();
        if (serverName == null || serverName.equals("")) {
            domainName = "";
        } else {
            // 将url地下转换为小写
            serverName = serverName.toLowerCase();
            // 如果url地址是以http://开头  将http://截取
            if (serverName.startsWith("http://")) {
                serverName = serverName.substring(7);
            }
            int end = serverName.length();
            // 判断url地址是否包含"/"
            if (serverName.contains("/")) {
                //得到第一个"/"出现的位置
                end = serverName.indexOf("/");
            }

            // 截取
            serverName = serverName.substring(0, end);
            // 根据"."进行分割
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len >= 4) {
                domainName = domains[len - 4] + "." + domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            }else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }

        if (domainName != null && domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        return domainName;
    }
}
```



### UUID工具类



```java
/**
 * @program: miaosha
 * @description:UUID工具类
 * @author: max-qaq
 * @create: 2022-04-02 21:17
 **/

public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
```



## 分布式Session



### SpringSession

1. 添加依赖

**使用的时候记得用新的哦~**

```xml
<!--        redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <version>2.6.4</version>
        </dependency>
<!--        对象池-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
            <version>2.11.1</version>
        </dependency>
<!--        springSession-->
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
            <version>2.6.2</version>
        </dependency>
```

1. 配置redis

```yaml
redis:
#    服务器地址
host: yourdomain.com
#    redis端口号
port: 6379
#    默认数据库
database: 0
#    超时时间
timeout: 10000ms
#    对象连接池
lettuce:
pool:
#        最大连接数
max-active: 8
#        最大等待时间默认-1不过期
max-wait: 10000ms
#        最大空闲连接
max-idle: 200
#        最小空闲连接
min-idle: 5

#密码
password: yourpassword
```

1. 成了!

![img](https://cdn.mazhiyong.icu/1649123104787-e9c06531-e794-4889-8af1-1c5543431860.png)

**spring 很神奇吧**

可以看见SpringSession是二进制存储的,所以直接用redis存session就得了,然后自己自定义序列化

### Redis直接实现分布式Session(分布式缓存)

1. 先删除SpringSession的依赖
2. 编写`redisconfig`,实现用`redisTemplate`操作序列化

```java
/**
 * @program: miaosha
 * @description: Redis配置类
 * @author: max-qaq
 * @create: 2022-04-05 11:44
 **/
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());//设置key序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());//设置value的序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());//hash key序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());//hash的value序列化

        //注入连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;

    }
}
```



1. 之后生成cookie不存到session存到redis里面,取也是从这里取值

```java
 //生成cookie
        String ticket = UUIDUtil.uuid();
        //用户信息存入redis
        redisTemplate.opsForValue().set("user:" + ticket,user,30, TimeUnit.MINUTES);
        //httpServletRequest.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(httpServletRequest,httpServletResponse,"userTicket",ticket);
        return RespBean.success();
```

这里redis添加cookie可以设置统一的过期时间,因为这个请求量不算太大,还有一个问题就是每次请求都新生成一个UUID,导致同样的对象重复存储了很多遍

没啥大问题,如果有cookie前端可以限制跳转,登出的时候根据ticket删除redis就可以

# 配置拦截器拦截请求

场景: `giftcontroller`里面每个方法都要先判断一下user是否为空,然后获取cookie,从cookie获取user对象,很麻烦

通过MVC拦截器使请求在到达controller之前就处理掉,直接向方法内传入user 省去一些步骤

一开始是以为用AOP,然后仔细思考了一下,AOP是没办法从cookie获取user传给controller的

AOP应用场景多是通知类的,**日志,鉴权,错误**或者监控什么的

不得不说翻译成前置通知和后置通知真的很精准

**ArgumentResovler是springmvc为我们提供的一个处理controller请求参数的扩展点。**

**我们通过ArgumentResovler来添加对controller入参的处理**

**可以理解为满足上面的条件,根据json生成user对象之后再执行下面的方法,然后再传给controller**

**WebConfig**

```java
/**
 * @program: miaosha
 * @description: MVC配置类
 * @author: max-qaq
 * @create: 2022-04-05 15:48
 **/
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //自定义参数解析器
        resolvers.add(userArgumentResolver);
    }
}
```

自定义UserArgumentResolver

```java
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
```

# 拦截器处理静态资源

除了swagger希望我这辈子不要用到

```java
@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //解决静态无法访问
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        //解决swagger无法访问
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
```

# 秒杀功能的实现

1. 判断用户是否重复抢
2. 库存是否够

## 极其简陋的第一版

逻辑就是点击秒杀按钮然后去查询商品,把库存字段减一 然后再传回去

我的评价是和我一样的水平,没啥好写的



劲爆1核2G服务器  吞吐量高达5

windows70左右 还是在windows上看吧

数据库IO都是在服务器上的 所以瓶颈还是服务器上 要好好优化

![img](https://cdn.mazhiyong.icu/1649416600222-70481cc9-5cba-48f7-8a9e-f2817d4366a7.png)

QPS瓶颈是数据库的读取,所以可以把变更比较少的数据作为缓存使用

## 库存超卖的问题

并发太大的时候有时候就会发现库存变为负数

1. 创建订单的时候防止重复

在数据库user_id和goods_id加上唯一索引

1. 插入的时候判断如果存量大于0再插入
2. 订单获取从redis里面

😃感觉好玄幻,虽然成功了但是应该有不少隐患,希望有朝一日能看见淘宝的解决方案

# 服务优化

## 页面优化

### 页面缓存

把后端要渲染的页面提前放到redis里面

这一步..可以跳过 现在都前后端分离了😀

### URL缓存

实质和页面缓存一样,把要返回的页面放到redis里面

不过是通过url获取页面

### 对象缓存

其实分布式缓存就已经是对象缓存了,获取对象直接从Redis里面

但是要注意更新的时候的双写一致性

一般两种处理方法,旁路缓存和延迟双删

**旁路缓存:**

- 读请求：如果未命中缓存则查询数据库并更新至缓存，否则返回缓存中数据
- 写请求：先更新数据库，再删除缓存（非延迟双删）

**延迟双删:**

1. 先删除缓存
2.  更新数据库
3. 休眠一会儿（比如1s），之后再删一次缓存数据

延迟双删就比较麻烦了,另外再说吧

## 接口优化

1. redis先预减库存,减少数据库访问次数
2. order订单用RabbitMQ异步出去
3. 客户端轮询订单,没排到就是排队中

### 使用Redis先预减库存



1. Controller初始化的时候加载库存

```
public class SecKillController implements InitializingBean
```

重写`afterPropertiesSet()`

```java
 @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(goodsVo)) return;
        goodsVo.forEach(goodsVo1 -> {
            redisTemplate.opsForValue().set("secKillGoods:"+goodsVo1.getGoodsId(),goodsVo1.getStockCount());
        });
    }
```

1. 用redis的原子操作递减

```java
Long decrement = valueOperations.decrement("secKillGoods:" + goodsId);
        if (decrement <= 0){
            model.addAttribute("errmsg",RespBeanEnum.EMPTY_STOCK.getMessage());
        }
```

1. 内存标记是否为空,如果位空就不查询redis

```java
private HashMap<Long,Boolean> EmptyStockMap = new HashMap<>();
/**
初始化的时候加载这个Map
**/
@Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(goodsVo)) return;
        goodsVo.forEach(goodsVo1 -> {
            EmptyStockMap.put(goodsVo1.getGoodsId(),true);//有库存
            redisTemplate.opsForValue().set("secKillGoods:"+goodsVo1.getGoodsId(),goodsVo1.getStockCount());
        });
    }
//操作redis之前判断false还是true
if (secKillOrder != null){
            //被抢购过了
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        if (!EmptyStockMap.get(goodsId)){
            return "orderDetail";
        }
        Long decrement = valueOperations.decrement("secKillGoods:" + goodsId);
```

### 使用RabbitMQ异步处理订单请求

1. MQ发送消息
2. MQ接收消息并处理
3. 客户端轮询订单处理结果

## 使用分布式锁优化redis预减库存

用lua脚本预减库存

```lua
if (redis.call("exists",KEYS[1]) == 1) then
    local stock = tonumber(redis.call("get",KEYS[1]));
    if(stock > 0) then
        redis.call("incrby",KEYS[1],-1);
        return stock;
    end;
    return 0;
end;
```

## 服务优化总结

1. 用RabbitMQ进行异步下单
2. 减少MYSQL的访问次数
3. 多使用Redis

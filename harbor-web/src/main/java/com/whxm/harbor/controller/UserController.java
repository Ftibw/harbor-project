package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.bean.User;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.lock.RedisDistributedLock;
import com.whxm.harbor.service.UserService;
import com.whxm.harbor.utils.*;
import com.whxm.harbor.wechat.WeChatConfig;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.whxm.harbor.utils.TokenUtils.chaos;
import static com.whxm.harbor.utils.TokenUtils.order;
import static com.whxm.harbor.utils.TokenUtils.salt;

@Api(description = "用户服务")
@RestController
@RequestMapping("/user")
@MyApiResponses
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户列表(需授权)")
    @GetMapping("/users")
    /*@ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "userLoginname", value = "登陆名", dataType = "String"),
                    @ApiImplicitParam(name = "userName", value = "用户名", dataType = "String"),
                    @ApiImplicitParam(name = "userAlias", value = "用户昵称", dataType = "String"),
                    @ApiImplicitParam(name = "userEmail", value = "用户邮箱", dataType = "String"),
                    @ApiImplicitParam(name = "userWechat", value = "用户微信", dataType = "String")
            }
    )*/
    public Result getUsers(PageQO pageQO, User condition) {

        PageVO<User> pageVO = userService.getUserList(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("获取用户(需授权)")
    @GetMapping("/user/{ID}")
    public Result getUser(
            @ApiParam(name = "ID", value = "用户的ID", required = true)
            @PathVariable("ID") String userId
    ) {

        Assert.notNull(userId, "用户ID不能为空");

        User user = userService.getUser(userId);

        if (null == user)
            throw new DataNotFoundException();

        return Result.success(user);
    }

    @ApiOperation("修改用户(需授权)")
    @PutMapping("/user")
    public Result updateUser(@RequestBody User user) {


        Assert.notNull(user, "用户数据不能为空");
        Assert.notNull(user.getUserId(), "用户ID不能为空");

        return userService.updateUser(user);
    }

    @ApiOperation("删除用户(需授权)")
    @DeleteMapping("/user")
    public Result delUser(
            @ApiParam(name = "ID", value = "用户的ID", required = true)
                    String id
    ) {
        Assert.notNull(id, "用户ID不能为空");

        return userService.deleteUser(id);
    }

    @ApiOperation("添加用户(需授权)")
    @PostMapping("/user")
    public Result addUser(@RequestBody User user) {

        Assert.notNull(user, "用户数据为空");
        Assert.isNull(user.getUserId(), "用户ID必须为空");
        Assert.notNull(user.getUserLoginname(), "用户登录名不能为空");
        Assert.notNull(user.getUserPassword(), "用户密码不能为空");

        //32位加密
        user.setUserPassword(MD5Utils.MD5(user.getUserPassword()));

        return userService.addUser(user);
    }


    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedisDistributedLock lock;

    @ApiOperation("登录接口,token有效时间为2小时")
    @PostMapping("/login")
    public Result userLogin(@Valid @RequestBody User user, @Value("${account.login-limit}") Integer limit) {

        User info = userService.login(user);

        if (null == info)
            return Result.failure(ResultEnum.USER_NOT_EXIST);

        String userId = info.getUserId();

        if (info.getUserPassword().equals(MD5Utils.MD5(user.getUserPassword()))) {

            String key = "USER_LIMIT_" + userId;

            String isOK = lock.StringLuaTemplate(""
                    + "local is_exist = redis.call('get', '" + key + "')"
                    + "local count = is_exist and tonumber(is_exist) or 0 "
                    + "if count < " + limit + " then return redis.call('set', '" + key + "',count + 1)"
                    + "else return 'NO' end"
            );

            if (!"OK".equals(isOK))
                return Result.failure(ResultEnum.USER_HAS_EXISTED, "超出同时登录上限");

            String salt = UUID.randomUUID().toString().replace("-", "");

            redisTemplate.boundValueOps(salt).set(userId, 2, TimeUnit.HOURS);
            //将userId和盐搅拌生成token
            return Result.success(chaos(userId, salt));
        }
        return Result.failure(ResultEnum.USER_PASSWORD_ERROR);

    }

    @ApiOperation("刷新token(需授权)")
    @GetMapping("/token")
    public Result token(
            @ApiParam(name = "token", value = "token值", required = true) String token) {

        if (token != null && 64 == token.length()) {

            String userId = order(token);

            String salt = salt(token);

            //从redis获取存储的userId
            if (userId.equals(redisTemplate.boundValueOps(salt).get())) {

                redisTemplate.delete(salt);

                String newSalt = UUID.randomUUID().toString().replace("-", "");

                redisTemplate.boundValueOps(newSalt + userId).set(userId, 2, TimeUnit.HOURS);

                return Result.success(chaos(userId, newSalt));
            }
        }
        return Result.failure(ResultEnum.USER_NOT_LOGGED_IN);
    }

    @ApiOperation("用户登出(需授权)")
    @GetMapping("/logout")
    public Result logout(@ApiParam(name = "token", value = "token值", required = true) String token) {

        if (token != null && 64 == token.length()) {

            String userId = order(token);

            String salt = salt(token);

            if (userId.equals(redisTemplate.boundValueOps(salt).get())) {

                redisTemplate.delete(salt);

                String key = "USER_LIMIT_" + userId;

                lock.StringLuaTemplate(""
                        + "local is_exist = redis.call('get', '" + key + "') "
                        + "local count = is_exist and tonumber(is_exist) or 0 "
                        + "if count > 1 then return redis.call('set', '" + key + "',count - 1) "
                        + "else return ''..redis.call('del','" + key + "') end "
                );

                return Result.success("登出成功");
            }
        }
        return Result.failure(ResultEnum.USER_NOT_LOGGED_IN);
    }


    @Autowired
    private WeChatConfig weChatConfig;

    @GetMapping("/wechat")
    public Object wechat() {

        WeChatConfig vo = new WeChatConfig();

        BeanUtils.copyProperties(weChatConfig,vo);

        return weChatConfig;
    }
}

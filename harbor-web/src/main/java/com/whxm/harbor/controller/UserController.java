package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.bean.User;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.service.UserService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.MD5Utils;
import com.whxm.harbor.utils.TokenUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.whxm.harbor.utils.TokenUtils.chaos;
import static com.whxm.harbor.utils.TokenUtils.order;

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

    @ApiOperation("登陆接口,token有效时间为2小时")
    @PostMapping("/login")
    public Result userLogin(@Valid @RequestBody User user) {

        Assert.notNull(user.getUserLoginname(), "用户登录名不能为空");
        Assert.notNull(user.getUserPassword(), "用户密码不能为空");

        User info = userService.login(user);

        if (null == info)
            return Result.failure(ResultEnum.USER_NOT_EXIST);

        String userId = info.getUserId();

        //设置String序列化器
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();

        redisTemplate.setKeySerializer(serializer);

        redisTemplate.setValueSerializer(serializer);

        //----------------已登录过的放行-------------------
        synchronized (UserController.class) {

            String oldSalt = (String) redisTemplate.boundValueOps(userId).get();

            if (null != oldSalt) {

                //发送推送消息给已登录用户是否确认允许登录

                return Result.success(chaos(userId, oldSalt));
            }
        }
        //----------------验证成功的放行-------------------
        if (info.getUserPassword().equals(MD5Utils.MD5(user.getUserPassword()))) {


            String salt = UUID.randomUUID().toString().replace("-", "");

            //以userId为key避免登陆状态冗余,以盐为value始终维持最新的登陆状态
            redisTemplate.boundValueOps(userId).set(salt, 2, TimeUnit.HOURS);

            //redisTemplate.boundHashOps(userId).put("salt",salt);
            //redisTemplate.boundHashOps(userId).put("user",JacksonUtils.toJSon(info));
            //redisTemplate.boundHashOps(userId).expire(2, TimeUnit.HOURS);

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
            //设置String序列化器
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();

            redisTemplate.setKeySerializer(serializer);

            redisTemplate.setValueSerializer(serializer);

            String userId = order(token);

            //从redis获取盐信息
            String salt = (String) redisTemplate.boundValueOps(userId).get();

            if (null != salt && salt.equals(TokenUtils.salt(token))) {

                String newSalt = UUID.randomUUID().toString().replace("-", "");

                redisTemplate.boundValueOps(userId).set(newSalt, 2, TimeUnit.HOURS);

                return Result.success(chaos(userId, newSalt));

            }
        }

        return Result.failure(ResultEnum.USER_NOT_LOGGED_IN);
    }

    @ApiOperation("用户登出(需授权)")
    @GetMapping("/logout")
    public Result logout(@ApiParam(name = "token", value = "token值", required = true) String token) {

        if (token != null && 64 == token.length()) {

            redisTemplate.delete(order(token));

            return Result.success("登出成功");
        }

        return Result.failure(ResultEnum.USER_NOT_LOGGED_IN);
    }
}

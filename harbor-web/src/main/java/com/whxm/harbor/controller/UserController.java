package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.bean.User;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.lock.RedisDistributedLock;
import com.whxm.harbor.service.UserService;
import com.whxm.harbor.utils.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    @ApiOperation("登陆接口,token有效时间为2小时")
    @PostMapping("/login")
    public Result userLogin(@Valid @RequestBody User user, HttpServletRequest request) {

        if (!lock.lock(IPv4Utils.getIpAddress(request) + MD5Utils.MD5(JacksonUtils.toJson(user)),
                String.valueOf(request.getRequestURL()),
                Constant.DEFAULT_SUBMIT_EXPIRE_TIME)) {

            return Result.failure(ResultEnum.INTERFACE_EXCEED_LOAD, "请勿重复登录");
        }

        User info = userService.login(user);

        if (null == info)
            return Result.failure(ResultEnum.USER_NOT_EXIST);

        String userId = info.getUserId();

        if (info.getUserPassword().equals(MD5Utils.MD5(user.getUserPassword()))) {

            String salt = UUID.randomUUID().toString().replace("-", "");

            BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(userId);

            hashOps.put(salt, System.currentTimeMillis());

            hashOps.put(userId, info);

            redisTemplate.boundSetOps(Constant.REDIS_USERS_KEY).add(userId);

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

            //从redis获取盐信息
            BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(userId);

            Long lastTimePoint = null;

            if (null != hashOps) {

                lastTimePoint = (Long) hashOps.get(salt);
            }

            if (null != lastTimePoint &&
                    System.currentTimeMillis() < Constant.LOGIN_EXPIRE + lastTimePoint) {

                hashOps.put(salt, System.currentTimeMillis());

                return Result.success(token);
            }
        }

        return Result.failure(ResultEnum.USER_NOT_LOGGED_IN);
    }

    @ApiOperation("用户登出(需授权)")
    @GetMapping("/logout")
    public Result logout(@ApiParam(name = "token", value = "token值", required = true) String token) {

        if (token != null && 64 == token.length()) {

            redisTemplate.boundHashOps(order(token)).delete(salt(token));

            return Result.success("登出成功");
        }

        return Result.failure(ResultEnum.USER_NOT_LOGGED_IN);
    }

    @Scheduled(initialDelay = Constant.TASK_INIT_DELAY, fixedRate = Constant.LOGIN_EXPIRE)
    public void loginExpire() {

        BoundSetOperations<Object, Object> setOps = redisTemplate.boundSetOps(Constant.REDIS_USERS_KEY);

        if (null == setOps) return;

        Set<Object> loginKeys = setOps.members();

        loginKeys.stream().map(
                key -> redisTemplate.boundHashOps(key)
        ).forEach(
                hashOps -> hashOps.entries().forEach(
                        (salt, lastTimePoint) -> {
                            if (System.currentTimeMillis() > Constant.LOGIN_EXPIRE + (Long) lastTimePoint) {
                                hashOps.delete(salt);
                            }
                        }
                )
        );
    }
}

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

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
    public Result userLogin(@Valid @RequestBody User user, HttpServletRequest request) {
        //现在得问题是同一个人只登录不登出就会冗余,只能用定时任务清理了
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

            BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(Constant.REDIS_USERS_KEY);

            Map map = new LinkedHashMap<>();

            map.put(salt, System.currentTimeMillis());

            map.put(Constant.REDIS_USER_INFO_KEY, info);

            Object obj = hashOps.get(userId);

            if (null != obj && obj instanceof Map) {

                map.putAll((Map) obj);
            }
            hashOps.put(userId, map);

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
            BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(Constant.REDIS_USERS_KEY);

            List<Map> _map = new ArrayList<>(1);

            Map info = _map.get(0);

            if (null != hashOps) {

                hashOps.entries().forEach((id, map) -> {
                    if (id.equals(userId) && map instanceof Map) {
                        _map.add((Map) map);
                    }
                });

                Object tmp = info.get(salt);

                if (null != tmp
                        && tmp instanceof Long
                        && System.currentTimeMillis() < Constant.LOGIN_EXPIRE + (Long) tmp) {

                    info.put(salt, System.currentTimeMillis());

                    hashOps.put(userId, info);

                    return Result.success(token);
                }
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

            BoundHashOperations<Object, Object, Object> hashOps = redisTemplate.boundHashOps(Constant.REDIS_USERS_KEY);

            Object tmp = null;

            if (null != hashOps
                    && null != (tmp = hashOps.get(userId))
                    && tmp instanceof Map) {

                Map map = (Map) tmp;

                map.remove(salt);

                hashOps.put(userId, map);

                return Result.success("登出成功");
            }
        }

        return Result.failure(ResultEnum.USER_NOT_LOGGED_IN);
    }
}

package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.bean.User;
import com.whxm.harbor.mapper.UserMapper;
import com.whxm.harbor.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public User login(User user) {

        User po = null;

        try {
            po = userMapper.selectUserLoginInfo(user);

        } catch (Exception e) {

            logger.error("用户信息 查询报错", e);
        }

        return po;
    }

    @Override
    public User getUser(String userId) {

        User user;

        try {
            user = userMapper.selectByPrimaryKey(userId);

        } catch (Exception e) {

            logger.error("ID为{}的用户数据 获取报错", userId, e);

            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public PageVO<User> getUserList(PageQO<User> pageQO) {

        PageVO<User> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(userMapper.getUserList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("用户列表 获取报错", e);

            throw new RuntimeException(e);
        }

        return pageVO;
    }

    @Override
    public Result deleteUser(String userId) {

        Result ret;

        try {
            int affectRow = userMapper.deleteByPrimaryKey(userId);

            logger.info(1 == affectRow ? "ID为的{}用户删除成功" : "ID为的{}用户删除失败", userId);

            ret = new Result("ID为的" + userId + "用户 删除了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("用户数据 删除报错", e);

            throw new RuntimeException(e);
        }


        return ret;
    }

    @Override
    public Result addUser(User user) {

        Result ret = null;

        Object exist = null;

        int affectRow = 0;

        try {
            user.setUserId(UUID.randomUUID().toString().replaceAll("-", ""));

            //仅为了避免重复索引抛异常,就多查一次,贼浪费
            synchronized (this) {

                exist = userMapper.selectUserLoginInfo(user);

                if (null == exist) affectRow = userMapper.insert(user);
            }

            if (exist != null)
                return Result.build(HttpStatus.NOT_ACCEPTABLE.value(), "账户名重复", user.getUserLoginname());

            logger.info(1 == affectRow ? "用户添加成功" : "用户添加失败");


            ret = new Result("用户数据 添加" + affectRow + "行");

        } catch (Exception e) {

            logger.error("用户数据 添加报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result updateUser(User user) {

        Result ret;

        try {
            int affectRow = userMapper.updateByPrimaryKeySelective(user);

            logger.info(1 == affectRow ? "ID为的{}用户修改成功" : "ID为的{}用户修改失败", user.getUserId());

            ret = new Result("用户数据 修改了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("用户数据 修改报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }

}

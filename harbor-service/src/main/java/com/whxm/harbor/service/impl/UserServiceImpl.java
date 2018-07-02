package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.bean.User;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.UserMapper;
import com.whxm.harbor.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Override
    public User login(User user) {

        return userMapper.selectUserLoginInfo(user);
    }

    @Override
    public User getUser(String userId) {

        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public PageVO<User> getUserList(PageQO pageQO, User condition) {

        PageVO<User> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<User> list = userMapper.getUserList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public Result deleteUser(String userId) {

        int affectRow = userMapper.deleteByPrimaryKey(userId);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的用户,无法删除", userId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public Result addUser(User user) {

        Result ret = null;

        Object exist = null;

        int affectRow = 0;

        user.setUserId(UUID.randomUUID().toString().replaceAll("-", ""));

        user.setIsDeleted(Constant.NO);
        //仅为了避免重复索引抛异常,就多查一次,贼浪费
        synchronized (this) {

            exist = userMapper.selectUserLoginInfo(user);

            if (null == exist) affectRow = userMapper.insert(user);
        }

        if (exist != null)
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的用户登录名%s重复", user.getUserId(), user.getUserLoginname()));

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "建筑无法添加")
                : Result.success(user);
    }

    @Override
    public Result updateUser(User user) {

        int affectRow = userMapper.updateByPrimaryKeySelective(user);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的用户数据,无法修改", user.getUserId()))
                : Result.success(user);
    }

}

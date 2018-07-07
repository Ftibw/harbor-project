package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.service.ActivityService;
import com.whxm.harbor.bean.BizActivity;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.conf.PathConfig;
import com.whxm.harbor.mapper.BizActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private PathConfig pathConfig;

    @Resource
    private BizActivityMapper bizActivityMapper;

    @Override
    public BizActivity getBizActivity(Integer bizActivityId) {

        BizActivity bizActivity;

        try {
            bizActivity = bizActivityMapper.selectByPrimaryKey(bizActivityId);

            if (null == bizActivity) {
                logger.info("ID为{}的活动不存在", bizActivityId);
            }
        } catch (Exception e) {

            logger.error("活动数据 获取报错", e);

            throw new RuntimeException(e);
        }

        return bizActivity;
    }

    @Override
    public PageVO<BizActivity> getBizActivityList(PageQO pageQO, BizActivity condition) {

        PageVO<BizActivity> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizActivity> list = bizActivityMapper.getBizActivityList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        list.forEach(item -> item.setActivityLogo(
                pathConfig.getResourceURLWithPost()
                        + item.getActivityLogo()
        ));

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public List<BizActivity> getBizActivityList() {

        List<BizActivity> list = bizActivityMapper.getBizActivityList(null);

        list.forEach(item -> item.setActivityLogo(
                pathConfig.getResourceURLWithPost()
                        + item.getActivityLogo()
        ));

        return list;
    }

    @Override
    public Result deleteBizActivity(Integer bizActivityId) {

        BizActivity activity = new BizActivity();

        activity.setActivityId(bizActivityId);

        activity.setIsDeleted(Constant.YES);

        int affectRow = bizActivityMapper.setIsDeleted(activity);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的活动数据正在被使用中,无法删除", bizActivityId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public Result updateBizActivity(BizActivity bizActivity) {

        bizActivity.setActivityLogo(bizActivity.getActivityLogo().replaceAll("^" + pathConfig.getResourceURLWithPost() + "(.*)$", "$1"));

        int affectRow = bizActivityMapper.updateByPrimaryKeySelective(bizActivity);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的活动,无法修改", bizActivity.getActivityId()))
                : Result.success(bizActivity);
    }

    @Override
    public Result addBizActivity(BizActivity bizActivity) {


        bizActivity.setIsDeleted(Constant.NO);

        int affectRow = bizActivityMapper.insert(bizActivity);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的活动,无法添加", bizActivity.getActivityId()))
                : Result.success(bizActivity);
    }
}

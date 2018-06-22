package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.ActivityService;
import com.whxm.harbor.bean.BizActivity;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.conf.UrlConfig;
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
    private UrlConfig urlConfig;

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

            throw new RuntimeException();
        }

        return bizActivity;
    }

    @Override
    public PageVO<BizActivity> getBizActivityList(PageQO<BizActivity> pageQO) {

        PageVO<BizActivity> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            List<BizActivity> list = bizActivityMapper.getBizActivityList(pageQO.getCondition());

            list.forEach(item -> item.setActivityLogo(
                    urlConfig.getUrlPrefix()
                            + item.getActivityLogo()
            ));

            pageVO.setList(list);

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {
            logger.error("活动列表 获取报错", e);

            throw new RuntimeException();
        }

        return pageVO;
    }

    @Override
    public List<BizActivity> getBizActivityList() {

        List<BizActivity> list;

        try {
            list = bizActivityMapper.getBizActivityList((BizActivity) Constant.DEFAULT_QUERY_CONDITION);

            list.forEach(item -> item.setActivityLogo(
                    urlConfig.getUrlPrefix()
                            + item.getActivityLogo()
            ));

        } catch (Exception e) {

            logger.error("活动数据列表 获取报错", e);

            throw new RuntimeException();
        }

        return list;
    }

    @Override
    public Result deleteBizActivity(Integer bizActivityId) {

        Result ret;

        try {
            BizActivity activity = new BizActivity();

            activity.setActivityId(bizActivityId);

            activity.setIsDeleted(Constant.RECORD_IS_DELETED);

            int affectRow = bizActivityMapper.updateByPrimaryKeySelective(activity);

            boolean isSuccess = 1 == affectRow;

            logger.info(
                    isSuccess ?
                            "ID为{}的活动 删除成功" :
                            "ID为{}的活动 删除失败",
                    bizActivityId
            );

            ret = new Result(isSuccess ?
                    "ID为" + bizActivityId + "的活动删除成功" :
                    "ID为" + bizActivityId + "的活动删除失败"
            );

        } catch (Exception e) {

            logger.error("活动数据 删除错误", e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result updateBizActivity(BizActivity bizActivity) {

        Result ret;

        try {

            bizActivity.setActivityLogo(bizActivity.getActivityLogo().replaceAll("^" + urlConfig.getUrlPrefix() + "(.*)$", "$1"));

            int affectRow = bizActivityMapper.updateByPrimaryKeySelective(bizActivity);

            logger.info(1 == affectRow ?
                            "ID为{}的活动 修改成功" :
                            "ID为{}的活动 修改失败",
                    bizActivity.getActivityId()
            );

            ret = new Result(bizActivity);

        } catch (Exception e) {

            logger.error("活动数据 修改报错", e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result addBizActivity(BizActivity bizActivity) {

        Result ret;

        try {
            bizActivity.setActivityId(Constant.INCREMENT_ID_DEFAULT_VALUE);

            bizActivity.setIsDeleted(Constant.RECORD_NOT_DELETED);

            int affectRow = bizActivityMapper.insert(bizActivity);

            logger.info(1 == affectRow ? "活动添加成功" : "活动添加失败");

            ret = new Result(bizActivity);

        } catch (Exception e) {

            logger.error("活动数据 添加报错", e);

            throw new RuntimeException();
        }

        return ret;
    }
}

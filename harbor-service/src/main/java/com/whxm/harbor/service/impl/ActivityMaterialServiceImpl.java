package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizActivityMaterial;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.BizActivityMaterialMapper;
import com.whxm.harbor.service.ActivityMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ActivityMaterialServiceImpl implements ActivityMaterialService {

    private final Logger logger = LoggerFactory.getLogger(ActivityMaterialServiceImpl.class);

    @Autowired
    private UrlConfig urlConfig;

    @Resource
    private BizActivityMaterialMapper bizActivityMaterialMapper;

    @Override
    public BizActivityMaterial getBizActivityMaterial(Integer bizActivityMaterialId) {

        BizActivityMaterial activityMaterial;

        activityMaterial = bizActivityMaterialMapper.selectMaterialWithActivityType(bizActivityMaterialId);

        if (null == activityMaterial)
            logger.info("ID为{}的活动素材不存在", bizActivityMaterialId);

        return activityMaterial;
    }

    @Override
    public PageVO<BizActivityMaterial> getBizActivityMaterialList(PageQO<BizActivityMaterial> pageQO) {

        PageVO<BizActivityMaterial> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizActivityMaterial> list = bizActivityMaterialMapper.getBizActivityMaterialList(pageQO.getCondition());

        list.forEach(item -> item.setActivityMaterialImgPath(
                urlConfig.getUrlPrefix()
                        + item.getActivityMaterialImgPath()
        ));

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public Result deleteBizActivityMaterial(Integer bizActivityMaterialId) {

        Result ret;


            int affectRow = bizActivityMaterialMapper.deleteByPrimaryKey(bizActivityMaterialId);

            logger.info(1 == affectRow ?
                            "ID为{}的活动素材 删除成功" :
                            "ID为{}的活动素材 删除失败",
                    bizActivityMaterialId
            );

        1 == affectRow? Result.success();

        return ret;
    }

    @Override
    public Result updateBizActivityMaterial(BizActivityMaterial bizActivityMaterial) {

        Result ret;

        try {
            bizActivityMaterial.setActivityMaterialImgPath(bizActivityMaterial.getActivityMaterialImgPath().replaceAll("^" + urlConfig.getUrlPrefix() + "(.*)$", "$1"));

            int affectRow = bizActivityMaterialMapper.updateByPrimaryKeySelective(bizActivityMaterial);

            if (this.logger.isDebugEnabled()) {
                logger.info(1 == affectRow ?
                                "ID为{}的活动素材 修改成功" :
                                "ID为{}的活动素材 修改失败",
                        bizActivityMaterial.getActivityMaterialId()
                );
            }
            ret = new Result(1 == affectRow ? bizActivityMaterial : "活动素材 修改失败");

        } catch (Exception e) {

            logger.error("活动素材数据 修改报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result addBizActivityMaterial(BizActivityMaterial bizActivityMaterial) {

        bizActivityMaterial.setActivityMaterialId(Constant.INCREMENT_ID_DEFAULT_VALUE);

        int affectRow = bizActivityMaterialMapper.insert(bizActivityMaterial);

        return 1 == affectRow ?
                Result.success(bizActivityMaterial) : Result.failure(ResultEnum.DATA_IS_WRONG, bizActivityMaterial);

    }

    @Override
    public Result addBizActivityMaterials(List<BizActivityMaterial> list) {

        Result ret;

        list.forEach(item -> item.setActivityMaterialId(Constant.INCREMENT_ID_DEFAULT_VALUE));

        int affectRow = bizActivityMaterialMapper.batchInsert(list);

        logger.info(0 == affectRow ?
                "活动素材 添加失败" :
                "活动素材 成功添加" + affectRow + "行"
        );

        ret = new Result("活动素材数据添加了" + affectRow + "行");

        return ret;
    }

    @Override
    public List<BizActivityMaterial> getMaterialListByActivityId(Integer activityId) {

        List<BizActivityMaterial> list;

        try {
            BizActivityMaterial activityMaterial = new BizActivityMaterial();

            activityMaterial.setActivityId(activityId);

            list = bizActivityMaterialMapper.getBizActivityMaterialList(activityMaterial);

            list.forEach(item -> item.setActivityMaterialImgPath(
                    urlConfig.getUrlPrefix()
                            + item.getActivityMaterialImgPath()
            ));

        } catch (Exception e) {

            logger.error("活动素材数据列表 获取报错", e);

            throw new RuntimeException(e);
        }

        return list;
    }
}

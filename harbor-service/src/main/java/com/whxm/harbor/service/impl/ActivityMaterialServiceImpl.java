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
import com.whxm.harbor.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ActivityMaterialServiceImpl implements ActivityMaterialService {

    @Autowired
    private UrlConfig urlConfig;

    @Resource
    private BizActivityMaterialMapper bizActivityMaterialMapper;

    @Override
    public BizActivityMaterial getBizActivityMaterial(Integer bizActivityMaterialId) {

        return bizActivityMaterialMapper.selectMaterialWithActivityType(bizActivityMaterialId);
    }

    @Override
    public PageVO<BizActivityMaterial> getBizActivityMaterialList(PageQO pageQO, BizActivityMaterial condition) {

        PageVO<BizActivityMaterial> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizActivityMaterial> list = bizActivityMaterialMapper.getBizActivityMaterialList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

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


        int affectRow = bizActivityMaterialMapper.deleteByPrimaryKey(bizActivityMaterialId);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的活动素材,无法删除", bizActivityMaterialId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public Result updateBizActivityMaterial(BizActivityMaterial bizActivityMaterial) {

        bizActivityMaterial.setActivityMaterialImgPath(bizActivityMaterial.getActivityMaterialImgPath().replaceAll("^" + urlConfig.getUrlPrefix() + "(.*)$", "$1"));

        int affectRow = bizActivityMaterialMapper.updateByPrimaryKeySelective(bizActivityMaterial);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的活动素材,无法修改", bizActivityMaterial.getActivityMaterialId()))
                : Result.success(bizActivityMaterial);
    }

    @Override
    public Result addBizActivityMaterial(BizActivityMaterial bizActivityMaterial) {

        int affectRow = bizActivityMaterialMapper.insert(bizActivityMaterial);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的活动素材,无法添加", bizActivityMaterial.getActivityMaterialId()))
                : Result.success(bizActivityMaterial);
    }

    @Override
    public Result addBizActivityMaterials(List<BizActivityMaterial> list) {

        int affectRow = bizActivityMaterialMapper.batchInsert(list);

        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "数据列表无法添加")
                : Result.success(list);
    }

    @Override
    public List<BizActivityMaterial> getMaterialListByActivityId(Integer activityId) {

        BizActivityMaterial activityMaterial = new BizActivityMaterial();

        activityMaterial.setActivityId(activityId);

        List<BizActivityMaterial> list = bizActivityMaterialMapper.getBizActivityMaterialList(activityMaterial);

        list.forEach(item -> item.setActivityMaterialImgPath(
                urlConfig.getUrlPrefix()
                        + item.getActivityMaterialImgPath()
        ));

        return list;
    }
}

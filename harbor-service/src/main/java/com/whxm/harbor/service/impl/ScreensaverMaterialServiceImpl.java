package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizScreensaverMaterial;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.mapper.BizScreensaverMaterialMapper;
import com.whxm.harbor.service.ScreensaverMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ScreensaverMaterialServiceImpl implements ScreensaverMaterialService {

    @Resource
    private BizScreensaverMaterialMapper bizScreensaverMaterialMapper;

    @Override
    public BizScreensaverMaterial getBizScreensaverMaterial(Integer bizScreensaverMaterialId) {

        return bizScreensaverMaterialMapper.selectByPrimaryKey(bizScreensaverMaterialId);
    }

    @Autowired
    private UrlConfig urlConfig;

    @Override
    public PageVO<BizScreensaverMaterial> getBizScreensaverMaterialList(PageQO pageQO, BizScreensaverMaterial condition) {

        PageVO<BizScreensaverMaterial> pageVO = new PageVO<>(pageQO);

        List<BizScreensaverMaterial> list;

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());


        if (Objects.nonNull(condition.getScreensaverId()))
            list = bizScreensaverMaterialMapper
                    .selectMaterialsByScreensaverId(condition.getScreensaverId());
        else
            list = bizScreensaverMaterialMapper.getBizScreensaverMaterialList(condition);

        if (null == list || list.isEmpty())
            throw new DataNotFoundException();

        list.forEach(item -> item.setScreensaverMaterialImgPath(
                urlConfig.getUrlPrefix()
                        + item.getScreensaverMaterialImgPath()
        ));

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public Result deleteBizScreensaverMaterial(Integer bizScreensaverMaterialId) {

        //删除屏保素材,先删屏保-屏保素材关系表,再删屏保素材表
        int affectRow = bizScreensaverMaterialMapper.delScreensaverMaterialRelation(bizScreensaverMaterialId);

        int affectRow1 = bizScreensaverMaterialMapper.deleteByPrimaryKey(bizScreensaverMaterialId);

        return 0 == affectRow + affectRow1 ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的屏保素材,无法删除", bizScreensaverMaterialId))
                : Result.success(ResultEnum.NO_CONTENT);
    }

    @Override
    public Result updateBizScreensaverMaterial(BizScreensaverMaterial bizScreensaverMaterial) {

        bizScreensaverMaterial.setScreensaverMaterialImgPath(bizScreensaverMaterial.getScreensaverMaterialImgPath().replaceAll("^" + urlConfig.getUrlPrefix() + "(.*)$", "$1"));

        int affectRow = bizScreensaverMaterialMapper.updateByPrimaryKeySelective(bizScreensaverMaterial);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s屏保素材,无法修改", bizScreensaverMaterial.getScreensaverMaterialId()))
                : Result.success(bizScreensaverMaterial);
    }

    @Override
    public Result addBizScreensaverMaterial(List<BizScreensaverMaterial> list) {

        int affectRow = bizScreensaverMaterialMapper.batchInsert(list);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "屏保素材列表,无法添加")
                : Result.success(list);
    }
}

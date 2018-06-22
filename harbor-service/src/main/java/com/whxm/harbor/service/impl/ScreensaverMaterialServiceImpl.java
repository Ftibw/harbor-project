package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizScreensaverMaterial;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.BizScreensaverMaterialMapper;
import com.whxm.harbor.service.ScreensaverMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ScreensaverMaterialServiceImpl implements ScreensaverMaterialService {

    private final Logger logger = LoggerFactory.getLogger(ScreensaverMaterialServiceImpl.class);

    @Resource
    private BizScreensaverMaterialMapper bizScreensaverMaterialMapper;

    @Override
    public BizScreensaverMaterial getBizScreensaverMaterial(Integer bizScreensaverMaterialId) {

        BizScreensaverMaterial screensaverMaterial;

        try {
            screensaverMaterial = bizScreensaverMaterialMapper.selectByPrimaryKey(bizScreensaverMaterialId);

            if (null == screensaverMaterial) {
                logger.info("ID为{}的屏保素材不存在", bizScreensaverMaterialId);
            }
        } catch (Exception e) {

            logger.error("屏保素材ID为{}的数据 获取报错", bizScreensaverMaterialId);

            throw new RuntimeException();
        }

        return screensaverMaterial;
    }

    @Autowired
    private UrlConfig urlConfig;

    @Override
    public PageVO<BizScreensaverMaterial> getBizScreensaverMaterialList(PageQO<BizScreensaverMaterial> pageQO) {

        PageVO<BizScreensaverMaterial> pageVO;

        List<BizScreensaverMaterial> list = null;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            BizScreensaverMaterial condition = pageQO.getCondition();

            if (Objects.nonNull(condition.getScreensaverId()))
                list = bizScreensaverMaterialMapper
                        .selectMaterialsByScreensaverId(condition.getScreensaverId());
            else
                list = bizScreensaverMaterialMapper.getBizScreensaverMaterialList(condition);

            list.forEach(item -> item.setScreensaverMaterialImgPath(
                    urlConfig.getUrlPrefix()
                            + item.getScreensaverMaterialImgPath()
            ));

            pageVO.setList(list);

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("屏保素材列表 获取报错", e);

            throw new RuntimeException();
        }

        return pageVO;
    }

    @Override
    public Result deleteBizScreensaverMaterial(Integer bizScreensaverMaterialId) {

        Result ret;

        try {

            bizScreensaverMaterialMapper.delScreensaverMaterialRelation(bizScreensaverMaterialId);

            int affectRow1 = bizScreensaverMaterialMapper.deleteByPrimaryKey(bizScreensaverMaterialId);

            logger.info(1 == affectRow1 ?
                    "ID为{}的屏保素材删除成功" : "ID为{}的屏保素材删除失败", bizScreensaverMaterialId);

            ret = new Result("ID为" + bizScreensaverMaterialId + "的屏保素材 删除了" + affectRow1 + "行");

        } catch (Exception e) {

            logger.error("屏保素材ID为{}的数据 删除错误", bizScreensaverMaterialId);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result updateBizScreensaverMaterial(BizScreensaverMaterial bizScreensaverMaterial) {

        Result ret;

        try {
            bizScreensaverMaterial.setScreensaverMaterialImgPath(bizScreensaverMaterial.getScreensaverMaterialImgPath().replaceAll("^" + urlConfig.getUrlPrefix() + "(.*)$", "$1"));

            int affectRow = bizScreensaverMaterialMapper.updateByPrimaryKeySelective(bizScreensaverMaterial);

            if (this.logger.isDebugEnabled()) {
                logger.debug(1 == affectRow ?
                                "ID为{}的屏保素材修改成功" : "ID为{}的屏保素材修改失败",
                        bizScreensaverMaterial.getScreensaverMaterialId()
                );
            }

            ret = new Result(1 == affectRow ? bizScreensaverMaterial : "屏保素材 修改了0行");

        } catch (Exception e) {

            logger.error("屏保素材数据 修改报错", e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result addBizScreensaverMaterial(List<BizScreensaverMaterial> list) {

        Result ret;

        try {
            list.forEach(item -> item.setScreensaverMaterialId(Constant.INCREMENT_ID_DEFAULT_VALUE));

            int affectRow = bizScreensaverMaterialMapper.batchInsert(list);

            if (this.logger.isDebugEnabled()) {

                logger.debug("屏保素材添加{}", 0 == affectRow ? "失败" : "成功添加" + affectRow + "行");
            }

            ret = new Result(0 != affectRow ? list : "屏保素材数据添加了0行");

        } catch (Exception e) {

            logger.error("屏保素材数据 添加报错", e);

            throw new RuntimeException();
        }

        return ret;
    }
}

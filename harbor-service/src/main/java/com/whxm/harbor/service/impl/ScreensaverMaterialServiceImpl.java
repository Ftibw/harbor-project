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

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ScreensaverMaterialServiceImpl implements ScreensaverMaterialService {

    private static final Logger logger = LoggerFactory.getLogger(ScreensaverMaterialServiceImpl.class);

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
        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            List<BizScreensaverMaterial> list = bizScreensaverMaterialMapper.getBizScreensaverMaterialList(pageQO.getCondition());

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

            int affectRow = bizScreensaverMaterialMapper.deleteByPrimaryKey(bizScreensaverMaterialId);

            logger.info(1 == affectRow ?
                    "ID为{}的屏保素材删除成功" : "ID为{}的屏保素材删除失败", bizScreensaverMaterialId);

            ret = new Result("ID为" + bizScreensaverMaterialId + "的屏保素材 删除了" + affectRow + "行");

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

            int affectRow = bizScreensaverMaterialMapper.updateByPrimaryKeySelective(bizScreensaverMaterial);

            logger.info(1 == affectRow ?
                            "ID为{}的屏保素材修改成功" : "ID为{}的屏保素材修改失败",
                    bizScreensaverMaterial.getScreensaverMaterialId()
            );

            ret = new Result("ID为" + bizScreensaverMaterial.getScreensaverMaterialId()
                    + "的屏保素材 修改了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("屏保素材数据 修改报错", e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result addBizScreensaverMaterial(BizScreensaverMaterial bizScreensaverMaterial) {

        Result ret;

        try {
            bizScreensaverMaterial.setScreensaverMaterialId(Constant.INCREMENT_ID_DEFAULT_VALUE);

            int affectRow = bizScreensaverMaterialMapper.insert(bizScreensaverMaterial);

            logger.info(1 == affectRow ? "屏保素材添加成功" : "屏保素材添加失败");

            ret = new Result("屏保素材数据添加了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("屏保素材数据 添加报错", e);

            throw new RuntimeException();
        }

        return ret;
    }
}

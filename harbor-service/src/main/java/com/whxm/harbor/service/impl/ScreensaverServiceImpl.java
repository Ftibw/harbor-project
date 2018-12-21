package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizScreensaver;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.BizScreensaverMapper;
import com.whxm.harbor.service.ScreensaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Transactional
public class ScreensaverServiceImpl implements ScreensaverService {

    private final Logger logger = LoggerFactory.getLogger(ScreensaverServiceImpl.class);

    @Resource
    private BizScreensaverMapper bizScreensaverMapper;

    @Override
    public BizScreensaver getBizScreensaver(Integer bizScreensaverId) {

        BizScreensaver screensaver;

        try {
            screensaver = bizScreensaverMapper.selectWithScreensaverMaterialAndPublishedTerminalAmount(bizScreensaverId);

            if (null == screensaver) logger.info("ID为{}的屏保不存在", bizScreensaverId);

        } catch (Exception e) {

            logger.error("屏保ID为{}的数据 获取报错", bizScreensaverId);

            throw new RuntimeException(e);
        }

        return screensaver;
    }

    @Override
    public PageVO<BizScreensaver> getBizScreensaverList(PageQO<BizScreensaver> pageQO) {

        PageVO<BizScreensaver> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(bizScreensaverMapper.getBizScreensaverList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {
            logger.error("屏保列表 获取报错", e);

            throw new RuntimeException(e);
        }

        return pageVO;
    }

    @Override
    public Result deleteBizScreensaver(Integer bizScreensaverId) {

        Result ret;

        try {
            //删除屏保,先删屏保-屏保素材关系表,再删屏保表
            int affectRow = bizScreensaverMapper.delScreensaverMaterialRelation(bizScreensaverId);


            int affectRow2 = bizScreensaverMapper.deleteByPrimaryKey(bizScreensaverId);

            //删除屏保-屏保发布终端关系表中可能存在的关联
            int affectRow3 = bizScreensaverMapper.delScreensaverPublishedTerminalRelation(bizScreensaverId);

            logger.info("ID为{}的屏保 删除{}行,屏保-屏保素材关系表删除{}行", bizScreensaverId, affectRow2, affectRow);

            ret = new Result(String.format("ID为%d的屏保 删除了%d行,屏保-屏保素材关系表删除了%d行,屏保-屏保发布终端关系表删除了%d行", bizScreensaverId, affectRow2, affectRow, affectRow3));

        } catch (Exception e) {

            logger.error("屏保ID为{}的数据 删除错误", bizScreensaverId);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result updateBizScreensaver(BizScreensaver bizScreensaver) {

        Result ret;

        try {
            int affectRow = bizScreensaverMapper.updateByPrimaryKeySelective(bizScreensaver);

            logger.info(1 == affectRow ? "ID为{}的屏保修改成功" : "ID为{}的屏保修改失败", bizScreensaver.getScreensaverId());

            ret = new Result(1 == affectRow ? bizScreensaver : "屏保数据修改了0行");

        } catch (Exception e) {

            logger.error("屏保数据 修改报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result addBizScreensaver(BizScreensaver bizScreensaver, Integer[] screensaverMaterialIds) {

        Result ret;

        try {
            bizScreensaver.setScreensaverId(Constant.INCREMENT_ID_DEFAULT_VALUE);

            bizScreensaver.setAddScreensaverTime(new Date());

            int affectRow = bizScreensaverMapper.insert(bizScreensaver);

            logger.info(1 == affectRow ?
                    "屏保数据添加成功,新增数据的主键为" + bizScreensaver.getScreensaverId() : "屏保数据添加失败");

            int affectRow2 = bizScreensaverMapper.insertScreensaverMaterials(
                    bizScreensaver.getScreensaverId(),
                    screensaverMaterialIds
            );

            logger.info(0 == affectRow2 ? "屏保-屏保素材中间表添加失败" : "屏保-屏保素材中间表添加了" + affectRow2 + "行");

            ret = new Result("屏保数据添加了" + affectRow + "行,该屏保添加了" + affectRow2 + "个屏保素材");

        } catch (Exception e) {

            logger.error("屏保数据 添加报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result publishScreensaver(Integer screensaverId, String[] terminalIds) {

        Result ret;

        try {
            //清除终端的屏保
            bizScreensaverMapper.batchDeleteScreensaverTerminalRelation(terminalIds);
            //发布屏保,就是向屏保-发布的终端关系表中插入数据
            int affectRow = bizScreensaverMapper.insertScreensaverPublishedTerminal(screensaverId, terminalIds, new Date());

            logger.info(0 == affectRow ?
                    "ID为{}的屏保发布失败" : "ID为{}的屏保成功发布终端" + affectRow + "个", screensaverId);

            ret = new Result("ID为" + screensaverId + "的屏保 发布终端" + affectRow + "个");

        } catch (Exception e) {

            logger.error("ID为{}的屏保 发布报错", screensaverId, e);

            throw new RuntimeException(e);
        }

        return ret;
    }
}

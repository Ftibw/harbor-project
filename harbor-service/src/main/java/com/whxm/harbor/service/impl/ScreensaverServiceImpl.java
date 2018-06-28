package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizScreensaver;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.mapper.BizScreensaverMapper;
import com.whxm.harbor.service.ScreensaverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ScreensaverServiceImpl implements ScreensaverService {

    @Resource
    private BizScreensaverMapper bizScreensaverMapper;

    @Override
    public BizScreensaver getBizScreensaver(Integer bizScreensaverId) {

        BizScreensaver screensaver = bizScreensaverMapper.selectWithScreensaverMaterialAndPublishedTerminalAmount(bizScreensaverId);

        if (null == screensaver)
            throw new DataNotFoundException();

        return screensaver;
    }

    @Override
    public PageVO<BizScreensaver> getBizScreensaverList(PageQO pageQO, BizScreensaver condition) {

        PageVO<BizScreensaver> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizScreensaver> list = bizScreensaverMapper.getBizScreensaverList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public Result deleteBizScreensaver(Integer bizScreensaverId) {

        //删除屏保,先删屏保-屏保素材关系表,再删屏保表
        int affectRow = bizScreensaverMapper.delScreensaverMaterialRelation(bizScreensaverId);

        int affectRow2 = bizScreensaverMapper.deleteByPrimaryKey(bizScreensaverId);

        //删除屏保-屏保发布终端关系表中可能存在的关联
        int affectRow3 = bizScreensaverMapper.delScreensaverPublishedTerminalRelation(bizScreensaverId);

        return 0 == affectRow + affectRow2 + affectRow3 ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的屏保,无法删除", bizScreensaverId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public Result updateBizScreensaver(BizScreensaver bizScreensaver) {

        int affectRow = bizScreensaverMapper.updateByPrimaryKeySelective(bizScreensaver);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的屏保,无法修改", bizScreensaver.getScreensaverId()))
                : Result.success(bizScreensaver);
    }

    @Override
    public Result addBizScreensaver(BizScreensaver bizScreensaver, Integer[] screensaverMaterialIds, String[] terminalIds) {

        bizScreensaver.setAddScreensaverTime(new Date());

        int affectRow = bizScreensaverMapper.insert(bizScreensaver);

        int affectRow2 = bizScreensaverMapper.insertScreensaverMaterials(
                bizScreensaver.getScreensaverId(),
                screensaverMaterialIds
        );
        //添加时,若选择了终端,则进行发布
        if (null != terminalIds && terminalIds.length > 0)
            publishScreensaver(bizScreensaver.getScreensaverId(), terminalIds);

        return 0 == affectRow + affectRow2 ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的屏保,无法添加", bizScreensaver.getScreensaverId()))
                : Result.success(bizScreensaver);
    }

    @Override
    public Result publishScreensaver(Integer screensaverId, String[] terminalIds) {

        //清除终端的屏保
        bizScreensaverMapper.batchDeleteScreensaverTerminalRelation(terminalIds);
        //发布屏保,就是向屏保-发布的终端关系表中插入数据
        int affectRow = bizScreensaverMapper.insertScreensaverPublishedTerminal(screensaverId, terminalIds, new Date());

        return 0 == affectRow ? Result.failure(ResultEnum.OPERATION_LOGIC_ERROR) : Result.success();
    }
}

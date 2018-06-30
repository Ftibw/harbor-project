package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizFormat;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.service.BusinessFormatService;
import com.whxm.harbor.mapper.BizFormatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class BusinessFormatServiceImpl implements BusinessFormatService {

    @Resource
    private BizFormatMapper bizFormatMapper;

    @Override
    public BizFormat getBizFormat(Integer bizFormatId) {

        return bizFormatMapper.selectByPrimaryKey(bizFormatId);

    }

    public PageVO<BizFormat> getBizFormatList(PageQO pageQO, BizFormat condition) {

        PageVO<BizFormat> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizFormat> list = bizFormatMapper.getBizFormatList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public List<BizFormat> getBizFormatList() {

        return bizFormatMapper.getBizFormatList(null);
    }

    @Override
    public Result deleteBizFormat(Integer bizFormatId) {


        BizFormat bizFormat = new BizFormat();

        bizFormat.setBizFormatId(bizFormatId);

        bizFormat.setIsDeleted(Constant.YES);

        bizFormat.setBizFormatNumber(null);

        int affectRow = bizFormatMapper.updateByPrimaryKeySelective(bizFormat);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的业态,无法删除", bizFormatId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public Result updateBizFormat(BizFormat bizFormat) {

        int affectRow = bizFormatMapper.updateByPrimaryKeySelective(bizFormat);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的业态,无法修改", bizFormat.getBizFormatId()))
                : Result.success(bizFormat);
    }

    @Override
    public Result addBizFormat(BizFormat bizFormat) {

        Object exist = null;

        int affectRow = 0;

        bizFormat.setIsDeleted(Constant.NO);

        //仅为了避免重复索引抛异常,就多查一次,贼浪费
        synchronized (this) {
            exist = bizFormatMapper.selectIdByNumber(bizFormat.getBizFormatNumber());
            if (Objects.isNull(exist)) {
                affectRow = bizFormatMapper.insert(bizFormat);
            }
        }
        if (Objects.nonNull(exist))
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的业态编号重复", bizFormat.getBizFormatId()));

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的业态,无法添加", bizFormat.getBizFormatId()))
                : Result.success(bizFormat);
    }
}

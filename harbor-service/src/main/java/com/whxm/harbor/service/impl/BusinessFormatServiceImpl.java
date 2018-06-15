package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.BizFormat;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.service.BusinessFormatService;
import com.whxm.harbor.mapper.BizFormatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class BusinessFormatServiceImpl implements BusinessFormatService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessFormatServiceImpl.class);

    @Resource
    private BizFormatMapper bizFormatMapper;

    @Override
    public BizFormat getBizFormat(Integer bizFormatId) {

        BizFormat bizFormat;

        try {
            bizFormat = bizFormatMapper.selectByPrimaryKey(bizFormatId);

            if (null == bizFormat) logger.info("ID为{}的业态不存在", bizFormatId);

        } catch (Exception e) {

            logger.error("ID为{}的业态数据 获取报错", bizFormatId);

            throw new RuntimeException();
        }

        return bizFormat;
    }

    public PageVO<BizFormat> getBizFormatList(PageQO<BizFormat> pageQO) {


        PageVO<BizFormat> pageVO;
        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(bizFormatMapper.getBizFormatList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("业态数据列表 获取报错", e);

            throw new RuntimeException();
        }

        return pageVO;
    }

    @Override
    public List<BizFormat> getBizFormatList() {

        List<BizFormat> list;

        try {
            list = bizFormatMapper.getBizFormatList((BizFormat) Constant.DEFAULT_QUERY_CONDITION);

        } catch (Exception e) {

            logger.error("业态数据列表 获取报错", e);

            throw new RuntimeException();
        }

        return list;
    }

    @Override
    public Result deleteBizFormat(Integer bizFormatId) {

        Result ret;

        try {
            BizFormat bizFormat = new BizFormat();

            bizFormat.setBizFormatId(bizFormatId);

            bizFormat.setIsDeleted(Constant.RECORD_IS_DELETED);

            boolean isSuccess = updateBizFormat(bizFormat)
                    .getData()
                    .toString()
                    .contains("1");

            logger.info(isSuccess ?
                    "ID为{}的业态数据 删除成功" : "ID为{}的业态数据 删除失败", bizFormatId);

            ret = new Result("ID为" + bizFormatId + "的业态数据删除成功");

        } catch (Exception e) {

            logger.error("ID为{}的业态 删除报错", bizFormatId);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result updateBizFormat(BizFormat bizFormat) {

        Result ret;

        if (null == bizFormat) {

            logger.info("业态为空");

            return new Result("业态为空");

        } else if (null == bizFormat.getBizFormatId()) {

            logger.info("业态ID为空");

            return new Result("业态ID为空");
        }

        try {
            int affectRow = bizFormatMapper.updateByPrimaryKeySelective(bizFormat);

            logger.info(1 == affectRow ?
                    "ID为{}的业态数据 修改成功" : "ID为{}的业态数据 修改失败", bizFormat.getBizFormatId());

            ret = new Result("业态数据 修改了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("ID为{}的业态 修改报错", bizFormat.getBizFormatId());

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result addBizFormat(BizFormat bizFormat) {

        Result ret;

        try {

            if (null != bizFormatMapper.selectIdByNumber(bizFormat.getBizFormatNumber())) {

                return new Result(HttpStatus.NOT_ACCEPTABLE.value(), "业态编号重复", Constant.NO_DATA);
            }

            bizFormat.setBizFormatId(Constant.INCREMENT_ID_DEFAULT_VALUE);

            bizFormat.setIsDeleted(Constant.RECORD_NOT_DELETED);

            int affectRow = bizFormatMapper.insert(bizFormat);

            logger.info(1 == affectRow ? "业态数据添成功" : "业态数据添失败");

            ret = new Result("业态数据添加了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("业态数据 添加报错", e);

            throw new RuntimeException();
        }

        return ret;
    }
}

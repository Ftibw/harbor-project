package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.TerminalVisitMapper;
import com.whxm.harbor.service.TerminalVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class TerminalVisitServiceImpl implements TerminalVisitService {

    private final  Logger logger = LoggerFactory.getLogger(TerminalVisitServiceImpl.class);

    @Resource
    private TerminalVisitMapper terminalVisitMapper;

    @Override
    public TerminalVisit getTerminalVisit(String terminalNumber) {

        TerminalVisit terminalVisit;

        try {
            terminalVisit = terminalVisitMapper.selectByPrimaryKey(terminalNumber);

            if (null == terminalVisit) {
                logger.info("编号为{}的终端访问数据不存在", terminalNumber);
            }
        } catch (Exception e) {

            logger.error("终端访问数据 获取报错", e);

            throw new RuntimeException();
        }

        return terminalVisit;
    }

    //如果是POST+JSON就会被防重复提交了...
    @Override
    public Result updateTerminalVisit(String terminalNumber) {

        Result ret;

        Assert.notNull(terminalNumber, "终端编号不能为空");

        try {

            int affectRow= terminalVisitMapper.updateAmountByID(terminalNumber);

            if (this.logger.isDebugEnabled()) {

                this.logger.debug(1 == affectRow ?
                        "编号为{}的终端访问数据更新成功" : "编号为{}的终端访问数据更新失败", terminalNumber);
            }

            ret = new Result("编号为" + terminalNumber + "终端访问数据 更新了" + affectRow);

        } catch (Exception e) {

            logger.error("编号为{}的终端访问更新 报错", terminalNumber, e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public PageVO<TerminalVisit> getTerminalVisitList(PageQO<BizTerminal> pageQO) {

        PageVO<TerminalVisit> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(terminalVisitMapper.getTerminalVisitList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("终端访问列表获取报错", e);

            throw new RuntimeException();
        }

        return pageVO;
    }

    @Override
    public List<TerminalVisit> getTerminalVisitList() {

        List<TerminalVisit> list;

        try {
            list = terminalVisitMapper.getTerminalVisitList((BizTerminal) Constant.DEFAULT_QUERY_CONDITION);

        } catch (Exception e) {

            logger.error("终端访问数据列表 获取报错", e);

            throw new RuntimeException();
        }

        return list;
    }

}

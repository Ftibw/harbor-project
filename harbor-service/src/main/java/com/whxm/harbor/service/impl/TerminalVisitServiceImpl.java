package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.mapper.TerminalVisitMapper;
import com.whxm.harbor.service.TerminalVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class TerminalVisitServiceImpl implements TerminalVisitService {

    private final Logger logger = LoggerFactory.getLogger(TerminalVisitServiceImpl.class);

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

            throw new RuntimeException(e);
        }

        return terminalVisit;
    }

    //如果是POST+JSON就会被防重复提交了...
    @Override
    public ResultMap<String, Object> updateTerminalVisit(String terminalNumber) {

        ResultMap<String, Object> ret;

        try {

            int affectRow = terminalVisitMapper.updateAmountByID(terminalNumber);

            if (this.logger.isDebugEnabled()) {

                this.logger.debug(1 == affectRow ?
                        "编号为{}的终端访问数据更新成功" : "编号为{}的终端访问数据更新失败", terminalNumber);
            }

            ret = new ResultMap<String, Object>(1).build("success", 1 == affectRow);

        } catch (Exception e) {

            logger.error("编号为{}的终端访问更新 报错", terminalNumber, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            ret = new ResultMap<String, Object>(1).build("success", false);
        }

        return ret;
    }

    @Override
    public PageVO<TerminalVisit> getTerminalVisitList(PageQO pageQO, BizTerminal condition) {

        PageVO<TerminalVisit> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<TerminalVisit> list = terminalVisitMapper.getTerminalVisitList(condition);

       /* if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public List<TerminalVisit> getTerminalVisitList() {

        return terminalVisitMapper.getTerminalVisitList(null);
    }

}

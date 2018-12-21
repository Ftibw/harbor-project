package com.whxm.harbor.service;

import com.whxm.harbor.bean.*;

import java.util.List;

/**
 * 终端访问服务
 */
public interface TerminalVisitService {
    
    /**
     * 根据终端编号获取终端访问数据
     *
     * @param terminalNumber 终端编号
     * @return 终端访问数据
     */
    TerminalVisit getTerminalVisit(String terminalNumber);

    /**
     * 获取终端访问列表
     *
     * @param pageQO
     * @param condition
     * @return list
     */
    PageVO<TerminalVisit> getTerminalVisitList(PageQO pageQO, BizTerminal condition);

    /**
     * 获取全部终端访问数据
     * @return 全部终端访问数据
     */
    List<TerminalVisit> getTerminalVisitList();
    
    /**
     * 更新终端访问数据
     *
     * @param terminalNumber 终端编号
     * @return ret
     */
    ResultMap<String, Object> updateTerminalVisit(String terminalNumber);


}

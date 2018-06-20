package com.whxm.harbor.service;

import com.whxm.harbor.bean.*;

import java.util.List;

/**
 * 终端访问服务
 */
public interface TerminalVisitService {
    
    /**
     * 根据终端ID获取终端访问数据
     *
     * @param terminalId 终端ID
     * @return 终端访问数据
     */
    TerminalVisit getTerminalVisit(String terminalId);

    /**
     * 获取终端访问列表
     *
     * @param pageQO
     * @return list
     */
    PageVO<TerminalVisit> getTerminalVisitList(PageQO<BizTerminal> pageQO);

    /**
     * 获取全部终端访问数据
     * @return 全部终端访问数据
     */
    List<TerminalVisit> getTerminalVisitList();
    
    /**
     * 更新终端访问数据
     *
     * @param terminalId 终端访问数据新值
     * @return ret
     */
    Result updateTerminalVisit(String terminalId);


}

package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizTerminal;
import com.whxm.harbor.bean.TerminalVisit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TerminalVisitMapper {

    int deleteByPrimaryKey(String terminalNumber);

    int insert(TerminalVisit record);

    int insertSelective(TerminalVisit record);

    TerminalVisit selectByPrimaryKey(String terminalNumber);

    int updateByPrimaryKeySelective(TerminalVisit record);

    int updateByPrimaryKey(TerminalVisit record);

    List<TerminalVisit> getTerminalVisitList(BizTerminal condition);

    int updateAmountByID(@Param("terminalNumber") String terminalNumber);
}
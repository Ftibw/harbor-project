package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.TerminalVisit;

public interface TerminalVisitMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TerminalVisit record);

    int insertSelective(TerminalVisit record);

    TerminalVisit selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TerminalVisit record);

    int updateByPrimaryKey(TerminalVisit record);
}
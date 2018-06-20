package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.VisitCountLog;

public interface VisitCountLogMapper {
    int deleteByPrimaryKey(String terminalId);

    int insert(VisitCountLog record);

    int insertSelective(VisitCountLog record);

    VisitCountLog selectByPrimaryKey(String terminalId);

    int updateByPrimaryKeySelective(VisitCountLog record);

    int updateByPrimaryKey(VisitCountLog record);
}
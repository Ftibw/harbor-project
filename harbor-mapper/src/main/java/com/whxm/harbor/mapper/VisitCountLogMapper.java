package com.whxm.harbor.mapper;

import org.apache.ibatis.annotations.Insert;

public interface VisitCountLogMapper {
    @Insert("")
    int logShopVisit();
    @Insert("")
    int logTerminalVisit();
}
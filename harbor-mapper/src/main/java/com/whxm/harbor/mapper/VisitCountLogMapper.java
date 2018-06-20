package com.whxm.harbor.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface VisitCountLogMapper {

    @Insert("insert into count_shop_log value(#{shopNumber},#{ip},#{signature},CURRENT_TIMESTAMP)")
    int logShopVisit(@Param("shopNumber") String shopNumber,@Param("ip")  String ip,@Param("signature")  String signature);

    @Insert("insert into count_terminal_log value(#{terminalNumber},#{ip},#{signature},CURRENT_TIMESTAMP)")
    int logTerminalVisit(@Param("terminalNumber")String terminalNumber,@Param("ip") String ip,@Param("signature") String signature);
}
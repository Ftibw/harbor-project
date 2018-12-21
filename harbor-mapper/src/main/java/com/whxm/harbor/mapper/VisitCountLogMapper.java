package com.whxm.harbor.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface VisitCountLogMapper {

    @Insert("insert into visit_count_log(num,terminal_ip,visit_message,log_time) " +
            "value(#{number},#{ip},#{message},CURRENT_TIMESTAMP)")
    int writeLog(@Param("number") String number, @Param("ip") String ip, @Param("message") String message);
}
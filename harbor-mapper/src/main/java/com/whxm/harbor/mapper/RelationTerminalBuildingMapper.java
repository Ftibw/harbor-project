package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.RelationTerminalBuilding;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface RelationTerminalBuildingMapper {

    int batchInsert(List<RelationTerminalBuilding> list);//批量插入

    @Delete("delete from rel_terminal_building where number =#{tn}")
    int deleteByTn(String tn);//根据终端编号批量删除

    int batchDeleteByBidList(List<String> list);//根据建筑ID批量删除
}
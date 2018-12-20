package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.RelationShopBuilding;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface RelationShopBuildingMapper {

    int batchInsert(List<RelationShopBuilding> list);//批量插入

    @Delete("delete from rel_shop_building where number =#{sn}")
    int deleteBySn(String sn);//根据商铺编号删除

    int batchDeleteByBidList(List<String> list);//根据建筑ID批量删除
}
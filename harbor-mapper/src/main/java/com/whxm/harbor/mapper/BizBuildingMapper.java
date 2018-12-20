
package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.model.BuildingVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BizBuildingMapper {

    int batchReplace(List<BizBuilding> list);

    int batchDelete(List<String> list);

    /**
     * 查询建筑信息,以及绑定了建筑ID终端、商铺信息--终端编号,已启用的商铺(ID,编号,名称)
     *
     * @param floorId  建筑所在楼层
     * @param typeList 建筑类型列表
     * @return list
     */
    List<BuildingVo> listBuildings(@Param("floorId") Integer floorId,
                                   @Param("typeList") List<Integer> typeList);

    @Update("update biz_terminal set bid=#{bid} where terminal_id=#{tid}")
    int setTerminalBuildingId(String tid, String bid);

    @Update("update biz_shop set bid=#{bid} where shop_id=#{sid}")
    int setShopBuildingId(String sid, String bid);
}
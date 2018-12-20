
package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizBuilding;
import com.whxm.harbor.model.BuildingVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BizBuildingMapper {

    int batchReplace(List<BizBuilding> list);

    int batchDelete(List<String> list);

    List<BuildingVo> listBuildings(@Param("floorId") Integer floorId,
                                   @Param("typeList") List<Integer> typeList);

    @ResultMap("BaseResultMap")
    @Select("select * from biz_building where number=#{number}")
    BizBuilding selectByNumber(@Param("number") String number);

    @Delete("delete from biz_building where number=#{number}")
    int deleteByNumber(String number);
}

package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizBuilding;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

public interface BizBuildingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BizBuilding record);

    int insertSelective(BizBuilding record);

    BizBuilding selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BizBuilding record);

    int updateByPrimaryKey(BizBuilding record);


    /*
    @Results(value = {
    @Result(property = "pageX"
    , column = "page_x",
    javaType = String.class, jdbcType = JdbcType.VARCHAR),
    @Result(property = "pageY"
    , column = "page_Y",
    javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @Select("select * from biz_building")
    * */
    List<BizBuilding> getBuildingList(@Param("floor") Integer floor);

    @ResultMap("BaseResultMap")
    @Select("select * from biz_building where number=#{number}")
    BizBuilding selectByNumber(@Param("number") String number);

    int batchInsert(List<BizBuilding> list);

    List<String> isExistsDuplicateNumber(List<BizBuilding> list);

    @Delete("delete from biz_building where number=#{number}")
    int deleteByNumber(@Param("number") String number);
}
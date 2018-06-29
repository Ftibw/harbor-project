package com.whxm.harbor.mapper;

import com.whxm.harbor.bean.BizScreensaverMaterial;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizScreensaverMaterialMapper {
    int deleteByPrimaryKey(Integer screensaverMaterialId);

    int insert(BizScreensaverMaterial record);

    int insertSelective(BizScreensaverMaterial record);

    BizScreensaverMaterial selectByPrimaryKey(Integer screensaverMaterialId);

    int updateByPrimaryKeySelective(BizScreensaverMaterial record);

    int updateByPrimaryKey(BizScreensaverMaterial record);

    List<BizScreensaverMaterial> getBizScreensaverMaterialList(BizScreensaverMaterial condition);

    List<BizScreensaverMaterial> selectMaterialsByScreensaverId(Object screensaverId);

    int batchInsert(List<BizScreensaverMaterial> list);

    @Delete("DELETE FROM screensaver_material_relation WHERE screensaver_material_id=#{bizScreensaverMaterialId}")
    int delScreensaverMaterialRelation(@Param("bizScreensaverMaterialId") Integer bizScreensaverMaterialId);

    List<BizScreensaverMaterial> selectFirstPageMaterials(BizScreensaverMaterial condition);

    @Delete("DELETE FROM terminal_first_page_relation WHERE screensaver_material_id=#{bizScreensaverMaterialId}")
    int delTerminalFirstPageMaterialRelation(@Param("bizScreensaverMaterialId") Integer bizScreensaverMaterialId);
}
package com.whxm.harbor.service;


import com.whxm.harbor.bean.BizScreensaverMaterial;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;

import java.util.List;

public interface ScreensaverMaterialService {
    /**
     * 根据屏保素材ID获取屏保素材数据
     *
     * @param screensaverMaterialId 屏保素材ID
     * @return 屏保素材数据
     */
    BizScreensaverMaterial getBizScreensaverMaterial(Integer screensaverMaterialId);

    /**
     * 获取屏保素材列表
     *
     * @param pageQO
     * @return list
     */
    PageVO<BizScreensaverMaterial> getBizScreensaverMaterialList(PageQO<BizScreensaverMaterial> pageQO);

    /**
     * 根据ID删除屏保素材
     *
     * @param screensaverMaterialId 屏保素材ID
     * @return ret
     */
    Result deleteBizScreensaverMaterial(Integer screensaverMaterialId);

    /**
     * 修改屏保素材数据
     *
     * @param screensaverMaterial 屏保素材数据新值
     * @return ret
     */
    Result updateBizScreensaverMaterial(BizScreensaverMaterial screensaverMaterial);

    /**
     * 新增屏保素材数据
     *
     * @param screensaverMaterial 新屏保素材数据
     * @return 添加操作结果
     */
    Result addBizScreensaverMaterial(List<BizScreensaverMaterial> screensaverMaterial);

    /**
     * 查询没有关联指定屏保的屏保素材
     * @param screensaverId 屏保ID
     * @return 素材列表
     */
    List<BizScreensaverMaterial> getMaterialsUnboundScreensaver(Integer screensaverId);
}

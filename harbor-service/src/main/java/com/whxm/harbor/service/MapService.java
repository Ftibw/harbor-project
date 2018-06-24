package com.whxm.harbor.service;

import com.whxm.harbor.bean.BizMap;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;

import java.util.List;

/**
 * 地图服务
 */
public interface MapService {
    /**
     * 根据地图ID获取地图数据
     *
     * @param bizMapId 地图ID
     * @return 地图数据
     */
    BizMap getBizMap(Integer bizMapId);

    /**
     * 获取地图列表
     *
     * @param pageQO
     * @return list
     */
    PageVO<BizMap> getBizMapList(PageQO<BizMap> pageQO);

    /**
     * 获取全部地图数据
     *
     * @return 全部地图数据
     */
    List<BizMap> getBizMapList();

    /**
     * 根据ID删除地图
     *
     * @param bizMapId 地图ID
     * @return ret
     */
    Result deleteBizMap(Integer bizMapId);

    /**
     * 修改地图数据
     *
     * @param bizMap 地图数据新值
     * @return ret
     */
    Result updateBizMap(BizMap bizMap);

    /**
     * 新增地图数据
     *
     * @param bizMap 新地图数据
     * @return 添加操作结果
     */
    Result addBizMap(BizMap bizMap);

    /**
     * 新增地图数据
     *
     * @param list 新地图数据列表
     * @return 添加操作结果
     */
    Result addBizMaps(List<BizMap> list);
}

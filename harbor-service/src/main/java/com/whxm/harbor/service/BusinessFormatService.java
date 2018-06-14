package com.whxm.harbor.service;


import com.whxm.harbor.bean.BizFormat;
import com.whxm.harbor.bean.PageQO;
import com.whxm.harbor.bean.PageVO;
import com.whxm.harbor.bean.Result;

import java.util.List;

/**
 * 业态服务
 */
public interface BusinessFormatService {
    /**
     * 根据业态ID获取一个业态数据
     *
     * @param bizFormatId 业态ID
     * @return 业态数据
     */
    BizFormat getBizFormat(Integer bizFormatId);

    /**
     * 分页+过滤获取业态集合
     *
     * @param pageQO
     * @return list
     */
    PageVO<BizFormat> getBizFormatList(PageQO<BizFormat> pageQO);

    /**
     * 获取所有业态数据
     *
     * @return 所有业态数据
     */
    List<BizFormat> getBizFormatList();

    /**
     * 根据ID删除一个业态数据
     *
     * @param bizFormatId 业态ID
     * @return ret
     */
    Result deleteBizFormat(Integer bizFormatId);

    /**
     * 修改一个业态数据
     *
     * @param bizFormat 业态数据新值
     * @return ret
     */
    Result updateBizFormat(BizFormat bizFormat);

    /**
     * 根据业态编号前缀查询业态
     * @param keyword   业态编号前缀
     * @return 业态列表
     */
//    List<BizFormat> getBizFormatByNumPrefix(String keyword);

    /**
     * 新增一个业态数据
     *
     * @param bizFormat 要新增的业态
     * @return 操作结果
     */
    Result addBizFormat(BizFormat bizFormat);
}

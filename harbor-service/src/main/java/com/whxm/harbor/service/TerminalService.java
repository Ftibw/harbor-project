package com.whxm.harbor.service;

import com.whxm.harbor.bean.*;

import java.util.List;
import java.util.Map;

/**
 * 终端服务
 */
public interface TerminalService {
    /**
     * 根据终端ID获取终端数据
     *
     * @param bizTerminalId 终端ID
     * @return 终端数据
     */
    BizTerminal getBizTerminal(String bizTerminalId);

    /**
     * 获取终端列表
     *
     * @param pageQO    分页查询对象
     * @param condition
     * @return pageVO
     */
    PageVO<BizTerminal> getBizTerminalList(PageQO pageQO, BizTerminal condition);

    /**
     * 根据ID停用/启用终端
     *
     * @param bizTerminalId 终端ID
     * @return ret
     */
    Result deleteBizTerminal(String bizTerminalId);

    /**
     * 修改终端数据
     *
     * @param bizTerminal 终端数据新值
     * @return ret
     */
    Result updateBizTerminal(BizTerminal bizTerminal);

    /**
     * 新增终端数据
     *
     * @param bizTerminal 终端数据新值
     * @return ret
     */
    Result addBizTerminal(BizTerminal bizTerminal);

    /**
     * 根据终端编号和终端平台确认终端是否注册
     *
     * @param params 终端编号和终端平台
     */
    BizTerminal register(Map<String, Object> params);

    /**
     * 根据终端编号和屏保ID
     *
     * @param params
     * @return
     */
    ResultMap<String, Object> getTerminalScreensaverProgram(Map<String, Object> params);

    /**
     * 获取无屏保的终端
     *
     * @param pageQO
     * @param condition
     */
    PageVO<BizTerminal> getBizTerminalListWithPublishedFlag(PageQO pageQO, BizTerminal condition);

    /**
     * 维持终端在线状态
     *
     * @param terminalNumber
     * @return
     */
    int updateTerminalOnline(String terminalNumber);

    /**
     * 批量更新终端在线状态
     *
     * @param terminalNumbers
     * @return
     */
    int updateTerminalOffline(List<Object> terminalNumbers);

    /**
     * 更新终端配置
     *
     * @param map 配置参数
     * @return 结果
     */
    Result updateTerminalConfig(Map<String, Object> map);

    /**
     * 获取终端首页轮播图
     *
     * @param sn  终端编号
     * @param ret 结果Map
     * @return 结果
     */
    Map<String, Object> getTerminalFirstPage(String sn, ResultMap<String, Object> ret);

    /**
     * 终端绑定首页轮播图
     *
     * @param terminalId             终端ID
     * @param firstPageIds 首页轮播图ID
     * @return 结果
     */
    Result bindFirstPage(String terminalId, Integer[] firstPageIds);
}
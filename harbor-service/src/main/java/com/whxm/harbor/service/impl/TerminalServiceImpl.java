package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.mapper.BizScreensaverMaterialMapper;
import com.whxm.harbor.mapper.BizTerminalMapper;
import com.whxm.harbor.service.TerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class TerminalServiceImpl implements TerminalService {

    private final Logger logger = LoggerFactory.getLogger(TerminalServiceImpl.class);

    @Resource
    private BizScreensaverMaterialMapper bizScreensaverMaterialMapper;

    @Autowired
    private UrlConfig urlConfig;

    @Resource
    private BizTerminalMapper bizTerminalMapper;

    @Override
    public BizTerminal getBizTerminal(String bizTerminalId) {

        BizTerminal terminal = bizTerminalMapper.selectByPrimaryKey(bizTerminalId);

        if (null == terminal) {
            throw new DataNotFoundException();
        }

        return terminal;
    }

    @Override
    public PageVO<BizTerminal> getBizTerminalList(PageQO pageQO, BizTerminal condition) {

        PageVO<BizTerminal> pageVO = new PageVO<>(pageQO);
        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizTerminal> list = bizTerminalMapper.getBizTerminalList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public Result deleteBizTerminal(String bizTerminalId) {

        bizTerminalMapper.delScreensaverTerminalRelation(bizTerminalId);

        BizTerminal bizTerminal = new BizTerminal();

        bizTerminal.setTerminalId(bizTerminalId);

        bizTerminal.setIsDeleted(Constant.RECORD_IS_DELETED);

        bizTerminal.setTerminalNumber(null);

        int affectRow = bizTerminalMapper.updateByPrimaryKeySelective(bizTerminal);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的终端,无法删除", bizTerminalId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @Override
    public Result updateBizTerminal(BizTerminal bizTerminal) {

        int affectRow = bizTerminalMapper.updateByPrimaryKeySelective(bizTerminal);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的终端,无法修改", bizTerminal.getTerminalId()))
                : Result.success(bizTerminal);
    }

    @Override
    public Result addBizTerminal(BizTerminal bizTerminal) {

        Object exist = null;

        int affectRow = 0;

        bizTerminal.setIsTerminalOnline(Constant.DISABLED_STATUS);

        bizTerminal.setIsDeleted(Constant.RECORD_NOT_DELETED);
        //使用终端的类型(二维码/横屏/竖屏)来判断平台0/1/2
        bizTerminal.setTerminalPlatform(Integer.parseInt(bizTerminal.getTerminalType()));

        bizTerminal.setAddTerminalTime(new Date());

        bizTerminal.setTerminalId(UUID.randomUUID().toString().replace("-", ""));

        //已经做了编号的唯一索引,仅仅是为了避免重复索引异常,这里真浪费,暂时这样,优先保证状态正确性
        synchronized (this) {

            exist = bizTerminalMapper.selectIdByNumber(bizTerminal.getTerminalNumber());

            if (Objects.isNull(exist)) {

                affectRow = bizTerminalMapper.insert(bizTerminal);
            }
        }

        if (Objects.nonNull(exist))
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的终端编号%s重复", bizTerminal.getTerminalId(), bizTerminal.getTerminalNumber()));

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, "新终端,无法添加")
                : Result.success(bizTerminal);
    }

    @Override
    public BizTerminal register(Map<String, Object> params) {

        if (0 != bizTerminalMapper.updateRegisteredTime(params)) {

            return bizTerminalMapper.selectIdByNumber(params.get("terminalNumber"));
        }

        return null;
    }

    @Override
    public ResultMap<String, Object> getTerminalScreensaverProgram(Map<String, Object> params) {

        ResultMap<String, Object> ret = new ResultMap<>(4);

        final List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> terminalInfo;

        String terminalNumber = (String) params.get("terminalNumber");

        Object screensaverId = null;

        Object terminalSwitchTime = null;

        try {
            terminalInfo = bizTerminalMapper.selectTerminalWithScreensaver(terminalNumber);

            if (null != terminalInfo) {
                //屏保ID
                screensaverId = terminalInfo.get("screensaverId");
                //终端开关机时间
                terminalSwitchTime = terminalInfo.get("terminalSwitchTime");
            }
            //先存了list引用再说
            ret.build("prog", screensaverId)
                    .build("on_off", terminalSwitchTime)
                    .build("data", list)
                    .build("delay", 10);

            if (null == screensaverId || "".equals(screensaverId)) {
                ret.build("code", 0);

            } else {
                bizScreensaverMaterialMapper
                        .selectMaterialsByScreensaverId(screensaverId)
                        .forEach(item -> list.add(
                                new ResultMap<String, Object>(2)
                                        .build("name", item.getScreensaverMaterialImgName())
                                        .build("url", urlConfig.getUrlPrefix() + item.getScreensaverMaterialImgPath())
                        ));

                ret = list.isEmpty() ? ret.build("code", 0) : ret.build("code", 1);
            }

        } catch (Exception e) {

            logger.error("编号为{}的终端屏保信息 查询报错", terminalNumber, e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public PageVO<BizTerminal> getNotPublishedTerminals(PageQO pageQO, BizTerminal condition) {

        PageVO<BizTerminal> pageVO = new PageVO<>(pageQO);
        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizTerminal> list = bizTerminalMapper.selectNotPublishedTerminal(condition);

       /* if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }
}

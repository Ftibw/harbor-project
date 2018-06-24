package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.BizScreensaverMaterialMapper;
import com.whxm.harbor.mapper.BizTerminalMapper;
import com.whxm.harbor.service.TerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        BizTerminal terminal;

        try {
            terminal = bizTerminalMapper.selectByPrimaryKey(bizTerminalId);

            if (null == terminal) {
                logger.error("ID为{}的终端不存在", bizTerminalId);
            }
        } catch (Exception e) {

            logger.error("终端ID为{}的数据 获取报错", bizTerminalId);

            throw new RuntimeException(e);
        }

        return terminal;
    }

    @Override
    public PageVO<BizTerminal> getBizTerminalList(PageQO<BizTerminal> pageQO) {

        PageVO<BizTerminal> pageVO;
        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            pageVO.setList(bizTerminalMapper.getBizTerminalList(pageQO.getCondition()));

            pageVO.setTotal(page.getTotal());

        } catch (Exception e) {

            logger.error("终端列表 获取报错", e);

            throw new RuntimeException(e);
        }

        return pageVO;
    }

    @Override
    public Result deleteBizTerminal(String bizTerminalId) {

        Result ret;

        try {

            bizTerminalMapper.delScreensaverTerminalRelation(bizTerminalId);

            BizTerminal bizTerminal = new BizTerminal();

            bizTerminal.setTerminalId(bizTerminalId);

            bizTerminal.setIsDeleted(Constant.RECORD_IS_DELETED);

            bizTerminal.setTerminalNumber(null);

            boolean isSuccess = updateBizTerminal(bizTerminal).getData().toString().contains("1");

            logger.info(isSuccess ? "ID为{}的终端 删除成功" : "ID为{}的终端 删除失败", bizTerminalId);

            ret = new Result(isSuccess ?
                    "ID为" + bizTerminalId + "的终端 删除成功" :
                    "ID为" + bizTerminalId + "的终端 删除失败"
            );

        } catch (Exception e) {

            logger.error("终端ID为{}的数据 删除错误", bizTerminalId);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result updateBizTerminal(BizTerminal bizTerminal) {

        Result ret;

        try {

            int affectRow = bizTerminalMapper.updateByPrimaryKeySelective(bizTerminal);

            logger.info(1 == affectRow ?
                    "ID为" + bizTerminal.getTerminalId() + "的终端 修改成功" :
                    "ID为" + bizTerminal.getTerminalId() + "的终端 修改失败"
            );

            ret = new Result("终端数据修改了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("终端数据 修改报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public Result addBizTerminal(BizTerminal bizTerminal) {

        Result ret;

        Object exist = null;

        int affectRow = 0;

        try {
            bizTerminal.setIsTerminalOnline(Constant.DISENABLED_STATUS);

            bizTerminal.setIsDeleted(Constant.RECORD_NOT_DELETED);

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
                return new Result(HttpStatus.NOT_ACCEPTABLE.value(), "终端编号重复", Constant.NO_DATA);

            logger.info(1 == affectRow ?
                    "终端数据添加成功" :
                    "终端数据添加失败"
            );

            ret = new Result("终端数据添加了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("终端数据 添加报错", e);

            throw new RuntimeException(e);
        }

        return ret;
    }

    @Override
    public BizTerminal register(Map<String, Object> params) {

        try {
            if (0 != bizTerminalMapper.updateRegisteredTime(params)) {

                return bizTerminalMapper.selectIdByNumber(params.get("terminalNumber"));
            }

        } catch (Exception e) {

            logger.error("终端是否注册 查询报错 ", e);

            throw new RuntimeException(e);
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
                                        .build("name", item.getScreensaverMaterialName())
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
    public List<BizTerminal> getNotPublishedTerminal(Integer screensaverId) {

        List<BizTerminal> list = null;

        try {
            list = bizTerminalMapper.selectNotPublishedTerminal(screensaverId);

            logger.info(list.isEmpty() ? "无屏保的终端不存在" : "查询无屏保的终端列表OK");

        } catch (Exception e) {

            logger.error("无屏保的终端列表查询报错", e);

            throw new RuntimeException(e);
        }

        return list;
    }
}

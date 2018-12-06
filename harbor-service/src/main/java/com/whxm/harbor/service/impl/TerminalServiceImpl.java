package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.cache.CacheService;
import com.whxm.harbor.conf.TerminalConfig;
import com.whxm.harbor.conf.PathConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.BusinessException;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.mapper.BizScreensaverMaterialMapper;
import com.whxm.harbor.mapper.BizTerminalMapper;
import com.whxm.harbor.service.MapService;
import com.whxm.harbor.service.TerminalService;
import com.whxm.harbor.utils.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class TerminalServiceImpl implements TerminalService {

    private final Logger logger = LoggerFactory.getLogger(TerminalServiceImpl.class);

    @Resource
    private BizScreensaverMaterialMapper bizScreensaverMaterialMapper;
    @Autowired
    private PathConfig pathConfig;
    @Resource
    private BizTerminalMapper bizTerminalMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private MapService mapService;
    @Resource
    private BizBuildingMapper bizBuildingMapper;

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

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result deleteBizTerminal(String bizTerminalId) {

        BizTerminal terminal = bizTerminalMapper.selectByPrimaryKey(bizTerminalId);

        if (null == terminal)
            return Result.success(String.format("ID为%s的终端不存在,无需删除", bizTerminalId));

        bizTerminalMapper.delScreensaverTerminalRelation(bizTerminalId);
        bizTerminalMapper.delTerminalFirstPageRelation(bizTerminalId);

        BizTerminal bizTerminal = new BizTerminal();
        bizTerminal.setTerminalId(bizTerminalId);
        bizTerminal.setIsDeleted(Constant.YES);
        bizTerminal.setIsTerminalOnline(Constant.NO);
        bizTerminal.setTerminalNumber(null);
        bizTerminal.setTerminalName(null);
        bizTerminal.setFloorId(null);

        int affectRow = bizTerminalMapper.updateByPrimaryKeySelective(bizTerminal);

        if (0 == affectRow)
            Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的终端,无法删除", bizTerminalId));

        String number = terminal.getTerminalNumber();

        //删building
        BizBuilding building = bizBuildingMapper.selectByNumber(number);
        if (null == building)
            return Result.success(String.format("终端编号为%s的建筑不存在", number));

        affectRow = bizBuildingMapper.deleteByNumber(number);
        if (0 == affectRow) {
            return Result.success(String.format("编号为%s的终端删除成功,对应的建筑删除行数为0", number));
        }
        //删edges
        MapEdge edgePoint = new MapEdge();
        Integer id = building.getId();
        edgePoint.setHead(id);
        edgePoint.setTail(id);
        Result result = mapService.delEdgesByTailOrHead(edgePoint);
        if (!result.getCode().equals(ResultEnum.SUCCESS_DELETED.getCode())) {
            result.setResultEnum(ResultEnum.SUCCESS_DELETED);
            result.setMsg(String.format("编号为%s的终端删除成功,终端对应建筑的有关边删除行数为0", number));
            return result;
        }
        return Result.success(ResultEnum.SUCCESS_DELETED);
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

        bizTerminal.setIsTerminalOnline(Constant.NO);

        bizTerminal.setIsDeleted(Constant.NO);
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
    public ResultMap<String, Object> getTerminalScreensaverProgram(String terminalNumber) {

        ResultMap<String, Object> ret = new ResultMap<>(8);

        final List<Map<String, Object>> list = new ArrayList<>();

        Object screensaverId = null;

        Object screensaverProgramName = null;

        TerminalConfig config = JacksonUtils.readValue(JacksonUtils.toJson(cacheService.getConfig(TerminalConfig.cacheKey).get(0)), TerminalConfig.class);

        if (null == config)
            throw new DataNotFoundException();

        try {
            Map<String, Object> terminalInfo = bizTerminalMapper.selectTerminalWithScreensaver(terminalNumber);

            if (null != terminalInfo) {
                //屏保ID
                screensaverId = terminalInfo.get("screensaverId");

                screensaverProgramName = terminalInfo.get("screensaverProgramName");

            }

            //先存了list引用再说
            ret.build("prog", null == screensaverId ? 0 : screensaverId)
                    .build("data", list)
                    .build("on_off", config.getOnOff())
                    .build("delay", config.getDelay())
                    .build("protect", config.getProtect())
                    //这个字段给后台使用的
                    .build("screensaverProgramName", screensaverProgramName);

            if (null == screensaverId || "".equals(screensaverId)) {
                ret.build("code", 0);

            } else {
                bizScreensaverMaterialMapper
                        .selectMaterialsByScreensaverId(screensaverId)
                        .forEach(item -> list.add(
                                new ResultMap<String, Object>(2)
                                        .build("name", item.getScreensaverMaterialImgName())
                                        .build("url", pathConfig.getResourceURLWithPost() + item.getScreensaverMaterialImgPath())
                        ));

                ret = list.isEmpty() ? ret.build("code", 0) : ret.build("code", 1);
            }

            return ret;

        } catch (Exception e) {

            logger.error("编号为{}的终端屏保信息 查询报错", terminalNumber, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ret.build("code", 0)
                    .build("prog", 0)
                    .build("data", new Object[]{})
                    .build("on_off", config.getOnOff())
                    .build("delay", config.getDelay())
                    .build("protect", config.getProtect())
                    .build("screensaverProgramName", "");
        }
    }

    @Override
    public PageVO<BizTerminal> getBizTerminalListWithPublishedFlag(PageQO pageQO, BizTerminal condition) {

        PageVO<BizTerminal> pageVO = new PageVO<>(pageQO);
        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());
        //[terminalType],[floorId],screensaverId
        List<BizTerminal> list = bizTerminalMapper.getBizTerminalListWithPublishedFlag(condition);

       /* if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    @Override
    public int updateTerminalOnline(String terminalNumber) {
        return bizTerminalMapper.keepOnline(terminalNumber);
    }

    @Override
    public int updateTerminalOffline(List<Object> terminalNumbers) {
        return bizTerminalMapper.offLine(terminalNumbers);
    }

    @Override
    public Map<String, Object> getTerminalFirstPage(String sn, ResultMap<String, Object> ret) {
        List<BizScreensaverMaterial> list = bizScreensaverMaterialMapper.getFirstPageByTerminalNumber(sn);
        if (null != list && !list.isEmpty()) {
            list.forEach(item -> item.setScreensaverMaterialImgPath(pathConfig.getResourceURLWithPost() + item.getScreensaverMaterialImgPath()));
            return ret.build("success", true)
                    .build("data", list);
        } else return ret;
    }

    @Override
    public Result bindFirstPage(String terminalId, Integer[] firstPageIds) {

        bizTerminalMapper.delTerminalFirstPageRelation(terminalId);

        int affectRow = bizTerminalMapper.insertTerminalFirstPageRelation(terminalId, firstPageIds);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的终端,无法设置ID为%s的首页轮播图", terminalId, Arrays.asList(firstPageIds)))
                : Result.success(firstPageIds);
    }

    @Override
    public Result updateTerminalConfig(TerminalConfig terminalConfig) {
        return Result.success(cacheService.updateConfig(terminalConfig));
    }

    @Override
    public Result getTerminalConfig() {

        return Result.success(cacheService.getConfig(TerminalConfig.cacheKey).get(0));
    }

    @Override
    public Result resetTerminalConfig() {

        cacheService.resetConfig(TerminalConfig.cacheKey);

        return Result.success();
    }
}
package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.PathConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.mapper.BizBuildingMapper;
import com.whxm.harbor.mapper.BizShopMapper;
import com.whxm.harbor.service.MapService;
import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.JacksonUtils;
import com.whxm.harbor.utils.PinyinUtils;
import com.whxm.harbor.vo.BizShopVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    @Autowired
    private PathConfig pathConfig;
    @Autowired
    private MapService mapService;
    @Resource
    private BizShopMapper bizShopMapper;
    @Resource
    private BizBuildingMapper bizBuildingMapper;

    @Override
    public BizShopVo getBizShop(String bizShopId) {

        BizShopVo vo = new BizShopVo();

        BizShop po = bizShopMapper.selectByPrimaryKey(bizShopId);

        if (null == po)
            return null;

        po.setShopLogoPath(pathConfig.getResourceURLWithPost() + po.getShopLogoPath());

        BeanUtils.copyProperties(po, vo);

        List<ShopPicture> list = this.getShopPicturesById(bizShopId);

        if (null == list || list.isEmpty())
            return null;

        vo.setPictures(list);

        return vo;
    }

    @Override
    public BizShopVo getBizShopByNumber(String shopNumber) {

        BizShop shop = bizShopMapper.selectIdByNumber(shopNumber);

        if (null == shop) return null;

        return getBizShop(shop.getShopId());
    }

    @Override
    public PageVO<BizShopVo> getBizShopList(PageQO pageQO, BizShop condition) {

        PageVO<BizShopVo> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        final List<BizShopVo> ret = new ArrayList<>();

        List<BizShop> list = bizShopMapper.getBizShopList(condition);

        /*if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        selectShopPictures(ret, list);

        pageVO.setList(ret);

        pageVO.setTotal(page.getTotal());

        return pageVO;
    }

    private void selectShopPictures(List<BizShopVo> ret, List<BizShop> list) {
        list.forEach(po -> {

            po.setShopLogoPath(pathConfig.getResourceURLWithPost() + po.getShopLogoPath());

            BizShopVo vo = new BizShopVo();

            BeanUtils.copyProperties(po, vo);

            vo.setPictures(this.getShopPicturesById(vo.getShopId()));

            ret.add(vo);
        });
    }

    /**
     * 终端获取商铺数据
     *
     * @param params 楼层ID/业态ID/商铺名称首字母
     * @return list
     */
    @Override
    public List<BizShopVo> getBizShopListOptional(Map<String, Object> params) {

        final List<BizShopVo> ret = new ArrayList<>();

        List<BizShop> list = bizShopMapper.getBizShopListOptional(params);

       /* if (null == list || list.isEmpty())
            throw new DataNotFoundException();*/

        selectShopPictures(ret, list);

        return ret;
    }

    @Override
    public Result triggerBizShop(String bizShopId) {

        BizShop bizShop = bizShopMapper.selectByPrimaryKey(bizShopId);

        int status = bizShop.getIsShopEnabled() ^ 1;

        bizShop.setIsShopEnabled(status);

        int affectRow = bizShopMapper.updateByPrimaryKeySelective(bizShop);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺,无法启停", bizShopId))
                : Result.success(bizShop);
    }

    @Override
    public Result updateBizShop(BizShopVo shopVo) {

        Object could = null;

        int affectRow = 0;

        int affectRow1 = 0;

        List<ShopPicture> pictures = shopVo.getPictures();

        Assert.notNull(pictures, "商铺图片集合不能为空");

        shopVo.setShopLogoPath(shopVo.getShopLogoPath().replaceAll("^" + pathConfig.getResourceURLWithPost() + "(.*)$", "$1"));

        pictures.forEach(item -> {
            Assert.notNull(item.getShopPicturePath(), "商铺图片不能为空");
            item.setShopPicturePath(item.getShopPicturePath().replaceAll("^" + pathConfig.getResourceURLWithPost() + "(.*)$", "$1"));
        });

        shopVo.setShopEnglishName(PinyinUtils.toPinyin(shopVo.getShopName()));

        synchronized (this) {

            could = bizShopMapper.couldUpdateUniqueNumber(shopVo);

            if (null != could) {

                affectRow = bizShopMapper.updateByPrimaryKeySelective(shopVo);
            }
        }

        if (null == could)
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺编号%s重复", shopVo.getShopId(), shopVo.getShopNumber()));

        bizShopMapper.deleteShopPictures(shopVo.getShopId());

        affectRow1 = bizShopMapper.insertShopPictures(shopVo.getShopId(), pictures);


        return 0 == affectRow || 0 == affectRow1 ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺,无法修改", shopVo.getShopId()))
                : Result.success(shopVo);
    }

    @Override
    public Result addBizShop(BizShopVo shopVo) {

        Object exist = null;

        int affectRow = 0;

        //赋值
        String shopId = UUID.randomUUID().toString().replace("-", "");

        shopVo.setShopEnglishName(PinyinUtils.toPinyin(shopVo.getShopName()));
        shopVo.setShopId(shopId);
        shopVo.setIsShopEnabled(Constant.YES);
        shopVo.setAddShopTime(new Date());
        shopVo.setShopDescript("");
        //已经做了编号的唯一索引,仅仅是为了避免重复索引异常,这里真浪费,暂时这样,优先保证状态正确性
        synchronized (this) {

            exist = bizShopMapper.selectIdByNumber(shopVo.getShopNumber());

            if (Objects.isNull(exist)) {

                affectRow = bizShopMapper.insert(shopVo);
            }
        }

        if (Objects.nonNull(exist))
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺编号%s重复", shopVo.getShopId(), shopVo.getShopNumber()));

        List<ShopPicture> pictures = shopVo.getPictures();

        bizShopMapper.insertShopPictures(shopId, pictures);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺,无法添加", shopVo.getShopId()))
                : Result.success(shopVo);
    }

    @Override
    public List<ShopPicture> getShopPicturesById(String bizShopId) {

        List<ShopPicture> list = bizShopMapper.selectShopPicturesById(bizShopId);

        list.forEach(picture ->
                picture.setShopPicturePath(
                        pathConfig.getResourceURLWithPost() + picture.getShopPicturePath()
                )
        );

        return list;
    }

    @CacheEvict(cacheNames = {"bizBuilding", "bizEdge"}, allEntries = true)
    @Override
    public Result deleteBizShop(String bizShopId) {
        //删图片
        bizShopMapper.deleteShopPictures(bizShopId);
        BizShop bizShop = bizShopMapper.selectByPrimaryKey(bizShopId);
        //删商铺
        String number = bizShop.getShopNumber();
        int affectRow = bizShopMapper.deleteByPrimaryKey(bizShopId);
        //删building
        BizBuilding building = bizBuildingMapper.selectByNumber(number);
        bizBuildingMapper.deleteByNumber(number);
        //删edges
        MapEdge pointKey = new MapEdge();
        Integer id = building.getId();
        pointKey.setHead(id);
        pointKey.setTail(id);
        mapService.delEdgesByPartKey(pointKey);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺,无法删除", bizShopId))
                : Result.success(ResultEnum.SUCCESS_DELETED);
    }

    @CacheEvict(cacheNames = "bizBuilding", allEntries = true)
    @Override
    public Result addShopWithPoint(BizShopVo vo) {
        int i = 0;
        BizBuilding building = null;
        Result result = addBizShop(vo);
        if (ResultEnum.SUCCESS.getCode().equals(result.getCode())) {
            building = new BizBuilding();
            building.setId(null);
            building.setNumber(vo.getShopNumber());
            building.setName(vo.getShopName());
            building.setLayer(vo.getFloorId());
            building.setType(vo.getBuildingType());
            building.setArea(JacksonUtils.toJson(vo.getArea()));
            building.setDx(vo.getDx());
            building.setDy(vo.getDy());
            i = bizBuildingMapper.insert(building);
        }
        return 0 == i ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("商铺[%s],无法添加", vo.getShopName()))
                : Result.success(building);
    }
}

package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.mapper.BizShopMapper;
import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.utils.PinyinUtils;
import com.whxm.harbor.vo.BizShopVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {


    @Autowired
    private UrlConfig urlConfig;

    @Resource
    private BizShopMapper bizShopMapper;

    @Override
    public BizShopVo getBizShop(String bizShopId) {

        BizShopVo vo = new BizShopVo();

        BizShop po = bizShopMapper.selectByPrimaryKey(bizShopId);

        if (null == po)
            throw new DataNotFoundException();

        po.setShopLogoPath(urlConfig.getUrlPrefix() + po.getShopLogoPath());

        BeanUtils.copyProperties(po, vo);

        List<ShopPicture> list = this.getShopPicturesById(bizShopId);

        if (null == list || list.isEmpty())
            throw new DataNotFoundException();

        vo.setPictures(list);

        return vo;
    }

    @Override
    public PageVO<BizShop> getBizShopList(PageQO pageQO, BizShop condition) {

        PageVO<BizShop> pageVO = new PageVO<>(pageQO);

        Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

        List<BizShop> list = bizShopMapper.getBizShopList(condition);

        if (null == list || list.isEmpty())
            throw new DataNotFoundException();

        list.forEach(item -> item.setShopLogoPath(
                urlConfig.getUrlPrefix()
                        + item.getShopLogoPath()
        ));

        pageVO.setList(list);

        pageVO.setTotal(page.getTotal());

        return pageVO;
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

        if (null == list || list.isEmpty())
            throw new DataNotFoundException();

        list.forEach(po -> {

            po.setShopLogoPath(urlConfig.getUrlPrefix() + po.getShopLogoPath());

            BizShopVo vo = new BizShopVo();

            BeanUtils.copyProperties(po, vo);

            vo.setPictures(this.getShopPicturesById(vo.getShopId()));

            ret.add(vo);
        });

        return ret;
    }

    @Override
    public Result triggerBizShop(String bizShopId) {

        BizShop bizShop = bizShopMapper.selectByPrimaryKey(bizShopId);
        /*
         * if(0==bizShop.getIsShopEnabled() ^ 1)
         * bizShopMapper.delShopPicturesRelation(bizShopId);
         * */
        int status = bizShop.getIsShopEnabled() ^ 1;

        bizShop.setIsShopEnabled(status);

        int affectRow = bizShopMapper.updateByPrimaryKeySelective(bizShop);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺,无法启停", bizShopId))
                : Result.success(bizShop);
    }

    @Override
    public Result updateBizShop(BizShop bizShop) {

        bizShop.setShopLogoPath(bizShop.getShopLogoPath().replaceAll("^" + urlConfig.getUrlPrefix() + "(.*)$", "$1"));

        int affectRow = bizShopMapper.updateByPrimaryKeySelective(bizShop);

        return 0 == affectRow ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺,无法修改", bizShop.getShopId()))
                : Result.success(bizShop);
    }

    @Override
    public Result addBizShop(BizShop bizShop, List<Map<String, Object>> pictureList) {

        Object exist = null;

        int affectRow = 0;

        int affectRow1 = 0;

        //赋值
        String shopId = UUID.randomUUID().toString().replace("-", "");

        bizShop.setShopEnglishName(
                PinyinUtils.toPinyin(bizShop.getShopName())
        );
        bizShop.setShopId(shopId);
        bizShop.setIsShopEnabled(Constant.ENABLED_STATUS);
        bizShop.setAddShopTime(new Date());

        //已经做了编号的唯一索引,仅仅是为了避免重复索引异常,这里真浪费,暂时这样,优先保证状态正确性
        synchronized (this) {

            exist = bizShopMapper.selectIdByNumber(bizShop.getShopNumber());

            if (Objects.isNull(exist)) {

                affectRow = bizShopMapper.insert(bizShop);
            }
        }

        if (Objects.nonNull(exist))
            return Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺编号%s重复", bizShop.getShopId(), bizShop.getShopNumber()));

        affectRow1 = bizShopMapper.insertShopPictures(shopId, pictureList);


        return 0 == affectRow + affectRow1 ?
                Result.failure(ResultEnum.OPERATION_LOGIC_ERROR, String.format("ID为%s的商铺,无法添加", bizShop.getShopId()))
                : Result.success(bizShop);
    }

    @Override
    public List<ShopPicture> getShopPicturesById(String bizShopId) {

        List<ShopPicture> list = bizShopMapper.selectShopPicturesById(bizShopId);

        list.forEach(picture ->
                picture.setShopPicturePath(
                        urlConfig.getUrlPrefix() + picture.getShopPicturePath()
                )
        );

        return list;
    }
}

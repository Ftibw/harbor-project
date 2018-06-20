package com.whxm.harbor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.UrlConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.mapper.BizShopMapper;
import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.utils.PinyinUtils;
import com.whxm.harbor.vo.BizShopVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {

    private final Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);

    @Autowired
    private UrlConfig urlConfig;

    @Resource
    private BizShopMapper bizShopMapper;

    @Override
    public BizShop getBizShop(String bizShopId) {

        BizShop bizShop;
        try {
            bizShop = bizShopMapper.selectByPrimaryKey(bizShopId);

        } catch (Exception e) {

            logger.error("ID为{}的商铺,获取报错", bizShopId);

            throw new RuntimeException();
        }

        return bizShop;
    }

    @Override
    public PageVO<BizShop> getBizShopList(PageQO<BizShop> pageQO) {

        PageVO<BizShop> pageVO;

        try {
            Page page = PageHelper.startPage(pageQO.getPageNum(), pageQO.getPageSize());

            pageVO = new PageVO<>(pageQO);

            List<BizShop> list = bizShopMapper.getBizShopList(pageQO.getCondition());

            list.forEach(item -> item.setShopLogoPath(
                    urlConfig.getUrlPrefix()
                            + item.getShopLogoPath()
            ));

            pageVO.setList(list);

            pageVO.setTotal(page.getTotal());

            logger.info("商铺列表 获取成功");

        } catch (Exception e) {

            logger.error("商铺列表 获取错误", e);

            throw new RuntimeException();
        }

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

        try {
            List<BizShop> list = bizShopMapper.getBizShopListOptional(params);

            list.forEach(po -> {

                po.setShopLogoPath(urlConfig.getUrlPrefix() + po.getShopLogoPath());

                BizShopVo vo = new BizShopVo();

                BeanUtils.copyProperties(po, vo);

                vo.setPictures(this.getShopPicturesById(vo.getShopId()));

                ret.add(vo);
            });

            logger.info(list.isEmpty() ? "{}条件下查询商铺列表无数据" : "{}条件下查询商铺列表成功", params);

        } catch (Exception e) {

            logger.error("商铺数据列表 获取报错", e);

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result triggerBizShop(String bizShopId) {

        Result ret;

        if (null != bizShopId) {

            try {
                BizShop bizShop = getBizShop(bizShopId);
                /*
                 * if(0==bizShop.getIsShopEnabled() ^ 1)
                 * bizShopMapper.delShopPicturesRelation(bizShopId);
                 * */
                int status = bizShop.getIsShopEnabled() ^ 1;

                bizShop.setIsShopEnabled(status);

                updateBizShop(bizShop);

                ret = new Result(status);

            } catch (Exception e) {

                logger.error("ID为{}的商铺 状态(启用/停用)变更报错", bizShopId);

                throw new RuntimeException();
            }
        } else {
            logger.error("商铺ID为空");

            ret = new Result(HttpStatus.NOT_FOUND.value(), "商铺ID为空", Constant.NO_DATA);
        }

        return ret;
    }

    @Override
    public Result updateBizShop(BizShop bizShop) {

        Result ret;

        if (null == bizShop
                || "".equals(bizShop.getShopId())
                || null == bizShop.getShopId()) {

            logger.error("商铺数据不存在");

            return new Result(HttpStatus.OK.value(), "商铺数据不存在", Constant.NO_DATA);
        }

        try {
            int affectRow = bizShopMapper.updateByPrimaryKeySelective(bizShop);

            logger.info(1 == affectRow ? "ID为{}的商铺 修改成功" : "ID为{}的商铺 修改失败", bizShop.getShopId());

            ret = new Result("商铺数据修改了" + affectRow + "行");

        } catch (Exception e) {

            logger.error("ID为{}的商铺 修改错误", bizShop.getBizFormatId());

            throw new RuntimeException();
        }

        return ret;
    }

    @Override
    public Result addBizShop(BizShop bizShop, List<Map<String, Object>> pictureList) {

        Result ret;

        if (null != bizShop) {
            try {
                synchronized (this) {
                    if (null != bizShopMapper.selectIdByNumber(bizShop.getShopNumber())) {

                        return new Result(HttpStatus.NOT_ACCEPTABLE.value(), "商铺编号重复", Constant.NO_DATA);
                    }
                }
                //赋值
                String shopId = UUID.randomUUID().toString().replace("-", "");

                bizShop.setShopEnglishName(
                        PinyinUtils.toPinyin(bizShop.getShopName())
                );
                bizShop.setShopId(shopId);
                bizShop.setIsShopEnabled(Constant.ENABLED_STATUS);
                bizShop.setAddShopTime(new Date());

                int affectRow = bizShopMapper.insert(bizShop);

                int affectRow2 = 0;

                //必填传入参数,前端需要把控
                if (null != pictureList && !pictureList.isEmpty() && !pictureList.get(0).isEmpty())

                    affectRow2 = bizShopMapper.insertShopPictures(shopId, pictureList);

                logger.info("新增" + affectRow + "行商铺记录,新增" + affectRow2 + "行商铺图片记录");

                ret = new Result("新增" + affectRow + "行商铺记录,新增" + affectRow2 + "行商铺图片记录");

            } catch (Exception e) {

                logger.error("商铺数据 添加错误");

                throw new RuntimeException();
            }

        } else {
            logger.error("要添加的商铺数据为空");

            ret = new Result(HttpStatus.NOT_ACCEPTABLE.value(), "要添加的商铺数据为空", Constant.NO_DATA);
        }

        return ret;
    }

    @Override
    public List<ShopPicture> getShopPicturesById(String bizShopId) {

        List<ShopPicture> list;

        try {
            list = bizShopMapper.selectShopPicturesById(bizShopId);

            list.forEach(picture ->
                    picture.setShopPicturePath(
                            urlConfig.getUrlPrefix() + picture.getShopPicturePath()
                    )
            );

            logger.info(list.isEmpty() ? "ID为{}的商铺图片不存在" : "ID为{}的商铺图片查询成功", bizShopId);

        } catch (Exception e) {

            logger.error("ID为{}的商铺图片 获取报错", bizShopId, e);

            throw new RuntimeException();
        }

        return list;
    }
}

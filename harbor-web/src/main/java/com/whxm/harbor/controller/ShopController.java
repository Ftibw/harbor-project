package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.annotation.VisitLogger;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.enums.ResultEnum;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.exception.ParameterInvalidException;
import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.service.ShopVisitService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.FileUtils;
import com.whxm.harbor.vo.BizShopVo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(description = "商铺服务")
@RestController
@RequestMapping("/shop")
@MyApiResponses
public class ShopController {

    private final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private ShopVisitService shopVisitService;

    @Autowired
    private ShopService shopService;

    @ApiOperation(value = "添加商铺以及坐标信息")
    @PostMapping(value = "/shopInfo")
    public Result addShop(@ApiParam("商铺建筑完整信息") @RequestBody BizShopVo shopVo) {
        Assert.notNull(shopVo, "添加的商铺数据不能为空");
        String number = shopVo.getShopNumber();
        List<ShopPicture> pictures = shopVo.getPictures();
        Assert.notEmpty(pictures, "编号为{}的商铺图片集合不能为空", number);

        Assert.notRepeat(pictures, "编号为" + number + "的商铺图片不能重复");

        pictures.forEach(item -> Assert.notNull(item.getShopPicturePath(), "编号为{}的商铺图片不能为空[params:{}]", number, item));
        return shopService.addShopWithPoint(shopVo);
    }

    @ApiOperation(value = "根据业态/楼层/商铺名称信息获取店铺列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "floor", value = "楼层ID", paramType = "form"),
            @ApiImplicitParam(name = "type", value = "业态ID", paramType = "form"),
            @ApiImplicitParam(name = "initial", value = "商铺名称大写首字母", paramType = "form")
    })
    @PostMapping(value = "/shops")
    public Map<String, Object> getBizShops(Integer floor, Integer type, String initial) {
        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizShopVo> list = shopService.getBizShopListOptional(
                    new ResultMap<String, Object>(3)
                            .build("floorId", floor)
                            .build("bizFormatId", type)
                            .build("initial", Objects.nonNull(initial) ? initial.toLowerCase() : null)
            );

            if (null == list || list.isEmpty())
                ret.build("data", new byte[]{}).build("success", false);

            ret.build("data", list).build("success", true);

        } catch (Exception e) {

            logger.error("floorId:{},bizFormatId:{},initial:{}---店铺列表 获取报错", floor, type, initial, e);

            ret.build("data", new byte[]{}).build("success", false);
        }

        return ret;
    }

    @ApiOperation(value = "根据商铺ID获取商铺图片")
    @GetMapping("/shopPictures/{ID}")
    public Result getPicturesById(@PathVariable("ID") String bizShopId) {

        Assert.notNull(bizShopId, "商铺ID不能为空");

        List<ShopPicture> shopPictures = shopService.getShopPicturesById(bizShopId);

        if (null == shopPictures || shopPictures.isEmpty())
            throw new DataNotFoundException();

        return Result.success(shopPictures);
    }

    @VisitLogger
    @ApiOperation(value = "访问商铺")
    @PostMapping("/visit")
    public Map<String, Object> updateShopVisit(
            @ApiParam(name = "no", value = "商铺编号")
            @RequestParam("no") String shopNumber) {

        Assert.notNull(shopNumber, "商铺编号不能为空");

        return shopVisitService.updateShopVisit(shopNumber);
    }

    @ApiOperation(value = "获取商铺访问数据列表")
    @GetMapping("/visits")
    public Result getShopVisitList(PageQO pageQO, BizShop condition) {

        PageVO<ShopVisit> pageVO = shopVisitService.getShopVisitList(pageQO, condition);

        return Result.success(pageVO);
    }
    //=========================以上为对外提供的API=================================

    @ApiOperation("上传商铺logo")
    @PostMapping("/logo")
    public Result uploadLogo(@RequestParam("file") MultipartFile file) {

        if (null == file || file.isEmpty()) {
            throw new ParameterInvalidException("上传的文件是空的");
        }

        return FileUtils.upload(file, Result::success);
    }

    @ApiOperation(value = "上传商铺图片", notes = "表单控件中name属性的值必须为file")
    @PostMapping("/pictures")
    public Result uploadPicture(HttpServletRequest request) {

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        if (null == files || files.isEmpty()) {
            throw new ParameterInvalidException("上传的文件是空的");
        }

        ArrayList<Object> retList = new ArrayList<>();

        files.forEach(file -> FileUtils.upload(file, retList::add));

        return Result.success(retList);
    }

    @ApiOperation("根据商铺ID或商铺编号获取商铺信息")
    @GetMapping("/shop")
    public Result getBizShop(
            @ApiParam(name = "id", value = "商铺的ID", required = true)
            @RequestParam("id") String shopId
    ) {
        Assert.notNull(shopId, "商铺ID不能为空");

        BizShopVo shop = null;
        if (32 == shopId.length()) {
            shop = shopService.getBizShop(shopId);
        } else {
            String shopNumber = shopId;
            shop = shopService.getBizShopByNumber(shopNumber);
        }

        return null == shop ? Result.failure(ResultEnum.RESULT_DATA_NONE, new Object[]{}) : Result.success(shop);
    }
    //==========================以下需授权的接口均被拦截============================

    @ApiOperation("获取商铺列表(需授权)")
    @GetMapping("/bizShops")
    public Result getBizShops(PageQO pageQO, BizShop condition) {

        PageVO<BizShopVo> pageVO = shopService.getBizShopList(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("启用/停用商铺(需授权)")
    @DeleteMapping(value = "/bizShop/status")
    public Result triggerBizShop(
            @ApiParam(name = "ID", value = "商铺的ID", required = true)
                    String id
    ) {
        Assert.notNull(id, "商铺ID不能为空");

        return shopService.triggerBizShop(id);
    }

    @ApiOperation("修改商铺")
    @PutMapping("/shops")
    public Result updateBizShop(@RequestBody BizShopVo shopVo) {

        Assert.notNull(shopVo, "商铺数据不能为空");

        Assert.notNull(shopVo.getShopId(), "商铺ID不能为空");

        List<ShopPicture> pictures = shopVo.getPictures();

        Assert.notEmpty(pictures, "商铺图片集合不能为空");

        shopVo.setPictures(pictures);

        return shopService.updateBizShop(shopVo);
    }

    @ApiOperation(value = "添加商铺(需授权)",
            notes = "pictureList中元素为map,map有3个key," +
                    "shopPictureName(商铺图片名称),shopPicturePath(商铺图片路径),shopPictureSize(商铺图片大小)")
    @PostMapping("/bizShop")
    public Result addBizShop(@RequestBody ShopParam param) {

        Assert.notNull(param, "提交数据不能为空");

        Assert.notNull(param.bizShop, "商铺数据不能为空[params:{}]", param);

        Assert.isNull(param.bizShop.getShopId(), "商铺ID必须为空[params:{}]", param);

        Assert.notNull(param.bizShop.getShopLogoPath(), "商铺logo不能为空[params:{}]", param);

        //-----------做适配---------------
        BizShopVo shopVo = new BizShopVo();

        BeanUtils.copyProperties(param.bizShop, shopVo);

        List<ShopPicture> pictures = param.pictureList;

        Assert.notEmpty(pictures, "商铺图片集合不能为空");

        Assert.notRepeat(pictures, "商铺图片不能重复");

        pictures.forEach(item -> Assert.notNull(item.getShopPicturePath(), "商铺图片不能为空[params:{}]", item));

        shopVo.setPictures(pictures);
        //--------------------------------

        return shopService.addBizShop(shopVo);
    }

    @ApiOperation("删除商铺")
    @DeleteMapping(value = "/shops")
    public Result deleteBizShop(
            @ApiParam(name = "id", value = "商铺的ID", required = true)
            @RequestParam String id
    ) {
        Assert.notNull(id, "商铺ID不能为空");

        return shopService.deleteBizShop(id);
    }
}

//商铺+商铺图片数据封装
class ShopParam {

    public BizShop bizShop;

    public List<ShopPicture> pictureList;
}
package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.annotation.VisitLogger;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.exception.DataNotFoundException;
import com.whxm.harbor.exception.ParameterInvalidException;
import com.whxm.harbor.service.ShopService;
import com.whxm.harbor.service.ShopVisitService;
import com.whxm.harbor.utils.Assert;
import com.whxm.harbor.utils.FileUtils;
import com.whxm.harbor.vo.BizShopVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    @ApiOperation(value = "根据业态/楼层/商铺名称信息获取店铺列表")
    @PostMapping(value = "/shops")
    public Map<String, Object> getBizShops(
            @ApiParam(name = "floor", value = "楼层ID")
                    Integer floor,
            @ApiParam(name = "type", value = "业态ID")
                    Integer type,
            @ApiParam(name = "initial", value = "商铺名称大写首字母")
                    String initial
    ) {
        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizShopVo> list = shopService.getBizShopListOptional(
                    new ResultMap<String, Object>(3)
                            .build("floorId", floor)
                            .build("bizFormatId", type)
                            .build("initial", Objects.nonNull(initial) ? initial.toLowerCase() : null)
            );

            if (null == list || list.isEmpty())
                throw new DataNotFoundException();

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

    @ApiOperation("根据商铺ID获取商铺信息")
    @GetMapping("/shop")
    public Result getBizShop(
            @ApiParam(name = "id", value = "商铺的ID", required = true)
            @RequestParam("id") String shopId
    ) {
        Assert.notNull(shopId, "商铺ID不能为空");

        BizShopVo shop = shopService.getBizShop(shopId);

        return Result.success(shop);
    }
    //==========================以下均被拦截============================

    @ApiOperation("获取商铺列表(需授权)")
    @GetMapping("/bizShops")
    public Result getBizShops(PageQO pageQO, BizShop condition) {

        PageVO<BizShop> pageVO = shopService.getBizShopList(pageQO, condition);

        return Result.success(pageVO);
    }

    @ApiOperation("修改商铺(需授权)")
    @PutMapping("/bizShop")
    public Result updateBizShop(@RequestBody BizShop bizShop) {

        Assert.notNull(bizShop, "商铺数据不能为空");
        Assert.notNull(bizShop.getShopId(), "商铺ID不能为空");

        return shopService.updateBizShop(bizShop);
    }

    @ApiOperation("启用/停用商铺(需授权)")
    @DeleteMapping(value = "/bizShop")
    public Result triggerBizShop(
            @ApiParam(name = "ID", value = "商铺的ID", required = true)
                    String id
    ) {
        Assert.notNull(id, "商铺ID不能为空");

        return shopService.triggerBizShop(id);
    }

    @ApiOperation(value = "添加商铺(需授权)",
            notes = "pictureList中元素为map,map有3个key," +
                    "shopPictureName(商铺图片名称),shopPicturePath(商铺图片路径),shopPictureSize(商铺图片大小)")
    @PostMapping("/bizShop")
    public Result addBizShop(@RequestBody ShopParam param) {

        Assert.notNull(param, "参数不能为空");

        BizShop bizShop = param.bizShop;

        Assert.notNull(bizShop, "商铺数据不能为空");

        Assert.notNull(bizShop.getShopLogoPath(), "商铺logo不能为空");

        List<Map<String, Object>> pictureList = param.pictureList;

        Assert.notNull(pictureList, "商铺图片集合不能为空");

        pictureList.forEach(item -> Assert.notNull(item.get("shopPicturePath"), "商铺图片不能为空"));

        return shopService.addBizShop(bizShop, pictureList);

    }
}

//商铺+商铺图片数据封装
class ShopParam {

    public BizShop bizShop;

    public List<Map<String, Object>> pictureList;
}
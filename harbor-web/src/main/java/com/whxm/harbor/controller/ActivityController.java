package com.whxm.harbor.controller;

import com.whxm.harbor.annotation.MyApiResponses;
import com.whxm.harbor.conf.FtpConfig;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.ftp.FtpSession;
import com.whxm.harbor.service.ActivityService;
import com.whxm.harbor.bean.*;
import com.whxm.harbor.conf.FileDir;
import com.whxm.harbor.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(description = "活动服务")
@RestController
@RequestMapping("/activity")
@MyApiResponses
public class ActivityController {

    private final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private FileDir fileDir;

    @ApiOperation("终端获取全部活动数据")
    @GetMapping("/activities")
    public ResultMap<String, Object> getActivities() {

        ResultMap<String, Object> ret = new ResultMap<>(2);

        try {
            List<BizActivity> list = activityService.getBizActivityList();

            ret.build("data", list);

            ret = list.isEmpty() ? ret.build("success", false) : ret.build("success", true);

        } catch (Exception e) {

            logger.error("活动列表 获取报错", e);

            ret.build("data", new Object[]{}).build("success", false);
        }

        return ret;
    }


    @ApiOperation("上传logo")
    @PostMapping("/logo")
    public Result uploadLogo(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        return FileUtils.upload(file, request);
    }

    //==========================以下均被拦截============================

    @ApiOperation("获取活动列表(需授权)")
    @GetMapping("/bizActivities")
    public Result getBizActivities(PageQO<BizActivity> pageQO, BizActivity condition) {
        Result ret = null;

        PageVO<BizActivity> pageVO = null;

        try {
            pageQO.setCondition(condition);

            pageVO = activityService.getBizActivityList(pageQO);

            ret = new Result(pageVO);

        } catch (Exception e) {

            logger.error("活动列表 获取报错", e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "活动列表 获取报错", pageQO);
        }

        return ret;
    }

    @ApiOperation("获取活动(需授权)")
    @GetMapping("/bizActivity/{ID}")
    public Result getBizActivity(
            @ApiParam(name = "ID", value = "活动的ID", required = true)
            @PathVariable("ID") Integer activityId
    ) {
        Result ret = null;
        BizActivity activity = null;
        try {
            activity = activityService.getBizActivity(activityId);

            ret = new Result(activity);

        } catch (Exception e) {

            logger.error("ID为{}的活动数据 获取报错", activityId, e);

            ret = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + activityId + "的活动数据 获取报错", Constant.NO_DATA);
        }

        return ret;
    }

    @ApiOperation("修改活动(需授权)")
    @PutMapping("/bizActivity")
    public Result updateBizActivity(@RequestBody BizActivity bizActivity) {
        Result result = null;
        try {

            result = activityService.updateBizActivity(bizActivity);
        } catch (Exception e) {

            logger.error("活动数据 修改报错", e);

            result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "活动数据 修改报错", bizActivity);
        }
        return result;
    }

    @ApiOperation("删除活动(需授权)")
    @DeleteMapping("/bizActivity")
    public Result delBizActivity(
            @ApiParam(name = "ID", value = "活动的ID", required = true)
                    Integer id
    ) {
        Assert.notNull(id, "活动ID不能为空");

        Result result = null;

        try {
            result = activityService.deleteBizActivity(id);

        } catch (Exception e) {

            logger.error("ID为{}的活动数据 删除报错", id, e);

            result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ID为" + id + "的活动数据 删除报错", Constant.NO_DATA);
        }
        return result;
    }

    @Autowired
    private FtpConfig ftpConfig;

    @ApiOperation("添加活动(需授权)")
    @PostMapping(value = "/bizActivity")
    public Result addBizActivity(@RequestBody BizActivity bizActivity, HttpServletRequest request) {

        //---------------------------------------------------------------------------
        FtpSession ftpSession = ftpConfig.openSession(true);

        bizActivity.setActivityLogo(ftpSession.clearLocalFileAfterUpload(bizActivity.getActivityLogo(), fileDir.getActivityLogoDir(), request));

        ftpConfig.closeSession(ftpSession);
        //---------------------------------------------------------------------------

        Result result = null;
        try {
            result = activityService.addBizActivity(bizActivity);

        } catch (Exception e) {

            logger.error("活动数据 添加报错", e);

            result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "活动数据 添加报错", bizActivity);
        }
        return result;
    }
}

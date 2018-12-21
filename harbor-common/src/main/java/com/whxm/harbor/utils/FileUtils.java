package com.whxm.harbor.utils;

import com.whxm.harbor.bean.Result;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Ftibw
 * @Email ftibw@live.com
 * @CreateTime 2018/6/13 21:58
 */

public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    public static void upload(

            MultipartFile file, HttpServletRequest request,

            Map<String, Object> result
    ) {

        String originName = null;

        Long size = null;

        String newName = null;

        String href = null;

        String tempDir = null;

        String orientation = null;

        try {
            //临时文件根目录
            String rootPath = request.getServletContext().getRealPath("/upload");
            //文件名称
            originName = file.getOriginalFilename();
            //文件大小
            size = file.getSize();
            //uuid生成新名称
            newName = StringUtils.createStrUseUUID(originName);
            //文件缓存的临时目录
            tempDir = UUID.randomUUID().toString().replace("-", "");
            //创建缓存目录
            File dirFile = new File(rootPath, tempDir);

            if (!dirFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dirFile.mkdirs();
            }

            href = "upload/" + tempDir + "/" + newName;
            //拷贝文件
            file.transferTo(new File(dirFile.getAbsolutePath(), newName));

            //判断图片横屏还是竖屏
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";

            String url = appUrl + href;

            orientation = FileUtils.getImageOrientation(url);

        } catch (IOException e) {

            logger.error("文件拷贝报错", e);

            throw new RuntimeException();
        }

        if (null != result) {
            result.put("fileOriginName", originName);
            result.put("fileSize", size);
            result.put("fileNewName", newName);
            result.put("filePath", href);
            result.put("tempDirName", tempDir);
            result.put("imageOrientation", orientation);
        }
    }

    public static Result upload(MultipartFile file, HttpServletRequest request) {

        if (!file.isEmpty()) {
            try {
                HashMap<String, Object> map = new HashMap<>(6);

                FileUtils.upload(file, request, map);

                return new Result(map);

            } catch (Exception e) {

                logger.error("文件上传 发生错误", e);

                return new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), "文件上传 发生错误", file);
            }
        } else {
            logger.error("上传的文件是空的");

            return new Result(HttpStatus.NOT_ACCEPTABLE.value(), "上传的文件是空的", file);
        }
    }

    /**
     * 判断图片是横屏还是竖屏
     */
    public static String getImageOrientation(String path) throws IOException {

        URL url = new URL(path);

        /*URLConnection con = url.openConnection();
        //不超时
        con.setConnectTimeout(0);
        //不允许缓存
        con.setUseCaches(false);
        con.setDefaultUseCaches(false);*/

        InputStream is = url.openConnection().getInputStream();

        BufferedImage bufferedImg = ImageIO.read(is);

        int imgWidth = bufferedImg.getWidth();

        int imgHeight = bufferedImg.getHeight();

        return imgWidth > imgHeight ? "横屏" : "竖屏";
    }
}

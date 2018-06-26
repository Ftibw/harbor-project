package com.whxm.harbor.utils;

import com.whxm.harbor.bean.Result;
import com.whxm.harbor.constant.Constant;
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

/**
 * @Author Ftibw
 * @Email ftibw@live.com
 * @CreateTime 2018/6/13 21:58
 */

public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    public static void upload(

            MultipartFile file, HttpServletRequest request,

            String uploadRootDir, Map<String, Object> result
    ) {

        String originName = null;

        Long size = null;

        String newName = null;

        String href = null;

        try {
            //资源服务器项目路径
            //request.getServletContext().getRealPath("/" + uploadRootDir);
            //文件名称
            originName = file.getOriginalFilename();
            //文件大小
            size = file.getSize();
            //uuid生成新名称
            newName = StringUtils.createStrUseUUID(originName);
            //文件保存的目录
            String filePath = Constant.ABSOLUTE_RESOURCE_PATH + "/" + uploadRootDir;
            //分文件夹管理时的文件夹名
            String dirName = StringUtils.createDirName();
            //文件夹
            File dirFile = new File(filePath, dirName);

            if (!dirFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dirFile.mkdirs();
            }

            href = uploadRootDir + "/" + dirName + "/" + newName;
            //拷贝文件
            file.transferTo(new File(filePath + "/" + dirName, newName));

        } catch (IOException e) {

            logger.error("文件拷贝报错", e);

            throw new RuntimeException();
        }

        if (null != result) {
            result.put("fileOriginName", originName);
            result.put("fileSize", size);
            result.put("fileNewName", newName);
            result.put("filePath", href);
        }
    }

    public static Result upload(MultipartFile file, HttpServletRequest request, String uploadRootDir) {

        if (!file.isEmpty()) {
            try {
                HashMap<String, Object> map = new HashMap<>(4);

                FileUtils.upload(file, request, uploadRootDir, map);

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

package com.whxm.harbor.utils;

import com.whxm.harbor.bean.ResultMap;
import com.whxm.harbor.callback.Callback;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.exception.InternalServerException;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author Ftibw
 * @Email ftibw@live.com
 * @CreateTime 2018/6/13 21:58
 */

public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    public static <R> R upload(
            MultipartFile file,
            Callback<Map<String, Object>, R> callback) {

        Assert.notNull(file, "文件不能为空");

        String originName = null;

        Long size = null;

        String newName = null;

        String href = null;

        try {
            //文件名称
            originName = file.getOriginalFilename();
            //文件大小
            size = file.getSize();
            //uuid生成新名称
            newName = StringUtils.createStrUseUUID(originName);
            //文件保存的绝对目录 = 资源服务器项目路径+项目中文件保存根目录
            String uploadRootDirectory = Constant.RESOURCE_ABSOLUTE_DIRECTORY_PATH + File.separator + Constant.PICTURE_UPLOAD_ROOT_DIRECTORY;
            //分文件夹管理时的文件夹名
            String dateDirectoryName = StringUtils.createDirName();
            //文件夹
            File dirFile = new File(uploadRootDirectory, dateDirectoryName);

            if (!dirFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dirFile.mkdirs();
            }

            href = Constant.PICTURE_UPLOAD_ROOT_DIRECTORY + File.separator + dateDirectoryName + File.separator + newName;

            File uploadedFile = new File(uploadRootDirectory + File.separator + dateDirectoryName, newName);
            //拷贝文件
            file.transferTo(uploadedFile);

            String imageOrientation = getImageOrientation(uploadedFile);

            return callback.call(new ResultMap<String, Object>(5)
                    .build("fileOriginName", originName)
                    .build("fileNewName", newName)
                    .build("fileSize", size)
                    .build("filePath", href)
                    .build("imageOrientation", imageOrientation)
            );
        } catch (IOException e) {

            logger.error("文件拷贝异常", e);

            throw new InternalServerException();
        }
    }

    /**
     * 判断图片是横屏还是竖屏
     */
    public static String getImageOrientation(String path) {

        try {
            BufferedImage bufferedImg = null;

            URL url = new URL(path);

            /*URLConnection con = url.openConnection();
            //不超时
            con.setConnectTimeout(0);
            //不允许缓存
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);*/

            InputStream is = url.openConnection().getInputStream();

            bufferedImg = ImageIO.read(is);

            int imgWidth = bufferedImg.getWidth();

            int imgHeight = bufferedImg.getHeight();

            return imgWidth > imgHeight ? Constant.HORIZONTAL_SCREEN_PICTURE : Constant.VERTICAL_SCREEN_PICTURE;
        } catch (IOException e) {

            logger.error("图片资源读取异常", e);

            throw new InternalServerException();
        }
    }

    /**
     * 判断图片是横屏还是竖屏
     */
    public static String getImageOrientation(File file) {

        try {
            BufferedImage bufferedImg = ImageIO.read(file);

            int imgWidth = bufferedImg.getWidth();

            int imgHeight = bufferedImg.getHeight();

            return imgWidth > imgHeight ? Constant.HORIZONTAL_SCREEN_PICTURE : Constant.VERTICAL_SCREEN_PICTURE;
        } catch (IOException e) {

            logger.error("图片资源读取异常", e);

            throw new InternalServerException();
        }

    }

    //解决异常:Numbers of source Raster bands and source color space components do not match
    //reader: com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader@233c0b17
    public static void main(String[] args) {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
        while (readers.hasNext()) {
            System.out.println("reader: " + readers.next());
        }
    }
}

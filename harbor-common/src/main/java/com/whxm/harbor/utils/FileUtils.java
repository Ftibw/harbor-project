package com.whxm.harbor.utils;

import com.whxm.harbor.callback.Callback;
import com.whxm.harbor.constant.Constant;
import com.whxm.harbor.exception.InternalServerException;
import com.whxm.harbor.exception.ParameterInvalidException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author Ftibw
 * @Email ftibw@live.com
 * @CreateTime 2018/6/13 21:58
 */
@Component
public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    private static String absoluteResourceDirectory;
    private static String relativePictureUploadDirectory;
    private static String horizontalScreenPicture;
    private static String verticalScreenPicture;
    private static String fileOriginName;
    private static String fileNewName;
    private static String fileSize;
    private static String filePath;
    private static String imageOrient;

    @Value("${file.absolute-resource-dir}")
    public void setAbsoluteResourceDirectory(String path) {

        absoluteResourceDirectory = path;
    }

    @Value("${file.relative-picture-upload-dir}")
    public void setRelativePictureUploadDirectory(String path) {

        relativePictureUploadDirectory = path;
    }

    @Value("${file.horizontal-screen-picture}")
    public static void setHorizontalScreenPicture(String horizontalScreenPicture) {
        FileUtils.horizontalScreenPicture = horizontalScreenPicture;
    }

    @Value("${file.vertical-screen-picture}")
    public static void setVerticalScreenPicture(String verticalScreenPicture) {
        FileUtils.verticalScreenPicture = verticalScreenPicture;
    }

    @Value("${file.file-origin-name}")
    public void setFileOriginName(String originName) {
        fileOriginName = originName;
    }

    @Value("${file.file-new-name}")
    public void setFileNewName(String newName) {
        fileNewName = newName;
    }

    @Value("${file.file-size}")
    public void setFileSize(String size) {
        fileSize = size;
    }

    @Value("${file.file-path}")
    public void setFilePath(String path) {
        filePath = path;
    }

    @Value("${file.image-orientation}")
    public void setImageOrient(String imageOrientation) {
        imageOrient = imageOrientation;
    }

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
            //10MB = 10485760BIT
            if (size >= 10485760)
                throw new ParameterInvalidException("文件大小超出了10MB,上传失败");

            //uuid生成新名称
            newName = StringUtils.createStrUseUUID(originName);
            //文件保存的绝对目录 = 资源服务器项目路径+项目中文件保存根目录
            String uploadRootDirectory = absoluteResourceDirectory + Constant.DEFAULT_FILE_SEPARATOR + relativePictureUploadDirectory;
            //分文件夹管理时的文件夹名
            String dateDirectoryName = StringUtils.createDirName().replace("-", "");
            //文件夹
            File dirFile = new File(uploadRootDirectory, dateDirectoryName);

            if (!dirFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dirFile.mkdirs();
            }

            href = relativePictureUploadDirectory + Constant.DEFAULT_FILE_SEPARATOR + dateDirectoryName + Constant.DEFAULT_FILE_SEPARATOR + newName;
            
            File uploadedFile = new File(uploadRootDirectory + Constant.DEFAULT_FILE_SEPARATOR + dateDirectoryName, newName);
            //拷贝文件
            file.transferTo(uploadedFile);

            String imageOrientation = getImageOrientation(uploadedFile);

            Map<String, Object> map = new HashMap<>();

            map.put(fileOriginName, originName);
            map.put(fileNewName, newName);
            map.put(fileSize, size);
            map.put(filePath, href);
            map.put(imageOrient, imageOrientation);

            return callback.call(map);

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

            if (bufferedImg == null) {
                return "此文件不是图片文件";
            }

            int imgWidth = bufferedImg.getWidth();

            int imgHeight = bufferedImg.getHeight();

            return imgWidth > imgHeight ? horizontalScreenPicture : verticalScreenPicture;
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

            if (bufferedImg == null) {
                return "此文件不是图片文件";
            }

            int imgWidth = bufferedImg.getWidth();

            int imgHeight = bufferedImg.getHeight();

            return imgWidth > imgHeight ? horizontalScreenPicture : verticalScreenPicture;
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

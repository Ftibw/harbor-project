package com.whxm.harbor.ftp;

import org.apache.commons.net.ftp.*;
import org.springframework.web.context.ContextLoader;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class FtpSession {

    private FTPClient ftp;
    private boolean is_connected;

    public FtpSession() {
        ftp = new FTPClient();
        is_connected = false;
    }

    public FtpSession(int defaultTimeoutSecond, int connectTimeoutSecond, int dataTimeoutSecond) {
        ftp = new FTPClient();
        is_connected = false;

        ftp.setDefaultTimeout(defaultTimeoutSecond * 1000);
        ftp.setConnectTimeout(connectTimeoutSecond * 1000);
        ftp.setDataTimeout(dataTimeoutSecond * 1000);
    }

    /**
     * Connects to FTP server.
     *
     * @param host       FTP server address or name
     * @param port       FTP server port
     * @param user       user name
     * @param password   user password
     * @param isTextMode text / binary mode switch
     * @throws IOException on I/O errors
     */
    public void connect(String host, int port, String user, String password, boolean isTextMode) throws IOException {
        // Connect to server.
        try {
            ftp.connect(host, port);
        } catch (UnknownHostException ex) {
            throw new IOException("Can't find FTP server '" + host + "'");
        }

        // Check rsponse after connection attempt.
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            disconnect();
            throw new IOException("Can't connect to server '" + host + "'");
        }

        // Login.
        if (!ftp.login(user, password)) {
            is_connected = false;
            disconnect();
            throw new IOException("Can't login to server '" + host + "'");
        } else {
            is_connected = true;
        }

        // Set data transfer mode.
        if (isTextMode) {
            ftp.setFileType(FTP.ASCII_FILE_TYPE);
        } else {
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        }
    }

    /**
     * 相对pwd的文件夹名(支持多级创建)
     *
     * @param pwd     当前目录
     * @param dirName 文件夹名
     * @return 创建后的文件夹绝对路径
     */
    public String mkdir(String pwd, String dirName) throws IOException {

        StringBuilder pathName = new StringBuilder(pwd);

        StringTokenizer s = new StringTokenizer(dirName, "/");

        s.countTokens();

        while (s.hasMoreElements()) {

            pathName.append("/").append(s.nextElement());

            //没有-p参数,无法跨级创建文件夹...不过优点是仅不存在的目录才创建
            ftp.doCommand(FTPCmd.MKD.name(), pathName.toString());

        }
        return pathName.toString();
    }

    /**
     * Downloads the file from the FTP server.
     *
     * @param ftpFileName server file name (with absolute path)
     * @param localFile   local file to download into
     * @throws IOException on I/O errors
     */
    public void download(String ftpFileName, File localFile) throws IOException {
        // Download.
        OutputStream out = null;
        try {
            // Use passive mode to pass firewalls.
            ftp.enterLocalPassiveMode();

            // Get file info.
            FTPFile[] fileInfoArray = ftp.listFiles(ftpFileName);
            if (fileInfoArray == null) {
                throw new FileNotFoundException("File " + ftpFileName + " was not found on FTP server.");
            }

            // Check file size.
            FTPFile fileInfo = fileInfoArray[0];
            long size = fileInfo.getSize();
            if (size > Integer.MAX_VALUE) {
                throw new IOException("File " + ftpFileName + " is too large.");
            }

            // Download file.
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            if (!ftp.retrieveFile(ftpFileName, out)) {
                throw new IOException("Error loading file " + ftpFileName + " from FTP server. Check FTP permissions and path.");
            }
            ftp.getReply();
            out.flush();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Removes the file from the FTP server.
     *
     * @param ftpFileName server file name (with absolute path)
     * @throws IOException on I/O errors
     */
    public void remove(String ftpFileName) throws IOException {
        if (!ftp.deleteFile(ftpFileName)) {
            throw new IOException("Can't remove file '" + ftpFileName + "' from FTP server.");
        }
    }

    /**
     * Lists the files in the given FTP directory.
     *
     * @param filePath absolute path on the server
     * @return files relative names list
     * @throws IOException on I/O errors
     */
    public List<String> list(String filePath) throws IOException {
        List<String> fileList = new ArrayList<String>();

        // Use passive mode to pass firewalls.
        ftp.enterLocalPassiveMode();

        FTPFile[] ftpFiles = ftp.listFiles(filePath);
        int size = (ftpFiles == null) ? 0 : ftpFiles.length;
        for (int i = 0; i < size; i++) {
            FTPFile ftpFile = ftpFiles[i];
            if (ftpFile.isFile()) {
                fileList.add(ftpFile.getName());
            }
        }

        return fileList;
    }

    /**
     * Disconnects from the FTP server
     *
     * @throws IOException on I/O errors
     */
    public void disconnect() throws IOException {

        if (ftp.isConnected()) {
            ftp.logout();
            ftp.disconnect();
            is_connected = false;
        }
    }

    /**
     * Test coonection to ftp server
     *
     * @return true, if connected
     */
    public boolean isConnected() {
        return is_connected;
    }

    /**
     * Get current directory on ftp server
     *
     * @return current directory
     */
    public String getWorkingDirectory() {
        if (!is_connected) {
            return "";
        }

        try {
            return ftp.printWorkingDirectory();
        } catch (IOException ignored) {
            return "";
        }
    }

    /**
     * Get file from ftp server into given output stream
     *
     * @param ftpFileName file name on ftp server
     * @param out         OutputStream
     * @throws IOException
     */
    public void getFile(String ftpFileName, OutputStream out) throws IOException {
        try {
            // Use passive mode to pass firewalls.
            ftp.enterLocalPassiveMode();

            // Get file info.
            FTPFile[] fileInfoArray = ftp.listFiles(ftpFileName);
            if (fileInfoArray == null) {
                throw new FileNotFoundException("File '" + ftpFileName + "' was not found on FTP server.");
            }

            // Check file size.
            FTPFile fileInfo = fileInfoArray[0];
            long size = fileInfo.getSize();
            if (size > Integer.MAX_VALUE) {
                throw new IOException("File '" + ftpFileName + "' is too large.");
            }

            // Download file.
            if (!ftp.retrieveFile(ftpFileName, out)) {
                throw new IOException("Error loading file '" + ftpFileName + "' from FTP server. Check FTP permissions and path.");
            }

            out.flush();

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Put file on ftp server from given input stream
     *
     * @param ftpFileName file name on ftp server
     * @param in          InputStream
     * @throws IOException
     */
    public void putFile(String ftpFileName, InputStream in) throws IOException {
        try {
            // Use passive mode to pass firewalls.
            ftp.enterLocalPassiveMode();

            if (!ftp.storeFile(ftpFileName, in)) {
                throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions and path.");
            }
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Uploads the file to the FTP server.
     *
     * @param ftpFileDir server upload directory (with absolute path)
     * @param localFile  local file to upload
     * @throws IOException on I/O errors
     */
    public String upload(String ftpFileDir, File localFile) throws IOException {
        // Upload.
        InputStream in = null;
        // File check.
        if (!localFile.exists()) {
            throw new IOException("Can't upload '" + localFile.getAbsolutePath() + "'. This file doesn't exist.");
        }
        // Use passive mode to pass firewalls.
        ftp.enterLocalPassiveMode();

        try {
            in = new BufferedInputStream(new FileInputStream(localFile));

            String fileName = localFile.getName();

            if (!ftp.storeFile(ftpFileDir + "/" + fileName, in)) {
                throw new IOException("Can't upload file to directory '" + ftpFileDir + "' on FTP server. Check FTP permissions and path.");
            }
            //ftp.getReply();
            return ftpFileDir + "/" + fileName;

        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException ignored) {
            }
        }

    }

    /**
     * 上传文件到ftp服务器同时清除本地缓存文件
     *
     * @param relativeLocalFilePath    本地缓存文件的相对路径(相对于web应用的realPath,结构:web应用的绝对路径--->缓存文件相对web应用的路径)
     * @param relativeFtpFileDirectory ftp文件的分类存储目录(相对于ftp用户的主目录,结构:主目录--->分类存储目录--->分时存储目录)
     * @return ftp服务器中上传文件的相对路径(相对于ftp用户的主目录路径, 结构 : 主目录路径 - - - > 文件相对主目录的路径)
     */
    public String clearLocalFileAfterUpload(String relativeLocalFilePath, String relativeFtpFileDirectory, HttpServletRequest request) {

        String rootPath = request.getServletContext().getRealPath("/");

        String filePath = rootPath + relativeLocalFilePath;

        File localFile = new File(filePath);

        try {
            String pwd = this.getWorkingDirectory();

            String ftpDirPath = this.mkdir(pwd, String.format("%s/%tF", relativeFtpFileDirectory, new Date()));

            String ftpFilePath = this.upload(ftpDirPath, localFile);

            String ftpFileRelativePath = ftpFilePath.replaceAll(String.format("^%s/(.*)$", pwd), "$1");

            return ftpFileRelativePath;

        } catch (IOException e) {

            e.printStackTrace();

            return null;
        } finally {
            //noinspection ResultOfMethodCallIgnored
            localFile.delete();
        }
    }

    public static void main(String[] args) {
        Date now = new Date();
        String format = String.format("%tF %tT", now, now);
        String format1 = String.format("%tF", now);
        String format2 = String.format("%s/%tF", "relativeFtpFileDirectory", new Date());
        String format3 = String.format("^%s/(.*)$", "pwd");
        System.out.println(format);
        System.out.println(format1);
        System.out.println(format2);
        System.out.println(format3);
    }
}
package com.whxm.harbor.utils;

import com.whxm.harbor.bean.FfpFileInfo;
import org.apache.commons.net.ftp.*;
import org.springframework.util.StringUtils;

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
import java.util.List;
import java.util.StringTokenizer;

public class FtpUtils {

    private FTPClient ftp;
    private boolean is_connected;

    public FtpUtils() {
        ftp = new FTPClient();
        is_connected = false;
    }

    public FtpUtils(int defaultTimeoutSecond, int connectTimeoutSecond, int dataTimeoutSecond) {
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
     * @param dirName 文件夹名
     * @return 创建后的文件夹绝对路径
     */
    public String mkdir(String dirName) throws IOException {

        boolean isSuccess = true;

        StringBuilder pathName = new StringBuilder(this.getWorkingDirectory());

        StringTokenizer s = new StringTokenizer(dirName, "/");

        s.countTokens();

        while (s.hasMoreElements()) {

            pathName.append("/").append(s.nextElement());

            //没有-p参数,无法跨级创建文件夹...不过优点是仅不存在的目录才创建
            isSuccess = ftp.doCommand(FTPCmd.MKD.name(), pathName.toString());
        }
        return isSuccess ? pathName.toString() : null;
    }

    /**
     * Uploads the file to the FTP server.
     *
     * @param ftpFileName server file name (with absolute path)
     * @param localFile   local file to upload
     * @throws IOException on I/O errors
     */
    public void upload(String ftpFileName, File localFile) throws IOException {
        // File check.
        if (!localFile.exists()) {
            throw new IOException("Can't upload '" + localFile.getAbsolutePath() + "'. This file doesn't exist.");
        }

        // Upload.
        InputStream in = null;
        try {

            // Use passive mode to pass firewalls.
            ftp.enterLocalPassiveMode();

            in = new BufferedInputStream(new FileInputStream(localFile));
            if (!ftp.storeFile(ftpFileName, in)) {
                throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions and path.");
            }

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
     * Sends an FTP Server site specific command
     *
     * @param args site command arguments
     * @throws IOException on I/O errors
     */
    public void sendSiteCommand(String args) throws IOException {
        if (ftp.isConnected()) {
            try {
                ftp.sendSiteCommand(args);
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Disconnects from the FTP server
     *
     * @throws IOException on I/O errors
     */
    public void disconnect() throws IOException {

        if (ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
                is_connected = false;
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Makes the full name of the file on the FTP server by joining its path and
     * the local file name.
     *
     * @param ftpPath   file path on the server
     * @param localFile local file
     * @return full name of the file on the FTP server
     */
    public String makeFTPFileName(String ftpPath, File localFile) {
        if (!StringUtils.hasText(ftpPath)) {
            return localFile.getName();
        } else {
            String path = ftpPath.trim();
            if (path.charAt(path.length() - 1) != '/') {
                path = path + "/";
            }

            return path + localFile.getName();
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
        }

        return "";
    }

    /**
     * Set working directory on ftp server
     *
     * @param dir new working directory
     * @return true, if working directory changed
     */
    public boolean setWorkingDirectory(String dir) {
        if (!is_connected) {
            return false;
        }

        try {
            return ftp.changeWorkingDirectory(dir);
        } catch (IOException e) {
        }

        return false;
    }

    /**
     * Change working directory on ftp server to parent directory
     *
     * @return true, if working directory changed
     */
    public boolean setParentDirectory() {
        if (!is_connected) {
            return false;
        }

        try {
            return ftp.changeToParentDirectory();
        } catch (IOException e) {
        }

        return false;
    }

    /**
     * Get parent directory name on ftp server
     *
     * @return parent directory
     */
    public String getParentDirectory() {
        if (!is_connected) {
            return "";
        }

        String w = getWorkingDirectory();
        setParentDirectory();
        String p = getWorkingDirectory();
        setWorkingDirectory(w);

        return p;
    }

    /**
     * Get directory contents on ftp server
     *
     * @param filePath directory
     * @return list of FTPFileInfo structures
     * @throws IOException
     */
    public List<FfpFileInfo> listFiles(String filePath) throws IOException {
        List<FfpFileInfo> fileList = new ArrayList<FfpFileInfo>();

        // Use passive mode to pass firewalls.
        ftp.enterLocalPassiveMode();
        FTPFile[] ftpFiles = ftp.listFiles(filePath);
        int size = (ftpFiles == null) ? 0 : ftpFiles.length;
        for (int i = 0; i < size; i++) {
            FTPFile ftpFile = ftpFiles[i];
            FfpFileInfo fi = new FfpFileInfo();
            fi.setName(ftpFile.getName());
            fi.setSize(ftpFile.getSize());
            fi.setTimestamp(ftpFile.getTimestamp());
            fi.setType(ftpFile.isDirectory());
            fileList.add(fi);
        }

        return fileList;
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
}
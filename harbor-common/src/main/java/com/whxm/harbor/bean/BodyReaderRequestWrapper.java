package com.whxm.harbor.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class BodyReaderRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body; // 报文

    private final static int BUFFER_SIZE = 4096;

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public BodyReaderRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        //读取输入流
        this.body = readBytes(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        //再封装数据
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                //在这里配置读取监听  可以使用这个方法限制读取次数
            }
        };
    }

    private static byte[] readBytes(InputStream in) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buff = new byte[BUFFER_SIZE];

        int size;

        while ((size = in.read(buff)) != -1) {

            baos.write(buff, 0, size);
        }

        return baos.toByteArray();
    }

} 
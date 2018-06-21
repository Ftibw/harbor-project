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

    /**
     * step1:请求经过过滤器的POST+json验证后将调用这个构造方法创建BodyReaderRequestWrapper实例
     */
    public BodyReaderRequestWrapper(HttpServletRequest request) throws IOException {
        /**step2:两次向父类构造,将request对象注入ServletRequestWrapper中,用ServletRequest接受*/
        super(request);
        //读取输入流并缓存到body中
        this.body = readBytes(request.getInputStream());
    }

    /**
     * step4:自定义的过滤器放行后,此对象在其他地方被调用,并获取流时将实际调用此方法(子类重新了改方法),因为request内容被body缓存了,每次去读都是拷贝body的数据,因此可以反复的读
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        /**将body中缓存的字节数组放入字节数组读入流,供调用者读取*/
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

    /**
     * step3:将request中的输入流inputStream,用字节数组输出流写入内存,再读取字节数组保存到body属性中
     */
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
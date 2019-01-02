package com.github.printparam;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author qinkangdeid
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream buffer;
    private ServletOutputStream out;
    private PrintWriter writer;

    ResponseWrapper(HttpServletResponse resp) {
        super(resp);
        buffer = new ByteArrayOutputStream();
        out = new WrapperOutputStream(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return out;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    byte[] getResponseData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    private class WrapperOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos;

        WrapperOutputStream(ByteArrayOutputStream stream) {
            bos = stream;
        }

        @Override
        public void write(int b) {
            bos.write(b);
        }

        @Override
        public void write(byte[] b) {
            bos.write(b, 0, b.length);
        }

        /**
         * This method can be used to determine if data can be written without blocking.
         *
         * @return <code>true</code> if a write to this <code>ServletOutputStream</code>
         * will succeed, otherwise returns <code>false</code>.
         * @since Servlet 3.1
         */
        @Override
        public boolean isReady() {
            return false;
        }

        /**
         * Instructs the <code>ServletOutputStream</code> to invoke the provided
         * {@link WriteListener} when it is possible to write
         *
         * @param writeListener the {@link WriteListener} that should be notified
         *                      when it's possible to write
         * @throws IllegalStateException if one of the following conditions is true
         *                               <ul>
         *                               <li>the associated request is neither upgraded nor the async started
         *                               <li>setWriteListener is called more than once within the scope of the same request.
         *                               </ul>
         * @throws NullPointerException  if writeListener is null
         * @since Servlet 3.1
         */
        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}

package ee.sk.mid.rest;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LoggingOutputStream extends FilterOutputStream {

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public LoggingOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        super.write(b);
        byteArrayOutputStream.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        byteArrayOutputStream.write(b);
    }

    public byte[] getBytes() {
        return byteArrayOutputStream.toByteArray();
    }
}
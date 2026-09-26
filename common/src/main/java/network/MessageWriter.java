package network;

import model.Request;
import model.Response;
import util.JsonUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class MessageWriter implements AutoCloseable {
    private final BufferedWriter writer;
    private final Object writeLock = new Object();

    public MessageWriter(OutputStream outputStream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    }

    public void writeRawMessage(String message) throws IOException {
        if (message == null) return;
        synchronized (writeLock) {
            writer.write(message);
            writer.write("\n");
            writer.flush();
        }
    }

    public void writeRequest(Request request) throws IOException {
        if (request == null) return;
        String json = JsonUtil.toJson(request);
        writeRawMessage(json);
    }

    public void writeResponse(Response response) throws IOException {
        if (response == null) return;
        String json = JsonUtil.toJson(response);
        writeRawMessage(json);
    }

    @Override
    public void close() throws IOException {
        synchronized (writeLock) {
            writer.close();
        }
    }
}

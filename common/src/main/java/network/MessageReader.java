package network;

import model.Request;
import model.Response;
import util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageReader implements AutoCloseable {
    private final BufferedReader reader;

    public MessageReader(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public String readRawMessage() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        return line.trim();
    }

    public Request readRequest() throws IOException {
        String raw = readRawMessage();
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        return JsonUtil.fromJson(raw, Request.class);
    }

    public Response readResponse() throws IOException {
        String raw = readRawMessage();
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        return JsonUtil.fromJson(raw, Response.class);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}

package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

public class RequestProcessor {
    public static void requestProcess(Socket socket, ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers){
        try(final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final var out = new BufferedOutputStream(socket.getOutputStream())){

            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");
            if (parts.length != 3){
                responseContent(out, 404, "Bad Request");
                return;
            }

            String method = parts[0];
            if(method == null || method.isBlank()){
                responseContent(out, 404, "Bad Request");
                return;
            }
            final var path = parts[1];
            Request request = new Request(method, path);

            if(!handlers.containsKey(request.getMethod())){
                responseContent(out, 404, "Bad Request");
                return;
            }

            Map<String, Handler> handlerMap = handlers.get(request.getMethod());
            String requestPath = request.getPath();
            if (handlerMap.containsKey(requestPath)){
                Handler handler = handlerMap.get(requestPath);
                handler.handle(request, out);
            }else{
                responseContent(out, 404, "Not Found");
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void responseContent(BufferedOutputStream out, int responseCode, String responseStatus) throws IOException {
        out.write((
                "HTTP/1.1 " + responseCode + " " + responseStatus + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
                ).getBytes());
        out.flush();
    }

    public static void defaultHandler(BufferedOutputStream out, String path) throws IOException {
        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            return;
        }

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }
}

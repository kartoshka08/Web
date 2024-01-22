package org.example;

public class Main {
    protected static final int PORT = 9999;
    public static int threadsNumber = 64;

    public static void main(String[] args) {
        final var server = new Server(PORT, threadsNumber);

//        server.addHandler("GET", "/messages", ((request, restStream) ->
//                RequestProcessor.responseContent(restStream, 404, "Not Found")));///
//
//        server.addHandler("POST", "/messages", ((request, restStream) ->
//                RequestProcessor.responseContent(restStream, 503, "Unvailable Service")));///
//
//        server.addHandler("GET", "/messages", ((request, restStream) ->
//                RequestProcessor.defaultHandler(restStream, "spring.svg")));///

        server.start();
    }
}
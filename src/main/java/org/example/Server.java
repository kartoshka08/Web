package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;


public class Server {
    private final int port;
    private final ExecutorService executorService;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();
    private Socket socket;


    public Server(int port, int threadsNumber) {
        this.port = port;
        executorService = Executors.newFixedThreadPool(threadsNumber);
    }

    public void start(){
        try(final var serverSocket = new ServerSocket(port)){
            System.out.println("Server is working");
            while (true){
                socket = serverSocket.accept();
                executorService.submit(() -> RequestProcessor.requestProcess(socket, handlers));
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            executorService.shutdown();
        }
    }

    void addHandler(String method, String path, Handler handler){
        if(!handlers.containsKey(method)){
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);
    }
}
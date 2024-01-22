package org.example;

public class Request {
    private final String method;
    private final String path;

    public Request(String requestMeyhod, String requestPath){
        this.method = requestMeyhod;
        this.path = requestPath;
    }
    public String getMethod(){return method;}
    public String getPath(){return path;}

}
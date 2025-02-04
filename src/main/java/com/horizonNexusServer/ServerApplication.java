package com.horizonNexusServer;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerApplication {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("Server started on port 80");
        System.out.println("Waiting for connections...");


    }
}

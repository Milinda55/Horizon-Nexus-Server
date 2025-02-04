package com.horizonNexusServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("Server started on port 80");
        System.out.println("Waiting for connections...");

        while (true) {
            Socket localsocket = serverSocket.accept();
            System.out.println("Accepted connection from " + localsocket.getRemoteSocketAddress());

            new Thread(() -> {
                try {
                    InputStream inputStream = localsocket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader br = new BufferedReader(isr);

                    String commandLine = br.readLine();
                    String[] s = commandLine.split(" ");
                    String command = s[0];
                    String path = s[1];
                    System.out.println(command + " " + path);

                    String line;
                    String host;
                    while ((line = br.readLine())!= null && !line.isEmpty()){
                        if(line.split(":")[0].strip().equalsIgnoreCase("host")){
                            host = line.split(":")[1].strip();
                            System.out.println(host);
                        }
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

    }
}

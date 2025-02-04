package com.horizonNexusServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

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
                    String host = null;
                    while ((line = br.readLine())!= null && !line.isEmpty()){
                        if(line.split(":")[0].strip().equalsIgnoreCase("host")){
                            host = line.split(":")[1].strip();
                            System.out.println(host);
                        }
                    }

                    OutputStream os = localsocket.getOutputStream();

                    String head = "";
                    Path index=null;
                    String content = "";

                    if(!command.equalsIgnoreCase("get")) {
                        head = """
                                HTTP/1.1 405 Method Not Allowed
                                Server: Horizon Nexus Server
                                Date:%s
                                Content-Type: text/html
                                
                                """.formatted(LocalDateTime.now());

                        os.write(head.getBytes());
                        os.flush();

                        content = """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                <title>Horizon Nexus Server</title>
                                </head>
                                <body>
                                <h1>500 Bad Request</h1>
                                <h6>copyright (c) Horizon Nexus Server</h6>
                                </body>
                                </html>
                                """;
                        os.write(content.getBytes());
                        os.flush();

                    }

                    else{

                        if(path.equalsIgnoreCase("/")){
                            index = Path.of("http", host, "index.html");

                        }else {
                            index = Path.of("http", host, path);
                        }

                        if(!Files.exists(index)) {
                            head = """
                                    HTTP/1.1 404 Not Found
                                    Server: Horizon Nexus Server
                                    Date:%s
                                    Content-Type: text/html
                                    
                                    """.formatted(LocalDateTime.now());

                            os.write(head.getBytes());
                            os.flush();

                            content = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                    <title>Horizon Nexus Server</title>
                                    </head>
                                    <body>
                                    <h1>404 Not Found index.html</h1>
                                    
                                    </body>
                                    </html>
                                    """;
                            os.write(content.getBytes());
                            os.flush();
                        }
                        else{
                            head = """
                                HTTP/1.1 200 OK
                                Content-Type: %s
                                
                                """.formatted(Files.probeContentType(index));
                            os.write(head.getBytes());
                            os.flush();

                            FileInputStream fis = new FileInputStream(index.toFile());
                            BufferedInputStream bis = new BufferedInputStream(fis);


                            byte[] buffer = new byte[1024];
                            while (bis.read(buffer) != -1) {
                                os.write(buffer);
                                os.flush();
                            }
                            bis.close();


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

package org.example.blocking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        ServerSocket ss= new ServerSocket(1234);
        System.out.println("i'm waiting connection");
        Socket socket = ss.accept();
        System.out.println("client ip "+ socket.getRemoteSocketAddress());
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        System.out.println("i'm waiting data ");
        int nb = is.read();
        System.out.println("i got "+nb+" from client ip :"+socket.getRemoteSocketAddress());
        int res = nb *2 ;
        os.write(res);
        socket.close();
    }
}

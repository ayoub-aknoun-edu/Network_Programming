package org.example.blocking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

public class MultiThreadBlockingServer extends Thread {
    int clientId=0;
    public static void main(String[] args) {
        new MultiThreadBlockingServer().start();
    }

    @Override
    public void run() {
        System.out.println("server started on port 1234");
        try {
            ServerSocket ss = new ServerSocket(1234);
            while (true){
                Socket socket = ss.accept();
                ++clientId;
                new Conversation(socket,clientId).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class Conversation extends Thread{

        private Socket socket;
        private int clientId;
        public Conversation(Socket socket,int clientId) {
            this.socket = socket ;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try{
                System.out.println("le client "+clientId+" connected with ip + "+socket.getRemoteSocketAddress());
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os,true);
                pw.println("welcome to my server your are client numer "+clientId);
                String request ;
                while ((request = br.readLine())!=null){
                    System.out.println("client "+clientId+": "+request);
                    pw.write("size : " +request.length());
                }

            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}

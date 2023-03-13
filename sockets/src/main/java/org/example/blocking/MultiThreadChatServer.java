package org.example.blocking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiThreadChatServer extends Thread {
    private List<Conversation> conversationList = new ArrayList<>();
    int clientId=0;
    public static void main(String[] args) {
        new MultiThreadChatServer().start();
    }

    @Override
    public void run() {
        System.out.println("server started on port 1234");
        try {
            ServerSocket ss = new ServerSocket(1234);
            while (true){
                Socket socket = ss.accept();
                ++clientId;
                Conversation conversation = new Conversation(socket, clientId);
                conversationList.add(conversation);
                conversation.start();
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
                    String message;
                    List<Integer> clientTo = new ArrayList<>();
                    if(request.contains("=>")){
                        String []items = request.split("=>");
                        message = items[1];
                        if (items[0].contains(",")){
                            String []ids =items[0].split(",");
                            for (String id : ids){
                                clientTo.add(Integer.parseInt(id.trim()));
                            }
                        }else {
                            String id = items[0];
                            clientTo.add(Integer.parseInt(id.trim()));
                        }
                    }else{
                        clientTo= conversationList.stream().map(client-> client.clientId).collect(Collectors.toList());
                        message=request;
                    }
                    broadcast(message,this,clientTo);
                }

            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }

        public void broadcast(String message,Conversation from,List<Integer> clientTo){

            try {
                for (Conversation conversation : conversationList){
                    if(conversation!=from && clientTo.contains(conversation.clientId)) {
                        Socket socket1 = conversation.socket;
                        OutputStream os = socket1.getOutputStream();
                        PrintWriter pw = new PrintWriter(os, true);
                        pw.println(message);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}

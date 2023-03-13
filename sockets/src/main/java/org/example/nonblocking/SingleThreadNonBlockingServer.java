package org.example.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SingleThreadNonBlockingServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel= ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("0.0.0.0",1234));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            int channelCount = selector.select();
            if(channelCount==0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    handlAccept(selectionKey,selector);
                } else if (selectionKey.isReadable()) {
                    handlReadWrite(selectionKey,selector);            
                }
                iterator.remove();
            }
        }
    }

    private static void handlAccept(SelectionKey selectionKey, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector,SelectionKey.OP_READ);
        System.out.println(String.format("New Connection from %s", socketChannel.getRemoteAddress()));
    }

    private static void handlReadWrite(SelectionKey selectionKey, Selector selector) throws IOException {
        SocketChannel socketChannel =(SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int dataSize = socketChannel.read(byteBuffer);
        if (dataSize==-1 ){
            System.out.println(String.format("the client %s has been deconected ",socketChannel.getRemoteAddress().toString()));
        }
        String request = new String(byteBuffer.array()).trim();
        System.out.println(String.format("new request %s from %s ",request,socketChannel.getRemoteAddress().toString()));
        String response = new StringBuffer(request).reverse().toString().toUpperCase()+"\n";
        ByteBuffer byteBufferResponse = ByteBuffer.allocate(1024);
        byteBufferResponse.put(response.getBytes());
        byteBufferResponse.flip();
        socketChannel.write(byteBufferResponse);
    }


}

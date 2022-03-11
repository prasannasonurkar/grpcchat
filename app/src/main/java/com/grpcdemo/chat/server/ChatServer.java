package com.grpcdemo.chat.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * This is gRPC chat server
 * @author prasanna sonurkar
 */
public class ChatServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(50052).addService(new ChatServiceImpl()).build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Server shutdown requested");
                server.shutdown();
                System.out.println("Server shutdown successfully");

            }));
        server.awaitTermination();

    }
    
}

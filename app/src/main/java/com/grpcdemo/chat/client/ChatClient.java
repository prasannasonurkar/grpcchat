package com.grpcdemo.chat.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.proto.chat.ChatRequest;
import com.proto.chat.ChatResponse;
import com.proto.chat.ChatServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * This is a gRPC chat client
 * @author prasanna sonurkar
 */
public class ChatClient {

    public static void main(String[] args) {
        ChatClient client = new ChatClient();

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();

    client.doUnaryCall(channel);

    client.doServerStreamingCall(channel);
    
    client.doClientStreamingCall(channel);

    client.doBiDiStreamingCall(channel);
    
    }

    /**
     * Unary gRPC chat
     * @param channel
     */
    private void doUnaryCall(ManagedChannel channel) {
        ChatServiceGrpc.ChatServiceBlockingStub stub =   ChatServiceGrpc.newBlockingStub(channel);

        //Unary gRPC call
        ChatRequest request = ChatRequest.newBuilder()
                    .setFirstName("charles")
                    .setLastName("josh")
                    .setContent("Hello one time").build();

        ChatResponse response = stub.unaryChat(request);
        System.out.println("Received respone to unary gRPC request:" + response);

    }

    /**
     * Server streaming gRPC chat
     * @param channel
     */
    private void doServerStreamingCall(ManagedChannel channel) {
        ChatServiceGrpc.ChatServiceBlockingStub stub =   ChatServiceGrpc.newBlockingStub(channel); 

        //Server streaming gRPC call
        ChatRequest request = ChatRequest.newBuilder()
                .setFirstName("charles")
                .setLastName("josh")
                .setContent("Hello one time").build();

        System.out.println("Received response to server streaming gRPC request");
        stub.serverStreamingChat(request).forEachRemaining(ChatResponse -> {System.out.println(ChatResponse.getResult());} );

    }
    
    /**
     * Client streaming gRPC chat
     * @param channel
     */
    private void doClientStreamingCall(ManagedChannel channel) {
        ChatServiceGrpc.ChatServiceStub stub =   ChatServiceGrpc.newStub(channel); 

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ChatRequest> requestObserver = stub.clientStreamingChat(new StreamObserver<ChatResponse>() {
            @Override
            public void onNext(ChatResponse value) {
                System.out.println("Received response for client streaming gRPC request: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //Error handling
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });


        //Client streaming gRPC call
        requestObserver.onNext(ChatRequest.newBuilder()
                .setFirstName("charles")
                .setLastName("josh")
                .setContent("Hello one time").build());

        requestObserver.onNext(ChatRequest.newBuilder()
                .setFirstName("charles")
                .setLastName("josh")
                .setContent("Hello second time").build());

        requestObserver.onCompleted();             
        
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Bidirectional streaming call
     * @param channel
     */
    private void doBiDiStreamingCall(ManagedChannel channel) {
        ChatServiceGrpc.ChatServiceStub stub =   ChatServiceGrpc.newStub(channel); 

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ChatRequest> requestObserver = stub.clientStreamingChat(new StreamObserver<ChatResponse>() {
            @Override
            public void onNext(ChatResponse value) {
                System.out.println("Received response for bidi gRPC request:" + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //Error handling
            }

            @Override
            public void onCompleted() {
              latch.countDown();
            }
        });


        //Bidi streaming gRPC call
        requestObserver.onNext(ChatRequest.newBuilder()
                .setFirstName("charles")
                .setLastName("josh")
                .setContent("Hello one time").build());

        requestObserver.onNext(ChatRequest.newBuilder()
                .setFirstName("charles")
                .setLastName("josh")
                .setContent("Hello second time").build());

        requestObserver.onCompleted();             
        
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

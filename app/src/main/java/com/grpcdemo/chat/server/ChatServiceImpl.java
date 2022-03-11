package com.grpcdemo.chat.server;
import com.proto.chat.ChatRequest;
import com.proto.chat.ChatResponse;
import com.proto.chat.ChatServiceGrpc;

import io.grpc.stub.StreamObserver;

/**
 * This is gRPC chat service implementation
 * @author prasanna sonurkar
 */
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    /**
     * Unary chat
     */
    @Override
    public void unaryChat(ChatRequest request, StreamObserver<ChatResponse> responseObserver) {

        ChatResponse response = ChatResponse.newBuilder().setResult("Hi there !" + request.getFirstName() + " " + 
        request.getLastName()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Server streaming chat
     */
    @Override
    public void serverStreamingChat(ChatRequest request, StreamObserver<ChatResponse> responseObserver) {
        for (int i=0; i <5; i++) {
            ChatResponse response = ChatResponse.newBuilder().setResult(i + "Hi there !" + request.getFirstName() 
            + " " + request.getLastName()).build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    /**
     * Client streaming chat
     */
    @Override
    public StreamObserver<ChatRequest> clientStreamingChat(StreamObserver<ChatResponse> responseObserver) {
        StreamObserver<ChatRequest> requestObserver = new StreamObserver<ChatRequest>() {

            String result = "";

            @Override
            public void onNext(ChatRequest value) {
                // client sends a message
                result += "Hello " + value.getFirstName() + "! ";
            }

            @Override
            public void onError(Throwable t) {
                // client sends an error
            }

            @Override
            public void onCompleted() {
                // client is done
                responseObserver.onNext(
                    ChatResponse.newBuilder()
                                .setResult(result)
                                .build()
                );
                responseObserver.onCompleted();
            }
        };


        return requestObserver;        
    }


    /**
     * Bidrectional chat
     */
    @Override
    public StreamObserver<ChatRequest> biDirectionalChat(StreamObserver<ChatResponse> responseObserver) {
        StreamObserver<ChatRequest> requestObserver = new StreamObserver<ChatRequest>() {
            @Override
            public void onNext(ChatRequest value) {
                String result = "Hello " + value.getFirstName();
                ChatResponse greetEveryoneResponse = ChatResponse.newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable t) {
                // do nothing
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }       
    
    
}

package edu.ncsu.csc510.tictactoe;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;


public class WebSocketClientImpl extends WebSocketClient {

    private MessageHandler messageHandler;

    public WebSocketClientImpl(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        if (this.messageHandler != null ) {
            this.messageHandler.handleMessage(message);
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public interface MessageHandler {

        public void handleMessage(String message);
    }
}

package edu.ncsu.csc510.tictactoe;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class WebSocketClientSingleton {
    static WebSocketClientImpl ws_instance = null;

    static GameState gameState = null;

    static User user = null;
    // private constructor can't be accessed outside the class
    public WebSocketClientSingleton() {
    }

    static public void clearInstance(){

        ws_instance = null;
        gameState = null;
        user = null;
    }
    // Factory method to provide the users with instances
    static public WebSocketClientImpl getInstance(URI uri)
    {
        if (ws_instance == null) {
                ws_instance = new WebSocketClientImpl(uri);
                gameState = new GameState();
                user = new User();
        }
        return ws_instance;
    }
    static public WebSocketClientImpl reconnectInstance(URI uri)
    {
        ws_instance = new WebSocketClientImpl(uri);
        return ws_instance;
    }
    static public WebSocketClientImpl getInstance()
    {
        NullPointerException nullPointer = new NullPointerException();
        if (ws_instance != null)
            return ws_instance;
        else
            throw nullPointer;
    }
    static public GameState getGameState()
    {
        return gameState;
    }
    static public void setGameState(GameState game)
    {
        gameState = game;
    }

    static public User getUser(){return user;}
    static  public void setUser(User usr){user = usr;}
}

class WebSocketClientImpl extends WebSocketClient {

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

    public void removeMessageHandler() {
        this.messageHandler = null;
    }

    public interface MessageHandler {

        public void handleMessage(String message);
    }
}
package edu.ncsu.csc510.tictactoe;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

public class JsonUtility {
    public static GameState jsonToGameState(String message) {
        GameState gameState = new GameState();
        String[] board = new String[9];
        try {
            gameState = WebSocketClientSingleton.getGameState();
            JSONObject obj = (JSONObject) new JSONParser().parse(message);
            if (obj == null) return gameState;
            if (obj.containsKey("description") && "Illegal move".equals(obj.get("description"))) {
                return gameState;
            }

            if (obj.containsKey("game_id")) {
                gameState.setGame_id(obj.get("game_id") != null ? obj.get("game_id").toString() : null);
            }
            if (obj.containsKey("activePlayer")) {
                gameState.setActivePlayer(obj.get("activePlayer") != null ? obj.get("activePlayer").toString() : null);
            }
            if (obj.containsKey("player_count")) {
                if (obj.get("player_count") != null)
                    gameState.setPlayer_count(Integer.parseInt(obj.get("player_count").toString()));
                else gameState.setPlayer_count(0);
            }
            if (obj.containsKey("last_move")){
                if(obj.get("last_move") != null) {
                    String str = obj.get("last_move").toString();
                    double doubleNum = Double.parseDouble(str);
                    long myLong = System.currentTimeMillis() + ((long) (doubleNum * 1000));
                    java.util.Date datetime = new java.util.Date(myLong);
                    gameState.setLast_move(datetime);
                }else{
                    gameState.setLast_move(null);
                }
            }
            if (obj.containsKey("p0")) {
                gameState.setP0(obj.get("p0") != null ? obj.get("p0").toString() : null);
            }
            if (obj.containsKey("p1")) {
                gameState.setP1(obj.get("p1") != null ? obj.get("p1").toString() : null);
            }
            if (obj.containsKey("p0_name")) {
                gameState.setP0_name(obj.get("p0_name") != null ? obj.get("p0_name").toString() : null);
            }
            if (obj.containsKey("p1_name")) {
                gameState.setP1_name(obj.get("p1_name") != null ? obj.get("p1_name").toString() : null);
            }
            if (obj.containsKey("winner")) {
                gameState.setWinner(obj.get("winner") != null ? obj.get("winner").toString() : null);
            }
            for (int i = 0; i < gameState.board.length; i++) {
                if (obj.containsKey("piece-" + i))
                    board[i] = obj.get("piece-" + i) != null ? obj.get("piece-" + i).toString() : null;
            }

            gameState.board = board;
        } catch (Exception e) {
            Log.d("updateClient", "Exception", e);
        }
        return gameState;
    }

}

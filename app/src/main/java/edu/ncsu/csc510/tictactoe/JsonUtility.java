package edu.ncsu.csc510.tictactoe;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Date;

public class JsonUtility {
    public static void jsonToGameState(String message) {
        try {
            GameState gameState = WebSocketClientSingleton.getGameState();
            JSONObject obj = (JSONObject) new JSONParser().parse(message);
            String[] board = new String[9];
            if (obj != null) {
                if (obj.get("game_id") != null)
                    gameState.setGame_id(obj.get("game_id").toString());
                if (obj.get("activePlayer") != null)
                    gameState.setActivePlayer(obj.get("activePlayer").toString());
                if (obj.get("last_move") != null) {
                    String str = obj.get("last_move").toString();
                    Date date = new Date(Long.parseLong(str));
                    gameState.setLast_move(date);
                }
                if (obj.get("p1") != null)
                    gameState.setP1(obj.get("p1").toString());
                if (obj.get("p0") != null)
                    gameState.setP0(obj.get("p0").toString());
                if (obj.get("winner") != null)
                    gameState.setWinner(obj.get("winner").toString());
                for (int i = 0; i < gameState.board.length; i++) {
                    if (obj.get("piece-" + i) != null) {
                        board[i] = obj.get("piece-" + i).toString();
                    }
                }
            }
            gameState.board = board;
            WebSocketClientSingleton.setGameState(gameState);

        } catch (Exception e) {
            Log.d("updateClient", "Exception", e);
        }
    }

}

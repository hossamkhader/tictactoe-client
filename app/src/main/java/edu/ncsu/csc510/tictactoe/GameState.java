package edu.ncsu.csc510.tictactoe;

import java.util.Date;

public class GameState {
    private String game_id = null;
    private String activePlayer = "0";
    private int player_count = 0;
    private String winner = null;
    private String p0 = null;
    private String p1 = null;
    private String p0_name = null;
    private String p1_name = null;
    private Date last_move = null;

    // private boolean gameActive = false;
    public String[] board = {null, null, null, null, null, null, null, null, null};

    public String getGame_id()
    {
        return  game_id;
    }
    public void setGame_id(String game_id)
    {
        this.game_id = game_id;
    }
    public String getActivePlayer()
    {
        return  activePlayer;
    }
    public void setActivePlayer(String activePlayer)
    {
        this.activePlayer = activePlayer;
    }
    public int getPlayer_count() {return player_count;}
    public void setPlayer_count(int player_count){this.player_count = player_count;}
    public String getWinner()
    {
        return  winner;
    }
    public void setWinner(String winner)
    {
        this.winner = winner;
    }
    public String getP0()
    {
        return  p0;
    }
    public void setP0(String p0)
    {
        this.p0 = p0;
    }
    public String getP1()
    {
        return  p1;
    }
    public void setP1(String p1)
    {
        this.p1 = p1;
    }
    public String getP0_name()
    {
        return  p0_name;
    }
    public void setP0_name(String p0_name)
    {
        this.p0_name = p0_name;
    }
    public String getP1_name()
    {
        return  p1_name;
    }
    public void setP1_name(String p1_name)
    {
        this.p1_name = p1_name;
    }
    public Date getLast_move()
    {
        return  last_move;
    }
    public void setLast_move(Date last_move)
    {
        this.last_move = last_move;
    }



}

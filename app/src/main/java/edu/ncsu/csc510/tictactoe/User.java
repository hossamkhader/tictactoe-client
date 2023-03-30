package edu.ncsu.csc510.tictactoe;

public class User {
    private String player_id;
    private String username;

    public User(){}
    public User(String player_id, String username) {
        this.player_id = player_id;
        this.username = username;
    }

    public String getPlayer_id() {
        return this.player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }
    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }
}

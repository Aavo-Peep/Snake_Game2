package com.example.opilane.snake_game;

/**
 * Created by opilane on 14.03.2018.
 */

public class Userprofiledata {
    public String eesNimi;
    public String pereNimi;
    public String email;


    public Userprofiledata(String eesNimi, String pereNimi, String email) {
        this.eesNimi = eesNimi;
        this.pereNimi = pereNimi;
        this.email = email;
    }

    public Userprofiledata(){
    }

    public String getEesNimi() {
        return eesNimi;
    }

    public void setEesNimi(String eesNimi) {
        this.eesNimi = eesNimi;
    }

    public String getPereNimi() {
        return pereNimi;
    }

    public void setPereNimi(String pereNimi) {
        this.pereNimi = pereNimi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

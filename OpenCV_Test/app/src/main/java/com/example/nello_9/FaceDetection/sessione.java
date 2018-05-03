package com.example.nello_9.FaceDetection;

/**
 * Created by Nello_9 on 27/04/2018.
 */

public class sessione {
    int idS;
    String date;
    public sessione(int x, String y){
        idS=x;
        date=y;
    }

    public int getIdS() {
        return idS;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

package com.example.nello_9.FaceDetection;

import android.graphics.Bitmap;

/**
 * Created by Nello_9 on 25/04/2018.
 */

public class soggetto {
    Bitmap n;
    int i;
    public soggetto(Bitmap b, int id){
        n=b;
        i=id;


    }

    public Bitmap getN() {
        return n;
    }

    public void setN(Bitmap n) {
        this.n = n;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}

package com.example.nello_9.FaceDetection;

/**
 * Created by Nello_9 on 01/05/2018.
 */

public class soggettoSenzaNome {
    String nome,cognome;
    int idImm;
    public soggettoSenzaNome(String n, String c, int x){
        nome=n;
        cognome=c;
        idImm=x;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public int getIdImm() {
        return idImm;
    }

    public void setIdImm(int idImm) {
        this.idImm = idImm;
    }
}

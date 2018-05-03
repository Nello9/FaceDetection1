package com.example.nello_9.FaceDetection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbhelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "opencvtest.db";
    private static final int DATABASE_VERSION = 1;

    // Lo statement SQL di creazione del database
    private static final String DATABASE_SOGGETTI = "create table soggetti (id integer PRIMARY KEY autoincrement, nome text, cognome text, id_immagini integer, FOREIGN KEY(id_immagini) REFERENCES immagini(id) ON DELETE CASCADE);";
    private static final String DATABASE_IMMAGINI="create table immagini (id integer primary key autoincrement, volto blob , perioculare_intero blob, perioculareDX blob , perioculareSX blob , id_sessione integer not null,FOREIGN KEY(id_sessione) REFERENCES sessioni(id) ON DELETE CASCADE)";
    private static final String DATABASE_SESSIONI ="create table sessioni (id integer primary key not null, data text not null)";

    // Costruttore
    public dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Questo metodo viene chiamato durante la creazione del database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_SESSIONI);
        database.execSQL(DATABASE_IMMAGINI);
        database.execSQL(DATABASE_SOGGETTI);
      //  database.execSQL(DATABASE_SENZAFOTO);
    }

    // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene incrementato il numero di versione
    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {

        database.execSQL("DROP TABLE IF EXISTS sessioni");

        database.execSQL("DROP TABLE IF EXISTS immagini");
        database.execSQL("DROP TABLE IF EXISTS soggetti");

        onCreate(database);

    }
}
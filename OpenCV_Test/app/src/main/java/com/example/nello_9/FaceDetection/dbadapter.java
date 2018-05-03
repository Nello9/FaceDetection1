package com.example.nello_9.FaceDetection;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class dbadapter {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = dbadapter.class.getSimpleName();

    private Context context;
    private SQLiteDatabase database;
    private dbhelper dbHelper;

    // Database fields
    private static final String DATABASE_SOGGETTI = "soggetti";
    private static final String DATABASE_IMMAGINI = "immagini";
    private static final String DATABASE_SESSIONI = "sessioni";
    private static final String DATABASE_SENZAFOTO = "soggetti_senza_foto";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "nome";
    public static final String KEY_SURNAME = "cognome";
    public static final String KEY_VISO = "volto";
    public static final String KEY_OCCHI ="perioculare_intero";
    public static final String KEY_OCCHIODX = "perioculareDX";
    public static final String KEY_OCCHIOSX = "perioculareSX";
    public static final String KEY_IDIMMAGINI ="id_immagini";
    public static final String KEY_IDSESSIONI ="id_sessione";
    public static final String KEY_DATA="data";

    public dbadapter(Context context) {
        this.context = context;
    }

    public dbadapter open() throws SQLException {
        dbHelper = new dbhelper(context);
        database = dbHelper.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys=ON");
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValuesSoggetti(String nome) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, nome);

       return values;
    }

    private ContentValues createContentValuesImmagini (byte[] volto, byte[] periInt, byte[] periDX, byte[] periSX){
        ContentValues values= new ContentValues();
        values.put(KEY_VISO,volto);
        values.put (KEY_OCCHI, periInt);
        values.put (KEY_OCCHIODX, periDX);
        values.put (KEY_OCCHIOSX, periSX);
        return values;


    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    //crea un'istanza
    public int createIstanzaImmagini(Bitmap img, Bitmap occhi,Bitmap occhiod, Bitmap occhios, int num) {


            byte[] dataViso = getBitmapAsByteArray(img); // this is a function
            byte[] dataocchi = getBitmapAsByteArray(occhi);
            byte[] dataDestro = getBitmapAsByteArray(occhiod);
            byte[] dataSinistro = getBitmapAsByteArray(occhios);
            ContentValues values = new ContentValues();
            values.put(KEY_VISO, dataViso);
            values.put(KEY_OCCHI, dataocchi);
            values.put(KEY_OCCHIODX, dataDestro);
            values.put(KEY_OCCHIOSX, dataSinistro);
            values.put(KEY_IDSESSIONI, num);
            database.insertOrThrow(DATABASE_IMMAGINI, null, values);
            Cursor curs;

            curs = database.query(DATABASE_IMMAGINI, new String[]{"MAX(id)"}, null, null, null, null, null);
            curs.moveToFirst();
            int test = curs.getInt(0);
            return test;
        }


    public void createIstanzaSoggetti(String nome, String cognome,int imm) {



        ContentValues values = new ContentValues();
        values.put(KEY_NAME,nome);
        values.put (KEY_SURNAME,cognome);
        values.put(KEY_IDIMMAGINI,imm);
        database.insertOrThrow( DATABASE_SOGGETTI, null, values );

    }

    public void createIstanzaSoggettiSenzaFoto(String nome, String cognome,int imm) {



        ContentValues values = new ContentValues();
        values.put(KEY_NAME,nome);
        values.put (KEY_SURNAME,cognome);
        values.put(KEY_IDSESSIONI,imm);
        database.insertOrThrow( DATABASE_SENZAFOTO, null, values );

    }

    public void createIstanzaSessioni(int num,String data) {



        ContentValues values = new ContentValues();
        values.put(KEY_ID, num);
        values.put(KEY_DATA,data);
        database.insertOrThrow( DATABASE_SESSIONI, null, values );

    }
    public ArrayList<Integer> prelevaID(){
        ArrayList<Integer> in= new ArrayList<Integer>();
        Cursor curs;
        String selectQuery = "SELECT id_immagini FROM soggetti WHERE nome='' AND cognome=''";
        curs=database.rawQuery(selectQuery,null);
        if (curs.moveToFirst()) {
            do {
                int intero=curs.getInt(0);
                in.add(intero);

            } while (curs.moveToNext());
for (int i=0; i<in.size();i++)
    Log.i("vedo","ID "+i+" è:"+in.get(i));
        }

   return in;

    }

    public ArrayList<soggettoSenzaNome> nomeCognome(){
        soggettoSenzaNome sogg= new soggettoSenzaNome("","",0);
        ArrayList<soggettoSenzaNome> iSoggetti= new ArrayList<soggettoSenzaNome>();
        ArrayList<Integer> in= new ArrayList<Integer>();
        in=soggettiNonAssegnati();
        Cursor curs;
        String selectQuery = "SELECT * FROM soggetti";
        curs=database.rawQuery(selectQuery,null);
        if (curs.moveToFirst()) {
            do {
                int intero=curs.getInt(0);
                String nome= curs.getString(1);
                String cognome= curs.getString(2);
                int immagine= curs.getInt(3);
                for (int i=0; i<in.size();i++) {
                    if (immagine==in.get(i)){
                    sogg=new soggettoSenzaNome(nome,cognome,immagine);
                    iSoggetti.add(sogg);}
                }
            } while (curs.moveToNext());

        }

        return iSoggetti;

    }



    public sessione prelevaIDSessione(){
        Date minima= new Date();
      ArrayList<sessione> in= new ArrayList<sessione>();
        sessione sessFinale= new sessione(0,"");
        Cursor curs;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Log.i("vedo","Sono in preveIDSessione 1");
        String selectQuery = "SELECT * FROM sessioni";
        curs=database.rawQuery(selectQuery,null);
        Log.i("vedo","Sono in preveIDSessione 2");
        if (curs.getCount()==0) {
        Log.i("vedo", "Il DB è vuoto ed il cursore è nullo");
        }
        else {
            if (curs.moveToFirst()) {
                do {
                    int x = curs.getInt(0);
                    String string = curs.getString(1);
                    sessione sess = new sessione(x, string);
                    in.add(sess);

                } while (curs.moveToNext());

            }

            try {

                minima = format.parse(in.get(0).getDate());
                for (int j = 0; j < in.size(); j++) {
                    sessFinale.setIdS(in.get(j).getIdS());
                    sessFinale.setDate(in.get(j).getDate());
                    Log.i("vedo", "Esamino: " + in.get(j).getIdS() + " - del " + in.get(j).getDate());
                    Date dataM = format.parse(in.get(j).getDate());
                    // Date date = format.parse(in.get(j+1).getDate());
                    if (dataM.before(minima)) {
                        minima = dataM;
                        Log.i("vedo", "Data minima: " + minima);
                        sessFinale.setIdS(in.get(j).getIdS());
                        sessFinale.setDate(in.get(j).getDate());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return sessFinale;
        }




public ArrayList<Integer> soggettiNonAssegnati() {
    ArrayList<Integer> id= new ArrayList<>();
    int k;
    Cursor cur;
    byte[] image;
    String selectQuery = "SELECT * FROM immagini";
    cur = database.rawQuery(selectQuery, null);
    if (cur.moveToFirst()) {
        do {
            k = cur.getInt(0);
            image = cur.getBlob(1);
            Bitmap bit = BitmapFactory.decodeByteArray(image, 0, image.length);
            Log.i("vedo", "Misura: "+bit.getHeight());
            if (bit.getHeight()==5)
                id.add(k);
        } while (cur.moveToNext());
    }
    return id;
}

    public ArrayList<soggetto> getimage(){
        int k;
        String selectQuery;
        Cursor curs;
    ArrayList<Integer> Gliid = new ArrayList<Integer>();
    ArrayList<soggetto> s= new ArrayList<soggetto>();
    byte[] image;
        Gliid=prelevaID();
        for(int i=0; i<Gliid.size();i++){
            selectQuery = "SELECT * FROM immagini WHERE id=" + Gliid.get(i);
            curs = database.rawQuery(selectQuery, null);
            if (curs.moveToFirst()) {
            do {

                k= curs.getInt(0);
                image = curs.getBlob(1);
                Bitmap bit = BitmapFactory.decodeByteArray(image, 0, image.length);
                soggetto so= new soggetto(bit,k);
                s.add(i, so);
            } while (curs.moveToNext());
       }}

        return s;

    }

    //update
    public void updateIstanza(int id, String n, String c) {
       String query= "UPDATE soggetti SET nome='"+n+"', cognome='"+c+"' WHERE id_immagini="+id;
       database.execSQL(query);
    }

    public void deleteIstanza(int id) {
        String query= "DELETE FROM immagini WHERE id="+id;
        database.execSQL(query);
    }
/*
    public void updateImmagini(Bitmap img, Bitmap occhi,Bitmap occhiod, Bitmap occhios, int num) {
         String query= "UPDATE immagini SET nome='"+n+"', cognome='"+c+"' WHERE id_immagini="+id;
        // return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
        byte[] dataViso = getBitmapAsByteArray(img); // this is a function
        byte[] dataocchi = getBitmapAsByteArray(occhi);
        byte[] dataDestro = getBitmapAsByteArray(occhiod);
        byte[] dataSinistro = getBitmapAsByteArray(occhios);
        ContentValues values = new ContentValues();
        values.put(KEY_VISO, dataViso);
        values.put(KEY_OCCHI, dataocchi);
        values.put(KEY_OCCHIODX, dataDestro);
        values.put(KEY_OCCHIOSX, dataSinistro);
        values.put(KEY_IDSESSIONI, num);
        database.insertOrThrow(DATABASE_IMMAGINI, null, values);
        Cursor curs;

        curs = database.query(DATABASE_IMMAGINI, new String[]{"MAX(id)"}, null, null, null, null, null);
        curs.moveToFirst();
        int test = curs.getInt(0);
        return test;
    }*/
/*
    //delete
    public boolean deleteIstanza(long istID) {
        return database.delete(DATABASE_TABLE, KEY_ID + "=" + istID, null) > 0;
    }
*/
    /*
    public String tutti() {

        return database.query("sessioni", new String[]{KEY_IDSESSIONI, KEY_DATA, null, null);

    }
*/
    //fetch contacts filter by a string
    public Cursor fetchIstanza(int filter) {
        Cursor mCursor = database.query(true, "sessioni", new String[]{
                        "id", KEY_DATA}, "id" + "=" + filter, null, null, null, null, null, null);

        return mCursor;
    }

}
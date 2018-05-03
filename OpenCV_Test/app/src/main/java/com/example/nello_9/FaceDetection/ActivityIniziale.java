package com.example.nello_9.FaceDetection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nello_9.FaceDetection.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityIniziale extends AppCompatActivity {
Button cameraFrontale, cameraPosteriore, nienteNome, nienteImmagine,copia;
    Cursor cur;
    Boolean bo;
    String nomeSalvato, cognomeSalvato;
    int sessione,x;
    dbadapter db;
   Bitmap a,b,c,d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        db=new dbadapter(this);
        db.open();
        setContentView(R.layout.activity_iniziale);
        cameraFrontale=(Button) findViewById(R.id.fotoCamAnteriore);
        cameraFrontale.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent passFrontale = new Intent(ActivityIniziale.this,activityCam2.class);
                startActivity(passFrontale);
            }
        });
        cameraPosteriore=(Button) findViewById(R.id.fotoCamPosteriore);
        cameraPosteriore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent passPosteriore = new Intent(ActivityIniziale.this,MainActivity_show_camera.class);
                startActivity(passPosteriore);
            }
        });
        nienteNome=(Button) findViewById(R.id.senzaNome);
        nienteNome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                visionaFoto();
            }
        });
        nienteImmagine =(Button) findViewById(R.id.senzaFoto);
        nienteImmagine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            mostra();
            }
        });

        copia=(Button) findViewById(R.id.copiaDB);
        copia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copiaDB();
            }
        });


    }
    private void copiaDB(){
        int i=0;
        try {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/opencvtest.db";
                  String backupDBPath = "copia"+i+".db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                i++;
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            Toast toast = Toast.makeText(getApplicationContext(), "DB copiato!", Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {

        }

    }
    private void visionaFoto(){
        Intent i= new Intent(this,scegliFoto.class);
        startActivity(i);

    }


    private void mostra(){
        sessione x= db.prelevaIDSessione();
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.aggiunta_sogg, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityIniziale.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText input = (EditText) promptsView
                .findViewById(R.id.Nome);

        final EditText cogn = (EditText) promptsView
                .findViewById(R.id.Cognome);

        final EditText sess = (EditText) promptsView
                .findViewById(R.id.Sessione);

        TextView info= (TextView) promptsView.findViewById(R.id.inform);
        if (x.getIdS()==0 && x.getDate().equals(""))
            info.setText("Nessuna sessione aperta");
        else
            info.setText("Ultima nuova sessione aperta: "+x.getIdS()+" del "+x.getDate());

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        nomeSalvato = input.getText().toString();
                        cognomeSalvato =cogn.getText().toString();
                        Log.i("sessione",sess.getText().toString());
                        if (sess.getText().toString().equalsIgnoreCase("")|| nomeSalvato.equalsIgnoreCase("")|| cognomeSalvato.equalsIgnoreCase("")){
                            Toast toast = Toast.makeText(getApplicationContext(), "TUTTI I CAMPI SONO OBBLIGATORI!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else{
                            sessione=Integer.parseInt(sess.getText().toString());
                            salvataggioinDB();
                        }

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancella",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.show();
    }

private void salvataggioinDB(){
    a=Bitmap.createBitmap(5,5, Bitmap.Config.RGB_565);
    b=Bitmap.createBitmap(5,5, Bitmap.Config.RGB_565);
    c=Bitmap.createBitmap(5,5, Bitmap.Config.RGB_565);
    d=Bitmap.createBitmap(5,5, Bitmap.Config.RGB_565);

    Date x= new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String dataScatto= ""+sdf.format(x);
    cur=db.fetchIstanza(sessione);
    if (cur.getCount()==0){
       db.createIstanzaSessioni(sessione,dataScatto);}
    int h=db.createIstanzaImmagini(a,b,c,d,sessione);
    db.createIstanzaSoggetti(nomeSalvato,cognomeSalvato,h);
    Toast toast = Toast.makeText(getApplicationContext(), "SALVATAGGIO COMPLETO!", Toast.LENGTH_SHORT);
    toast.show();
}
}
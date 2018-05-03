package com.example.nello_9.FaceDetection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nello_9.FaceDetection.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Cursor cur;
    private dbadapter db;
    private boolean fatto=true;
    int sessione,i=0;
    String nomeSalvato, cognomeSalvato;
    Button torna,copia;
    Button salvataggio;
    ImageView immagine;
    Bitmap bitmap,facc,sinis,dest;
    Mat sourceImage,visoCorr,destro,sinistro;
    String dx,sx,nome, peri,g;
    File okok= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/FotoViso/tmp");
    File pathGiusto= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/FotoViso");
    File t= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/FotoViso/VisoSenzaSoggetto");
    private static final String TAG = "vedo";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfullySSS");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new dbadapter(this);
        db.open();

        setContentView(R.layout.activity_main);
        torna=(Button) findViewById(R.id.Torna);

        torna.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancellaTmp();
                Intent torniamo = new Intent(MainActivity.this,MainActivity_show_camera.class);
                startActivity(torniamo);
            }
        });
        salvataggio=(Button) findViewById(R.id.Salva);
        salvataggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mostra();


              // salvataggioCartella();
            }
        });
        copia=(Button) findViewById(R.id.copia);
        copia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
copiaDB();

            }
        });
        Log.i(TAG,"Entrato nella seconda activity");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        immagine=(ImageView) findViewById(R.id.imageV);
        Intent intent = getIntent();
        nome= intent.getStringExtra("fotografia");
        dx= intent.getStringExtra("des");
        sx= intent.getStringExtra("sin");
        peri= intent.getStringExtra("perioculare");
        Log.i(TAG,"Nome arrivato alla seconda: "+nome);
        try {
            Log.i(TAG,"fatto è:" +fatto);
            if (fatto)
            prendoFoto(nome,dx,sx,peri);
            fatto=false;
            Log.i(TAG,"Sto nel try");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.i(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.i(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void prendoFoto(String str, String d, String s, String x) throws IOException {
        File imm = new File(str);
        String filepath=imm.getPath();
        File imm2 = new File(d);
        String filepath2=imm2.getPath();
        File imm3 = new File(s);
        String filepath3=imm3.getPath();
        File imm4= new File (x);
        String filepath4= imm4.getPath();

        sourceImage = Imgcodecs.imread(filepath);
        destro =Imgcodecs.imread(filepath2);
        sinistro= Imgcodecs.imread(filepath3);
         visoCorr= Imgcodecs.imread(filepath4);
        if (imm.exists()) {
            bitmap =  Bitmap.createBitmap(sourceImage.cols(), sourceImage.rows(), Bitmap.Config.RGB_565);
            dest=Bitmap.createBitmap(destro.cols(),destro.rows(),Bitmap.Config.RGB_565);
            sinis=Bitmap.createBitmap(sinistro.cols(),sinistro.rows(),Bitmap.Config.RGB_565);
            facc=Bitmap.createBitmap(visoCorr.cols(),visoCorr.rows(),Bitmap.Config.RGB_565);

            Utils.matToBitmap(sourceImage,bitmap);
            Utils.matToBitmap(destro,dest);
            Utils.matToBitmap(sinistro,sinis);
            Utils.matToBitmap(visoCorr,facc);
            immagine.setImageBitmap(bitmap);
            Log.i(TAG,"Ci sono arrivato? "+bitmap.getWidth());


        }
        else
            Log.i(TAG,"non esiste");
    }

    private void cancellaTmp(){

        File fil= new File (nome);
        File fil2= new File (sx);
        File fil3= new File (dx);
        File fil4= new File (peri);

        fil.delete();
        fil2.delete();
        fil3.delete();
        fil4.delete();

    }

    private void mostra(){
        sessione x= db.prelevaIDSessione();
        Log.i(TAG,"la sessione arrivata quì è:"+x.getIdS()+" del "+x.getDate());
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

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
                       sessione=Integer.parseInt(sess.getText().toString());
                        if(sess.getText().toString().equalsIgnoreCase("")){
                            Toast toast = Toast.makeText(getApplicationContext(), "NON SALVATO! INSERIRE LA SESSIONE!  ", Toast.LENGTH_SHORT);
                            toast.show();

                        }
                        else if (nomeSalvato.equalsIgnoreCase("")&& cognomeSalvato.equalsIgnoreCase("")){
                                salvataggioCartella(t.getPath());
                            salvataggioinDB();
                        }

                        else{
                        salvataggioCartella(okok.getPath());
                        salvataggioinDB();}
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


    private void salvataggioCartella(String p){

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(nome);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.i(TAG,"Salvato1");
            out = new FileOutputStream(sx);
            sinis.compress(Bitmap.CompressFormat.PNG,100,out);
            Log.i(TAG,"Salvato2");
            out = new FileOutputStream(dx);
            dest.compress(Bitmap.CompressFormat.PNG,100,out);
            Log.i(TAG,"Salvato3");
            out = new FileOutputStream(peri);
            facc.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.i(TAG,"Salvato4");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!pathGiusto.exists()){
            pathGiusto.mkdir();
        }
        copyFile(nome,p,"viso.bmp");
        copyFile(dx,p,"destro.bmp");
        copyFile(sx,p,"sinistro.bmp");
        copyFile(peri,p,"perioculare.bmp");
        cancellaTmp();
        Toast toast = Toast.makeText(getApplicationContext(), "Dati salvati correttamente!", Toast.LENGTH_SHORT);
        toast.show();
       }


    private void copyFile(String inputFile, String outputPath, String name) {


        Date data= new Date();
        Long l=data.getTime();
        InputStream in = null;
        OutputStream out = null;
        try {


            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputPath+nomeSalvato+l+name);
            if (i==0)
            g=outputPath+nomeSalvato+l+name;
            Log.i(TAG, "Mo copio");
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        i++;
    }




    private void salvataggioinDB()  {
        int immg;
            Date x= new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
       String dataScatto= ""+sdf.format(x);
        cur=db.fetchIstanza(sessione);
        if (cur.getCount()==0){
            Log.i(TAG,"Apro una nuova sessione");
        db.createIstanzaSessioni(sessione,dataScatto);}
        immg=db.createIstanzaImmagini(bitmap,facc,dest,sinis,sessione);
        db.createIstanzaSoggetti(nomeSalvato,cognomeSalvato,immg);
    }
    private void copiaDB(){
        int i=0;
        try {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/opencvtest.db";
                Log.i(TAG,"CurentPathDB: "+currentDBPath);
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
            Log.i(TAG,"Errore nell'export del db");
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}

package com.example.nello_9.FaceDetection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nello_9.FaceDetection.R;

import java.util.ArrayList;

public class scegliFoto extends AppCompatActivity {
    String nome,cognome;
    boolean ok=false;
     EditText cogn,input;
    int xx;
    ArrayList<soggettoSenzaNome> sogg= new ArrayList<soggettoSenzaNome>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<soggetto> soggettos = new ArrayList<soggetto>();
      Bitmap bit;

        final dbadapter db = new dbadapter(this);

        db.open();
        ImageView myImage= new ImageView(this);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scegli_foto);

        LinearLayout picLL = new LinearLayout(this);
        picLL.setScrollY(2);
        picLL.layout(0, 0, 100, 100);
        picLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        picLL.setOrientation(LinearLayout.VERTICAL);
        ArrayList<ImageView> imm = new ArrayList<ImageView>();
        for (int j=0; j<200; j++){
        imm.add(j,myImage );}
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        soggettos = db.getimage();
        for(int i=0;i<soggettos.size();i++)
        {
            bit=soggettos.get(i).getN();
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(300,300));
            image.setImageBitmap(bit);
            final int prendoID=soggettos.get(i).getI();
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                   mostra(prendoID);
                }
            });
            // Adds the view to the layout
            picLL.addView(image);
        }
        db.close();
        setContentView(picLL);

    }


    public void check(View v){

        final PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        for (int i=0; i<sogg.size();i++) {
            Log.i("vedo","For arrivato: "+sogg.get(i).getNome());
            popup.getMenu().add(""+sogg.get(i).getNome()+" "+sogg.get(i).getCognome()+" "+sogg.get(i).idImm);

        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String[] items = item.getTitle().toString().split(" ");
                for (int i=0;i<items.length;i++) {
                    input.setText(""+items[0]);
                    cogn.setText(""+items[1]);
                    xx=Integer.parseInt(""+items[2]);
                }

                return true;
            }
        });
        popup.show();
    }



    private void mostra(final int x){

        final dbadapter dbadapter= new dbadapter(this);
        dbadapter.open();
        sessione g= dbadapter.prelevaIDSessione();
        sogg=dbadapter.nomeCognome();

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.mostrafoto, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(scegliFoto.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        input = (EditText) promptsView
                .findViewById(R.id.Nome);

       cogn = (EditText) promptsView
                .findViewById(R.id.Cognome);

        TextView info= (TextView) promptsView.findViewById(R.id.inform);
        if (g.getIdS()==0 && g.getDate().equals(""))
            info.setText("Nessuna sessione aperta");
        else
            info.setText("Ultima nuova sessione aperta: "+g.getIdS()+" del "+g.getDate());

        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        nome = input.getText().toString();
                        cognome =cogn.getText().toString();

                        if (nome.equalsIgnoreCase("")&& cognome.equalsIgnoreCase("")){
                            Toast toast = Toast.makeText(getApplicationContext(), "TUTTI I CAMPI SONO OBBLIGATORI!", Toast.LENGTH_LONG);
                            toast.show();

                        }
                        else{
                            dbadapter.updateIstanza(x,nome,cognome);
                            Toast toast = Toast.makeText(getApplicationContext(), "AGGIORNATO!!", Toast.LENGTH_LONG);
                            toast.show();
                            dbadapter.deleteIstanza(xx);
                            Intent i= new Intent(scegliFoto.this,ActivityIniziale.class);
                            startActivity(i);
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

}

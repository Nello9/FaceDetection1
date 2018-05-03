package com.example.nello_9.FaceDetection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nello_9.FaceDetection.R;

public class Errore extends AppCompatActivity {
Button menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errore);

        menu=(Button) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passMenu = new Intent(Errore.this,ActivityIniziale.class);
                startActivity(passMenu);
            }
        });

    }
}

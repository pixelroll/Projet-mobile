package com.paullouis.travel;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TravelPathActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Placeholder UI

        String location = getIntent().getStringExtra("location");
        TextView tv = new TextView(this);
        tv.setText("Générateur de parcours pour : " + (location != null ? location : "Lieu inconnu"));
        tv.setPadding(32, 32, 32, 32);
        setContentView(tv);
    }
}

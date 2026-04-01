package com.paullouis.travel;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GroupDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Détails du groupe (Stub)\nID: " + getIntent().getStringExtra("GROUP_ID"));
        tv.setPadding(50, 50, 50, 50);
        setContentView(tv);
    }
}

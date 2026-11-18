package com.example.budgetchallenge;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView txtResults = findViewById(R.id.txtResults);

        String bilan = getIntent().getStringExtra("results");
        txtResults.setText(bilan);

        Button btnBackResults = findViewById(R.id.btnBackResults);
        btnBackResults.setOnClickListener(v -> finish());
        Button btnReplay = findViewById(R.id.btnReplay);

        btnReplay.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
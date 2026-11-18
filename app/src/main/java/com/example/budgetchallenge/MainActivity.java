package com.example.budgetchallenge;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
            startActivity(intent);
        });

        // Modifier la couleur du titre
        TextView title = findViewById(R.id.txtTitle);

        String text = "BUDGET CHALLENGE";
        SpannableString spannable = new SpannableString(text);

        int start = text.indexOf("CHALLENGE");

        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#A8E6A3")),
                start,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        title.setText(spannable);


    }
}

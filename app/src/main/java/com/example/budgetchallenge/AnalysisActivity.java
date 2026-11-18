package com.example.budgetchallenge;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        String analyse = getIntent().getStringExtra("analysis");

        TextView txtAnalysis = findViewById(R.id.txtAnalysis);
        TextView txtA = findViewById(R.id.txtStrategyA);
        TextView txtB = findViewById(R.id.txtStrategyB);
        TextView txtC = findViewById(R.id.txtStrategyC);
        TextView txtRecommendation = findViewById(R.id.txtRecommendation);

        if (analyse != null) {
            String[] parties = analyse.split("COMPARAISON DES STRATÉGIES");

            txtAnalysis.setText(parties[0].trim());

            if (parties.length > 1) {
                String[] strategies = parties[1].split("RECOMMANDATION");

                String comparaison = strategies[0];

                String[] lignes = comparaison.split("\n");

                if (lignes.length >= 5) {
                    txtA.setText("🏦 " + lignes[2].trim());
                    txtB.setText("📈 " + lignes[3].trim());
                    txtC.setText("⭐ " + lignes[4].trim());
                }

                if (strategies.length > 1) {
                    txtRecommendation.setText(
                            "💡 NOTRE RECOMMANDATION\n\n" +
                                    strategies[1].trim()
                    );
                }
            }
        }

        findViewById(R.id.btnBackAnalysis).setOnClickListener(v -> finish());
    }
}
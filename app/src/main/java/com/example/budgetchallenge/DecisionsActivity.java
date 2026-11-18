package com.example.budgetchallenge;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class DecisionsActivity extends AppCompatActivity {

    private double balance;
    private double expenses;
    private double decisionAmount;
    private String decisionDescription = "";


    private double food;
    private double transport;
    private double other;

    private double newBalance;
    private double newExpenses;
    private double newFood;
    private double newTransport;
    private double newOther;

    private double postponedExpense = 0;

    private Spinner spinnerCategory;
    private TextView tvAvailableBalance, tvImpact;
    private EditText etAmount;
    private RadioGroup rgDecisions;

    private boolean previewCalculated = false;
    private int selectedDecision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decisions);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvAvailableBalance = findViewById(R.id.tvAvailableBalance);
        tvImpact = findViewById(R.id.tvImpact);
        etAmount = findViewById(R.id.etAmount);
        rgDecisions = findViewById(R.id.rgDecisions);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnPreview = findViewById(R.id.btnPreview);
        Button btnValidate = findViewById(R.id.btnValidate);

        // Catégories
        String[] categories = {
                "Alimentation",
                "Transport",
                "Autres dépenses"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(adapter);

        // Récupération des données du Dashboard
        Intent intent = getIntent();

        balance = intent.getDoubleExtra("balance", 400);
        expenses = intent.getDoubleExtra("expenses", 1400);
        food = intent.getDoubleExtra("food", 350);
        transport = intent.getDoubleExtra("transport", 150);
        other = intent.getDoubleExtra("other", 200);

        newBalance = balance;
        newExpenses = expenses;
        newFood = food;
        newTransport = transport;
        newOther = other;

        updateBalance();

        btnBack.setOnClickListener(v -> finish());

        rgDecisions.setOnCheckedChangeListener((group, checkedId) -> {
            previewCalculated = false;
            tvImpact.setText(
                    "Saisissez un montant puis calculez l'impact."
            );
        });

        btnPreview.setOnClickListener(v -> calculateImpact());

        btnValidate.setOnClickListener(v -> validateDecision());
    }

    private void calculateImpact() {

        selectedDecision = rgDecisions.getCheckedRadioButtonId();

        if (selectedDecision == -1) {
            Toast.makeText(this,
                    "Veuillez choisir une action.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        newBalance = balance;
        newExpenses = expenses;
        newFood = food;
        newTransport = transport;
        newOther = other;
        postponedExpense = 0;

        // Comparaison des stratégies
        if (selectedDecision == R.id.rbCompare) {

            tvImpact.setText(
                    "Comparaison des stratégies :\n\n" +

                            "Stratégie A - Budget actuel :\n" +
                            "Épargne estimée : " + format(balance) + " DT\n\n" +

                            "Stratégie B - Réduction de 100 DT :\n" +
                            "Épargne estimée : " + format(balance + 100) + " DT\n\n" +

                            "Stratégie C - Réduction de 200 DT :\n" +
                            "Épargne estimée : " + format(balance + 200) + " DT\n\n" +

                            "La stratégie C permet d'obtenir le solde le plus élevé."
            );

            previewCalculated = true;
            return;
        }

        // Formation : coût fixe de 300 DT
        if (selectedDecision == R.id.rbTraining) {

            decisionAmount = 300;

            newBalance = balance - 300;
            newExpenses = expenses + 300;

            tvImpact.setText(
                    "Formation fictive : 300 DT\n\n" +
                            "Nouvelles dépenses : " + format(newExpenses) + " DT\n" +
                            "Nouveau solde : " + format(newBalance) + " DT"
            );

            previewCalculated = true;
            return;
        }

        // Saisie du montant pour les autres décisions
        String amountText = etAmount.getText().toString().trim();

        if (amountText.isEmpty()) {
            etAmount.setError("Saisissez un montant");
            return;
        }

        try {
            decisionAmount = Double.parseDouble(
                    amountText.replace(",", ".")
            );
        } catch (NumberFormatException e) {
            etAmount.setError("Montant invalide");
            return;
        }

        if (decisionAmount <= 0) {
            etAmount.setError(
                    "Le montant doit être supérieur à zéro"
            );
            return;
        }

        // Réduire une catégorie
        if (selectedDecision == R.id.rbReduce) {

            String category = spinnerCategory.getSelectedItem().toString();

            double categoryAmount = 0;

            if (category.equals("Alimentation")) {
                categoryAmount = food;
            } else if (category.equals("Transport")) {
                categoryAmount = transport;
            } else if (category.equals("Autres dépenses")) {
                categoryAmount = other;
            }

            if (decisionAmount > categoryAmount) {
                etAmount.setError(
                        "La réduction dépasse le montant de cette catégorie."
                );
                return;
            }

            newExpenses = expenses - decisionAmount;
            newBalance = balance + decisionAmount;

            if (category.equals("Alimentation")) {
                newFood = food - decisionAmount;
            } else if (category.equals("Transport")) {
                newTransport = transport - decisionAmount;
            } else if (category.equals("Autres dépenses")) {
                newOther = other - decisionAmount;
            }

            tvImpact.setText(
                    "Catégorie : " + category + "\n" +
                            "Réduction : " + format(decisionAmount) + " DT\n" +
                            "Nouvelles dépenses : " + format(newExpenses) + " DT\n" +
                            "Nouveau solde : " + format(newBalance) + " DT"
            );
        }

        // Reporter une dépense
        else if (selectedDecision == R.id.rbPostpone) {

            if (decisionAmount > expenses) {
                etAmount.setError(
                        "Le montant dépasse les dépenses disponibles."
                );
                return;
            }

            postponedExpense = decisionAmount;

            newExpenses = expenses - decisionAmount;
            newBalance = balance + decisionAmount;

            tvImpact.setText(
                    "Dépense reportée : " +
                            format(decisionAmount) + " DT\n\n" +
                            "Dépenses de ce mois : " +
                            format(newExpenses) + " DT\n" +
                            "Nouveau solde : " +
                            format(newBalance) + " DT\n\n" +
                            "Cette dépense devra être prise en compte le mois suivant."
            );
        }

        previewCalculated = true;
    }

    private void validateDecision() {

        if (!previewCalculated) {
            Toast.makeText(this,
                    "Veuillez d'abord calculer l'impact.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        balance = newBalance;
        expenses = newExpenses;
        food = newFood;
        transport = newTransport;
        other = newOther;



        if (selectedDecision == R.id.rbReduce) {
            decisionDescription = "Réduction de " +
                    format(decisionAmount) + " DT dans " +
                    spinnerCategory.getSelectedItem().toString();

        } else if (selectedDecision == R.id.rbPostpone) {
            decisionDescription = "Report d'une dépense de " +
                    format(decisionAmount) + " DT";

        } else if (selectedDecision == R.id.rbTraining) {
            decisionDescription = "Participation à une formation de 300 DT";

        } else if (selectedDecision == R.id.rbCompare) {
            decisionDescription = "Comparaison des stratégies financières";
        }
        Intent result = new Intent();
        result.putExtra("decisionDescription", decisionDescription);


        result.putExtra("balance", balance);
        result.putExtra("expenses", expenses);
        result.putExtra("food", food);
        result.putExtra("transport", transport);
        result.putExtra("other", other);

        result.putExtra("postponedExpense", postponedExpense);

        setResult(RESULT_OK, result);

        Toast.makeText(this,
                "Décision validée avec succès !",
                Toast.LENGTH_LONG).show();

        finish();
    }

    private void updateBalance() {
        tvAvailableBalance.setText(format(balance) + " DT");
    }

    private String format(double value) {
        return String.format(Locale.FRANCE, "%.2f", value);
    }
}
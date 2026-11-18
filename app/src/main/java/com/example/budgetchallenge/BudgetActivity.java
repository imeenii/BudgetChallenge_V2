package com.example.budgetchallenge;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BudgetActivity extends AppCompatActivity {

    private EditText etIncome, etRent, etFood, etTransport;
    private EditText etBills, etOther, etGoal, etDuration;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget);
        // Bouton Retour vers la page d'accueil
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Récupération des champs
        etIncome = findViewById(R.id.etIncome);
        etRent = findViewById(R.id.etRent);
        etFood = findViewById(R.id.etFood);
        etTransport = findViewById(R.id.etTransport);
        etBills = findViewById(R.id.etBills);
        etOther = findViewById(R.id.etOther);
        etGoal = findViewById(R.id.etGoal);
        etDuration = findViewById(R.id.etDuration);

        btnContinue = findViewById(R.id.btnContinue);

        // Bouton de démarrage
        btnContinue.setOnClickListener(v -> calculateBudget());
    }


    private void calculateBudget() {

        // Vérifier les champs vides
        if (etIncome.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir votre revenu mensuel.", etIncome);
            return;
        }

        if (etRent.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir le montant du loyer.", etRent);
            return;
        }

        if (etFood.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir le montant de l'alimentation.", etFood);
            return;
        }

        if (etTransport.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir le montant du transport.", etTransport);
            return;
        }

        if (etBills.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir le montant des factures.", etBills);
            return;
        }

        if (etOther.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir les autres dépenses.", etOther);
            return;
        }

        if (etGoal.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir votre objectif d'épargne.", etGoal);
            return;
        }

        if (etDuration.getText().toString().trim().isEmpty()) {
            showError("Veuillez saisir la durée de simulation.", etDuration);
            return;
        }

        try {

            double income = readValue(etIncome);
            double rent = readValue(etRent);
            double food = readValue(etFood);
            double transport = readValue(etTransport);
            double bills = readValue(etBills);
            double other = readValue(etOther);
            double goal = readValue(etGoal);
            int duration = readDuration();

            if (income <= 0) {
                showError("Le revenu mensuel doit être supérieur à 0.", etIncome);
                return;
            }

            if (rent < 0) {
                showError("Le loyer ne peut pas être négatif.", etRent);
                return;
            }

            if (food < 0) {
                showError("L'alimentation ne peut pas être négative.", etFood);
                return;
            }

            if (transport < 0) {
                showError("Le transport ne peut pas être négatif.", etTransport);
                return;
            }

            if (bills < 0) {
                showError("Les factures ne peuvent pas être négatives.", etBills);
                return;
            }

            if (other < 0) {
                showError("Les autres dépenses ne peuvent pas être négatives.", etOther);
                return;
            }

            if (goal < 0) {
                showError("L'objectif d'épargne ne peut pas être négatif.", etGoal);
                return;
            }

            if (duration <= 0) {
                showError("La durée doit être supérieure à 0.", etDuration);
                return;
            }

            double totalExpenses = rent + food + transport + bills + other;
            double initialBalance = income - totalExpenses;

            if (totalExpenses > income) {

                new AlertDialog.Builder(this)
                        .setTitle("Attention : budget déficitaire")
                        .setMessage(
                                "Tes dépenses dépassent tes revenus de "
                                        + String.format("%.2f", totalExpenses - income)
                                        + " DT.\n\nVeux-tu continuer ?"
                        )
                        .setPositiveButton("Continuer", (dialog, which) ->
                                showBudgetSummary(
                                        income, totalExpenses,
                                        initialBalance, goal, duration,
                                        rent, food, transport, bills, other
                                )
                        )
                        .setNegativeButton("Modifier", null)
                        .show();

            } else {

                showBudgetSummary(
                        income, totalExpenses,
                        initialBalance, goal, duration,
                        rent, food, transport, bills, other
                );
            }

        } catch (NumberFormatException e) {

            new AlertDialog.Builder(this)
                    .setTitle("Erreur de saisie")
                    .setMessage("Veuillez saisir uniquement des nombres valides.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
    private void showError(String message, EditText field) {

        new AlertDialog.Builder(this)
                .setTitle("Erreur de saisie")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    field.setError(message);
                    field.requestFocus();
                })
                .show();
    }


    private double readValue(EditText field) {

        String value = field.getText().toString().trim();

        if (value.isEmpty()) {
            field.setError("Ce champ est obligatoire");
            field.requestFocus();
            throw new NumberFormatException();
        }

        return Double.parseDouble(value.replace(",", "."));
    }

    private int readDuration() {

        String value = etDuration.getText().toString().trim();

        if (value.isEmpty()) {
            etDuration.setError("La durée est obligatoire");
            etDuration.requestFocus();
            throw new NumberFormatException();
        }

        return Integer.parseInt(value);
    }


    private void showBudgetSummary(
            double income,
            double expenses,
            double balance,
            double goal,
            int duration,
            double rent,
            double food,
            double transport,
            double bills,
            double other
    ) {

        String message =
                "Revenu mensuel : " + String.format("%.2f", income) + " DT\n\n"
                        + "Dépenses totales : " + String.format("%.2f", expenses) + " DT\n\n"
                        + "Solde initial : " + String.format("%.2f", balance) + " DT\n\n"
                        + "Objectif d'épargne : " + String.format("%.2f", goal) + " DT\n\n"
                        + "Durée : " + duration + " mois";

        new AlertDialog.Builder(this)
                .setTitle("Résumé de ton budget")
                .setMessage(message)
                .setPositiveButton("Voir mon Dashboard", (dialog, which) -> {

                    Intent intent = new Intent(
                            BudgetActivity.this,
                            DashboardActivity.class
                    );

                    intent.putExtra("income", income);
                    intent.putExtra("expenses", expenses);
                    intent.putExtra("balance", balance);
                    intent.putExtra("goal", goal);
                    intent.putExtra("duration", duration);

                    intent.putExtra("rent", rent);
                    intent.putExtra("food", food);
                    intent.putExtra("transport", transport);
                    intent.putExtra("bills", bills);
                    intent.putExtra("other", other);

                    startActivity(intent);
                })
                .show();
    }
}


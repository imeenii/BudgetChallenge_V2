package com.example.budgetchallenge;

import android.os.Bundle;
import android.graphics.Color;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.app.AlertDialog;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView tabExpenses, tabIncome, tabSavings, tabGoals;
    private Budget budget;

    private ArrayList<String> decisionsHistorique = new ArrayList<>();
    private ArrayList<String> historiqueMois = new ArrayList<>();

    private double income, expenses, balance, goal;
    private double rent, food, transport, bills, other;

    private int duration;
    private int currentMonth = 1;
    private int monthsInDeficit = 0;

    private double cumulativeSavings = 0;
    private ArrayList<MonthlyResult> resultatsMensuels = new ArrayList<>();
    private double postponedExpense = 0;

    private EditText etMonthlySavings;
    private Button btnNextMonth;


    private boolean decisionsTraitees = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Récupérer les données reçues
        income = getIntent().getDoubleExtra("income", 1800);
        expenses = getIntent().getDoubleExtra("expenses", 1400);
        balance = getIntent().getDoubleExtra("balance", 400);
        goal = getIntent().getDoubleExtra("goal", 1200);
        duration = getIntent().getIntExtra("duration", 3);

        rent = getIntent().getDoubleExtra("rent", 500);
        food = getIntent().getDoubleExtra("food", 350);
        transport = getIntent().getDoubleExtra("transport", 150);
        bills = getIntent().getDoubleExtra("bills", 200);
        other = getIntent().getDoubleExtra("other", 200);
        budget = new Budget(
                income,
                rent,
                food,
                transport,
                bills,
                other,
                goal,
                duration
        );
        double totalBudget = budget.calculateTotalExpenses();
        if (totalBudget != expenses) {
            Log.d("BUDGET", "Attention : les dépenses ne correspondent pas !");
        } else {
            Log.d("BUDGET", "Les dépenses sont correctes !");
        }
        double soldeBudget = budget.calculateBalance();

        Log.d("BUDGET", "Total des dépenses : " + totalBudget);
        Log.d("BUDGET", "Solde initial : " + soldeBudget);

        // Récupérer les boutons
        Button btnDecisions = findViewById(R.id.btnDecisions);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnEvent = findViewById(R.id.btnEvent);
        Button btnAnalysis = findViewById(R.id.btnAnalysis);

        etMonthlySavings = findViewById(R.id.etMonthlySavings);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        // Bouton Décisions
        btnDecisions.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    DecisionsActivity.class
            );

            startActivityForResult(intent, 1);
        });

        // Bouton Historique
        btnHistory.setOnClickListener(v -> {

            if (historiqueMois.isEmpty()) {

                showDialog(
                        "Historique vide",
                        "Aucun mois n'a encore été enregistré."
                );

            } else {

                String[] historique =
                        historiqueMois.toArray(new String[0]);

                new AlertDialog.Builder(DashboardActivity.this)
                        .setTitle("Historique de la simulation")
                        .setItems(historique, null)
                        .setPositiveButton("Fermer", null)
                        .show();
            }
        });

        // Bouton Mois suivant
        btnNextMonth.setOnClickListener(v -> passerAuMoisSuivant());

        // Bouton Retour
        btnBack.setOnClickListener(v -> finish());

        // Bouton Événement imprévu
        btnEvent.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    EventActivity.class
            );

            intent.putExtra("balance", balance);
            intent.putExtra("expenses", expenses);

            intent.putExtra("rent", rent);
            intent.putExtra("food", food);
            intent.putExtra("transport", transport);
            intent.putExtra("bills", bills);
            intent.putExtra("other", other);

            intent.putExtra("cumulativeSavings", cumulativeSavings);
            intent.putExtra("postponedExpense", postponedExpense);

            startActivityForResult(intent, 2);
        });

        // Gestion des marges de l'écran
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

        // Récupérer les onglets
        tabExpenses = findViewById(R.id.tabExpenses);
        tabIncome = findViewById(R.id.tabIncome);
        tabSavings = findViewById(R.id.tabSavings);
        tabGoals = findViewById(R.id.tabGoals);

        // Afficher les données initiales
        actualiserDashboard();

        // Graphique des dépenses
        DonutChartView chart = findViewById(R.id.donutChart);

        chart.setData(
                rent,
                food,
                transport,
                bills,
                other
        );

        // Onglet Dépenses
        tabExpenses.setOnClickListener(v -> {

            selectTab(tabExpenses);
            showExpenses();

        });

        // Onglet Revenus
        tabIncome.setOnClickListener(v -> {

            selectTab(tabIncome);
            showIncome();

        });

        // Onglet Épargne
        tabSavings.setOnClickListener(v -> {

            selectTab(tabSavings);
            showSavings();

        });

        // Onglet Objectifs
        tabGoals.setOnClickListener(v -> {

            selectTab(tabGoals);
            showGoals();

        });

        // Bouton Analyse
        btnAnalysis.setOnClickListener(v -> ouvrirAnalyse());
    }

    // Actualiser les informations du Dashboard
    private void actualiserDashboard() {

        TextView txtIncome = findViewById(R.id.txtIncome);
        TextView txtExpenses = findViewById(R.id.txtExpenses);
        TextView txtBalance = findViewById(R.id.txtBalance);
        TextView txtSavings = findViewById(R.id.txtSavings);
        TextView txtMonth = findViewById(R.id.txtMonth);

        txtIncome.setText(
                String.format(Locale.FRANCE, "%.2f DT", income)
        );

        txtExpenses.setText(
                String.format(Locale.FRANCE, "%.2f DT", expenses)
        );

        txtBalance.setText(
                String.format(Locale.FRANCE, "%.2f DT", balance)
        );

        txtSavings.setText(
                String.format(Locale.FRANCE, "%.2f DT", cumulativeSavings)
        );

        txtMonth.setText(
                "Mois " + currentMonth + " sur " + duration
        );

        // Progression de l'objectif
        ProgressBar progressGoal = findViewById(R.id.progressGoal);
        TextView txtGoalProgress = findViewById(R.id.txtGoalProgress);
        TextView txtGoalPercent = findViewById(R.id.txtGoalPercent);

        int percent = 0;

        if (goal > 0) {

            percent = (int) Math.max(
                    0,
                    Math.min(100, (cumulativeSavings / goal) * 100)
            );
        }

        progressGoal.setProgress(percent);

        txtGoalProgress.setText(
                String.format(
                        Locale.FRANCE,
                        "%.2f DT sur %.2f DT",
                        cumulativeSavings,
                        goal
                )
        );

        txtGoalPercent.setText(percent + " % atteint");
    }

    // Ouvrir l'analyse budgétaire
    private void ouvrirAnalyse() {

        String etat;

        if (balance < 0) {

            etat = "Budget déficitaire : tes dépenses dépassent ton revenu.";

        } else if (balance == 0) {

            etat = "Budget équilibré : tes dépenses sont égales à ton revenu.";

        } else {

            etat = "Budget excédentaire : tu disposes d'un solde positif.";
        }

        // Calcul de l'objectif restant
        double montantRestant = Math.max(
                0,
                goal - cumulativeSavings
        );

        int moisRestants = Math.max(
                0,
                duration - currentMonth + 1
        );

        double epargneNecessaire = moisRestants > 0
                ? montantRestant / moisRestants
                : montantRestant;

        // Comparaison des trois stratégies
        double strategieA = Math.max(0, income - expenses);

        double strategieB = strategieA + 100;

        double strategieC = strategieA + 200;

        // Recommandation
        String recommandation;

        if (montantRestant <= 0) {

            recommandation =
                    "Objectif atteint ! Tu as déjà constitué l'épargne nécessaire. " +
                            "Aucun sacrifice supplémentaire n'est nécessaire.";

        } else if (strategieA >= epargneNecessaire) {

            recommandation =
                    "Stratégie A recommandée : ton budget actuel permet " +
                            "d'atteindre ton objectif sans réduire tes dépenses. " +
                            "C'est la solution qui demande le moins de sacrifices.";

        } else if (strategieB >= epargneNecessaire) {

            recommandation =
                    "Stratégie B recommandée : une réduction de 100 DT " +
                            "par mois permet d'atteindre ton objectif. " +
                            "Le sacrifice reste modéré.";

        } else if (strategieC >= epargneNecessaire) {

            recommandation =
                    "Stratégie C recommandée : une réduction de 200 DT " +
                            "par mois permet d'atteindre ton objectif. " +
                            "Cette solution demande davantage d'efforts.";

        } else {

            recommandation =
                    "Attention : même la stratégie C ne suffit pas " +
                            "pour atteindre ton objectif. Il faut revoir ton objectif " +
                            "ou augmenter la durée de simulation.";
        }

        // Calcul du taux d'épargne estimé
        double tauxEpargne = income > 0
                ? (Math.max(0, balance) / income) * 100
                : 0;

        // Construction du texte de l'analyse
        String analyse =
                etat + "\n\n" +

                        "Revenu : " + income + " DT\n" +
                        "Dépenses : " + expenses + " DT\n" +
                        "Solde : " + balance + " DT\n" +
                        "Épargne cumulée : " + cumulativeSavings + " DT\n" +

                        "Taux d'épargne estimé : " +
                        String.format(
                                Locale.FRANCE,
                                "%.1f",
                                tauxEpargne
                        ) + " %\n\n" +

                        "Objectif restant : " + montantRestant + " DT\n" +

                        "Épargne nécessaire par mois : " +
                        String.format(
                                Locale.FRANCE,
                                "%.1f",
                                epargneNecessaire
                        ) + " DT\n\n" +

                        "COMPARAISON DES STRATÉGIES\n\n" +

                        "Stratégie A - Budget actuel : " +
                        strategieA + " DT par mois\n" +

                        "Stratégie B - Réduction de 100 DT : " +
                        strategieB + " DT par mois\n" +

                        "Stratégie C - Réduction de 200 DT : " +
                        strategieC + " DT par mois\n\n" +

                        "RECOMMANDATION\n" +
                        recommandation;

        // Ouvrir la page Analyse
        Intent intent = new Intent(
                DashboardActivity.this,
                AnalysisActivity.class
        );

        intent.putExtra("analysis", analyse);

        startActivity(intent);
    }

    // Résultat du retour des autres activités
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            decisionsTraitees = true;

            String decision =
                    data.getStringExtra("decisionDescription");

            if (decision != null && !decision.isEmpty()) {

                decisionsHistorique.add(decision);
            }

            // Récupérer les nouvelles valeurs
            balance = data.getDoubleExtra("balance", balance);

            expenses = data.getDoubleExtra("expenses", expenses);

            food = data.getDoubleExtra("food", food);

            transport = data.getDoubleExtra("transport", transport);

            other = data.getDoubleExtra("other", other);

            actualiserDashboard();

            DonutChartView chart = findViewById(R.id.donutChart);

            chart.setData(
                    rent,
                    food,
                    transport,
                    bills,
                    other
            );
        }

        else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            cumulativeSavings = data.getDoubleExtra(
                    "cumulativeSavings",
                    cumulativeSavings
            );

            postponedExpense = data.getDoubleExtra(
                    "postponedExpense",
                    postponedExpense
            );

            decisionsTraitees = true;

            balance = data.getDoubleExtra("balance", balance);

            expenses = data.getDoubleExtra("expenses", expenses);

            rent = data.getDoubleExtra("rent", rent);

            food = data.getDoubleExtra("food", food);

            transport = data.getDoubleExtra("transport", transport);

            other = data.getDoubleExtra("other", other);

            actualiserDashboard();

            DonutChartView chart = findViewById(R.id.donutChart);

            chart.setData(
                    rent,
                    food,
                    transport,
                    bills,
                    other
            );
        }
    }

    // Changer l'apparence de l'onglet sélectionné
    private void selectTab(TextView selected) {

        TextView[] tabs = {
                tabExpenses,
                tabIncome,
                tabSavings,
                tabGoals
        };

        for (TextView tab : tabs) {

            tab.setBackgroundColor(Color.TRANSPARENT);

            tab.setTextColor(Color.parseColor("#68766C"));
        }

        selected.setBackgroundColor(Color.parseColor("#174C3B"));

        selected.setTextColor(Color.WHITE);
    }

    // Afficher les dépenses
    private void showExpenses() {

        String message =
                "Loyer : " + rent + " DT\n\n" +
                        "Alimentation : " + food + " DT\n\n" +
                        "Transport : " + transport + " DT\n\n" +
                        "Factures : " + bills + " DT\n\n" +
                        "Autres : " + other + " DT\n\n" +
                        "Total : " + expenses + " DT";

        showDialog("Détail des dépenses", message);
    }

    // Afficher les revenus
    private void showIncome() {

        String message =
                "Ton revenu mensuel est de :\n\n" +
                        income + " DT\n\n" +
                        "C'est le montant disponible chaque mois pour gérer ton budget.";

        showDialog("Mes revenus", message);
    }

    // Afficher l'épargne
    private void showSavings() {

        String message =
                "Épargne cumulée : " + cumulativeSavings + " DT\n\n" +
                        "Solde disponible : " + balance + " DT\n\n" +
                        "Tu peux utiliser une partie de ton solde pour constituer ton épargne.";

        showDialog("Mon épargne", message);
    }

    // Afficher les objectifs
    private void showGoals() {

        double monthlyRequired =
                duration > 0 ? goal / duration : 0;

        double remaining = Math.max(
                0,
                goal - cumulativeSavings
        );

        String message =
                "Objectif d'épargne : " + goal + " DT\n\n" +
                        "Durée : " + duration + " mois\n\n" +
                        "Montant restant : " + remaining + " DT\n\n" +
                        "Épargne moyenne nécessaire par mois : " +
                        String.format(
                                Locale.FRANCE,
                                "%.2f DT",
                                monthlyRequired
                        );

        showDialog("Mes objectifs", message);
    }

    // Passer au mois suivant
    private void passerAuMoisSuivant() {

        if (!decisionsTraitees) {

            showDialog(
                    "Décisions nécessaires",
                    "Tu dois d'abord traiter les décisions de ce mois avant de continuer."
            );

            return;
        }

        String valeur = etMonthlySavings.getText()
                .toString()
                .trim();

        if (valeur.isEmpty()) {

            showDialog(
                    "Attention",
                    "Entre un montant d'épargne."
            );

            return;
        }

        double montant;

        try {

            montant = Double.parseDouble(
                    valeur.replace(",", ".")
            );

        } catch (NumberFormatException e) {

            showDialog(
                    "Erreur",
                    "Montant invalide."
            );

            return;
        }

        if (montant < 0) {

            showDialog(
                    "Erreur",
                    "Le montant doit être positif."
            );

            return;
        }

        if (montant > Math.max(0, balance)) {

            showDialog(
                    "Attention",
                    "Tu ne peux pas épargner plus que ton solde disponible."
            );

            return;
        }

        // Ajouter l'épargne cumulée
        cumulativeSavings += montant;

        balance -= montant;
        MonthlyResult resultat = new MonthlyResult(
                currentMonth,
                income,
                expenses,
                balance,
                montant,
                cumulativeSavings,
                "Décisions du mois"
        );

        resultatsMensuels.add(resultat);
        Toast.makeText(
                this,
                "Résultats enregistrés : " + resultatsMensuels.size(),
                Toast.LENGTH_SHORT
        ).show();

        // Enregistrer le bilan du mois
        historiqueMois.add(
                "Mois " + currentMonth +
                        " : Revenus = " + income +
                        " DT, Dépenses = " + expenses +
                        " DT, Épargne = " + montant +
                        " DT, Solde restant = " + balance + " DT"
        );

        // Actualiser les données affichées
        actualiserDashboard();

        // Passer au mois suivant
        if (currentMonth < duration) {

            if (balance < 0) {

                monthsInDeficit++;
            }

            currentMonth++;

            decisionsTraitees = false;

            // Récupérer les dépenses reportées
            double depenseReportee = postponedExpense;

            // Recalculer les dépenses mensuelles
            expenses = rent + food + transport + bills + other
                    + depenseReportee;

            // Réinitialiser les dépenses reportées
            postponedExpense = 0;

            // Calculer le nouveau solde
            balance = income - expenses;

            etMonthlySavings.setText("");

            actualiserDashboard();

            // Actualiser le graphique
            DonutChartView chart = findViewById(R.id.donutChart);

            chart.setData(
                    rent,
                    food,
                    transport,
                    bills,
                    other
            );

        } else {

            // Fin de la simulation
            btnNextMonth.setEnabled(false);

            if (balance < 0) {

                monthsInDeficit++;
            }

            showDialog(
                    "Simulation terminée",
                    "Bravo ! Ton épargne totale est de "
                            + cumulativeSavings + " DT."
            );
        }
    }

    // Afficher une boîte de dialogue
    private void showDialog(String title, String message) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Fermer", null)
                .show();
    }
}
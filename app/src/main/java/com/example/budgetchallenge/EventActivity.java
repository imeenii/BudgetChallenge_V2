package com.example.budgetchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class EventActivity extends AppCompatActivity {

    private TextView txtEventTitle, txtEventDescription, txtEventCost;
    private ImageView imgEvent;
    private RadioGroup rgEventChoices;
    private Button btnValidateEvent, btnBackEvent;

    private int eventCost;
    private int eventIndex;

    private double balance, expenses;
    private double rent, food, transport, bills, other;
    private double cumulativeSavings, postponedExpense;

    private final String[] eventTitles = {
            "Panne de téléphone",
            "Dépense médicale imprévue",
            "Augmentation du loyer",
            "Opportunité de formation",
            "Réduction du transport"
    };

    private final String[] eventDescriptions = {
            "Ton téléphone est tombé en panne.",
            "Une dépense médicale imprévue apparaît.",
            "Ton loyer augmente à partir de ce mois.",
            "Une formation intéressante est disponible.",
            "Tu peux économiser sur tes frais de transport."
    };

    private final int[] eventCosts = {180, 250, 100, 300, -50};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        txtEventTitle = findViewById(R.id.txtEventTitle);
        txtEventDescription = findViewById(R.id.txtEventDescription);
        txtEventCost = findViewById(R.id.txtEventCost);
        imgEvent = findViewById(R.id.imgEvent);

        rgEventChoices = findViewById(R.id.rgEventChoices);
        btnValidateEvent = findViewById(R.id.btnValidateEvent);
        btnBackEvent = findViewById(R.id.btnBackEvent);

        Intent intent = getIntent();

        balance = intent.getDoubleExtra("balance", 0);
        expenses = intent.getDoubleExtra("expenses", 0);
        rent = intent.getDoubleExtra("rent", 0);
        food = intent.getDoubleExtra("food", 0);
        transport = intent.getDoubleExtra("transport", 0);
        bills = intent.getDoubleExtra("bills", 0);
        other = intent.getDoubleExtra("other", 0);

        cumulativeSavings =
                intent.getDoubleExtra("cumulativeSavings", 0);

        postponedExpense =
                intent.getDoubleExtra("postponedExpense", 0);

        afficherEvenementAleatoire();

        btnBackEvent.setOnClickListener(v -> finish());

        btnValidateEvent.setOnClickListener(v -> {

            if (rgEventChoices.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this,
                        "Choisis une réponse.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            appliquerChoix();
        });
    }

    private void afficherEvenementAleatoire() {

        eventIndex = new Random().nextInt(eventTitles.length);
        eventCost = eventCosts[eventIndex];

        // Créer l'objet Event
        Event evenement = new Event(
                eventTitles[eventIndex],
                eventDescriptions[eventIndex],
                eventCosts[eventIndex]
        );

        // Utiliser l'objet
        txtEventTitle.setText(evenement.getTitle());
        txtEventDescription.setText(evenement.getDescription());

        if (eventCost < 0) {
            txtEventCost.setText("Économie possible : 50 DT");
        } else if (eventIndex == 2) {
            txtEventCost.setText("Augmentation : 100 DT par mois");
        } else {
            txtEventCost.setText("Coût estimé : " + eventCost + " DT");
        }

        int[] eventImages = {
                R.drawable.telephone,
                R.drawable.medical,
                R.drawable.loyer,
                R.drawable.formation,
                R.drawable.transport
        };

        imgEvent.setImageResource(eventImages[eventIndex]);
    }

    private void appliquerChoix() {

        int choix = rgEventChoices.getCheckedRadioButtonId();

        // Réduction du transport
        if (eventIndex == 4) {

            transport = Math.max(0, transport - 50);
            expenses = Math.max(0, expenses - 50);
            balance += 50;

            retournerResultat(
                    "Tu as économisé 50 DT sur le transport."
            );
            return;
        }

        // Augmentation du loyer
        if (eventIndex == 2) {
            rent += 100;
        }

        // Choix 1 : réduire les dépenses
        if (choix == R.id.rbReduceExpenses) {

            double reduction = Math.min(50, food);

            food -= reduction;
            expenses += eventCost - reduction;
            balance -= eventCost - reduction;
        }

        // Choix 2 : utiliser l'épargne
        else if (choix == R.id.rbUseSavings) {

            double montantUtilise =
                    Math.min(cumulativeSavings, eventCost);

            cumulativeSavings -= montantUtilise;

            expenses += eventCost;
            balance -= eventCost - montantUtilise;
        }

        // Choix 3 : reporter la dépense
        else if (choix == R.id.rbPostponeExpense) {

            postponedExpense += eventCost;

            Toast.makeText(this,
                    "La dépense sera reportée au mois suivant.",
                    Toast.LENGTH_SHORT).show();
        }

        // Choix 4 : accepter le déficit
        else if (choix == R.id.rbAcceptDeficit) {

            expenses += eventCost;
            balance -= eventCost;
        }

        retournerResultat("Ton choix a été enregistré.");
    }

    private void retournerResultat(String message) {

        Intent result = new Intent();

        result.putExtra("balance", balance);
        result.putExtra("expenses", expenses);

        result.putExtra("rent", rent);
        result.putExtra("food", food);
        result.putExtra("transport", transport);
        result.putExtra("bills", bills);
        result.putExtra("other", other);

        result.putExtra("cumulativeSavings", cumulativeSavings);
        result.putExtra("postponedExpense", postponedExpense);

        setResult(RESULT_OK, result);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        finish();
    }
}
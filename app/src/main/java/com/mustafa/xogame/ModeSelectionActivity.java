package com.mustafa.xogame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ModeSelectionActivity extends AppCompatActivity {
    private Button pvpButton;
    private Button pvaiButton;
    private LinearLayout difficultyLayout;
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        pvpButton = findViewById(R.id.pvpButton);
        pvaiButton = findViewById(R.id.pvaiButton);
        difficultyLayout = findViewById(R.id.difficultyLayout);
        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);
    }

    private void setupClickListeners() {
        pvpButton.setOnClickListener(v -> {
            Intent intent = new Intent(ModeSelectionActivity.this, GameActivity.class);
            intent.putExtra("mode", "PVP");
            startActivity(intent);
        });

        pvaiButton.setOnClickListener(v -> {
            difficultyLayout.setVisibility(View.VISIBLE);
        });

        easyButton.setOnClickListener(v -> startAIGame("EASY"));
        mediumButton.setOnClickListener(v -> startAIGame("MEDIUM"));
        hardButton.setOnClickListener(v -> startAIGame("HARD"));
    }

    private void startAIGame(String difficulty) {
        Intent intent = new Intent(ModeSelectionActivity.this, GameActivity.class);
        intent.putExtra("mode", "PVAI");
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
}

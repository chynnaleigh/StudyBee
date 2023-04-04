package com.example.finalproject.flashcards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalproject.R;

public class AddFlashcardSetActivity extends AppCompatActivity {
    private EditText editFlashCardSetName;
    private RecyclerView cardRecycler;
    private Button addCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard_set);

        editFlashCardSetName = findViewById(R.id.flashcard_set_title);
        addCard = findViewById(R.id.add_card_button);
        cardRecycler = findViewById(R.id.card_recycler);

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
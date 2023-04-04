package com.example.finalproject.flashcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddFlashcardsActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {
    private EditText editFlashCardSetName, editSideA, editSideB;
    private RecyclerView cardRecycler;
    private Button addCard, saveCard;

    private String courseId, flashcardSetId;
    private int cardCount = 0;
    private List<Card> cardList = new ArrayList<>();
    private Date now = new Date();
    private long timestamp = now.getTime();
    private CardAdapter cardAdapter;

    private FirebaseFirestore db;
    private CollectionReference colCardRef;
    private DocumentReference courseRef, flashcardSetRef, cardRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard_set);

        editFlashCardSetName = findViewById(R.id.flashcard_set_title);
        addCard = findViewById(R.id.add_card_button);
        cardRecycler = findViewById(R.id.card_recycler);

        cardRecycler.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(cardList, this);
        cardRecycler.setAdapter(cardAdapter);

        courseId = getIntent().getStringExtra("courseId");
        flashcardSetId = getIntent().getStringExtra("flashcardSetId");

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        flashcardSetRef = courseRef.collection("flashcards").document(flashcardSetId);
        colCardRef = flashcardSetRef.collection("cards");
        cardRef = colCardRef.document();

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleInput = editFlashCardSetName.getText().toString();
                if(TextUtils.isEmpty(editFlashCardSetName.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter a flashcard set name first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    flashcardSetRef.update("flashcardSetTitle", titleInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "AddFlashcardsActivity --- POPUP WINDOW");
                            createDialogBuilder(null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddFlashcardsActivity.this, "Error creating new flashcard",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        if(colCardRef != null) {
            colCardRef.orderBy("cardTimestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    if(error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }

                    cardList.clear();

                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Card card = documentSnapshot.toObject(Card.class);
                        cardList.add(card);
                    }
                    cardAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void createDialogBuilder(Card card) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popView = getLayoutInflater().inflate(R.layout.popup_new_card, null);

        editSideA = popView.findViewById(R.id.side_a_view);
        editSideB = popView.findViewById(R.id.side_b_view);
        saveCard = popView.findViewById(R.id.save_card_button);

        if(card != null) {
            editSideA.setText(card.getCardSideA());
            editSideB.setText(card.getCardSideB());
        }

        dialogBuilder.setView(popView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        saveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flashcard flashcard = new Flashcard(editFlashCardSetName.getText().toString(), flashcardSetId, cardCount);
                String sideAInput = editSideA.getText().toString();
                String sideBInput = editSideB.getText().toString();

                if(TextUtils.isEmpty(sideAInput) || TextUtils.isEmpty(sideBInput)) {
                    Toast.makeText(getApplicationContext(), "Please input both sides",
                            Toast.LENGTH_SHORT).show();
                } else if (card != null) {
                    cardRef = colCardRef.document(card.getCardId());
                    cardRef.update("cardSideA", sideAInput);
                    cardRef.update("cardSideB", sideBInput);

                    Toast.makeText(getApplicationContext(), "Flashcard updated",
                            Toast.LENGTH_SHORT).show();
                } else {
                    cardRef = colCardRef.document();
                    Card card = new Card();
                    card.setCardSideA(sideAInput);
                    card.setCardSideB(sideBInput);
                    card.setCardId(cardRef.getId());
                    card.setCardTimestamp(timestamp);

                    flashcard.setCardCount(cardCount++);
                    flashcardSetRef.update("cardCount", cardCount);

                    saveNewCard(card);
                }

                dialog.dismiss();
            }
        });
    }

    public void saveNewCard(Card card) {
        cardRef.set(card).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG", "AddFlashcardsActivity -- NEW CARD SAVED");
                Toast.makeText(getApplicationContext(), "Added",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddFlashcardsActivity.this, "Could not add",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCardItemClick(Card card) {
        createDialogBuilder(card);
    }
}
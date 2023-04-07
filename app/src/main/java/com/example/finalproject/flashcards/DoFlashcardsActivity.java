package com.example.finalproject.flashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoFlashcardsActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView doneButton;
    private TextView flashcardSetTitle;
    private ViewPager cardsViewPager;
    private GestureDetectorCompat gestureDetector;

    private String courseId, flashcardSetId;
    private int totalCards = 0;
    private List<Card> cardList = new ArrayList<>();
    private CardPagerAdapter cardPagerAdapter;

    private FirebaseFirestore db;
    private DocumentReference courseRef, flashcardSetRef;
    private CollectionReference cardRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_flashcards);

        backButton = findViewById(R.id.do_flashcards_back_button);
        doneButton = findViewById(R.id.do_flashcards_done_button);
        flashcardSetTitle = findViewById(R.id.do_flashcards_title_view);
        cardsViewPager = findViewById(R.id.do_flashcards_view_pager);

        cardPagerAdapter = new CardPagerAdapter(this, cardList);
        cardsViewPager.setAdapter(cardPagerAdapter);
        cardsViewPager.setPageTransformer(true, new ViewPagerStack());
        cardsViewPager.setOffscreenPageLimit(4);
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());

        courseId = getIntent().getStringExtra("courseId");
        flashcardSetId = getIntent().getStringExtra("flashcardSetId");

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        flashcardSetRef = courseRef.collection("flashcards").document(flashcardSetId);
        cardRef = flashcardSetRef.collection("cards");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoFlashcardsActivity.this, FlashcardActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TO DO: LOGIC FOR HOW MANY CARDS THEY UNDERSTODD AND AN ALERTDIALOG FOR IT

                Intent intent = new Intent(DoFlashcardsActivity.this, FlashcardActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        flashcardSetRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    String setTitle = documentSnapshot.getString("flashcardSetTitle");
                    totalCards = documentSnapshot.getLong("cardCount").intValue();
                    Log.d("TAG", "DoFlashcardsActivity --- NUMBER OF CARDS IN THIS SET " + totalCards);

                    if(setTitle.length() > 20) {
                        setTitle = setTitle.substring(0, 20) + "...";
                    }

                    flashcardSetTitle.setText(setTitle);

                    loadCards();
                } else {
                    Log.d("TAG", "No such document");
                }
            }
        });
    }

    public void loadCards() {
        cardRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Card card = document.toObject(Card.class);
                        cardList.add(card);
                    }
                }
                Collections.shuffle(cardList);
                Log.d("TAG", "DoFlashActivity --- Card List: " + cardList);
                cardPagerAdapter.notifyDataSetChanged();

                cardsViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        gestureDetector.onTouchEvent(motionEvent);
                        return false;
                    }
                });
            }
        });
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if(Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if(diffX > 0) {
                        cardPagerAdapter.swipeRight();
                    } else {
                        cardPagerAdapter.swipeLeft();
                    }
                    result = true;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            cardPagerAdapter.flipCard();
            return true;
        }
    }

    public class ViewPagerStack implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;
        @Override
        public void transformPage(@NonNull View page, float position) {
            if(position >= 0){
                page.setScaleX(MIN_SCALE - 0.05f * position);
                page.setScaleY(MIN_SCALE);

                page.setTranslationX(-page.getWidth() * position);
                page.setTranslationY(-30 * position);
            }
        }
    }
}
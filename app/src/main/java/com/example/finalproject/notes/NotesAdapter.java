package com.example.finalproject.notes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<Note> notesList;
    private OnNoteClickListener onNoteClickListener;

    public NotesAdapter(List<Note> notesList, OnNoteClickListener onNoteClickListener) {
        this.notesList = notesList;
        this.onNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_view_note, parent, false);
        return new NotesViewHolder(itemView, onNoteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.titleItemNotes.setText(note.getTitle());
        holder.bodyItemNotes.setText(note.getPreview());
    }

    @Override
    public int getItemCount() {
        Log.d("TAG", "NotesAdapter -- ITEM COUNT " + notesList.size());
        return notesList.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleItemNotes;
        public TextView bodyItemNotes;
        public LinearLayout noteItem;
        private OnNoteClickListener onNoteClickListener;
        public NotesViewHolder(@NonNull View itemView, OnNoteClickListener onNoteClickListener) {
            super(itemView);
            titleItemNotes = itemView.findViewById(R.id.it_title_note);
            bodyItemNotes = itemView.findViewById(R.id.it_body_note);
            noteItem = itemView.findViewById(R.id.note);
            this.onNoteClickListener = onNoteClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteClickListener.onNoteItemClick(notesList.get(getAdapterPosition()));
        }
    }

    public interface OnNoteClickListener {
        void onNoteItemClick(Note note);
    }
}



//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            private GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    onNoteClickListener.onNoteItemClick(note);
//                    return super.onSingleTapUp(e);
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                    builder.setMessage("Are you sure you want to delete this note?")
//                            .setCancelable(false)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    // Delete note from Firestore
//                                    DocumentReference noteRef = FirebaseFirestore.getInstance()
//                                            .collection("notes").document(note.getNoteId());
//                                    noteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            Toast.makeText(holder.itemView.getContext(),
//                                                    "Note deleted successfully", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(holder.itemView.getContext(),
//                                                    "Error deleting note: " + e.getMessage(),
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                    super.onLongPress(e);
//                }
//            });
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });

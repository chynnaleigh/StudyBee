package com.example.finalproject.notes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String body;
    private String noteId;

    public Note() {

    }

    public Note(String title, String body, String noteId) {
        this.title = title;
        this.body = body;
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getPreview() {
        if(body == null || body.isEmpty()) {
            return  "";
        } else if(body.length() <= 50) {
            return body;
        } else {
            return body.substring(0, 50) + "...";
        }
    }

}

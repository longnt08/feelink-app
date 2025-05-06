package com.example.diaryapp.data.sync;

import android.util.Log;

import com.example.diaryapp.data.local.entities.Entry;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirestoreRepository {
    private static final String TAG = "FirestoreRepository";
    private final FirebaseFirestore db;
    private final String currentUserId;
    
    public FirestoreRepository(String firebaseUserId) {
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = firebaseUserId;
    }
    
    public Task<Void> uploadEntry(Entry entry) {
        Map<String, Object> entryMap = convertEntryToMap(entry);
        
        String documentId = entry.getFirebaseId();
        if (documentId == null || documentId.isEmpty()) {
            documentId = UUID.randomUUID().toString();
        }
        
        final String finalDocId = documentId;
        
        return db.collection("users")
                .document(currentUserId)
                .collection("entries")
                .document(documentId)
                .set(entryMap)
                .addOnSuccessListener(aVoid -> {
                    entry.setFirebaseId(finalDocId);
                    Log.d(TAG, "Entry uploaded successfully with ID: " + finalDocId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading entry", e);
                });
    }
    
    public Task<Void> deleteEntry(Entry entry) {
        if (entry.getFirebaseId() == null || entry.getFirebaseId().isEmpty()) {
            // Entry chưa từng được đồng bộ lên server
            return Tasks.forResult(null);
        }
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("isDeleted", true);
        updates.put("updatedAt", new Date().getTime());
        
        return db.collection("users")
                .document(currentUserId)
                .collection("entries")
                .document(entry.getFirebaseId())
                .update(updates);
    }
    
    public Task<QuerySnapshot> getEntriesUpdatedSince(long timestamp) {
        return db.collection("users")
                .document(currentUserId)
                .collection("entries")
                .whereGreaterThan("updatedAt", timestamp)
                .get();
    }
    
    private Map<String, Object> convertEntryToMap(Entry entry) {
        Map<String, Object> entryMap = new HashMap<>();
        entryMap.put("title", entry.getTitle());
        entryMap.put("content", entry.getContent());
        entryMap.put("mood", entry.getMood());
        entryMap.put("createdAt", entry.getCreatedAt());
        entryMap.put("updatedAt", entry.getUpdatedAt() > 0 ? entry.getUpdatedAt() : new Date().getTime());
        entryMap.put("isDeleted", entry.isDeleted());
        
        return entryMap;
    }
    
    public Entry convertDocumentToEntry(DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        String title = document.getString("title");
        String content = document.getString("content");
        String mood = document.getString("mood");
        Long createdAt = document.getLong("createdAt");
        Long updatedAt = document.getLong("updatedAt");
        Boolean isDeleted = document.getBoolean("isDeleted");
        
        // Tạo Entry mới từ dữ liệu Firestore
        Entry entry = new Entry(0, title, content, mood, createdAt != null ? createdAt : 0);
        entry.setFirebaseId(document.getId());
        if (updatedAt != null) {
            entry.setUpdatedAt(updatedAt);
        }
        if (isDeleted != null) {
            entry.setDeleted(isDeleted);
        }
        
        return entry;
    }
}
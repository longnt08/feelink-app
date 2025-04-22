package com.example.diaryapp.data.sync;

public class SyncResult {
    public boolean success;
    public Exception error;
    public int localUploaded;
    public int remoteDownloaded;
    
    public SyncResult() {
        this.success = false;
        this.localUploaded = 0;
        this.remoteDownloaded = 0;
        this.error = null;
    }
}
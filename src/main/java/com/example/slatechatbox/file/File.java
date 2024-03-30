package com.example.slatechatbox.file;

public class File {
    
    String filename;
    String message;
    int uid;
    String senderName;
    String timeStampMilliseconds;

    public File(String filename, String message, int uid, String senderName, String timeStampMilliseconds) {
        this.filename = filename;
        this.message = message;
        this.uid = uid;
        this.senderName = senderName;
        this.timeStampMilliseconds = timeStampMilliseconds;
    }

    public File() {}

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTimeStampMilliseconds() {
        return timeStampMilliseconds;
    }

    public void setTimeStampMilliseconds(String timeStampMilliseconds) {
        this.timeStampMilliseconds = timeStampMilliseconds;
    }

}

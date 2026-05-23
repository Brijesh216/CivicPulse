package com.civicpulse.dto;

public class ComplaintUpdateRequest {
    private String updateText;
    private String photoUrl;
    private String status;

    public String getUpdateText() { return updateText; }
    public void setUpdateText(String updateText) { this.updateText = updateText; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

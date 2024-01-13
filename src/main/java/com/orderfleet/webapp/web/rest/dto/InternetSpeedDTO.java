package com.orderfleet.webapp.web.rest.dto;
/**
 * A DTO for the Internet Speed
 *
 * @author Rakhi Vineeth
 * @since Jan 05, 2024
 * @version 1.116.2
 */
public class InternetSpeedDTO {
    private String username;
    private String uploadSpeed;
    private String downloadSpeed;
    private String currentDateTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(String uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public String getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(String downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    @Override
    public String toString() {
        return "InternetSpeedDTO{" +
                "username='" + username + '\'' +
                ", uploadSpeed='" + uploadSpeed + '\'' +
                ", downloadSpeed='" + downloadSpeed + '\'' +
                ", currentDateTime='" + currentDateTime + '\'' +
                '}';
    }
}

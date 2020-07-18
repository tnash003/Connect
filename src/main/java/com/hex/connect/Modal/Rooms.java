package com.hex.connect.Modal;

public class Rooms {
    String roomId;
    String roomTitle;
    String roomDescription;
    String eventDate;
    String groupIcon;
    String createdBy;
    String deepLink;

    public Rooms() {
    }

    public Rooms(String roomId, String roomTitle, String roomDescription, String eventDate, String groupIcon, String createdBy, String deepLink) {
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.roomDescription = roomDescription;
        this.eventDate = eventDate;
        this.groupIcon = groupIcon;
        this.createdBy = createdBy;
        this.deepLink = deepLink;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }
}

package com.skillcourt.structures;

public class SessionData {
    private String sessionID;
    private String sessionDate;

    public SessionData(String sessionID, String sessionDate) {
        this.sessionID = sessionID;
        this.sessionDate = sessionDate;
    }


    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }
}

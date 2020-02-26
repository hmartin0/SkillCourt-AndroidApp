package com.skillcourt.structures;

public class PlayerData {
    private String Id;
    private String Date;
    private String GTime;
    private String Score;
    private String Hits;
    private String SessioPlayerID;
    private String Notes;

    public PlayerData(String id, String date, String gTime, String score, String hits, String sessionPlayerID, String notes) {
        Id = id;
        Date = date;
        GTime = gTime;
        Score = score;
        Hits = hits;
        SessioPlayerID = sessionPlayerID;
        Notes = notes;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getGTime() {
        return GTime;
    }

    public void setGTime(String GTime) {
        this.GTime = GTime;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getHits() {
        return Hits;
    }

    public void setHits(String hits) {
        Hits = hits;
    }

    public String getSessioPlayerID() {
        return SessioPlayerID;
    }

    public void setSessioPlayerID(String sessioPlayerID) {
        SessioPlayerID = sessioPlayerID;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}

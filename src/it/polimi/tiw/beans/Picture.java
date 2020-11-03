package it.polimi.tiw.beans;

import java.sql.Timestamp;


public class Picture {
    public int picture_id;
    String title;
    Timestamp ins_date;
    String descr;
    String filepath;

    public Picture() {

    }

    public int getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(int picture_id) {
        this.picture_id = picture_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getIns_date() {
        return ins_date;
    }

    public void setIns_date(Timestamp ins_date) {
        this.ins_date = ins_date;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
}

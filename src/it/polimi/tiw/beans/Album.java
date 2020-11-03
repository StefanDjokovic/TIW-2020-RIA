package it.polimi.tiw.beans;

import java.sql.Timestamp;


public class Album {
    public int id;
    public String name;
    public Timestamp creation_date;

    public Album() {

    }
    
    public int get_id() {
    	return id;
    }
    
    public void set_id(int id) {
    	this.id = id;
    }

    public String get_name() {
    	return name;
    }
    
    public void set_name(String name) {
    	this.name = name;
    }

    public Timestamp getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Timestamp creation_date) {
        this.creation_date = creation_date;
    }

    
}

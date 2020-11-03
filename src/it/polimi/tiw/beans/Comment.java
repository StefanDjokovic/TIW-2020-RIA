package it.polimi.tiw.beans;

import java.sql.Timestamp;


public class Comment {
    public int id;
    public String username;
    public String comment;
    public Timestamp creation_date;
    public int pic_id;

    public Comment() {

    }
    
    public int get_picId() {
    	return pic_id;
    }
    
    public void set_picId(int pic_id) {
    	this.pic_id = pic_id;
    }
    
    public int get_id() {
    	return id;
    }
    
    public void set_id(int id) {
    	this.id = id;
    }

    public String get_username() {
    	return username;
    }
    
    public void set_username(String username) {
    	this.username = username;
    }
    
    public String get_comment() {
    	return comment;
    }
    
    public void set_comment(String comment) {
    	this.comment = comment;
    }
   

    public Timestamp getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Timestamp creation_date) {
        this.creation_date = creation_date;
    }

    
}

package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Picture;

public class PictureDAO {
	private Connection con;

	public PictureDAO(Connection connection) {
		this.con = connection;
	}
	
	// Checks if the picture exists by checking the id
	public boolean idExists(int pic_id) throws SQLException {
		String query = "SELECT picture_id FROM picture WHERE picture_id = " + pic_id + ";";
		
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			if (result.next())
				return true;
			return false;
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				pstatement.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
	}
	
	// Finds all the picture of the selected album
	public List<Picture> findAlbumPics(int album_id) throws SQLException {
		List<Picture> pictures = new ArrayList<Picture>();
		
		String query = "SELECT p.picture_id, p.title, p.ins_date, p.descr, p.filepath\r\n" + 
				"FROM album AS a JOIN album_contains_pic AS acp ON a.id = acp.album_id\r\n" + 
				"JOIN picture AS p ON acp.pic_id = p.picture_id WHERE a.id = " + album_id + ";";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				Picture pic = new Picture();
				pic.setPicture_id(result.getInt("picture_id"));
				pic.setTitle(result.getString("title"));
				pic.setIns_date(result.getTimestamp("ins_date"));
				pic.setDescr(result.getString("descr"));
				pic.setFilepath(result.getString("filepath"));
				pictures.add(pic);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				pstatement.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		
		return pictures;
	}

	// Gets all the pictures in the DB
	public List<Picture> findAllPics() throws SQLException {
		List<Picture> pictures = new ArrayList<Picture>();
		String query = "SELECT * FROM picture ORDER BY ins_date";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				Picture pic = new Picture();
				pic.setPicture_id(result.getInt("picture_id"));
				pic.setTitle(result.getString("title"));
				pic.setIns_date(result.getTimestamp("ins_date"));
				pic.setDescr(result.getString("descr"));
				pic.setFilepath(result.getString("filepath"));
				pictures.add(pic);
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				pstatement.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return pictures;
	}

}

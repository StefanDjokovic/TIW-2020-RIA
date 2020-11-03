package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class OrderDAO
 */


public class OrderDAO {
	private Connection con;

	public OrderDAO(Connection connection) {
		this.con = connection;
	}
	
	// Adds the reference to the file that keeps the order of the albums of the user
	public void addOrder(int user_id, String filepath) throws SQLException {

		String query = "INSERT INTO tiwexam.pers_order (user_id, path) VALUES (? , ?)";
	
		try (PreparedStatement pstatement = con.prepareStatement(query);) { 
			pstatement.setInt(1, user_id);
			pstatement.setString(2, filepath);
			pstatement.executeUpdate();
		} 
	}

	// Gets the path for the file that keeps the order of the albums of the user
	public String getOrder(int user_id) throws SQLException {
		String query = "SELECT path FROM pers_order WHERE user_id = ?";		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, user_id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					return result.getString("path");

				}
			}
		}
	}

}

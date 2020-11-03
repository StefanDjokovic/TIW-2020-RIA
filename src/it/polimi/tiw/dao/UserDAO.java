package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	// Checks the credentials for the user
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  id, username, name, surname FROM user  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					System.out.println("Username from SQL: " + result.getString("username"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
	
	// Register the user to the database
	public void register(String username, String name, String surname, String email, String password) throws SQLException {
		
		String query = "INSERT INTO tiwexam.user (username, name, surname, email, password) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) { 
			pstatement.setString(1, username);
			pstatement.setString(2, name);
			pstatement.setString(3, surname);
			pstatement.setString(4, email);
			pstatement.setString(5, password);

			pstatement.executeUpdate();
		} 
	}
	
	// Gets the id of the user, returns -2 if it doesn't exist
	public int getIdFromUsername(String username) throws SQLException {
		System.out.println("Username: " + username);
		
		String query = "SELECT * FROM user WHERE username = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, username);

			result = pstatement.executeQuery();
			while (result.next()) {
				return result.getInt("id");
			}
			return -2;
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
}

package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Comment;

public class CommentDAO {
	private Connection con;

	public CommentDAO(Connection connection) {
		this.con = connection;
	}
	
	// Adds the comment to the right picture from the corresponding user 
	public void addComment(int pic_id, int user_id, String comment) throws SQLException {
		String query = "INSERT INTO tiwexam.comment (pic_id, user_id, text) VALUES (? , ?, ?)";
	
		try (PreparedStatement pstatement = con.prepareStatement(query);) { 
			pstatement.setInt(1, pic_id);
			pstatement.setInt(2, user_id);
			pstatement.setString(3, comment);
			pstatement.executeUpdate();
		} 
	}

	// Gets all the comments from a pic
	public List<Comment> getComments(int pic_id) throws SQLException {
		List<Comment> comments = new ArrayList<Comment>();
		String query = "SELECT  username, text, date FROM comment JOIN user ON comment.user_id = user.id WHERE pic_id = " + pic_id + " ORDER BY date;";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				Comment comment = new Comment();
				comment.set_username(result.getString("username"));
				comment.set_comment(result.getString("text"));
				comment.setCreation_date(result.getTimestamp("date"));
				comments.add(comment);
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
		return comments;
	}
}

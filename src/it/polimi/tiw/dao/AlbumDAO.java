package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Album;

public class AlbumDAO {
	private Connection con;

	public AlbumDAO(Connection connection) {
		this.con = connection;
	}

	// All the albums with their id, name and creating data
	public List<Album> findAllAlbums() throws SQLException {
		List<Album> albums = new ArrayList<Album>();
		String query = "SELECT * FROM album ORDER BY creation_date";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				Album album = new Album();
				album.set_id(result.getInt("id"));
				album.set_name(result.getString("name"));
				album.setCreation_date(result.getTimestamp("creation_date"));
				albums.add(album);
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
		return albums;
	}

}
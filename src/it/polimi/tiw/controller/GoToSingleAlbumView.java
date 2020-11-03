package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.dao.PictureDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.beans.*;

@WebServlet("/GoToSingleAlbumView")
public class GoToSingleAlbumView extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());

	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		// Getting the album_id from the parameter
		Integer album_id = null;
		try {
			album_id = Integer.parseInt(req.getParameter("album_id"));
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("No pic_id provided");
			return;
		}
		
		
		// Getting all the pictures in the selected album
		PictureDAO picDAO = new PictureDAO(connection);
		List<Picture> pictures;
		try {
			pictures = picDAO.findAlbumPics(album_id);
		} catch (SQLException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Not possible to recover missions");
			return;
		}
		
		
		// Formatting the output and sending it to the user
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(pictures);
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);
	
	}
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}
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

import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.beans.*;

@WebServlet("/OpenInfo")
public class OpenInfoAndComments extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		// Getting the pic_id from URL parameter
		Integer pic_id = null;
		try {
			pic_id = Integer.parseInt(req.getParameter("pic_id"));
			System.out.println(pic_id);
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("No pic_id provided");
			return;
		}
		
		// Getting all the comments for the selected picture
		CommentDAO commentDAO = new CommentDAO(connection);
		List<Comment> comments;
		try {
			comments = commentDAO.getComments(pic_id);
		} catch (SQLException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Not possible to get comments");
			return;
		}
		
		// Generating output in JSON and sending it
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(comments);
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);
	
	}
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}
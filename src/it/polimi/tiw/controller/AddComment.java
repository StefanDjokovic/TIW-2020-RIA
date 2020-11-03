package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.google.gson.Gson;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.PictureDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddComment() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Comment reqComment = new Gson().fromJson(request.getReader().readLine(), Comment.class);
		
		String comment = reqComment.get_comment();
		HttpSession session = request.getSession();
		int userID = (int)session.getAttribute("user_id");
		
		// If the commment is empty send error message
		if (comment.strip().length() <= 0) {
			System.out.println("The tried to trick us with such a short message!");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Comments cannot be null");
			return;
		}
		
		// If the commment is over 280 characters send error message
		if (comment.length() > 280) {
			System.out.println("How did such a long message come through?!");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Comments can be of maximum 280 characters");
			return;
		}
		
		// Checking that the username sent by the user is equal to the one in the session
		if (!(((String)session.getAttribute("username")).equals(reqComment.get_username()))) {
			System.out.println("The tried to trick us!");
			System.out.println("What the real username was: " + session.getAttribute("username") + " ; size: " + ((String)session.getAttribute("username")).length());
			System.out.println("What they tried to send us: " + reqComment.get_username() + " ; size: " + reqComment.get_username().length());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("User auth doesn't match the username sent by the broswer");
			return;
		}
		
		// Checking if the picture exists, then adding the comment
		PictureDAO pictureDAO = new PictureDAO(connection);
		int pic_id = reqComment.get_picId();
		try {
			boolean exists = pictureDAO.idExists(pic_id);
			if (exists) {
				CommentDAO commentDAO = new CommentDAO(connection);
				commentDAO.addComment(pic_id, userID, comment);
			}
		} catch (SQLException e) {
			System.out.println("Apparently the pic id doesn't exist?!");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check credentials");
			return;
		}
		
		System.out.println("Comment added");
		
		// Send confirmation message
        String json = new Gson().toJson(reqComment.get_comment());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
	}
}
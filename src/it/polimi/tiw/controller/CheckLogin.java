package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.MultipartConfig;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CheckLoginInfo")
@MultipartConfig
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckLogin() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse res)
			throws ServletException, IOException {
		
		// Getting the name and password as parameters of the form and checking if they are not null
		String username = null;
		String pwd = null;
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pwd = StringEscapeUtils.escapeJava(request.getParameter("password"));
			if (username == null || pwd == null || username.isEmpty() || pwd.isEmpty()) {
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				System.out.println("Credentials must be not null");
				res.getWriter().println("Credentials must be not null");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.getWriter().println("Bad request!");
			return;
		}
		

		System.out.println("Accessing the DB to get check the credentials");
		// Query db to authenticate for user
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			System.out.println("Trying to access with username: " + username);
			user = userDao.checkCredentials(username, pwd);
		} catch (SQLException e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Internal server error, retry later");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise show login page with error message 
		// (note: hot fix for db case insensitive columns!?)
		if (user == null || !(user.getUsername().equals(username))) {
			System.out.println("User Unauthorized");
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().println("Incorrect credentials");
		} else {
			System.out.println("User Authorized");
			request.getSession().setAttribute("username", user.getUsername());
			request.getSession().setAttribute("user_id", user.getId());
			res.setStatus(HttpServletResponse.SC_OK);
			res.setContentType("application/json");
			res.setCharacterEncoding("UTF-8");
			res.getWriter().print(username);
		}

	}
}

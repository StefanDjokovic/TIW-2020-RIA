package it.polimi.tiw.controller;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.OrderDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.beans.*;

@WebServlet("/GoToAlbumPage")
public class GoToAlbumPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		// Checking if user has logged in already for this session
		String loginpath = getServletContext().getContextPath() + "/login.html";
		HttpSession session = req.getSession();
		if (session.isNew() || session.getAttribute("username") == null) {
			System.out.println("It seems that the user was not Authenticated!");
			res.sendRedirect(loginpath);
			return;
		}
		
		// Getting all the available albums in the DB
		AlbumDAO albumDAO = new AlbumDAO(connection);
		List<Album> albums;
		try {
			albums = albumDAO.findAllAlbums();
		} catch (SQLException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Not possible to recover missions");
			return;
		}
		
		// Getting the order of the albums for the user if it was personalized
		OrderDAO orderDAO = new OrderDAO(connection);
		String orderPath;
		try {
			orderPath = orderDAO.getOrder((int)session.getAttribute("user_id"));
		} catch (SQLException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().println("Not possible to recover missions");
			return;
		}
		
		
		// If there was no personalized order, send the default order
		Gson gson;
		if (orderPath == null) {
			gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
			String json = gson.toJson(albums);
			
			res.setContentType("application/json");
			res.setCharacterEncoding("UTF-8");
			res.getWriter().write(json);
			
			return;
		}
		
		// Formatting the structure for the JSON order file
		gson = new Gson();
		Type listType = new TypeToken<ArrayList<Order>>(){}.getType();
		JsonReader reader = new JsonReader(new FileReader(orderPath));
		ArrayList<Order> data = gson.fromJson(reader, listType); 
		
		// Keeping the file in the personalized order and appending the new ones
		List<Album> sortedAlbums = new ArrayList<>();
		for(int i = 0; i < data.size(); i++) {
			Order o = (Order)data.get(i);
			int id = o.getAlbum_id();
			for (int j = 0; j < albums.size(); j++) {
				if (albums.get(j).get_id() == id) {
					sortedAlbums.add(albums.get(j));
					albums.remove(j);
					break;
				}
			}
		}
		for (Album a: albums) {
			sortedAlbums.add(a);
		}

		// Formatting and sending the data to the user
		gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(sortedAlbums);
		
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}
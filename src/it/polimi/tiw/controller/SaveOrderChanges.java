package it.polimi.tiw.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


import it.polimi.tiw.beans.Order;
import it.polimi.tiw.dao.OrderDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class SaveOrderChanges
 */
@WebServlet("/SaveOrderChanges")
@MultipartConfig
public class SaveOrderChanges extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Order change request");
		// Initializing data for 
	    Gson gson = new Gson();
	    Type type = new TypeToken<List<Order>>(){}.getType();
	    List<Order> orderList = gson.fromJson(request.getReader().readLine(), type);
		gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		
		// Getting the user_id from the session
		HttpSession session = request.getSession();
		int userID = (int) session.getAttribute("user_id");
		
		// Full file path for debugging purpuses, to change later
		String filepath = "D:\\Eclipse\\workspace2\\RealTiwExam\\WebContent\\Order\\" + userID + ".json";
		
		// Writing the JSON for the new album order
		Writer writer = new FileWriter(filepath);
		gson.toJson(orderList, writer);
		writer.flush();
		writer.close();
		
        System.out.println("Update new order completed");
        
        // Adding the reference to the JSON file to the database (if this reference doesn't already exist)
        OrderDAO orderDAO = new OrderDAO(connection);
        try {
        	if (orderDAO.getOrder(userID) == null)
        		orderDAO.addOrder(userID, filepath);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
        
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("Uploaded successfully");
	}

}

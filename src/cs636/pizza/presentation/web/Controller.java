package cs636.pizza.presentation.web;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Each controller does the guts of doGet/doPost
// via handleRequest declared here
// Note: handleRequest should only throw an exception for detected 
// bug situations. For user errors, report to user and continue
public interface Controller {
	String handleRequest(HttpServletRequest request, 
			HttpServletResponse response)
	throws IOException, ServletException;
}

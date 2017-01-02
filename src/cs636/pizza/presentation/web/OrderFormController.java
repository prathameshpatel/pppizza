package cs636.pizza.presentation.web;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs636.pizza.service.StudentService;
import cs636.pizza.service.ServiceException;

public class OrderFormController implements Controller {
	private StudentService studentService;
	private int numRooms;
	private String view;

	public OrderFormController(StudentService studentService, int numRooms,
			String view) {
		this.studentService = studentService;
		this.numRooms = numRooms;
		this.view = view;
	}

	public String handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		try {
			System.out.println("in OrderFormController");
			Set<String> allSizes = studentService.getSizeNames();
			Set<String> allToppings = studentService.getToppingNames();
			request.setAttribute("allSizes", allSizes);
			request.setAttribute("allToppings", allToppings);
			request.setAttribute("numRooms", numRooms);
		} catch (ServiceException e) {
			System.out.println("OrderFormController: " + e);
			throw new ServletException(e);
		}
		return view;
	}
}

package cs636.pizza.presentation.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs636.pizza.service.ServiceException;
import cs636.pizza.service.StudentService;

public class OrderReceiveController implements Controller {

	private StudentService studentService;
	private String view;

	public OrderReceiveController(StudentService studentService, String view) {
		this.studentService = studentService;
		this.view = view;
	}

	public String handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		System.out.println("in OrderReceiveController");
		HttpSession session = request.getSession();
		StudentBean student = (StudentBean) session.getAttribute("student");
		Integer roomNo = student.getRoomNo();
		System.out.println("in OrderReceiveController, roomNo = " + roomNo);
		if (roomNo == null || roomNo == 0) {
			// this is a bug
			throw new ServletException("OrderStatus Controller: no room number in receive info ");
		}
		
		try {
			studentService.receiveOrders(roomNo);
		} catch (ServiceException e) {
			throw new ServletException("OrderReceive Controller: ", e);
		}
		return view;
	}
}

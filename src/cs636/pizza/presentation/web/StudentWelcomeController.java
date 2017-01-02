package cs636.pizza.presentation.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.StudentService;

public class StudentWelcomeController implements Controller {

	private String view;
	private StudentService studentService;

	public StudentWelcomeController(StudentService studentService, String view) {
		System.out.println("setting studentService in StudentWelcomeController (isnull = "
						+ (studentService == null));
		this.studentService = studentService;
		this.view = view;
	}

	public String handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		System.out.println("in StudentWelcomeController (isnull = " + (studentService == null));
		HttpSession session = request.getSession();
		Integer roomNo = null;
		// take room parameter over session var in StudentBean--
		String paramRoomNoString = (String) request.getParameter("room");
		if (paramRoomNoString != null) {
			try {
				roomNo = Integer.parseInt(paramRoomNoString);
				System.out.println("Got roomNo from param = " + roomNo);
			} catch (NumberFormatException e) {
				// if get here, it's a bug: user can't directly enter room no.
				System.out.println("studentWelcome: bad number format in room");
				throw new ServletException(
						"Bad roomNo param in StudentWelcomeController");
			}
		}

		StudentBean student = (StudentBean) session.getAttribute("student");
		if (student == null)
			student = new StudentBean();
		if (roomNo != null)
			student.setRoomNo(roomNo); // set newly obtained roomNo
		if (student.getRoomNo() > 0)
			roomNo = student.getRoomNo(); // just set or older setting
		session.setAttribute("student", student);

		List<PizzaOrderData> report = null;
		Boolean hasBaked = false;
		try {
			System.out.println("in StudentWelcomeController pt B (isnull = "
					+ (studentService == null));
			if (roomNo != null && roomNo > 0) {
				report = studentService.getOrderStatus(roomNo);
				if (report != null)
					for (PizzaOrderData order : report)
						if (order.getStatus() == PizzaOrderData.BAKED)
							hasBaked = true;
			}
			Set<String> allSizes = studentService.getSizeNames();
			Set<String> allToppings = studentService.getToppingNames();
			System.out.println("#sizes = "+ allSizes.size());
			request.setAttribute("allSizes", allSizes);
			request.setAttribute("allToppings", allToppings);
		} catch (Exception e) {
			System.out.println("in StudentWelcomeController pt C");
			System.out.println(PizzaSystemConfig.exceptionReport(e));
			throw new ServletException("OrderStatus Controller: ", e);
		}
		request.setAttribute("statusReport", report);
		request.setAttribute("hasBaked", hasBaked);
		request.setAttribute("numRooms", PizzaSystemConfig.NUM_OF_ROOMS);
		return view;
	}
}

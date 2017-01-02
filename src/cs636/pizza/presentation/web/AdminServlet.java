package cs636.pizza.presentation.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.ServiceException;
import cs636.pizza.service.StudentService;

public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private AdminService adminService;
	private StudentService studentService;
	private static final String ADMIN_JSP_DIR = "/WEB-INF/admin/";

	// Initialization of servlet: runs before any request is
	// handled in the web app. It does PizzaSystemConfig initialization
	// then sets up all the controllers
	@Override
	public void init() throws ServletException {
		System.out.println("Starting admin servlet initialization");
		// Dispatcher servlet has initialized system first
		// by load-on-startup settings in web.xml
		adminService = PizzaSystemConfig.getAdminService();
		studentService = PizzaSystemConfig.getStudentService();
		if (adminService == null)
			System.out.println("Servlet initialization problem!!!");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String requestURI = request.getRequestURI();
		System.out.println("AdminServlet: requestURL = " + requestURI);
		String url = null;
		if (requestURI.contains("initializeDB.html")) {
			url = initializeDB(request, response);
		} else {
			doGet(request, response);
			return;
		}

		getServletContext().getRequestDispatcher(url)
				.forward(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String requestURI = request.getRequestURI();
		System.out.println("in doGet of AdminServlet, requestURI = "
				+ requestURI);
		String url = null;
		if (requestURI.contains("adminWelcome.html")) {
			url = ADMIN_JSP_DIR + "adminWelcome.jsp";
		} else if (requestURI.contains("/toppings")) {
			url = manageToppings(request, response);
		} else if (requestURI.contains("/sizes")) {
			url = manageSizes(request, response); 
		} else if (requestURI.contains("/orders")) {
			url = manageOrders(request, response); 
		} else if (requestURI.contains("/days")) {
			url = manageDays(request, response); 
		} else if (requestURI.contains("listVariables.html")) {
			url =  ADMIN_JSP_DIR + "listVariables.jsp";
		} else if (requestURI.contains("logout.html")) {
			request.getSession().invalidate();  // drop session
			url =  ADMIN_JSP_DIR + "logout.jsp"; 
		} else {
			System.out.println("no match for requestURI = " + requestURI);
		}

		getServletContext().getRequestDispatcher(url)
				.forward(request, response);
	}
	
	private String manageDays(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String command = request.getParameter("command");
		if (command != null && command.equals("advance")) {   // user clicked button
			try {
				adminService.advanceDay();
			} catch (ServiceException e) {
				String error = "manageDays failed: "
						+ PizzaSystemConfig.exceptionReport(e);
				request.setAttribute("error", error);
				String url = ADMIN_JSP_DIR + "error.jsp";
				return url;
			}
		}
		int day = 0;	
		List<PizzaOrderData> orders1 = null;
		try {
			day = adminService.getCurrentDay();
			orders1 = adminService.getOrdersByDay(day);
			System.out.println("seeing " + orders1.size() + " orders");
		} catch (ServiceException e) {
			String error = "manageDays failed: "
					+ PizzaSystemConfig.exceptionReport(e);
			request.setAttribute("error", error);
			String url = ADMIN_JSP_DIR + "error.jsp";
			return url;
		}
		request.setAttribute("currentDay", day);
		request.setAttribute("orders", orders1);
		String url = ADMIN_JSP_DIR + "dayView.jsp";
		return url;
	}
	
	private String manageOrders(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String command = request.getParameter("command");
		if (command != null && command.equals("mark")) { 														// button
			try {
				adminService.markNextOrderReady();
			} catch (ServiceException e) {
				String error = "manageOrders failed: "
						+ PizzaSystemConfig.exceptionReport(e);
				request.setAttribute("error", error);
				String url = ADMIN_JSP_DIR + "error.jsp";
				return url;
			}
		}

		List<PizzaOrderData> orders1 = null;
		List<PizzaOrderData> orders2 = null;
		try {
			orders1 = adminService.getTodaysOrdersByStatus(PizzaOrderData.PREPARING);
			System.out.println("seeing " + orders1.size() + " preparing orders");
			orders2 = adminService.getTodaysOrdersByStatus(PizzaOrderData.BAKED);
			System.out.println("seeing " + orders2.size() + " baked orders");

		} catch (ServiceException e) {
			String error = "manageOrders failed: " + PizzaSystemConfig.exceptionReport(e);
			request.setAttribute("error", error);	
			String url = ADMIN_JSP_DIR + "error.jsp";
			return url;
		}
		request.setAttribute("orders_preparing", orders1);
		request.setAttribute("orders_baked", orders2);
		String url = ADMIN_JSP_DIR + "orderView.jsp";
		return url;
	}
	
	private String initializeDB(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String info = null;
		try {
			adminService.initializeDb();
			info = "success";
		} catch (ServiceException e) {
			info = "failed: " + PizzaSystemConfig.exceptionReport(e);
		}
		request.setAttribute("info", info);
		String url = ADMIN_JSP_DIR + "initializeDB.jsp";
		return url;
	}

	private String manageToppings(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String command = request.getParameter("command");
		String item = request.getParameter("item");
		Set<String> allToppings = null;
		try {
			if (item != null) {
				// null command means textbox entry with <CR> submitted by
				// browser
				if (command == null || command.equalsIgnoreCase("add")) {
					adminService.addTopping(item); // item is name
				} else if (command.equalsIgnoreCase("remove")) {
					removeTopping(item); // item is id
				} else {
					String error = "manageToppings failed because of bad request parameter: "
							+ command;
					System.out.println(error);
					String url = ADMIN_JSP_DIR + "error.jsp";
					return url;
				}
			}
			allToppings = studentService.getToppingNames();
		} catch (ServiceException e) {
			String error = PizzaSystemConfig.exceptionReport(e);
			request.setAttribute("error", error);
			String url = ADMIN_JSP_DIR + "error.jsp";
			return url;
		}

		request.setAttribute("allToppings", allToppings);
		String url = ADMIN_JSP_DIR + "toppingView.jsp";
		return url;
	}

	private String manageSizes(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String command = request.getParameter("command");
		String item = request.getParameter("item");
		Set<String> allSizes = null;
		try {
			if (item != null) {
				// null command means textbox entry with <CR> submitted 
				if (command == null || command.equalsIgnoreCase("add")) {
					adminService.addPizzaSize(item); // item is name
				} else if (command.equalsIgnoreCase("remove")) {
					removeSize(item); // item is id
				} else {
					String error = "manageSizess failed because of bad request parameter: "
							+ command;
					request.setAttribute("error", error);
					String url = ADMIN_JSP_DIR + "error.jsp";
					return url;
				}
			}
			allSizes = studentService.getSizeNames();
		} catch (ServiceException e) {
			String error = PizzaSystemConfig.exceptionReport(e);
			request.setAttribute("error", error);
			String url = ADMIN_JSP_DIR + "error.jsp";
			return url;
		}

		request.setAttribute("allSizes", allSizes);
		String url = ADMIN_JSP_DIR + "sizeView.jsp";
		return url;
	}

	private void removeSize(String size) throws ServiceException {
		adminService.removePizzaSize(size);
	}

	private void removeTopping(String topping) throws ServiceException {
		adminService.removeTopping(topping);
	}

}

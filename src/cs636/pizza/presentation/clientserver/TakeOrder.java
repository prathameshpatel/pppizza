package cs636.pizza.presentation.clientserver;

import cs636.pizza.presentation.PresentationUtils;
import cs636.pizza.service.StudentService;
import cs636.pizza.service.ServiceException;
import cs636.pizza.service.PizzaOrderData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

import cs636.pizza.config.PizzaSystemConfig;
//use static import to simplify use of constants--
import static cs636.pizza.config.PizzaSystemConfig.NUM_OF_ROOMS;; 
/**
 * Student's interface for order and status report.
 */

public class TakeOrder {
	
	private StudentService studentService;
	private Scanner in = new Scanner(System.in); // input from the user
	
	public TakeOrder() throws Exception {
		PizzaSystemConfig.configureServices();
		studentService = PizzaSystemConfig.getStudentService();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TakeOrder orderTaker;
		try {
			orderTaker = new TakeOrder();
			// loop until Q command
			while (orderTaker.executeCommand())			
				;
			System.out.println("Thanks for visiting the pizza shop.");
		} catch (Exception e) {
			System.out.println("Error in run of TakeOrder: " );
			System.out.println(PizzaSystemConfig.exceptionReport(e));
		}
	}
	
	// execute one student command: O, S, P, or Q
	public boolean executeCommand() throws IOException {
		
		System.out.println("Possible Commands");
		System.out.println("O: Order");
		System.out.println("S: Status Report");
		System.out.println("R: Receive Order (acknowledge receipt)");
		System.out.println("Q: Quit");
		String command = PresentationUtils.readEntry(in,
			 	"Please Enter the Command");
		if (command.equalsIgnoreCase("O"))
			try {
				getTheOrder();
			} catch (ServiceException e) {
				System.out.println("Sorry, problem with inserting order: ");
				System.out.println(PizzaSystemConfig.exceptionReport(e));
			}
		if (command.equalsIgnoreCase("R")) {
			String room = PresentationUtils.readEntry(in,
					"Enter the room number");		
			try {
				studentService.receiveOrders(Integer.parseInt(room));
			} catch (ServiceException e) {
				System.out.println("Sorry, problem with recording receipt of order: ");
				System.out.println(PizzaSystemConfig.exceptionReport(e));
			}
		}
		else if (command.equalsIgnoreCase("S")) {
			String room = PresentationUtils.readEntry(in,
					"Enter the room number");
			try {
				List<PizzaOrderData> report =
					studentService.getOrderStatus(Integer.parseInt(room));
				PresentationUtils.printOrderStatus(report, System.out);
			} catch (NumberFormatException e) {
				System.out.println("Invalid Input!");
			} catch (ServiceException e) {
				System.out.println("Sorry, problem with getting order status: ");
				System.out.println(PizzaSystemConfig.exceptionReport(e));
			}
		} else if (command.equalsIgnoreCase("Q")) {
			return false;
		} else
			System.out.println("Invalid Command!");
		return true; // continue
	}
	
	public void getTheOrder() throws IOException, ServiceException {
		String roomNumStr = PresentationUtils.readEntry(in,
				"Please Enter the room Number");
		int roomNum = 0;
		if ((roomNum = checkNumInput(roomNumStr, NUM_OF_ROOMS)) < 0) {
			System.out.println("Invalid Room Number");
			return;
		}
		listMenu();
		System.out.println("Available pizza sizes to choose from:");
		Set<String> allSizes = studentService.getSizeNames();
		if (allSizes.size() == 0) {
			System.out.println("Sorry, no pizza sizes available (admin needs to add them)");
			return;
		}
			
		// set up map of choice numbers to name for menu
		Map<Integer, String> sizeTokens = new TreeMap<Integer, String>();
		int choiceNum = 1;
		for (String s : allSizes) {
			sizeTokens.put(choiceNum++, s);
		}
	
		int sizeChoice = 
			PresentationUtils.getMenuEntry("Enter the size #", sizeTokens, in);
		if (sizeChoice < 0) {
			System.out.println("No size specified, please try again");
			return;
		}
		String chosenSize = sizeTokens.get(sizeChoice);
	
		System.out.println("Available pizza toppings to choose from:");
		Set<String> allToppings = studentService.getToppingNames();

		Map<Integer, String> toppingTokens = new TreeMap<Integer, String>();
		int toppingChoice = 1;
		for (String t : allToppings) {
			toppingTokens.put(toppingChoice++, t);
		}
		Set<String> chosenToppings = new TreeSet<String>();
		while (true) {
			int currToppingNum = 
				PresentationUtils.getMenuEntry("Enter Topping number, or " + 
						PresentationUtils.QUIT_KEY
					+ " for no more Toppings", toppingTokens, in);
			if (currToppingNum == PresentationUtils.NO_MORE)
				break;
			String currToppingName = toppingTokens.get(currToppingNum);
			chosenToppings.add(currToppingName);
		}
		studentService.makeOrder(roomNum, chosenSize, chosenToppings);
		System.out.println("Thank you for your order");
	}

	private int checkNumInput(String numStr, int maxBound) {
		int num = 0;
		try {
			num = Integer.parseInt(numStr);
			if (num > 0 && num <= maxBound)
				return num;
		} catch (NumberFormatException e) {
		}
		return -1;
	}

	private void listMenu() throws ServiceException {
		System.out.println("Basic Pizza: tomato sauce and cheese ");
		System.out.println("Additional toppings:");
		Set<String> toppings = studentService.getToppingNames();
		for (String t: toppings)
			System.out.println("  " + t);
		System.out.println("Sizes:");

		Set<String> sizes = studentService.getSizeNames();
		for (String s: sizes)
			System.out.println("  " + s);
	}
	
}

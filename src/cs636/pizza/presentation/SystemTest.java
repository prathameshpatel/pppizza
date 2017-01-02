package cs636.pizza.presentation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.ServiceException;
import cs636.pizza.service.StudentService;

/**
 * @author Saaid Baraty
 * 
 *         This class tests the whole system.
 */
public class SystemTest {

	private AdminService adminService;
	private StudentService studentService;
	private String inFile;

	public SystemTest(String inFile) throws Exception {
		this.inFile = inFile;
		PizzaSystemConfig.configureServices();
		adminService = PizzaSystemConfig.getAdminService();
		studentService = PizzaSystemConfig.getStudentService();
	}
	// for client-server case only
	public static void main(String[] args) {
		String inFile = null;
		if (args.length == 0) {  
			inFile = "test.dat";
		}
		else if (args.length == 1) {
			inFile = args[0];
		} else  {
			System.out.println("usage:java <inputFile> ");
			return;
		}
		SystemTest test = null;
		try {
				test = new SystemTest(inFile);
				test.run();
				System.out.println("Run complete, exiting");
		} catch (Exception e) {
			System.out.println("Error in run of SystemTest: " );
			System.out.println(PizzaSystemConfig.exceptionReport(e));
		}
	}
	
	// run tests: for client-server or web cases
	public void run() throws IOException, ServiceException {
		String command = null;
		Scanner in = new Scanner(new File(inFile));
		while ((command = getNextCommand(in)) != null) {
			System.out.println("\n\n*************" + command
					+ "***************\n");
			if (command.equalsIgnoreCase("ai")) { // admin init db
				adminService.initializeDb(); // create new tables, etc.
				adminService.addPizzaSize("small");
				adminService.addTopping("Pepperoni");
			} else if (command.equalsIgnoreCase("anr")) // admin next ready
				adminService.markNextOrderReady();
			else if (command.equalsIgnoreCase("aad")) // admin advance day
				adminService.advanceDay();
		    else if (command.equalsIgnoreCase("aip")) { // admin in-progress report
				List<PizzaOrderData> report = 
						adminService.getTodaysOrdersByStatus(PizzaOrderData.PREPARING);
				PresentationUtils.printReport(report, System.out);
				report = 
						adminService.getTodaysOrdersByStatus(PizzaOrderData.BAKED);
				PresentationUtils.printReport(report, System.out);
			} else if (command.startsWith("ss")) // student status
				handleOrderStatus(command);
			else if (command.startsWith("so")) // student order
				handleStudentOrder(command);
			else if (command.startsWith("sr")) // student order received
				handleOrderReceive(command);
			else
				System.out.println("Invalid Command: " + command);
			System.out.println("----OK");
		}
		in.close();
	}

	
	private void handleOrderStatus(String command) throws ServiceException {
		String[] tokens = getTokens(command);
		int roomNumber = Integer.parseInt(tokens[1]);
		List<PizzaOrderData> report = studentService.getOrderStatus(roomNumber);
		PresentationUtils.printOrderStatus(report, System.out);
	}
	
	private void handleOrderReceive(String command) throws ServiceException {
		String[] tokens = getTokens(command);
		int roomNumber = Integer.parseInt(tokens[1]);
		studentService.receiveOrders(roomNumber);
	}

	private void handleStudentOrder(String command) throws ServiceException {
		String[] tokens = getTokens(command);
		int roomNumber = Integer.parseInt(tokens[1]);
		Set<String> allToppings = studentService.getToppingNames();
		Set<String> allPizzaSizes = studentService.getSizeNames();
		// get a particular PizzaSize
		String chosenPizzaSize = allPizzaSizes.iterator().next();
		studentService.makeOrder(roomNumber, chosenPizzaSize, allToppings);
	}
	
	// Return line or null if at end of file
	public String getNextCommand(Scanner in) throws IOException {
		String line = null;
		try {
			line = in.nextLine();
		} catch (NoSuchElementException e) { } // leave line null
		return (line != null) ? line.trim() : line;
	}

	// use powerful but somewhat mysterious split method of String
	private String[] getTokens(String command) {
		return command.split("\\s+"); // white space
	}

}

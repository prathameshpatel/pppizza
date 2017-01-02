package cs636.pizza.presentation;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.ServiceException; 


/**
 * Presentation Utility methods: none of these call the service layer
 * themselves. Instead, as proper "utilities", they just work with their 
 * argued data structures.
 *
 */
public class PresentationUtils {
	// for getMenuEntry menu handler
	public static final int NUM_OF_ATTEMPTS = 3;
	public static final int NO_MORE = -2;
	public static final String QUIT_KEY = "q";
	public static final int ERROR = -1;

	public static void printOrderStatus(List<PizzaOrderData> report, PrintStream out)
	{
		for (PizzaOrderData order: report) {
			out.println("----------Order Status--------------");
			out.println("Order " + order.getId());
			out.println(order.getStatusString());
			out.println("Room " + order.getRoomNumber());
			out.print("Order's Toppings: ");
			for (String t: order.getToppings()) {
				out.print(t + " ");
			}
			out.print("\nOrder's Size: ");
			out.println(order.getPizzaSize());
			out.println("-------------------------------------");
			}
	}
	
	public static void printReport(List<PizzaOrderData> report, PrintStream out) throws ServiceException 
	{
		for (PizzaOrderData order: report) {
			out.println(order);
			out.println("---------------------");
		}
	}
	// a primitive menu handler: displays choices, gets choice # from user
	// (a non-negative number or QUIT_KEY)
	// Returns choice number (Map key value), or NO_MORE, or ERROR
	public static int getMenuEntry(String promptMsg, Map<Integer, String> validEntries,
			Scanner in) throws IOException {
		int loop = 0;
		while (loop < NUM_OF_ATTEMPTS) {
			for (int id : validEntries.keySet()) {
				System.out.println("" + id + "  " + validEntries.get(id));
			}
			String entryLine = PresentationUtils.readEntry(in, promptMsg);
			if (entryLine.equalsIgnoreCase(QUIT_KEY))
				return NO_MORE;
			loop++;
			int num = -1;
			try {
				num = new Integer(entryLine);  // user's choice number
			} catch (NumberFormatException e) {
				System.out.println("Please enter a whole number");
				continue; // let user try again
			}
			if (validEntries.get(num) != null)  
				return num;
			else {
				System.out.println("Not a valid choice, try again");
				continue;
			}
		}
		System.out.println("Invalid Entry after " + NUM_OF_ATTEMPTS
				+ " attempts");
		return ERROR;
	}
	// super-simple prompted input from user
	// changed print to println, so prompts show under ant execution
	public static String readEntry(Scanner in, String prompt) throws IOException {
		System.out.println(prompt + ":");
		return in.nextLine().trim();
	}
	
}

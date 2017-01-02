package cs636.pizza.presentation.clientserver;

import cs636.pizza.service.PizzaOrderData;
import cs636.pizza.service.ServiceException;
import cs636.pizza.service.AdminService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.presentation.PresentationUtils;

/**
 *         Baker and administrator's user interface
 */

public class ShopAdmin {
	// Commands for admin--
	public static final String INIT = "INIT";
	public static final String AT = "AT";
	public static final String DT = "DT";
	public static final String AS = "AS";
	public static final String DS = "DS";
	public static final String AD = "AD";
	public static final String NR = "NR";
	public static final String PC = "PC";
	public static final String QS = "QS";
	public static final String IP = "IP";

	private AdminService adminService;
	private Scanner in = new Scanner(System.in); // the user

	public ShopAdmin() throws Exception {
		PizzaSystemConfig.configureServices();
		adminService = PizzaSystemConfig.getAdminService();
	}

	public static void main(String[] args) {
		ShopAdmin admin = null;
		try {
			admin = new ShopAdmin();
			admin.printCommands();
			admin.executeCommand(admin.getCommand());
		} catch (Exception e) {
			System.out.println("Error in run of ShopAdmin: " );
			System.out.println(PizzaSystemConfig.exceptionReport(e));
		}
		System.out.println("Command done, exiting");
	}

	public void printCommands()  {
		System.out.println("Enter one of the following commands:");
		System.out.println(INIT + ": Initialize Database");
		System.out.println(AT + ": Add new Topping");
		System.out.println(DT + ": Delete Topping");
		System.out.println(AS + ": Add new pizza Size");
		System.out.println(DS + ": Delete pizza Size");
		System.out.println(AD + ": Advance the Day");
		System.out.println(NR + ": Make Next order Ready");
		System.out.println(IP + ": In-progress Orders Report");
		System.out.println(QS + ": Quit System");
		System.out.println(PC + ": Print list of Commands");
	}

	public String getCommand() throws IOException {
		return PresentationUtils.readEntry(in, "Please Enter The Command");
	}

	public void executeCommand(String command) throws IOException,
			ServiceException {
		if (command.equalsIgnoreCase(INIT))
			adminService.initializeDb();
		else if (command.equalsIgnoreCase(AT))
			adminService.addTopping(PresentationUtils.readEntry(in,
					"Enter the topping Name"));
		else if (command.equalsIgnoreCase(DT))
			adminService.removeTopping(PresentationUtils.readEntry(in,
					"Enter the topping name"));
		else if (command.equalsIgnoreCase(AS))
			adminService.addPizzaSize(PresentationUtils.readEntry(in,
					"Enter the size name"));
		else if (command.equalsIgnoreCase(DS))
			adminService.removePizzaSize(PresentationUtils.readEntry(in,
					"Enter the size name"));
		else if (command.equalsIgnoreCase(AD))
			adminService.advanceDay();
		else if (command.equalsIgnoreCase(NR))
			adminService.markNextOrderReady();
		else if (command.equalsIgnoreCase(IP)) {
			List<PizzaOrderData> report = 
					adminService.getTodaysOrdersByStatus(PizzaOrderData.PREPARING);
			PresentationUtils.printReport(report, System.out);
			report = 
					adminService.getTodaysOrdersByStatus(PizzaOrderData.PREPARING);
			PresentationUtils.printReport(report, System.out);
		} else if (command.equalsIgnoreCase(QS))
			return;
		else if (command.equalsIgnoreCase(PC))
			printCommands();
		else
			System.out.println("\nInvalid Command!");
	}

}

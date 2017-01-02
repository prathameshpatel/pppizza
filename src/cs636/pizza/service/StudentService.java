package cs636.pizza.service;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.domain.MenuSize;
import cs636.pizza.domain.MenuTopping;
import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * This class captures the business logic for student interactions. 
 *
 * Only one instance of this class is instantiated, i.e.,
 * its object is a singleton object, and this singleton receives 
 * references to the singleton DAO objects at its own creation time.
 */


public class StudentService {

	private PizzaOrderDAO pizzaOrderDAO;
	private MenuDAO menuDAO;
	private AdminDAO adminDAO;
	private DbDAO dbDAO;

	public StudentService(DbDAO dbDAO, PizzaOrderDAO pizzaDAO, MenuDAO mDAO, 
			AdminDAO admDAO) {
		this.dbDAO = dbDAO;
		pizzaOrderDAO = pizzaDAO;
		menuDAO = mDAO;
		adminDAO = admDAO;
	}
	public Set<String> getSizeNames()throws ServiceException
	{
		Set<MenuSize> sizes = null;
		Set<String>sizeStrings = new TreeSet<String>();
		try {
            dbDAO.startTransaction();  
			sizes = menuDAO.findMenuSizes();
			for (MenuSize s: sizes) {
				sizeStrings.add(s.getSizeName());
			}	
            dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException(); 
			throw new ServiceException("Can't access pizza sizes in db: ", e);
		}
	   return sizeStrings;	
	}

	public Set<String> getToppingNames()throws ServiceException
	{
		Set<MenuTopping> toppings = null;
		Set<String>toppingStrings = new TreeSet<String>();
		try {
            dbDAO.startTransaction();  
			toppings = menuDAO.findMenuToppings();
			for (MenuTopping t: toppings) {
				toppingStrings.add(t.getToppingName());
			}	
            dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException(); 
			throw new ServiceException("Can't access toppings in db: ", e);
		}
		return toppingStrings;
	}

	// Transaction to make an order
	// Check the arguments to make sure the size and toppings are still on the menu, throw if not
	public void makeOrder(int roomNum, String size, Set<String> toppings) 
				throws ServiceException {
		try {	
			dbDAO.startTransaction(); 
			// check out the size and toppings vs. the current menu offerings
			// create dependent objects here, not in PizzaOrder constructor
			// to avoid calling a constructor in a constructor
			if (menuDAO.findMenuSize(size) == null) 
				throw new ServiceException(
						"Order cannot be placed because specified size " + size + " is unavailable");
			PizzaSize pizzaSize = new PizzaSize(size);
			Set<PizzaTopping> orderToppings = new HashSet<PizzaTopping>();
			for (String t:toppings) {
				System.out.println("cking topping "+t);
				if (menuDAO.findMenuTopping(t) == null)
					throw new ServiceException(
							"Order cannot be placed because specified topping " + t + " is unavailable");
				else
					orderToppings.add(new PizzaTopping(t));  // it's OK, collect it up
			}
			PizzaOrder order = new PizzaOrder(roomNum, pizzaSize, orderToppings,
					adminDAO.findCurrentDay(), PizzaOrder.PREPARING);
			pizzaOrderDAO.insertOrder(order);
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Order can not be placed ", e);
		}
	}

	// return all orders for this room
	// mark such orders FINISHED (student has been notified)
	public List<PizzaOrderData> getOrderStatus(int roomNumber)
			throws ServiceException {
		List<PizzaOrder> pizzaOrders = null;
		List<PizzaOrderData> pizzaOrders1 = new ArrayList<PizzaOrderData>();
		try {
			dbDAO.startTransaction(); 																				
			pizzaOrders = pizzaOrderDAO.findOrdersByRoom(roomNumber, adminDAO
					.findCurrentDay());

			for (PizzaOrder order : pizzaOrders) {
				pizzaOrders1.add(new PizzaOrderData(order));
	
				// JPA by default eagerly fetches the PizzaSize object to go with 
				// each PizzaOrder because of the to-one relationship, but is lazy 
				// about the toppings because they have a to-many relationship.
				// Thus we need to access the Toppings here (to get them loaded
				// during the transaction) to make sure they are available when 
				// the presentation code needs them, i.e., after the commit.
				// This can be done automatically by additional JPA configuration
				for (PizzaTopping t: order.getToppings())
				    t.getToppingName();
			}
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Error in getting status ", e);
		}
		return pizzaOrders1;
	}
	
	// receive (acknowledge receipt of) pizza orders that are ready, for a certain room
	// i.e., student acknowledges getting the pizza(s)
	// each such order is marked "finished"
	public void receiveOrders(int roomNumber)throws ServiceException {
		List<PizzaOrder> pizzaOrders = null;
		try {
			dbDAO.startTransaction(); 	
			pizzaOrders = pizzaOrderDAO.findOrdersByRoom(roomNumber, adminDAO.findCurrentDay());
			for (PizzaOrder order: pizzaOrders) {
				if (order.getStatus()== PizzaOrder.BAKED) {
					order.receive();	// mark this pizza "finished"
					// Note there is no need to call the DAO for the update
					// because the JPA runtime system is tracking changes and 
					// will apply them at commit				
				}
			}
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Error in getting status" + e, e);
		}
	}
					
}

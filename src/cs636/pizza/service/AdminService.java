package cs636.pizza.service;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.domain.PizzaOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class captures the business logic for admin-related interactions, the
 * actions that ordinary users (students) don't need.
 * 
 * Only one instance of this class is instantiated, i.e., its object is a
 * singleton object, and this singleton receives references to the singleton DAO
 * objects at its own creation time.
 */

// Note all the similar code for each service call. This can be eliminated by
// using container-managed transactions (not available in Tomcat, though).
// Note that each call catches DAO/JPA PersistenceExceptions and throws its own
// exception, after rolling back the transaction. The new exception, with
// a useful message, then gets caught in the presentation layer.

public class AdminService {

	private DbDAO dbDAO;
	private AdminDAO adminDAO;
	private MenuDAO menuDAO;
	private PizzaOrderDAO pizzaOrderDAO;

	public AdminService(DbDAO db, AdminDAO admDAO, PizzaOrderDAO poDAO, MenuDAO mDAO) {
		dbDAO = db;
		adminDAO = admDAO;
		menuDAO = mDAO;
		pizzaOrderDAO = poDAO;
	}

	public void initializeDb() throws ServiceException {
		try {
			dbDAO.startTransaction();
			dbDAO.initializeDb();
			dbDAO.commitTransaction();
		} catch (Exception e) { // any exception
			// the following doesn't itself throw, but it handles the case that
			// rollback throws, discarding that exception object
			dbDAO.rollbackAfterException();
			throw new ServiceException(
					"Can't initialize DB: (probably need to load DB)", e);
		}
	}

	public void addTopping(String name) throws ServiceException {
		try {
			dbDAO.startTransaction();
			menuDAO.createMenuTopping(name);
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Topping was not added successfully: ",
					e);
		}
	}

	public void removeTopping(String topping) throws ServiceException {
		try {
			dbDAO.startTransaction();
			menuDAO.deleteMenuTopping(topping);
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Error while removing topping ", e);
		}
	}

	public void addPizzaSize(String name) throws ServiceException {
		try {
			dbDAO.startTransaction();
			menuDAO.createMenuSize(name);
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Pizza size was not added successfully",
					e);
		}
	}

	public void removePizzaSize(String size) throws ServiceException {
		try {
			dbDAO.startTransaction();
			menuDAO.deleteMenuSize(size);
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Error while removing topping", e);
		}
	}

	public void markNextOrderReady() throws ServiceException {
		PizzaOrder order = null;
		try {
			dbDAO.startTransaction();
			order = pizzaOrderDAO.findFirstOrder(PizzaOrder.PREPARING);
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Error in marking the next order ready",
					e);
		}
		if (order == null) {
			throw new ServiceException("No PREPARING orders exist!");
		}
		order.makeReady(); // this change is tracked by JPA
		try {
			dbDAO.commitTransaction(); // update occurs here
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException(
					"Error at commit in marking the next order ready", e);
		}
	}

	public int getCurrentDay() throws ServiceException {
		int day;
		try {
			dbDAO.startTransaction(); // read-only
			day = adminDAO.findCurrentDay();
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Can't access date in db: ", e);
		}
		return day;
	}

	public void advanceDay() throws ServiceException {
		try {
			dbDAO.startTransaction();
			List<PizzaOrder> pizzaOrders = getTodaysOrders();
			// day is done, so mark today's pizzas as "finished"
			for (PizzaOrder order : pizzaOrders) {
				order.finish();
			}
			adminDAO.advanceDay();
			dbDAO.commitTransaction();
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Unsuccessful advance day", e);
		}
	}

	// helper method to advanceDay
	// executes inside the current transaction
	private List<PizzaOrder> getTodaysOrders() {
		int today = adminDAO.findCurrentDay();
		List<PizzaOrder> orders = pizzaOrderDAO.findOrdersByDays(today, today);
		return orders;
	}

	public List<PizzaOrderData> getOrdersByDay(int day) throws ServiceException {
		try {
			dbDAO.startTransaction();
			List<PizzaOrder> orders = pizzaOrderDAO.findOrdersByDays(day, day);
			List<PizzaOrderData> orders1 = new ArrayList<PizzaOrderData>();
			for (PizzaOrder o : orders) {
				orders1.add(new PizzaOrderData(o));
			}
			dbDAO.commitTransaction();
			return orders1;
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Error while getting daily report ", e);
		}
	}

	public List<PizzaOrderData> getTodaysOrdersByStatus(int status)
			throws ServiceException {
		try {
			dbDAO.startTransaction();
			List<PizzaOrder> orders = getTodaysOrders();	
			dbDAO.commitTransaction();
			List<PizzaOrderData> orders1 = new ArrayList<PizzaOrderData>();
			for (PizzaOrder o : orders) {
				if (o.getStatus() == status)
					orders1.add(new PizzaOrderData(o));
			}		
			return orders1;
		} catch (Exception e) {
			dbDAO.rollbackAfterException();
			throw new ServiceException("Error while getting daily report ", e);
		}
	}

}

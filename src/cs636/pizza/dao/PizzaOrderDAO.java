package cs636.pizza.dao;
/**
 *
 * Data access class for pizza order objects, including their sizes and toppings
 */

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaTopping;

// Note: these can throw various subclasses of RuntimeException, 
// as defined by JPA (compare to SQLException of JDBC, a checked exception)
public class PizzaOrderDAO {
	
    private DbDAO dbDAO;
    
	public PizzaOrderDAO(DbDAO db) {
		this.dbDAO = db;
	}
	
  	public void insertOrder(PizzaOrder order) 
	{
  		EntityManager em = dbDAO.getEM();
   		em.persist(order);
  		for (PizzaTopping t: order.getToppings()) {
  			t.setOrder(order);
  			em.persist(t);
  		}
  		em.persist(order.getPizzaSize());		
	}
	
	// Get orders for a certain day and room number
  	// Callers can get toppings as needed by using o.getToppings() (by lazy loading)
  	// while the persistence context is still available (i.e., until commit)
	public List<PizzaOrder> findOrdersByRoom(int roomNumber, int day) 
	{
    	TypedQuery<PizzaOrder> query = dbDAO.getEM().createQuery("select o from PizzaOrder o where o.roomNumber = "
				+ roomNumber + " and o.day = " + day + " order by o.id", PizzaOrder.class);
		List<PizzaOrder> orders = query.getResultList();	
		return orders;
	}
	
	// find first order with specified status, or null if no orders there
	public PizzaOrder findFirstOrder(int status) 
	{
    	TypedQuery<PizzaOrder> query = dbDAO.getEM().createQuery("select o from PizzaOrder o where o.status = "
				+ status + " order by o.id", PizzaOrder.class);	
		List<PizzaOrder> orders = query.getResultList();
		if (orders.isEmpty())
			return null;
		else
			return orders.get(0);
	}
	
	// get all orders between day1 and day2 (inclusive)
	public List<PizzaOrder> findOrdersByDays(int day1, int day2) {
    	TypedQuery<PizzaOrder> query = dbDAO.getEM().createQuery("select o from PizzaOrder o where o.day >= " 
				+ day1 + " and o.day <= " + day2 + " order by o.id", PizzaOrder.class);	
    	List<PizzaOrder> orders = query.getResultList();
		return orders;
	}
}

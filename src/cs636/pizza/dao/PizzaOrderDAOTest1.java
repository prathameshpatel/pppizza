package cs636.pizza.dao;

// Example JUnit4 test 
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs636.pizza.config.PizzaSystemConfig;
import cs636.pizza.domain.PizzaOrder;
import cs636.pizza.domain.PizzaSize;
import cs636.pizza.domain.PizzaTopping;

public class PizzaOrderDAOTest1 {
	private DbDAO dbDAO;
	private PizzaOrderDAO pizzaOrderDAO;
	private static EntityManagerFactory emf;
	
	@BeforeClass
	public static void setUpClass() {
		// we usually use HSQLDB as a db for testing, but 
		// this can run for other DBs too
		// Note: need to load it first and "ant config-hsqldb"
		// to get the persistence.xml file onto the classpath
		// Do this part once for this whole class--takes some time
		emf = PizzaSystemConfig.configureJPA();
	}

	@Before
	// each test runs in its own transaction, on same db setup
	public void setup() {
		dbDAO = new DbDAO(emf);
		dbDAO.startTransaction();
		dbDAO.initializeDb(); // no orders, toppings, sizes
		dbDAO.commitTransaction();
		pizzaOrderDAO = new PizzaOrderDAO(dbDAO);
	}

	@After
	public void tearDown() {
		// This executes even after an exception
		// so we need to rollback here in case of exception
		// (If the transaction was successful, it's already
		// committed, and this won't hurt.)
		dbDAO.rollbackAfterException();
	}
	@AfterClass
	public static void tearDownClass() throws Exception {
		PizzaSystemConfig.shutdownServices();
	}
	@Test
	public void testMakeOrder() throws SQLException
	{	
		dbDAO.startTransaction();	
		PizzaSize size = new PizzaSize("small");
		Set<PizzaTopping> tops = new TreeSet<PizzaTopping>();
		tops.add(new PizzaTopping("pepperoni"));
		
		PizzaOrder order = new PizzaOrder(5, size, tops, 1, 1);
		pizzaOrderDAO.insertOrder(order);
		dbDAO.commitTransaction();
	}
	
	

	// no orders yet, so findFirstOrder should return null
	@Test
	public void noFirstOrderYet() {
		dbDAO.startTransaction();
		PizzaOrder p = pizzaOrderDAO.findFirstOrder(1);
		assertTrue(p == null);
		dbDAO.commitTransaction();
	}
}

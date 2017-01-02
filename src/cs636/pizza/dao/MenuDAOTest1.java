package cs636.pizza.dao;

// Example JUnit4 test 
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs636.pizza.config.PizzaSystemConfig;

public class MenuDAOTest1 {
	private DbDAO dbDAO;
	private MenuDAO menuDAO;
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
		menuDAO = new MenuDAO(dbDAO);
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
	public void testCreateTopping() throws SQLException
	{
		dbDAO.startTransaction();
		menuDAO.createMenuTopping("anchovies");	
		int count = menuDAO.findMenuToppings().size();
		dbDAO.commitTransaction();
		assertTrue("first topping not created", count == 1);
	}
	

	@Test(expected=RuntimeException.class)
	public void testDuplicateTopping()
	{
		dbDAO.startTransaction();
		menuDAO.createMenuTopping("anchovies");	
		menuDAO.createMenuTopping("anchovies");	
		dbDAO.commitTransaction();
	}
	
}

package cs636.pizza.config;

//System configuration for JPA using a single DB. See persistence.xml
//for its JDBC properties.
//Here we set up singleton service objects
//and DAOs for the app.

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import cs636.pizza.dao.DbDAO;
import cs636.pizza.dao.MenuDAO;
import cs636.pizza.dao.AdminDAO;
import cs636.pizza.dao.PizzaOrderDAO;
import cs636.pizza.service.AdminService;
import cs636.pizza.service.StudentService;

public class PizzaSystemConfig {
	// for ease of testing, handle only a few rooms--
	public static final int NUM_OF_ROOMS = 10;

	// the service objects in use, representing all lower layers to the app
	private static AdminService adminService;
	private static StudentService studentService;

	private static EntityManagerFactory emf;
	private static DbDAO dbDAO;
	
	// configure service and DAO objects--called explicitly for client-server
	public static void configureServices() throws Exception {
		try {
			// read persistence.xml, etc.
			emf = configureJPA(); 
			testEMF(emf);  // check we can connect, and try to report JDBC isolation level
			System.out.println("calling dbDAO constructor");
			dbDAO = new DbDAO(emf);
			// configure rest of service and DAO singleton objects--
			// These objects get what they need at creation-time
			// This is "constructor injection"
			AdminDAO adminDAO = new AdminDAO(dbDAO);
			MenuDAO menuDAO = new MenuDAO(dbDAO);
			PizzaOrderDAO pizzaOrderDAO = new PizzaOrderDAO(dbDAO);
			adminService = new AdminService(dbDAO, adminDAO, pizzaOrderDAO, menuDAO);
			studentService = new StudentService(dbDAO, pizzaOrderDAO, menuDAO, adminDAO);
		} catch (Exception e) {
			System.out.println("Problem with contacting DB");
		    // rethrow to notify caller (caller should print exception details)
			throw new Exception("Exception in configureServices",e); 
		}
	}
	
	// The configuration information is read from the persistence.xml file
	// on the classpath.  This may throw a RuntimeException.
	public static EntityManagerFactory configureJPA() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("pizza3el");
		return emf;
	}
	
	// This method is not needed for setup: just testing early for ability to get an EM
	// Try a test EM session and get the isolation level, if such access is supported by this driver
	private static void testEMF(EntityManagerFactory emf) throws Exception
	{
		EntityManager em = null;
		System.out.println("starting testEMF");
		try {
			em = emf.createEntityManager();
		} catch (Throwable e) {
			System.out.println("Running outside of web container, i.e, in client-server context, so JNDI is unavailable");
			System.out.println("To run a client-server app, use ant config-clientserver-hsqldb or similar");
			throw new Exception("can't get EM, probably need ant config-clientserver-xxxx", e);  // bail out
		}
		Map<String, Object> props = emf.getProperties();
		System.out.println(props);
		System.out.println("testEMF: Got an EM");
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// dig down in software to get the actual JDBC Connection, if this driver will allow us...
		try {
			System.out.println("testEM: Trying to get JDBC Connection from EM (not always supported) ...");
			// This trick doesn't work for mysql, or some versions of it anyway
			Connection conn = em.unwrap(Connection.class);
			if (conn == null)
				System.out.println("failed to get underlying JDBC Connection from EM, so won't be able to get isolation level this way");
			else {
				System.out.println("Got Connection: JDBC isolation level (0=none,1=RU,2=RC,4=RR,8=SR) is " 
					+ conn.getTransactionIsolation());
			}
			tx.commit();
			em.close();
		} catch (Exception e) {
			System.out.println(" Exception trying to get isolation level: " + e + "\n Continuing...");
		}
	}

	// Compose an exception report
	// and return the string for callers to use
	public static String exceptionReport(Exception e) {
		String message = e.toString(); // exception name + message
		if (e.getCause() != null) {
			message += "\n  cause: " + e.getCause();
			if (e.getCause().getCause() != null) {
				message += "\n    cause's cause: " + e.getCause().getCause();
			}
		}
		message += "\n Stack Trace: " + exceptionStackTraceString(e);
		return message;
	}

	private static String exceptionStackTraceString(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	// call this to free up system resources
	// allocated by configureJPA(). 
	public static void shutdownServices() {
		if (emf != null && emf.isOpen())
			emf.close();
	}
	// Let the apps get the business logic layer services
	public static AdminService getAdminService() {
		return adminService;
	}

	public static StudentService getStudentService() {
		return studentService;
	}
}

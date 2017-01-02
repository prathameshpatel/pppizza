package cs636.pizza.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import static cs636.pizza.dao.DBConstants.*;

public class DbDAO {
	private EntityManagerFactory emf;

	// Each request runs in one Java thread from start to finish,
	// and thus runs in one Java thread for a whole transaction.
	// Multiple transactions (in multiple threads) can be running
	// at once, each of which needs its own EM. We save the thread's 
	// EntityManager in a ThreadLocal, i.e., in thread-private memory,
	// a standard Java capability. Also see comment in commitTransaction.
	
	// Side Note: There are other ways to provide an EM for each
	// transaction. We could have startTransaction return an object to 
	// the service layer, where it could be kept in a local variable 
	// for the transaction lifetime, and add an argument to each DAO
	// method for returning the object to the DAO on each method call.
	// Or we could give up on DAO singletons, and create a DAO
	// object for each transaction. Or even give up on singletons
	// for both service objects and DAOs. This would be closest to
	// EJB architecture, but EJB provides object pools to make this
	// a higher performance option.
	private ThreadLocal<EntityManager> threadEM = new ThreadLocal<EntityManager>();

	public EntityManager getEM() {
		return threadEM.get(); // get this thread's EM
	}

	public DbDAO(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public void initializeDb() {
		// drop tables with FK cols before the tables they refer to

		clearTable(PIZZA_TOPPING_TABLE);
		clearTable(ORDER_TABLE);
		clearTable(PIZZA_SIZE_TABLE);
		clearTable(MENU_TOPPING_TABLE);
		clearTable(MENU_SIZE_TABLE);
		clearTable(SYS_TABLE);
		clearTable(PIZZA_ID_GEN_TABLE);
		
		initSysTable();
		initIdGenTable();
	}

	// We can use direct SQL for DB setup easily as follows.
	// Any SQLException is handled by EL, marking the
	// transaction as rollback-only, and then EL throws a
	// org.eclipse.persistence.exceptions.DatabaseException
	private void clearTable(String tableName) {
		Query q = getEM().createNativeQuery("delete from " + tableName);
		int n = q.executeUpdate(); // SQL of update shows in FINE logging
		System.out.println("deleted " + n + " rows from " + tableName);
	}

	private void initSysTable() {
		System.out.println("inserting row (1,1,1) into " + SYS_TABLE);
		Query q = getEM().createNativeQuery(
				"insert into " + SYS_TABLE + " values (1,1,1,1,1,1)");
		int n = q.executeUpdate();
		System.out.println("inserted " + n + " rows into " + SYS_TABLE);
	}
	
	// reinitialize id gen table so
	// as to get same sequence of ids on each re-init'd run
	private void initIdGenTable() {
		for (int i = 0; i < PIZZA_GEN_NAMES.length; i++)
			initIdGenTableOneRow(PIZZA_GEN_NAMES[i]);
	}
	private void initIdGenTableOneRow(String genName) {
		System.out.println("inserting row (" + genName + ", 0) into " + PIZZA_ID_GEN_TABLE);
		Query q = getEM().createNativeQuery(
				"insert into " + PIZZA_ID_GEN_TABLE + " values ('" + genName + "', 0)");
		int n = q.executeUpdate();
		System.out.println("inserted " + n + " rows into " + PIZZA_ID_GEN_TABLE);
	}
	public void startTransaction() {
		EntityManager em = emf.createEntityManager();
		threadEM.set(em); // save in thread-local storage
		EntityTransaction tx = em.getTransaction();
		tx.begin();

	}

	public void commitTransaction() {
		// the commit call can throw, and then the caller needs to rollback
		getEM().getTransaction().commit();
		// We are using an application-managed entity manager, so we need
		// to explicitly close it to release its resources.
		// See Keith & Schincariol, pg. 138, first paragraph.
		// By closing the em at the end of the transaction, we are
		// following the pattern of transaction-scoped entity managers
		// used in EJBs by default.
		getEM().close(); // this causes the entities to become detached
		
		// Drop reference from thread to EM object, a possibly significant 
		// amount of memory even after it has been closed. In an application 
		// server, threads are pooled, so an individual thread actually lives 
		// longer than a request. Also, Tomcat 6.0.2x checks for this kind
		// of possible memory leak and outputs nasty messages "SEVERE: ..."
		// when it detects objects ref'd from ThreadLocals
		threadEM.set(null);
	}

	public void rollbackTransaction() {
		try {
			getEM().getTransaction().rollback();
		} finally {
			getEM().close();
			threadEM.set(null);  // see comment in commitTransaction()
		}
	}

	// Exceptions occurring in JPA code are almost always fatal to the
	// EntityManager context, meaning that we need to rollback the transaction
	// (and also close the EntityManager in our setup) and start over
	// or fail the action. An exception to this rule is the NoResultException
	// from the method singleResult()--it's OK to handle the exception and
	// continue the EntityManager/transaction after that particular exception.
	// If the caller has already seen an exception, it probably
	// doesn't want to handle a failing rollback, so it can use this.
	// Then the caller should issue its own exception based on the
	// original exception.
	public void rollbackAfterException() {
		try {
			rollbackTransaction();
		} catch (Exception e) {
			// discard secondary exception--probably server can't be reached
		}
	}
}

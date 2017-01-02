package cs636.pizza.dao;

import static cs636.pizza.dao.DBConstants.SYS_TABLE;

import javax.persistence.EntityManager;
import javax.persistence.Query;

// Note: these can throw a RuntimeException, which is caught in the
// service layer
public class AdminDAO {

	private DbDAO dbDAO;

	public AdminDAO(DbDAO db) {
		dbDAO = db;
	}

	// We are using plain SQL for this DAO since the SYS_TABLE, having
	// only one row, is not a classic entity table. We could still use
	// set up a domain class for it, but it would clutter up our domain
	// package with an unneeded class. It is normal practice to provide
	// entities only for the database rows actively used by the service
	// layer of the app. (as objects)

	// Alternative way: set up an entity class SysTime...
	// public int findLastReportDayUsingEntity() {
	// EntityManager em = dbDAO.getEM();
	// // access the one row--
	// SysTime sysTab = (SysTime) em.find(SysTime.class, 1);
	// return sysTab.getLastReport();
	// }

	public void advanceDay() {
		EntityManager em = dbDAO.getEM();
		Query q = em.createNativeQuery("update " + SYS_TABLE
				+ " set current_day = current_day + 1");
		q.executeUpdate();
	}
	
	public int findCurrentDay() {
		EntityManager em = dbDAO.getEM();
		Query q = em.createNativeQuery("select current_day from " + SYS_TABLE);
		// the result may be Integer or BigDecimal, both Numbers--
		int day = ((Number) q.getSingleResult()).intValue();
		return day;
	}



}

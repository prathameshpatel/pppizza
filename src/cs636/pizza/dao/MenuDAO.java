package cs636.pizza.dao;
/**
 *
 * Data access class for menu objects: sizes and toppings
 */

import java.util.Set;
import java.util.TreeSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import cs636.pizza.domain.MenuSize;
import cs636.pizza.domain.MenuTopping;

// Note: these can throw various subclasses of RuntimeException, 
// as defined by JPA (compare to SQLException of JDBC, a checked exception)
public class MenuDAO {
	
    private DbDAO dbDAO;
    
	public MenuDAO(DbDAO db) {
		this.dbDAO = db;
	}
	  
	public void createMenuTopping(String toppingName) {
		EntityManager em = dbDAO.getEM();
		em.persist(new MenuTopping(toppingName));
	}
	
	public void createMenuSize(String sizeName) {
		EntityManager em = dbDAO.getEM();
		System.out.println("in createMenuSize");
		em.persist(new MenuSize(sizeName));
	}
	
	public MenuTopping findMenuTopping(String toppingName) {
		EntityManager em = dbDAO.getEM();
		TypedQuery<MenuTopping> query = 
				em.createQuery("select t from MenuTopping t where t.toppingName = '" + toppingName + "'", 
						MenuTopping.class);
		List<MenuTopping> tops = (List<MenuTopping>)query.getResultList();
		if (tops.size() == 0)
			return null;
		return tops.get(0);
	}
	
	// With PizzaSize, we can use JPQ to select on s_status
	public MenuSize findMenuSize(String sizeName) {
		EntityManager em = dbDAO.getEM();
		System.out.println("in findPizzaSize");
		TypedQuery<MenuSize> query = em.createQuery(
				"select s from MenuSize s where s.sizeName = '" + sizeName
						+ "'", MenuSize.class);
		List<MenuSize> sizes = (List<MenuSize>)query.getResultList();
		if (sizes.size() == 0)
			return null;
		return sizes.get(0);
	}

	public void deleteMenuTopping(String toppingName) {
		EntityManager em = dbDAO.getEM();
		MenuTopping t = findMenuTopping(toppingName);
		em.remove(t);
	}


	public void deleteMenuSize(String sizeName) {
		EntityManager em = dbDAO.getEM();
		MenuSize s = findMenuSize(sizeName);
		em.remove(s);
	}
	
	public Set<MenuTopping> findMenuToppings() {
		EntityManager em = dbDAO.getEM();
		TypedQuery<MenuTopping> query = 
				em.createQuery("select t from MenuTopping t", 
						MenuTopping.class);
		List<MenuTopping> tops = (List<MenuTopping>)query.getResultList();
		return new TreeSet<MenuTopping>(tops);
	}
	
	public Set<MenuSize> findMenuSizes() {
		EntityManager em = dbDAO.getEM();
		TypedQuery<MenuSize> query = em.createQuery(
				"select s from MenuSize s", MenuSize.class);
		List<MenuSize> sizes = query.getResultList();
		return new TreeSet<MenuSize>(sizes);
	}
}

package cs636.pizza.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the pizza topping objects for an ordered pizza
 * Each pizza "owns" its set of PizzaTopping objects
 * * The Id generator follows the example in Keith & Schincariol, pg. 74
 * 
 */
@Entity
@Table(name="PIZZA_TOPPINGS")
	
public class PizzaTopping implements Serializable, Comparable<PizzaTopping> {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name="ToppingIdGen",
			table = "PIZZA_ID_GEN",
			pkColumnName = "GEN_NAME",
			valueColumnName = "GEN_VAL",
			pkColumnValue = "ToppingId_Gen")
				
	@GeneratedValue(generator="ToppingIdGen")
	@Column(unique=true, nullable=false)
	private int id;
	
	// bi-directional many-to-one association to PizzaOrder
	// We don't need the capabiity of finding the PizzaOrder for
	// a certain PizzaTopping, but JPA insists on bi-directional
	// many-to-one associations (unless we set up a join table, harder)
	@ManyToOne
	@JoinColumn(name="ORDER_ID", nullable=false)
	private PizzaOrder order;
	
	@Column(name="TOPPING_NAME", nullable=false, length=30)
	private String toppingName;

    public PizzaTopping() {
    }
    
	/** create a Topping, id and orderId are handled by JPA */
    public PizzaTopping(String toppingName) {
        this.toppingName = toppingName;
    }
    
    // to attach this object to an order
    public void setOrder(PizzaOrder order) {
    	this.order = order;
    }
    
	public int getId() {
		return this.id;
	}

	public String getToppingName() {
		return this.toppingName;
	}

	// so we can use TreeSet<Topping> or HashSet<Topping> any time we want--
    // "business key equality/comparison/hashCode" where business key is toppingName
    // see comments in PizzaSize
	public int compareTo(PizzaTopping x)
	{
		return getToppingName().compareTo(x.getToppingName());
	}
	@Override
	public boolean equals(Object x)
	{
		if (x == null || x.getClass()!= getClass())
			return false;
		return getToppingName().equals(((PizzaTopping)x).getToppingName());
	}
	@Override
	public int hashCode()
	{
		return getToppingName().hashCode();
	}
}
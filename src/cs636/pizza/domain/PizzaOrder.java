package cs636.pizza.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;
import java.util.TreeSet;


/**
 * The persistent class for the PIZZA_ORDERS database table.
 * 
 */
@Entity
@Table(name="PIZZA_ORDERS")
public class PizzaOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name="OrderNumberGen",
			table = "PIZZA_ID_GEN",
			pkColumnName = "GEN_NAME",
			valueColumnName = "GEN_VAL",
			pkColumnValue = "Ordno_Gen")
	@GeneratedValue(generator="OrderNumberGen")
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false)
	private int day;

	@Column(name="room_number", nullable=false)
	private int roomNumber;

	@Column(nullable=false)
	private int status;
	
	// Added to generated class: pizza order status values--
 	public static final int PREPARING = 1;
 	public static final int BAKED = 2;
 	public static final int FINISHED = 3;
 	public static final int NO_SUCH_ORDER = 0;
 	private static final String[] STATUS_NAME = { "NO_SUCH_ORDER", 
         "PREPARING", 
         "BAKED",
         "FINISHED"
         };  

	// one-to-many association to PizzaTopping
 	// Alternatively, we could put cascade=CascadeType.ALL here, like Murach, pg. 433,
 	// that is, have PizzaOrder manage its toppings as its details,
 	// and then we would not need to persist the toppings individually
 	// in insertOrder.
    @OneToMany(mappedBy="order")
	private Set<PizzaTopping> pizzaToppings;

	//uni-directional one-to-one association to PizzaSize
    // Alternatively, like toppings above, we could put cascade=CascadeType.ALL here,
    // and not individually persist the PizzaSize in insertOrder.
    @OneToOne
	@JoinColumn(name="SIZE_ID", nullable=false)
	private PizzaSize pizzaSize;

    public PizzaOrder() {
    }
 
 	/** full constructor, except id: leave it null, JPA provider fills it in */
 	public PizzaOrder(int roomNumber, PizzaSize size, Set<PizzaTopping> pizzaToppings, int day, int status) { 
       this.roomNumber = roomNumber;
       this.pizzaSize = size;
       this.day = day;
       this.status = status;
       this.pizzaToppings = pizzaToppings;
       // connect toppings to order
       for (PizzaTopping t: pizzaToppings)
    	   t.setOrder(this); // for JPA's required bidirectional one-to-many association
    }
	// mutators, AKA "commands": the application-specific ways to change a PizzaOrder
	public void makeReady()
	{
		status = BAKED;
	}
	
	public void receive()
	{
		assert(status == BAKED);
		status = FINISHED;
	}

	public void finish()
	{
		status = FINISHED;
	}

	public int getId() {
		return this.id;
	}

	public int getDay() {
		return this.day;
	}

	public int getRoomNumber() {
		return this.roomNumber;
	}

	public int getStatus() {
		return this.status;
	}


	public Set<PizzaTopping> getToppings() {
		return this.pizzaToppings;
	}

	public Set<String> getPizzaToppingNames()
	{
		Set<String> names = null;
		if (pizzaToppings != null) {
			names = new TreeSet<String>();
			for (PizzaTopping t : pizzaToppings) {
				names.add(t.getToppingName());
			}
		}
		return names;
	}
	public PizzaSize getPizzaSize() {
		return this.pizzaSize;
	}

	// string equivalent of status code
	public String statusString()
	{
		return STATUS_NAME[status];
	}
	
	// with JPA in use, we can depend on filled-in details
	// as long as we call this within the transaction
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ORDER ID: " + getId() + "\n");
		buffer.append("ORDER DAY: " + getDay() + "\n");
		buffer.append("SIZE: " + (getPizzaSize() != null?getPizzaSize().getSizeName():"not available") + "\n");
		buffer.append("ROOM NUMBER: " + getRoomNumber() + "\n");
		buffer.append("STATUS: " + statusString());
		return buffer.toString();
	}
	// Note: no compareTo, so can't use TreeSet<PizzaOrder>, just HashSet<PizzaOrder>
	// or List<PizzaOrder>. Here equals and hashCode are not overridden, 
	// so simple object equality is in use, based on object addresses.
	// This works fine for apps like this one that do not need to
	// compare detached objects with each other or with managed
	// or new (not yet persisted) entity objects. In a persistence context, 
	// JPA ensures that there is only one managed entity object for each id value.
	// Any new entities have different addresses.
	// Thus a HashSet<PizzaOrder> may have both new and managed PizzaOrders
	// in it without problems (just not detached ones).
}
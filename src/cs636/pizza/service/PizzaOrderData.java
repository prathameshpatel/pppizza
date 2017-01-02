package cs636.pizza.service;

/*
 * Data object (AKA transfer object or DTO for data transfer object) for Pizza Order, 
 * to be returned to presentation.
 * We don't want to return PizzaOrder objects, because presentation
 * shouldn't be able to call makeReady() or finish() on them. Only the
 * app itself can do that. In fact, we are not returning any domain objects
 * to the presentation layer.
 */

import java.util.Set;

import cs636.pizza.domain.PizzaOrder;

public class PizzaOrderData {
	private int roomNumber;
	private String pizzaSize;
	private Set<String> toppings;
	private int id;
	private int day;
	private int status;
	private String statusString;
	// pizza order status values--
	public static final int PREPARING = 1;
	public static final int BAKED = 2;
	public static final int FINISHED = 3;
	public static final int NO_SUCH_ORDER = 0;

	public PizzaOrderData(int id, int roomNumber, String size,
			Set<String> toppings, int day, int status, String statusString) {
		this.roomNumber = roomNumber;
		this.pizzaSize = size;
		this.id = id;
		this.day = day;
		this.status = status;
		this.statusString = statusString;
		this.toppings = toppings;
	}

	public PizzaOrderData(PizzaOrder po) {
		this.roomNumber = po.getRoomNumber();
		this.pizzaSize = po.getPizzaSize().getSizeName();
		this.id = po.getId();
		this.day = po.getDay();
		this.status = po.getStatus();
		this.statusString = po.statusString();
		this.toppings = po.getPizzaToppingNames();;
	}

	// Getters. Note there are no corresponding setters--
	// This is an invariant object, used to carry data to the presentation layer
	public Set<String> getToppings() {
		return toppings;
	}
			
	public int getDay() {
		return day;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public String getPizzaSize() {
		return pizzaSize;
	}

	public int getId() {
		return id;
	}

	public int getStatus() {
		return status;
	}

	public String getStatusString() {
		return statusString;
	}
	
	// with JPA in use, we can be sure that related PizzaSize
	// and Toppings will be there for a PizzaOrder during the
	// transaction, but if we want to use this method in presentation,
	// best to check for null refs to detail objects.
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ORDER ID: " + getId() + "\n");
		buffer.append("ORDER DAY: " + getDay() + "\n");
		if (getPizzaSize() != null) 
			buffer.append("SIZE: " + getPizzaSize() + "\n");
		if (getToppings() != null) {
			buffer.append("TOPPINGS: ");
			for (String t: getToppings()) {
				buffer.append(t);
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		buffer.append("ROOM NUMBER: " + getRoomNumber() + "\n");
		buffer.append("STATUS: " + getStatusString());
		return buffer.toString();
	}
}

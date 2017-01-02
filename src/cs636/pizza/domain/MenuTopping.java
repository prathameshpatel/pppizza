package cs636.pizza.domain;
// Domain object for a topping offered for new pizzas
// Immutable object (no setters oe other mutators), with id and toppingName properties

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity(name = "MenuTopping")
@Table(name="MENU_TOPPINGS", uniqueConstraints = @UniqueConstraint(columnNames="TOPPING_NAME"))

public class MenuTopping implements  Serializable, Comparable<MenuTopping> {
	private static final long serialVersionUID = 1L;
	
	@Id
	@TableGenerator(name="MenuToppingIdGen",
			table = "PIZZA_ID_GEN",
			pkColumnName = "GEN_NAME",
			valueColumnName = "GEN_VAL",
			pkColumnValue = "MenuToppingId_Gen")
				
	@GeneratedValue(generator="MenuToppingIdGen")
	@Column(unique=true, nullable=false)

	private int id;
	
	@Column(name="TOPPING_NAME", nullable=false, length=30)
	private String toppingName;
	
	public MenuTopping()
	{
		this.toppingName = null;
	}
	
	public MenuTopping(String name)
	{
		this.toppingName = name;
	}
	
	public int getId() {
		return id;
	}

	public String getToppingName() {
		return toppingName;
	}
	
	// so we can use TreeSet<Topping> or HashSet<Topping> any time we want--
    // "business key equality/comparison/hashCode" where business key is toppingName
    // see comments in PizzaSize
	public int compareTo(MenuTopping x)
	{
		return getToppingName().compareTo(x.getToppingName());
	}
	@Override
	public boolean equals(Object x)
	{
		if (x == null || x.getClass()!= getClass())
			return false;
		return getToppingName().equals(((MenuTopping)x).getToppingName());
	}
	@Override
	public int hashCode()
	{
		return getToppingName().hashCode();
	}
}

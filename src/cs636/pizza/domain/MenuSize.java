package cs636.pizza.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

// size currently available for new orders
// Immutable object
@Entity(name = "MenuSize")
@Table(name="MENU_SIZES", uniqueConstraints = @UniqueConstraint(columnNames="SIZE_NAME"))
public class MenuSize implements Serializable, Comparable<MenuSize> {
	private static final long serialVersionUID = 1L;
	@Id
	@TableGenerator(name="MenuSizeIdGen",
			table = "PIZZA_ID_GEN",
			pkColumnName = "GEN_NAME",
			valueColumnName = "GEN_VAL",
			pkColumnValue = "MenuSizeId_Gen")				
	@GeneratedValue(generator="MenuSizeIdGen")
	@Column(unique=true, nullable=false)
	private int id;
	
	@Column(name="SIZE_NAME", nullable=false, length=30)
	private String sizeName;
	
	
	public MenuSize()
	{
		this.sizeName = null;
	}
	public MenuSize(String name)
	{
		this.sizeName = name;
	}
	public int getId() {
		return id;
	}

	public String getSizeName() {
		return sizeName;
	}
	
	// Implement compareTo and equals so we can use TreeSet<PizzaSize>,
	// with ordering based on sizeName ("large", "medium", "small") for ex.
    // and hashCode too so we can use HashSet<PizzaSize>
    @Override
	public int compareTo(MenuSize x)
	{
		return getSizeName().compareTo(x.getSizeName());
	}
	@Override
	public boolean equals(Object x)
	{
		if (x == null || x.getClass()!= getClass())
			return false;
		return getSizeName().equals(((MenuSize)x).getSizeName());
	}
	@Override
	public int hashCode()
	{
		return getSizeName().hashCode();
	}
}

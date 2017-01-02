		create table menu_sizes(
				id integer not null, 
				size_name varchar(30) not null,
				primary key (id),
				unique (size_name));
				
	    create table pizza_sizes(
				id integer not null, 
				size_name varchar(30) not null,  -- not unique: several pizzas may each be small
				primary key (id));
 											
	    create table pizza_orders(
				id integer not null, 
				room_number integer not null, 
				size_id integer not null, 
				day integer not null, 
				status integer not null,
				primary key(id),
				constraint po_size_id foreign key (size_id) references pizza_sizes(id));		
								
			create table menu_toppings(
				id integer not null, 
				topping_name varchar(30) not null, -- not unique: several pizzas may each have Onions
				primary key(id),
				unique (topping_name));
				
            create table pizza_toppings(
				id integer not null, 
				order_id integer not null,
				topping_name varchar(30) not null,
				primary key(id),
				foreign key (order_id) references pizza_orders(id));
				
	-- We leave this with unused next_* columns (for pizza2) so we can run pizza1 on this DB		
	create table pizza_sys_tab (
				next_menu_topping_id integer not null,
				next_menu_size_id integer not null,
				next_order_id integer not null, 
				next_pizza_topping_id integer not null, 
				next_pizza_size_id integer not null, 
				current_day integer not null);
			
		insert into pizza_sys_tab values (1, 1, 1, 1, 1 ,1);
		
-- for generated ids specific to the pizza project
-- pizza_id_gen has one row for each table that needs ids, i.e. each entity table
-- gen_val start at 0, so first generated id for each entity is 1
CREATE TABLE PIZZA_ID_GEN (GEN_NAME VARCHAR(50) NOT NULL, GEN_VAL INTEGER, PRIMARY KEY (GEN_NAME));
INSERT INTO PIZZA_ID_GEN (GEN_NAME, GEN_VAL) values ('Ordno_Gen', 0);
INSERT INTO PIZZA_ID_GEN (GEN_NAME, GEN_VAL) values ('SizeId_Gen', 0);
INSERT INTO PIZZA_ID_GEN (GEN_NAME, GEN_VAL) values ('ToppingId_Gen', 0);
INSERT INTO PIZZA_ID_GEN (GEN_NAME, GEN_VAL) values ('MenuSizeId_Gen', 0);
INSERT INTO PIZZA_ID_GEN (GEN_NAME, GEN_VAL) values ('MenuToppingId_Gen', 0);



rem for Windows, execute ShopAdmin
rem be sure to start and load hsqldb, also use ant config-clientserver-hsqldb  before using this
java -javaagent:WebContent/WEB-INF/lib/eclipselink.jar -cp WebContent/WEB-INF/lib/eclipselink.jar;WebContent/WEB-INF/lib/lib/javax.persistence_2.1.1.v201509150925.jar;lib/hsqldb.jar;WebContent/WEB-INF/classes cs636.pizza.presentation.clientserver.ShopAdmin

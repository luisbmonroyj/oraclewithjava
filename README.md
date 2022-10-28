# oraclewithjava

## This class features a connection to an Oracle DBMS engine. Nevertheless, the queries work (or at least should) with other DBMS.

### The constructor has a boolean argument that allows/restricts to print information about connections or queries., similar to a debug routine. it helps to prove the queries in a database managing tool, like sqldeveloper or similar.

### This class uses JDK 18 and needs the ojdbc11.jar added in the libraries folder.

### It has an interface class that gathers the private information for the connection, Properties.java. Remember that both files must be in the same package. This interface allows to change between DBMS. Remember that oracle tables are invoked with \<user>\.\<table>\, that's why Properties.java has a String value, RDBMS_NAME, if equals to "Oracle", it means that all queries must have the form <properties.DATABASE_USER>.TABLE when making queries in the implementation.

### Arguments and variables are named in spanish to make them more distinguishable inside the code. You can always use the search/replace tool

### The parameter "orden" of the orderable queries can be null, there is a routine that add ORDER BY \<column>\ to the query when necessary.



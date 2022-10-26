package oraclewithjava;
/*
  @author
  LuisBernardo Monroy Jaramillo
*/
public interface Properties {
       /*final String nombreDataBase = "reto5";
       final String USERNAME= "root";
       final String PASSWORD= "santy98";
       final String dbURL = "jdbc:mysql://localhost:3306/"+nombreDataBase+"?serverTimezone=UTC";
       final String DRIVER ="com.mysql.cj.jdbc.Driver";
       */
       final static String RDBMS_NAME = "Oracle";
       final static String DATABASE_NAME = "XE";
       final static String DATABASE_URL= "jdbc:oracle:thin:@localhost:1521/"+DATABASE_NAME;
       final static String DATABASE_USER = "dbUser";//sys, whatever user 
       final static String DATABASE_PASSWORD = "strong"; //Always use a strong password
       final static String DATABASE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    
}

/*
* @author
* Luis Bernardo Monroy Jaramillo
*/

package oraclewithjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class DBController {
    //variables instance
    private static Connection conexion;
    private static Statement sentencia;
    
    static boolean conectado;
    static boolean echoON;
    
    public DBController(boolean echoON){
        this.echoON = echoON;
    }
    
    public static boolean conectar() {
        //driver initializing
        //Inicializando driver 
        conectado = false;
        try {
            Class.forName(Properties.DATABASE_DRIVER);
            if(echoON)System.out.println("JDBC driver found.");
        }
        catch (ClassNotFoundException e) {
            if(echoON)System.out.println(Properties.RDBMS_NAME + " JDBC driver not found.");
            e.printStackTrace();
        }
        //connecting to database with the stored credentials in Properties.java
        //conectando a la DB con las credenciales guardadas en Properties.java
        try {
            conexion = DriverManager.getConnection(Properties.DATABASE_URL, Properties.DATABASE_USER, Properties.DATABASE_PASSWORD);
            if(echoON)System.out.println("JDBC driver connected.");
            sentencia = conexion.createStatement();
            conectado = true;
        } 
        catch (SQLException e) {
            if(echoON)System.out.println("Connection to "+Properties.RDBMS_NAME+" Database failed");
            e.printStackTrace();
        }
        return conectado;
    }
    public static boolean desconectar() {
        //disconnecting from DB
        //Desconectarse de la DB
        try {
            if (!conexion.isClosed()){
                conexion.close();
                conectado = false;
                if(echoON)System.out.println("Connection to "+ Properties.RDBMS_NAME+" finished");
            }
        }
        catch(SQLException e) {
            if(echoON)System.out.println("Closing connection to "+Properties.RDBMS_NAME+" Database failed");
            e.printStackTrace();
        }
        return conectado;
    }
    //CREATE a single row because it is oracle. For multiple rows in another DBMS, use a loop
    public boolean insertValues(String tabla,String campos,String csv){
        boolean exitoso = false;
        try {
            sentencia.execute("insert into "+tabla+" ("+campos+") values ("+csv+");");
            exitoso = true;
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return exitoso;
    }
    //UPDATE is made by using the pk only
    public boolean updateValues(String tabla,String campo,String valor,String pk, String id){
        boolean exitoso = false;
        try {
            if(echoON)System.out.println("updateValues: update "+tabla+" set "+ campo +"= "+valor+" where " + pk +" = "+id+";");
            sentencia.execute("update "+tabla+" set "+ campo +"= "+valor+" where " + pk +" = "+id+";");
            exitoso = true;
        } 
        catch (SQLException e) {
            if(echoON)System.out.println("Error actualizando datos");
            e.printStackTrace();
        }
        return exitoso;
    }
    //DELETE "llave" is the PK. if the PK is a String, isString is true, otherwise false
    public boolean deleteValues(String tabla, String llave, String id, boolean isString){
        boolean exitoso = false;
        try {
            if(echoON)System.out.println("deleteValues: delete from "+tabla+" where "+llave+" = '"+id+"'");
            if (isString)
                sentencia.execute("delete from "+tabla+" where "+llave+" = '"+id+"'");
            else
                sentencia.execute("delete from "+tabla+" where "+llave+" = "+id);
            exitoso = true;
        } 
        catch (SQLException e) {
            if(echoON)System.out.println("error en deleteValues");
            e.printStackTrace();
        }
        return exitoso;
    }
    //looks for a fk being used in another registry, it is useful for warning of a cascade deletion 
    public boolean unusedPrimaryKey(String dbTable, String fk, String pk){
        //busca si un dato que es llave foranea en otra tabla, esta siendo usada en algun otro registro
        //sirve para advertir sobre el borrado en cascada de una FK
        int contador = 0;
        try {
            if(echoON)System.out.println("unusedPrimaryKey:SELECT * from " + dbTable + " where " + fk + " = '" + pk + "'");
            ResultSet rs = sentencia.executeQuery("SELECT * from " + dbTable + " where " + fk + " = '" + pk + "'");
            while (rs.next())
                contador++;
        }
        catch (SQLException e) {
            if(echoON)System.out.println("Error en el metodo unusedPrimaryKey");
            e.printStackTrace();
        }
        if (contador > 0) { return false;}
        else              {  return true;}
    }
    //READ, this method delivers how many registries are, columna must be a not null value or the PK
    public int getRowCount (String dbTable,String columna){
        //obtiene la cantidad de datos(filas) en la tabla, columna debe ser un dato no nulo o la PK
        int contador = 0;
        try {
            if(echoON)System.out.println("getCount: SELECT "+columna+" FROM "+dbTable);
            ResultSet rs = sentencia.executeQuery("SELECT "+columna+" FROM "+dbTable);
            while (rs.next())
                contador++;
            if(echoON)System.out.println("Total de filas: "+Integer.toString(contador));
        }
        catch (SQLException e) {
            if(echoON)System.out.println("Error en el metodo getCount");
            e.printStackTrace();
        }
        return contador;
    }
    //READ, this method delivers how many columns are being read, specially useful when using * (all columns)
    //it is used to instantiate the tables
    public int getColumnCount(String dbTable, String columnasCSV){
        //obtiene la cantidad de columnas del parametro columnasCSV, especialmente cuando es *
        int salida = 0;
        try{
            ResultSet rs = sentencia.executeQuery("SELECT "+columnasCSV+" FROM "+dbTable);
            ResultSetMetaData metaData =  rs.getMetaData();
            salida = metaData.getColumnCount();
        }    
        catch (Exception e) { 
            if(echoON)JOptionPane.showMessageDialog(null,e.getLocalizedMessage());
            e.printStackTrace();
        }
        return salida;
    }
    //READ this method reads data from one column. 
    public String[] getColumnValues(String dbTable,String columna,String orden){
        //Obtiene los datos de una columna, ordenados por el campo que se introduzca en "orden"
        if(echoON)System.out.println("getStringValues: SELECT "+columna+" FROM "+dbTable+" ORDER BY "+orden);    
        String[][] tabla = getTableValues(dbTable,columna,orden);
        String[] datos = new String[getRowCount(dbTable,columna)];
        for (int a=0;a<tabla.length;a++){
            datos[a] = tabla[a][0];
        }
        return datos;
    }
    //READ this method reads the values of the given columns in a String[] object
    public String[][] getTableValues(String dbTable,String[] columnas,String orden){
        //obtiene los datos de las columnas entregadas como un String[]
        String[][] tabla = new String[getRowCount(dbTable,orden)][columnas.length];
        //ArrayList<ArrayList <String>> tabla = new ArrayList<ArrayList <String>>();            
        try{
            for (int i=0;i<columnas.length;i++){
                if(echoON)System.out.println("getTableValues: SELECT "+columnas[i]+" FROM "+dbTable+" ORDER BY "+orden);
                ResultSet rs = sentencia.executeQuery("SELECT "+columnas[i]+" FROM "+dbTable+" ORDER BY "+orden);
                int j=0;//contador
                while ( rs.next() ){
                    tabla[j][i] = (rs.getString(1));
                    j++;
                }
            }
        }
        catch (Exception e) { 
            JOptionPane.showMessageDialog(null,e.getLocalizedMessage());
            e.printStackTrace();
        }
        return tabla;
    }
    //READ this method reads the values of the given columns in a String constructed like a csv. the csv can be "*"
    public String[][] getTableValues(String dbTable,String columnasCSV,String orden){
        //Obtiene los datos de las columnas en formato CSV (comma separated values) ordenadas segun orden
        //ArrayList<ArrayList <String>> tabla = new ArrayList<ArrayList <String>>();            
        String[][] tabla = new String[getRowCount(dbTable,columnasCSV)][getColumnCount(dbTable,columnasCSV)];
        try{
            ResultSet rs = sentencia.executeQuery("SELECT "+columnasCSV+" FROM "+dbTable+" ORDER BY "+orden);
            if(echoON)System.out.println("getTableValues: SELECT "+columnasCSV+" FROM "+dbTable+" ORDER BY "+orden);
            int j=0;//contador
            while ( rs.next() ){
                for (int i=0;i<tabla[0].length;i++){
                    tabla[j][i] = (rs.getString(i+1));
                }
                j++;
            }
        }
        catch (Exception e) { 
            JOptionPane.showMessageDialog(null,e.getLocalizedMessage() );
            e.printStackTrace();
        }
        return tabla;
    }
    //READ this methods gets one registry, the first that matches the condition in the query
    public String [] getSingleRow(String dbTable,String columnasCSV,String campo, String valor, String orden){
        //obtiene una sola fila (el primer resultado segun orden) de una busqueda
        String salida[] = new String[getColumnCount(dbTable,columnasCSV)];
        if(echoON)System.out.println("getSingleRow: SELECT " + columnasCSV +" FROM "+dbTable+" WHERE "+campo+"= '"+valor+"'"+" ORDER BY "+orden);
        try{
            String tabla[][] = getTableValues(dbTable+" WHERE "+campo+" = '"+valor+"'",columnasCSV,orden);
            salida = tabla[0];
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"Problema al cargar los datos","getSingleRow",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return salida;
    }
    //READ this method returns one data from one column according to the condition in the query
    public String getSingleData(String data, String dbTable,String campo, String valor){
        //obtiene un solo valor con el criterio de busqueda WHERE
        String salida = null;
        if(echoON)System.out.println("SELECT "+data+" FROM "+dbTable+" WHERE "+campo+"= '"+valor+"';");
        try{
            //String RR[][] = null;
            ResultSet rs = sentencia.executeQuery("SELECT "+data+" FROM "+dbTable+" WHERE "+campo+"= '"+valor+"'");
            while ( rs.next() ){
                salida = rs.getString(1);
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"Problema al cargar los datos","getSingleData",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return salida;
    }
    
    //me da mucha pereza a veces usar la sentencia completa,     
    public void sout (Object output){
        System.out.println(output.toString());
    }
    
}
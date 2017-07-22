package midFidelty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

/*
 * @author: Mujtaba Ali
 */
public class Connector {
	private Connection cn, cn2;
	
	public Connector(){
		cn=null;
		cn2 = null;
	}

	public Connection connectDataBase(){
		String constr="jdbc:mysql://localhost:3306/peranaldb";
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			cn=DriverManager.getConnection(constr,"root","");
			}
		
			catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Unable to connect database\n"+e);
			}
			 catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Unable to connect database\n"+e);
			}
		
		return cn;
	}
	
	public Connection connectDataBase2(){
		String constr="jdbc:mysql://localhost:3306/webhistory";
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			cn2=DriverManager.getConnection(constr,"root","");
			}
		
			catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Unable to connect database_2\n"+e);
			}
			 catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Unable to connect database_2\n"+e);
			}
		
		return cn2;
	}
	
}

package app;

import java.sql.*;
import javax.swing.*;

public class sqliteConnection {
	
	Connection conn = null;
	
	public static Connection dbConnector() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:DataBase/dbInzenjeringZnanja.sqlite");
			//JOptionPane.showMessageDialog(null,"Connection is succesfull!");
			return conn;
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
	
	
	public static Connection dbConnector2() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:DataBase/dbInzenjeringZnanja.sqlite");
			//JOptionPane.showMessageDialog(null,"Connection to Case-Based is succesfull!");
			return conn;
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
}
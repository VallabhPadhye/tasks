/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Dell
 */
public class dbcon {
      public static Connection mycon() throws InstantiationException, IllegalAccessException{
          
     try {
         
         Class.forName("org.h2.Driver").newInstance();
         Connection con = DriverManager.getConnection("jdbc:h2:"+"./database/taskmanager","root","password");
         Statement stmt = con.createStatement(); 
         String sql = "CREATE TABLE IF NOT EXISTS CURRENT(taskid varchar(255),subject varchar(255),task clob,startdate varchar(255),duedate varchar(255), UNIQUE KEY(taskid))";
         stmt.executeUpdate(sql);
         String sql2 = "CREATE TABLE IF NOT EXISTS COMPLETED(taskid varchar(255),subject varchar(255),task clob ,start_date varchar(255),due_date varchar(255), UNIQUE KEY(taskid))";
         stmt.executeUpdate(sql2);
         String sql3 = "CREATE TABLE IF NOT EXISTS EXPIRED(taskid varchar(255),subject varchar(255),task clob ,start_date varchar(255),expired varchar(255), UNIQUE KEY(taskid))";
         stmt.executeUpdate(sql3);
       
         return con;
        
         
     } catch (ClassNotFoundException | SQLException e) {
         
         System.out.println(e);
         JOptionPane.showMessageDialog(null, "Database Connection Failed!", "Error",JOptionPane.WARNING_MESSAGE);
         System.exit(0);
         
         
         
     }
         return null;
      } 
}

package com.md_4.thunderauthentication.backend.core.engine.tools;

// https://stackoverflow.com/questions/34856113/implementing-sql-in-a-bukkit-spigot-plugin
// Hint on Implementing professional Query Design as used on large database Systems.
// https://www.javaworld.com/article/2077706/core-java/named-parameters-for-preparedstatement.html

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import com.md_4.thunderauthentication.backend.core.engine.Config;

public final class MySQL {

    //public static String host = "localhost";
    //public static String port = "3306";
    //public static String username = "******";
    //public static String password = "*****";
    //public static Connection con;

    private static Connection MySqlConnection = null;

	static ConsoleCommandSender console = Bukkit.getConsoleSender();

    // connect
    public static void connect() {
        if (!isConnected()) {
            String host = Config.MySqlDbHost;
            String port = Config.MySqlDbPort;
            String database = Config.MySqlDbDbse;
            String dboption = Config.MySqlOption;
            String username = Config.MySqlDbUser;
            String password = Config.MySqlDbPswd;
            
        	String dbconnect = database; 
            if(!dboption.trim().equals("")) {
            	dbconnect = dbconnect + "?" + dboption; 
            }

        	if(host.equals("")) {
    		    console.sendMessage("[ThunderAuthentication] MySQL is Disabled, check MySqlDbHost in config.yml:");
        		return;
        	}
		    console.sendMessage("[ThunderAuthentication] Connecting to MySQL database system:");
        	try {
    		    console.sendMessage("[ThunderAuthentication] using host: " + host + " port: " + port + ", " +
    		    					                  "database: " + database + ", userid: "+ username);
    		    
    		    if(port.equals("")){
        			MySqlConnection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbconnect, username, password);
        			//con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + dbconnect + "&user=" + username + "&password=" + password);
        		} else {
        			MySqlConnection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbconnect, username, password);
        		}

        		console.sendMessage("[ThunderAuthentication] Connection to MySQL-Database (" + database + ") ok, now table check:");

        		PreparedStatement ps;
    		    ps = MySqlConnection.prepareStatement(
    		    				"CREATE TABLE IF NOT EXISTS Players " + 
    		    				"  (UUID      VARCHAR(40) not null,  " +
    		    				"   Name      VARCHAR(25) not null,  " +
    		    				"   Joyn      BIGINT,       " +
    		    				"   Quit      BIGINT,       " +
    		    				"   Login     BIGINT,       " +
    		    				"   LoggedIn  BOOL,         " +
    		    				"   Password  VARCHAR(150), " +
    		    				"   Hash      VARCHAR(20),  " +
    		    				"   IpAdress  VARCHAR(16),  " +
    		    				"   EmlAdress VARCHAR(255), " +
    		    				"   Vip       BOOL,         " +
    		    				"   GodModeEx BIGINT,       " +
    		    				"PRIMARY KEY (UUID))"				
    		    				);
    		    ps.executeUpdate();
    		    //if(ps.executeUpdate()>0) {
        		//  //ps = MySqlConnection.prepareStatement(
    	    	//	//		"alter table Players drop index (Name) "  
    	    	//	//		);
    		    //	//ps.executeUpdate();
    		    //	ps = MySqlConnection.prepareStatement(
    		    //			"alter table Players add index (Name) "  
    		    //			);
    		    //	ps.executeUpdate();
    		    //}

    			ps = MySqlConnection.prepareStatement(
    								"SELECT count(*) as Aantal " +
    								"FROM INFORMATION_SCHEMA.STATISTICS " + 
    								"WHERE TABLE_NAME = 'Players' " +
    								"  AND INDEX_NAME = 'Name' " + 
    								"  AND INDEX_SCHEMA='" + database + "' " 
    					);
    	    	ResultSet res = ps.executeQuery();
    	    	if (res.next() == true) {
    	    		if (res.getInt("Aantal")==0) {
        		    	ps = MySqlConnection.prepareStatement(
        		    			"alter table Players add index (Name) "  
        		    			);
        		    	ps.executeUpdate();
    	    		}
    	    	}
    	        res.close();
    		    ps.close();
    		    console.sendMessage("[ThunderAuthentication] All is well, Connected to MySQL database system.");
   		    } catch (SQLException e) {
   		    	e.printStackTrace();
   		    	Config.MySqlDbHost=""; // Error, Disable MySQL database Usage !!!!!
   			    console.sendMessage("[ThunderAuthentication] ---------------------------------------------------------------");
   			    console.sendMessage("[ThunderAuthentication] USAGE OF MySQL DISABLED (CONNECTION OR TABLES SETUP FAILED)!!!!");
   			    console.sendMessage("[ThunderAuthentication] ---------------------------------------------------------------");
   			    console.sendMessage("[ThunderAuthentication] Did you setup the ThunderAuthentication Database on MySQL?, like:");
   			    console.sendMessage("[ThunderAuthentication] -- create database " + database + "");
   			    console.sendMessage("[ThunderAuthentication] -- grant all privileges on " + database + ".* " +
   			    		                                             "to '" + username + "' " + 
   			    		                                             "identified by '<password>'");
   			    console.sendMessage("[ThunderAuthentication] -- flush privileges;");
   			    console.sendMessage("[ThunderAuthentication] (some MySQL Databases do not need the portnumber, try set it to '')");
   		    }
		} else {
    		console.sendMessage("[ThunderAuthentication] 'connect()' Already connected to MySQL database system?");
        }
    }

    // disconnect
    public static void disconnect() {
        if (isConnected()) {
            try {
            	MySqlConnection.close();
            	MySqlConnection=null;
                console.sendMessage("[ThunderAuthentication] Disconnected from MySQL database system!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // isConnected
    public static boolean isConnected() {
        return (MySqlConnection == null ? false : true);
    }

    // getConnection
    public static Connection getConnection() {
    	if(!isConnected()) {
    		connect();
    	}
		return MySqlConnection;    		
    }
}
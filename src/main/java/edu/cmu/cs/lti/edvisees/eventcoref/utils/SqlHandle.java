package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlHandle {
	
	private String sqlDb;
	private Connection conn;
	
		
	public SqlHandle(){
		super();
		this.sqlDb = "src/main/resources/simplewikidata/bklsimplewiki_lemma_sql.db";
		String sDriverName = "org.sqlite.JDBC";
		try {
			Class.forName(sDriverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("xxxxx");
		}
		String sJdbc = "jdbc:sqlite";
		String sDbUrl = sJdbc + ":" + sqlDb;
		try {
			Connection conn = DriverManager.getConnection(sDbUrl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("xxxxxx");
		}
		//return conn;
	}
	
	public SqlHandle(String location) throws Exception {
		super();
		this.sqlDb = location;
		String sDriverName = "org.sqlite.JDBC";
		Class.forName(sDriverName);
		String sJdbc = "jdbc:sqlite";
		String sDbUrl = sJdbc + ":" + sqlDb;
		conn = DriverManager.getConnection(sDbUrl);
		//return conn;
	}
	
	
	
//	public Connection sqlInit() throws Exception{
		
//	}
	
	

	public ResultSet sqlGet(String query) throws Exception{
		Statement stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery(query);
			return rs;
		
		
	}



	public String getSqlDb() {
		return sqlDb;
	}



	public void setSqlDb(String sqlDb) {
		this.sqlDb = sqlDb;
	}
}
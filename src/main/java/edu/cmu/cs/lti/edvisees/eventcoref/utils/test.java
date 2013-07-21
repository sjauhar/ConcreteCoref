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

import org.sqlite.SQLiteJDBCLoader;




public class test {
public static void main(String[] args) throws Exception{
	SqlHandle tsq= new SqlHandle("src/main/resources/simplewikidata/bklsimplewiki_lemma_sql0.db");
	String[] words = {"run_root"};
	System.out.println(SQLiteJDBCLoader.isNativeMode());
	for (String w : words){
		//try{
		ResultSet rs= tsq.sqlGet("SELECT * from Triples WHERE w1 = '"+w+"'");
		//try{
			while(rs.next()){
				
				String sResultW2 = rs.getString("w2");
				String sResultRel = rs.getString("rel");
				System.out.println(w+" "+sResultW2+" "+ sResultRel);
			}
		//}catch (Exception ignore){continue;}
		//}catch (Exception ignore) {	}
	
	}
		
}
}


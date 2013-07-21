package edu.cmu.cs.lti.edvisees.eventcoref.utils;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.GoldArgm;
//import edu.cmu.cs.lti.edvisees.eventcoref.utils.ObjectSizeFetcher;




import java.util.*;

import com.google.common.collect.*;
import com.google.common.collect.Table.Cell;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.CompositionUtils;

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

import objectexplorer.ObjectGraphMeasurer;
public class Gold {
	public static Table<String,String,Multiset<String>> golds(GoldArgm a,SqlHandle tsq) throws Exception{
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		String w1= a.getW1();
		String w2= a.getW2();
		String rel= a.getRel();
		String relt= new String();
		if (rel.equals("A")){
			relt= "'nsubj','dobj','pobj','partmod'";
		}
		else{
			relt="'dobj','nsubj','ccomp','xcomp','partmod','pobj'";
		}
		String sent="";
		ResultSet rs= tsq.sqlGet("select SentenceIDs,corpus from Triples where w1= '"+w1+"' and w2 = '"+w2+"' and rel IN ("+relt+")");
		while(rs.next()){
			sent+= rs.getString("SentenceIDs");
			//System.out.println(sent);
		}
		List<String> ids= Arrays.asList(sent.replaceAll("^_+","").split("_"));
		Multiset<String> common = HashMultiset.create();
		
		common.addAll(ids);
		if (common.contains("") && common.size()==1){
			//System.out.println("no common ids");
			return rettable;
		}
		
		
		Table<String,String,Multiset<String>> w1dict = CompositionUtils.Singledict(w1, tsq);
		Table<String,String,Multiset<String>> w2dict = CompositionUtils.Singledict(w2, tsq);
		
		Table<String,String,Multiset<String>> w1dict1= CompositionUtils.tintersect(w1dict, common);
		Table<String,String,Multiset<String>> w2dict1= CompositionUtils.tintersect(w2dict, common);
		rettable= CompositionUtils.tjoin(w1dict1, w2dict1);
		//System.out.println(common.toString());
		
		return rettable;
	}
	
	public static Table<String,String,Multiset<String>> goldsst1(GoldArgm a,SqlHandle tsq) throws Exception{
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		String w1= a.getW1();
		String w2= a.getW2();
		String rel= a.getRel();
		String relt= new String();
		if (rel.equals("A")){
			relt= "'nsubj','dobj','pobj','partmod'";
		}
		else{
			relt="'dobj','nsubj','ccomp','xcomp','partmod','pobj'";
		}
		String sent="";
		String w1str= "B-"+w1+"','I-"+w1+"','O-"+w1;
		ResultSet rs= tsq.sqlGet("select SentenceIDs,corpus from Triples where w1 IN ('"+w1str+"') and w2 = '"+w2+"' and rel IN ("+relt+")");
		while(rs.next()){
			sent+= rs.getString("SentenceIDs");
			//System.out.println(sent);
		}
		List<String> ids= Arrays.asList(sent.replaceAll("^_+","").split("_"));
		Multiset<String> common = HashMultiset.create();
		
		common.addAll(ids);
		if (common.contains("") && common.size()==1){
			//System.out.println("no common ids");
			return rettable;
		}
		
		
		Table<String,String,Multiset<String>> w1dict = CompositionUtils.Singledictsst(w1, tsq);
		Table<String,String,Multiset<String>> w2dict = CompositionUtils.Singledict(w2, tsq);
		
		Table<String,String,Multiset<String>> w1dict1= CompositionUtils.tintersect(w1dict, common);
		Table<String,String,Multiset<String>> w2dict1= CompositionUtils.tintersect(w2dict, common);
		rettable= CompositionUtils.tjoin(w1dict1, w2dict1);
		//System.out.println(common.toString());
		
		return rettable;
	}
	
	public static Table<String,String,Multiset<String>> goldsst2(GoldArgm a,SqlHandle tsq) throws Exception{
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		String w1= a.getW1();
		String w2= a.getW2();
		String rel= a.getRel();
		String relt= new String();
		if (rel.equals("A")){
			relt= "'nsubj','dobj','pobj','partmod'";
		}
		else{
			relt="'dobj','nsubj','ccomp','xcomp','partmod','pobj'";
		}
		String sent="";
		String w2str= "B-"+w2+"','I-"+w2+"','O-"+w2;
		ResultSet rs= tsq.sqlGet("select SentenceIDs,corpus from Triples where w2 IN ('"+w2str+"') and w1 = '"+w1+"' and rel IN ("+relt+")");
		while(rs.next()){
			sent+= rs.getString("SentenceIDs");
			//System.out.println(sent);
		}
		List<String> ids= Arrays.asList(sent.replaceAll("^_+","").split("_"));
		Multiset<String> common = HashMultiset.create();
		
		common.addAll(ids);
		if (common.contains("") && common.size()==1){
			//System.out.println("no common ids");
			return rettable;
		}
		
		
		Table<String,String,Multiset<String>> w1dict = CompositionUtils.Singledict(w1, tsq);
		Table<String,String,Multiset<String>> w2dict = CompositionUtils.Singledictsst(w2, tsq);
		
		Table<String,String,Multiset<String>> w1dict1= CompositionUtils.tintersect(w1dict, common);
		Table<String,String,Multiset<String>> w2dict1= CompositionUtils.tintersect(w2dict, common);
		rettable= CompositionUtils.tjoin(w1dict1, w2dict1);
		//System.out.println(common.toString());
		
		return rettable;
	}
	
	public static Table<String,String,Multiset<String>> goldsst(GoldArgm a,SqlHandle tsq) throws Exception{
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		String w1= a.getW1();
		String w2= a.getW2();
		String rel= a.getRel();
		String relt= new String();
		if (rel.equals("A")){
			relt= "'nsubj','dobj','pobj','partmod'";
		}
		else{
			relt="'dobj','nsubj','ccomp','xcomp','partmod','pobj'";
		}
		String sent="";
		String w1str= "B-"+w1+"','I-"+w1+"','O-"+w1;
		String w2str= "B-"+w2+"','I-"+w2+"','O-"+w2;
		ResultSet rs= tsq.sqlGet("select SentenceIDs,corpus from Triples where w2 IN ('"+w2str+"') and w1 IN ('"+w1str+"') and rel IN ("+relt+")");
		while(rs.next()){
			sent+= rs.getString("SentenceIDs");
			//System.out.println(sent);
		}
		List<String> ids= Arrays.asList(sent.replaceAll("^_+","").split("_"));
		Multiset<String> common = HashMultiset.create();
		
		common.addAll(ids);
		if (common.contains("") && common.size()==1){
			//System.out.println("no common ids");
			return rettable;
		}
		
		
		Table<String,String,Multiset<String>> w1dict = CompositionUtils.Singledictsst(w1, tsq);
		Table<String,String,Multiset<String>> w2dict = CompositionUtils.Singledictsst(w2, tsq);
		
		Table<String,String,Multiset<String>> w1dict1= CompositionUtils.tintersect(w1dict, common);
		Table<String,String,Multiset<String>> w2dict1= CompositionUtils.tintersect(w2dict, common);
		rettable= CompositionUtils.tjoin(w1dict1, w2dict1);
		//System.out.println(common.toString());
		
		return rettable;
	}
	
	public static void main(String[] args) throws Exception{
		//System.out.println(java.lang.Runtime.getRuntime().maxMemory());
		//System.out.println(java.lang.Runtime.getRuntime().totalMemory());
		GoldArgm argm = new GoldArgm("he_root","eat_root","A");
		GoldArgm argm1 = new GoldArgm("noun.person_sst","eat_root","A");
		SqlHandle tsq= new SqlHandle("src/main/resources/simplewikidata/bklsimplewiki_lemma_sql0.db");
		SqlHandle tsq1= new SqlHandle("src/main/resources/simplewikidata/repaired_bklsimplewiki_word_lemma_sst_sql0.db");
		Table<String, String, Multiset<String>> golden = goldsst1(argm1,tsq1);
		for(Cell<String,String,Multiset<String>> cell:golden.cellSet()){
			System.out.println(cell.getRowKey()+": "+cell.getColumnKey()+": "+cell.getValue());
		}
		//System.out.println(CompositionUtils.fill_lem("eat_root","A",tsq));
		
	}

}

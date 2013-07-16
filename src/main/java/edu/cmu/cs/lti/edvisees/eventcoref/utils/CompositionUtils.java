package edu.cmu.cs.lti.edvisees.eventcoref.utils;
import java.util.*;

import com.google.common.collect.*;
import com.google.common.collect.Table.Cell;

public class CompositionUtils {
	public static Table<String,String,Multiset<String>> tintersect(Table<String,String,Multiset<String>> tab1,Table<String,String,Multiset<String>> tab2){
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		for(Cell<String,String,Multiset<String>> cell:tab1.cellSet()){
			String row=cell.getRowKey();
			String column=cell.getColumnKey();
			Multiset<String> mulset1= cell.getValue();
			Multiset<String> mulset2= tab2.get(row, column);
			if(!(mulset2==null)){
					Multiset<String> mulset= Multisets.intersection(mulset1, mulset2);
					rettable.put(row, column, mulset);
				}
		}
		return rettable;
			
	}
	
	public static Table<String,String,Multiset<String>> tjoin(Table<String,String,Multiset<String>> tab1,Table<String,String,Multiset<String>> tab2){
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		for(Cell<String,String,Multiset<String>> cell:tab1.cellSet()){
			String row=cell.getRowKey();
			String column=cell.getColumnKey();
			Multiset<String> mulset1= cell.getValue();
			Multiset<String> mulset2= tab2.get(row, column);
			if(!(mulset2 == null)){
				Multiset<String> mulset= Multisets.sum(mulset1, mulset2);
				rettable.put(row, column, mulset);
				}
				else{
					rettable.put(row, column, mulset1);
				}
		}
		for(Cell<String,String,Multiset<String>> cell:tab2.cellSet()){
			String row=cell.getRowKey();
			String column=cell.getColumnKey();
			Multiset<String> mulset1= cell.getValue();
			Multiset<String> mulset2= tab1.get(row, column);
			if(mulset2 == null){
					rettable.put(row, column, mulset1);
				}
		}
		return rettable;
	}
	
	public static Table<String,String,Multiset<String>> tintersect(Table<String,String,Multiset<String>> tab1,Multiset<String> tab2){
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		for(Cell<String,String,Multiset<String>> cell:tab1.cellSet()){
			String row=cell.getRowKey();
			String column=cell.getColumnKey();
			Multiset<String> mulset1= cell.getValue();
			Multiset<String> mulset2= tab2;
			if(!(mulset2==null)){
					Multiset<String> mulset= Multisets.intersection(mulset1, mulset2);
					rettable.put(row, column, mulset);
				}
		}
		return rettable;
			
	}
	
	
	public static void main(String [] args){
		/*HashMap<String, HashMap<String, HashMap<String, Integer>>> map1 = new HashMap <String,HashMap <String,HashMap<String,Integer>>>();
		HashMap<String, HashMap<String, HashMap<String, Integer>>> map2 = new HashMap <String,HashMap <String,HashMap<String,Integer>>>();
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> map1c = new HashMap <String,HashMap <String,HashMap<String,Integer>>>();
		HashMap<String, HashMap<String, HashMap<String, Integer>>> map2c = new HashMap <String,HashMap <String,HashMap<String,Integer>>>();
		
		
		HashMap<String,Integer> sub= new HashMap<String,Integer>();
		HashMap<String,Integer> sub2= new HashMap<String,Integer>();
		sub.put("sentids", 2);
		HashMap<String, HashMap<String, Integer>> sub1 = new HashMap<String, HashMap<String,Integer>>();
		sub1.put("word", sub);
		map1.put("rel",sub1);
		//sub.remove("sentids");
		//sub1.remove("word");
		sub2.put("sentids", 4);
		HashMap<String, HashMap<String, Integer>> sub21 = new HashMap<String, HashMap<String,Integer>>();
		sub21.put("word", sub2);
		
		//HashMap<String, HashMap<String, Integer>> sub1 = new HashMap<String, HashMap<String,Integer>>();
		map2.put("rel",sub21);
		map1c.putAll(map1);
		map2c.putAll(map2);
		HashMap<String, HashMap<String, HashMap<String, Integer>>> maptest = CompositionUtils.intersection(map1c, map2c, "join");
		System.out.println(maptest.toString());
		map1c.clear();
		map2c.clear();
		System.out.println(map1.toString());
		map1c.putAll(map1);
		map2c.putAll(map2);
		
		HashMap<String, HashMap<String, HashMap<String, Integer>>> maptest2 = CompositionUtils.union(map1c, map2c);
		System.out.println(maptest2.toString());*/
		Table<String,String,Multiset<String>> tbl = HashBasedTable.create();
		Table<String,String,Multiset<String>> tbl2 = HashBasedTable.create();
		Multiset<String> mu= HashMultiset.create();
		mu.add("434");
		mu.add("412",3);
		mu.add("54");
		
		tbl.put("nsub", "eat", mu);
		
		//System.out.println(tbl.get("nsub", "eat").toString());
		//System.out.println(tbl.get("nsub", "puke"));
		Multiset<String> m= HashMultiset.create();
		m.add("412",4);
		m.add("234");
		m.add("434");
		tbl2.put("nsub", "play", m);
		System.out.println(tintersect(tbl,tbl2));
		System.out.println(tjoin(tbl,tbl2));
		//System.out.println(Multisets.intersection(mu, m).toString());
		//System.out.println(mu.toString()+m.toString());
		//System.out.println(tbl.columnKeySet().toString());
		
	}
}

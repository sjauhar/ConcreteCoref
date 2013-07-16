package edu.cmu.cs.lti.edvisees.eventcoref.utils;
import java.util.*;

import com.google.common.collect.*;
import com.google.common.collect.Table.Cell;

public class CompositionUtils {
	public static Table<String,String,Multiset<String>> tintersect(Table<String,String,Multiset<String>> tab1,Table<String,String,Multiset<String>> tab2,String par){
		//Iterator itr = tab1.cellSet().iterator();
		//while (itr.hasNext()){
		//	nex= itr.next().;
		//Cell<String,String,Multiset<String>> cell = new Cell<String,String,Multiset<String>>();
		Table<String,String,Multiset<String>> rettable= HashBasedTable.create();
		for(Cell<String,String,Multiset<String>> cell:tab1.cellSet()){
			String row=cell.getRowKey();
			String column=cell.getColumnKey();
			Multiset<String> mulset1= cell.getValue();
			Multiset<String> mulset2= tab2.get(row, column);
			if (par.equals("int")){
			Multiset<String> mulset= Multisets.intersection(mulset1, mulset2);
			rettable.put(row, column, mulset);
			}
			else
			{
				Multiset<String> mulset= Multisets.
			}
			
		}
		return rettable;
			
	}
	public static HashMap <String,HashMap <String,HashMap	<String,Integer>>> intersection(HashMap <String,HashMap <String,HashMap	<String,Integer>>> map11, HashMap <String,HashMap <String,HashMap <String,Integer>>> map21, String dec){
		Set comset= Sets.intersection(map11.keySet(), map21.keySet());
		//map11.keySet().retainAll(map21.keySet());
		//map2.keySet().retainAll(map1.keySet());
		//System.out.println(map1.toString());
		Iterator itr= map11.keySet().iterator();
		while(itr.hasNext()){
			String next= (String)itr.next();
			
			map11.get(next).keySet().retainAll(map21.get(next).keySet());
			//map2.get(next).keySet().retainAll(map1.get(next).keySet());
			Iterator itr1= map11.get(next).keySet().iterator();
			while(itr1.hasNext()){
				String next1=(String)itr1.next();
				map11.get(next).get(next1).keySet().retainAll(map21.get(next).get(next1).keySet());
				//map2.get(next).get(next1).keySet().retainAll(map1.get(next).get(next1).keySet());
			}
		}
		
		Iterator itrn= map11.keySet().iterator();
		while(itrn.hasNext()){
			
			String next= (String)itrn.next();
			//System.out.println(map1.get(next).equals(new HashMap()));
			
			Iterator itr1= map11.get(next).keySet().iterator();
			while(itr1.hasNext()){
				//System.out.println("2nd level");
				String next1=(String)itr1.next();
				
				Iterator itr2= map11.get(next).get(next1).keySet().iterator();
				while(itr2.hasNext()){
					//System.out.println("3rd level");
					String next2= (String)itr2.next();
					if (dec.equals("red")){
						//System.out.println(map1.get(next).get(next1).get(next2));
						//System.out.println(map2.get(next).get(next1).get(next2));
						map11.get(next).get(next1).put(next2,Math.min(map11.get(next).get(next1).get(next2),map21.get(next).get(next1).get(next2)));
					}
					else if (dec.equals("join")){
						map11.get(next).get(next1).put(next2,(map11.get(next).get(next1).get(next2)+map21.get(next).get(next1).get(next2)));
					}
				}
				if (map11.get(next).get(next1).equals(new HashMap())){
					map11.get(next).remove(next1);
					continue;
				}
			}
			if (map11.get(next).equals(new HashMap())){
				map11.remove(next);
				continue;
			}
		}
		//System.out.println(map1.toString());
		//System.out.println(map2.toString());
		return map11; 
	}
	
	public static HashMap <String,HashMap <String,HashMap	<String,Integer>>> union(HashMap <String,HashMap <String,HashMap<String,Integer>>> map1, HashMap <String,HashMap <String,HashMap <String,Integer>>> map2){
		HashMap<String, HashMap<String, HashMap<String, Integer>>> map1c = new HashMap <String,HashMap <String,HashMap<String,Integer>>>();
		HashMap<String, HashMap<String, HashMap<String, Integer>>> map2c = new HashMap <String,HashMap <String,HashMap<String,Integer>>>();
		map1c.putAll(map1);
		map2c.putAll(map2);
		CompositionUtils.intersection(map1c, map2c, "join");
		map2c.keySet().removeAll(map1.keySet());
		return map1;
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
		Table<String,String,Multiset> tbl = HashBasedTable.create();
		Multiset<String> mu= HashMultiset.create();
		mu.add("ant");
		mu.add("horse");
		mu.add("ant");
		
		tbl.put("nsub", "eat", mu);
		
		System.out.println(tbl.get("nsub", "eat").toString());
		System.out.println(tbl.get("nsub", "puke"));
		Multiset<String> m= HashMultiset.create();
		m.add("horse",4);
		m.add("ant");
		m.add("cheetah");
		tbl.put("nsub", "play", m);
		System.out.println(Multisets.intersection(mu, m).toString());
		System.out.println(mu.toString()+m.toString());
		System.out.println(tbl.columnKeySet().toString());
		
	}
}

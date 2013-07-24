package edu.cmu.cs.lti.edvisees.eventcoref.utils;
import java.sql.ResultSet;
import java.util.*;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.GoldArgm;






//import edu.cmu.cs.lti.edvisees.eventcoref.utils.ObjectSizeFetcher;
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
					if (!mulset.isEmpty()){
					rettable.put(row, column, mulset);
					}
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
					if (!mulset.isEmpty()){
					rettable.put(row, column, mulset);
					}
				}
		}
		return rettable;
			
	}
	
	public static Table<String,String,Multiset<String>> Singledict(String w1, SqlHandle tsq) throws Exception{
		String sent="";
		Table<String,String,Multiset<String>> w1dict = HashBasedTable.create();
		
		int count=0;
		
		ResultSet rs= tsq.sqlGet("select rel,w2,SentenceIDs,corpus from Triples where w1= \""+w1+"\"");
		while(rs.next()){
			count =count +1;
			sent = rs.getString("SentenceIDs");
			String rel1=rs.getString("rel");
			String w21= rs.getString("w2");
			
			List<String> ids1= Arrays.asList(sent.replaceAll("^_+","").split("_"));
			Multiset<String> intermed = HashMultiset.create();
			intermed.addAll(ids1);
			
			if(w1dict.get(rel1, w21)==null){
			w1dict.put(rel1, w21, intermed);
			
			}
			else{
				w1dict.put(rel1, w21,Multisets.sum(intermed, w1dict.get(rel1,w21)));
			}
		}
		
		sent="";
		rs= tsq.sqlGet("select rel,w1,SentenceIDs,corpus from Triples where w2= \""+w1+"\"");
		while(rs.next()){
			
			sent = rs.getString("SentenceIDs");
			String rel1=rs.getString("rel");
			String w21= rs.getString("w1");
			//System.out.println("In w1 1: "+w21+": "+rel1);
			List<String> ids1= Arrays.asList(sent.replaceAll("^_+","").split("_"));
			Multiset<String> intermed = HashMultiset.create();
			intermed.addAll(ids1);
			if(w1dict.get(rel1, w21)==null){
			w1dict.put(rel1, w21, intermed);
			}
			else{
				w1dict.put(rel1, w21,Multisets.sum(intermed, w1dict.get(rel1,w21)));
			}
		}
		
		return w1dict;
	}
	
	public static Table<String,String,Multiset<String>> Singledictsst(String w1, SqlHandle tsq) throws Exception{
		String sent="";
		Table<String,String,Multiset<String>> w1dict = HashBasedTable.create();
		
		int count=0;
		String w1str= "B-"+w1+"','I-"+w1+"','O-"+w1;
		ResultSet rs= tsq.sqlGet("select rel,w2,SentenceIDs,corpus from Triples where w1 IN ('"+w1str+"')");
		while(rs.next()){
			count =count +1;
			sent = rs.getString("SentenceIDs");
			String rel1=rs.getString("rel");
			String w21= rs.getString("w2");
			
			List<String> ids1= Arrays.asList(sent.replaceAll("^_+","").split("_"));
			Multiset<String> intermed = HashMultiset.create();
			intermed.addAll(ids1);
			
			if(w1dict.get(rel1, w21)==null){
			w1dict.put(rel1, w21, intermed);
			
			}
			else{
				w1dict.put(rel1, w21,Multisets.sum(intermed, w1dict.get(rel1,w21)));
			}
		}
		
		sent="";
		rs= tsq.sqlGet("select rel,w1,SentenceIDs,corpus from Triples where w2 IN ('"+w1str+"')");
		while(rs.next()){
			
			sent = rs.getString("SentenceIDs");
			String rel1=rs.getString("rel");
			String w21= rs.getString("w1");
			//System.out.println("In w1 1: "+w21+": "+rel1);
			List<String> ids1= Arrays.asList(sent.replaceAll("^_+","").split("_"));
			Multiset<String> intermed = HashMultiset.create();
			intermed.addAll(ids1);
			if(w1dict.get(rel1, w21)==null){
			w1dict.put(rel1, w21, intermed);
			}
			else{
				w1dict.put(rel1, w21,Multisets.sum(intermed, w1dict.get(rel1,w21)));
			}
		}
		
		return w1dict;
	}
	
	
	
	
	public static HashMap<String,Double> fill_lem(String event, String rel, SqlHandle tsq) throws Exception{
		HashMap<String,Double> retmap= new HashMap<String,Double>();
		HashMultimap<Double,String> invretmap= HashMultimap.create();
		String relt= "";
		if (rel.equals("A")){
			relt= "'nsubj','dobj','pobj','partmod'";
		}
		else if(rel.equals("P")){
			relt="'dobj','nsubj','ccomp','xcomp','partmod','pobj'";
		}
		ResultSet rs= tsq.sqlGet("select w1,count from Triples where w2= \""+event+"\" and rel IN ("+relt+") order by count DESC");
		int count=0;
		while(rs.next()&&count<1){
			//System.out.println(rs.getString("count")+": "+rs.getString("w1"));
			invretmap.put((double)Integer.parseInt(rs.getString("count")),rs.getString("w1"));
			
			count +=1;
		}
		
		rs= tsq.sqlGet("select w2,count from Triples where w1= \""+event+"\" and rel IN ("+relt+") order by count DESC");
		count=0;
		while(rs.next()&&count<1){
			//System.out.println(rs.getString("count")+": "+rs.getString("w2"));
			invretmap.put((double)Integer.parseInt(rs.getString("count")),rs.getString("w2"));
			count +=1;
		}
		List l=Lists.newArrayList(invretmap.keySet());
		Collections.sort(l,Collections.reverseOrder());
		count=0;
		int i=0;
		while(count<1 && i<l.size()){
			Double freq= (Double)l.get(i);
			for(String s:invretmap.get(freq)){
				retmap.put(s,freq);
				count +=1;
			}
			i+=1;
		}
		//HashMap<Double,Double> normmap= new HashMap<Double,Double>();
		Collection<Double> l1 = retmap.values();
		Double sum=0.0;
		for(Double k:l1){
			sum += k;
		}
		for(String k:retmap.keySet()){
			retmap.put(k, retmap.get(k)/sum);
			
		}
		return retmap;
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
		mu.add("444");
		mu.add("432",3);
		mu.add("54");
		
		tbl.put("nsub", "eat", mu);
		
		//System.out.println(tbl.get("nsub", "eat").toString());
		//System.out.println(tbl.get("nsub", "puke"));
		Multiset<String> m= HashMultiset.create();
		m.add("412",4);
		m.add("234");
		m.add("434");
		tbl2.put("nsub", "play", m);
		System.out.println(tintersect(tbl,m).get("nsub", "eat").isEmpty());
		System.out.println(tjoin(tbl,tbl2));
		//System.out.println(tbl.get("sadsa", "sada")==null);
		System.out.println(tbl2);
		//tbl2.put("nsub", "play", mu);
		//System.out.println(tbl2);
		//System.out.println(Multisets.intersection(mu, m).toString());
		//System.out.println(mu.toString()+m.toString());
		//System.out.println(tbl.columnKeySet().toString());
		
	}
}

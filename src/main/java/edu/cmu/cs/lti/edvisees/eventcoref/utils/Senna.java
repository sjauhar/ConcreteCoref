package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Senna {

	private static HashMap<String, ArrayList<Double>> sVector = new HashMap<String, ArrayList<Double>>();
	
	static{
		try {
			
			String wordsFile = "/Users/shashans/Documents/Work/Senna/senna/hash/words.lst";
			String embedFile = "/Users/shashans/Documents/Work/Senna/senna/embeddings/embeddings.txt";
			//String embedFile = "main/resources/embeddings.txt";
			BufferedReader wordsBr = new BufferedReader(new FileReader(wordsFile));
			BufferedReader embedBr = new BufferedReader(new FileReader(embedFile));

			while (true) {
			    String lineOne = wordsBr.readLine();
			    String lineTwo = embedBr.readLine();

			    if (lineOne == null || lineTwo == null)
			        break;

			    ArrayList<Double> t = new ArrayList<Double>();
			    String[] tmp = lineTwo.split(" ");    //Split space
			    for(String s: tmp)
			       t.add(Double.parseDouble(s));

			    sVector.put(lineOne, t);		    
			}
			
			wordsBr.close();
			embedBr.close();
			
		} catch (Exception e) {
			System.out.println("COULD NOT OPEN SENNA FILES!");
		}
	}
	
	public static ArrayList<Double> getVector( String str){
		if(sVector.containsKey(str))
			return sVector.get(str);
		else
			return sVector.get("UNKNOWN");
	}
	
	public static double dotProduct( String s1, String s2){
		ArrayList<Double>v1 = getVector(s1);
		ArrayList<Double>v2 = getVector(s2);
		double sum = 0;
		for(int i=0; i<getVector(s1).size(); i++){
			sum += v1.get(i)*v2.get(i);
		}
		return sum;
	}
	
	/**
	 * Returns cosine similarity between Senna Representations of argument strings s1 and s2
	 */
	public static double cosineSim( String s1, String s2){
		return dotProduct(s1,s2)/Math.sqrt(dotProduct(s1, s1)*dotProduct(s2, s2));
	}
	
	public static double l2Distance( String s1, String s2){
		ArrayList<Double>v1 = getVector(s1);
		ArrayList<Double>v2 = getVector(s2);
		double x = 0;
		for(int i=0; i<getVector(s1).size(); i++){
			double t = (v1.get(i)-v2.get(i));
			x += t*t;
		}
		return Math.sqrt(x);
	}
	
	public static double l1Distance( String s1, String s2){
		ArrayList<Double>v1 = getVector(s1);
		ArrayList<Double>v2 = getVector(s2);
		double x = 0;
		for(int i=0; i<getVector(s1).size(); i++){
			x += Math.abs(v1.get(i)-v2.get(i));
		}
		return x;
	}
	
}

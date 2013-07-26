package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Eigen {

	private static HashMap<String, ArrayList<Double>> sVector = new HashMap<String, ArrayList<Double>>();
	
	static{
		try {
			
			String embedFile = "src/main/resources/eigenwords.txt";
			BufferedReader embedBr = new BufferedReader(new FileReader(embedFile));

			while (true) {
			    String line = embedBr.readLine();
			    if (line == null)
			        break;

			    ArrayList<Double> t = new ArrayList<Double>();
			    String[] tmp = line.split(" ");    //Split space
			    String lineOne = tmp[0];
			    for(int i=1;i<tmp.length;i++)
			    	t.add(Double.parseDouble(tmp[i]));
			    sVector.put(lineOne, t);		    
			}
			embedBr.close();
			
		} catch (Exception e) {
			System.out.println(e+"COULD NOT OPEN EIGEN FILE!");
		}
	}
	
	public static ArrayList<Double> getVector( String str){
		if(sVector.containsKey(str))
			return sVector.get(str);
		else
			return sVector.get("<OOV>");
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
	 * Returns cosine similarity between Eigen Representations of argument strings s1 and s2
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

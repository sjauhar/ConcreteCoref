package edu.cmu.cs.lti.edvisees.eventcoref.features;
import java.util.ArrayList;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.*;


public class LexicographicfeaturesNew {
	public enum Similarities {
		levenshtein,
		jaro,
		soundex,
		qgrams
	}
	public static ArrayList<Double> genfeat(PredicateArgument pa1, PredicateArgument pa2){
		ArrayList<Double> ret= new ArrayList<Double>();
		String action1 = pa1.getAction();
		String action2 = pa2.getAction();
		String agent1 = pa1.getAgent();
		String agent2 = pa2.getAgent();
		String patient1 = pa1.getPatient();
		String patient2 = pa2.getPatient();

		ret.addAll(stringSimFeatures(action1,action2,agent1,agent2,patient1,patient2,"levenshtein"));
		ret.addAll(stringSimFeatures(action1,action2,agent1,agent2,patient1,patient2,"jaro"));
		//ret.addAll(stringSimFeatures(action1,action2,agent1,agent2,patient1,patient2,"soundex"));
		//ret.addAll(stringSimFeatures(action1,action2,agent1,agent2,patient1,patient2,"qgrams"));
		
		/*
		System.out.println("Agent1:"+agent1+":Action1:"+action1+":Patient1:"+patient1+":Agent2:"+agent2+":Action2:"+action2+":Patient2:"+patient2);
		System.out.print("[");
		for(Double r:ret){
			System.out.print(r+",");
		}System.out.println("]");
		*/
		return ret;
	}
	public static ArrayList<Double> stringSimFeatures(String action1, String action2,String agent1, String agent2, String patient1, String patient2, String sim){
		ArrayList<Double> ret= new ArrayList<Double>();
		Similarities simVal = Similarities.valueOf(sim);
		switch (simVal){
		case levenshtein:
		{
			Levenshtein simClass = new Levenshtein();
			if (action1.equals("-")||action2.equals("-"))ret.add(-1.0);
			else ret.add((double)simClass.getSimilarity(action1, action2));
			
			if (agent1.equals("-")||agent2.equals("-"))ret.add(-1.0);
			else ret.add((double)simClass.getSimilarity(agent1, agent2));

			if (patient1.equals("-")||patient2.equals("-"))ret.add(-1.0);
			else ret.add((double)simClass.getSimilarity(patient1, patient2));
			break;
		}	
		case jaro:
		{	Jaro simClass = new Jaro();
		    if (action1.equals("-")||action2.equals("-"))ret.add(-1.0);
		    else ret.add((double)simClass.getSimilarity(action1, action2));
		
			if (agent1.equals("-")||agent2.equals("-"))ret.add(-1.0);
			else ret.add((double)simClass.getSimilarity(agent1, agent2));

			if (patient1.equals("-")||patient2.equals("-"))ret.add(-1.0);
			else ret.add((double)simClass.getSimilarity(patient1, patient2));
		break;
		}
		case soundex:
		{	Soundex simClass = new Soundex();
		if (action1.equals("-")||action2.equals("-"))ret.add(-1.0);
		else ret.add((double)simClass.getSimilarity(action1, action2));
		
		if (agent1.equals("-")||agent2.equals("-"))ret.add(-1.0);
		else ret.add((double)simClass.getSimilarity(agent1, agent2));

		if (patient1.equals("-")||patient2.equals("-"))ret.add(-1.0);
		else ret.add((double)simClass.getSimilarity(patient1, patient2));
		break;
		}
		case qgrams:
		{	QGramsDistance simClass = new QGramsDistance();
		if (action1.equals("-")||action2.equals("-"))ret.add(-1.0);
		else ret.add((double)simClass.getSimilarity(action1, action2));
		
		if (agent1.equals("-")||agent2.equals("-"))ret.add(-1.0);
		else ret.add((double)simClass.getSimilarity(agent1, agent2));

		if (patient1.equals("-")||patient2.equals("-"))ret.add(-1.0);
		else ret.add((double)simClass.getSimilarity(patient1, patient2));
		break;
		}
		}
		return ret;
	}
}


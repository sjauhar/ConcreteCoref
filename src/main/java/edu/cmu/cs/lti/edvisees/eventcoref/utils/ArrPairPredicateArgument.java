package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import edu.cmu.cs.lti.edvisees.eventcoref.features.SDSMfeatures;
import edu.cmu.cs.lti.edvisees.eventcoref.features.SennaSimilarity;

public class ArrPairPredicateArgument {
	ArrayList<PairPredicateArgument> arrPredPairs;

	public ArrPairPredicateArgument(ArrayList<PairPredicateArgument> arrPredPairs) {
		super();
		this.arrPredPairs = arrPredPairs;
	}

	public ArrPairPredicateArgument(String fileName) {
		//Constructor to be called by Kartik
		BufferedReader br = null;
		arrPredPairs = new ArrayList<PairPredicateArgument>();
	    try {
	    	br = new BufferedReader(new FileReader(fileName));
	        String line = br.readLine();
	        line = br.readLine();
	        while (line != null) {
	            String arr[] = line.split("\t");
	            if(arr.length!=29)
	            	System.out.println("Some error in line "+line+"\nNumber of fields is not 29.");
	            else{
	            	//PredicateArgument pa = new PredicateArgument(action, agent, patient, actionPOS, agentPOS, patientPOS, agentRelation, patientRelation, context);
	            	PredicateArgument pa1 = new PredicateArgument(arr[1], arr[4], arr[6], arr[2], arr[5], arr[7], "", "", arr[3]);
	            	PredicateArgument pa2= new PredicateArgument(arr[15],arr[18],arr[20],arr[16],arr[19],arr[21],"","",arr[17]);
	            	PairPredicateArgument pairpa = new PairPredicateArgument(pa1,pa2,line);
	            	arrPredPairs.add(pairpa);
	            }
	            line = br.readLine();
	        }
	        br.close();
	    }
	    catch(Exception e){
	    	System.out.println(e);
	    }
	}
	
	public ArrayList<PairPredicateArgument> getArrPredPairs() {
		return arrPredPairs;
	}

	public void setArrPredPairs(ArrayList<PairPredicateArgument> arrPredPairs) {
		this.arrPredPairs = arrPredPairs;
	}
	
	public static void main(String args[]){
		//Used only for testing this code
		String fileName = "/usr0/home/kartikgo/git/ConcreteCoref/src/main/resources/pairwiseJudgementsConsistent.txt";
		ArrPairPredicateArgument test = new ArrPairPredicateArgument(fileName);
		//Test if this is correct
		ArrayList<PairPredicateArgument> arrPredPairs = test.getArrPredPairs();
		
		for(PairPredicateArgument pairpa:arrPredPairs){
			PredicateArgument pa1 = pairpa.getPa1();
			PredicateArgument pa2 = pairpa.getPa2();
			ArrayList<Double> featureVec = new ArrayList<Double>();
			
			System.out.println(pa1.getAction());
		}
	}
}

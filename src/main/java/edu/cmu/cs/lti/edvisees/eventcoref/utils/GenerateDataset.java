package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.io.PrintWriter;
import java.util.ArrayList;

import edu.cmu.cs.lti.edvisees.eventcoref.features.*;

public class GenerateDataset {
	
	public static void main(String args[]) throws Exception{
		SqlHandle tsq1= new SqlHandle("src/main/resources/simplewikidata/bklsimplewiki_lemma_sql0.db");
		System.out.println(java.lang.Runtime.getRuntime().maxMemory());
		ArrPairPredicateArgument test = new ArrPairPredicateArgument("src/main/resources/pairwiseJudgementsConsistent.txt");
		ArrayList<PairPredicateArgument> arrPredPairs = test.getArrPredPairs();
		SennaSimilarity s = new SennaSimilarity();
		EigenSimilarity e = new EigenSimilarity();
		Lexicographicfeatures l = new Lexicographicfeatures();
		int lineNo = 0;
		PrintWriter writer = new PrintWriter("fileName1.txt", "UTF-8");
		for(PairPredicateArgument pairpa:arrPredPairs){
			System.out.println(lineNo++);
			PredicateArgument pa1 = pairpa.getPa1();
			PredicateArgument pa2 = pairpa.getPa2();
			//System.out.println("GENDATA Agent1:"+pa1.getAgent()+":Action1:"+pa1.getAction()+":Patient1:"+pa1.getPatient()+":Agent2:"+pa2.getAgent()+":Action2:"+pa2.getAction()+":Patient2:"+pa2.getPatient());
			
			String aux = pairpa.getEntireLine(); 
			ArrayList<Double> featureVec = new ArrayList<Double>();
			
			//Four Features each from Senna/Eigenwords
			featureVec.add(s.computeVal(pa1, pa2));
			featureVec.addAll(s.getfeats(pa1, pa2));
			featureVec.add(e.computeVal(pa1, pa2));
			featureVec.addAll(e.getfeats(pa1, pa2));
							
			//9 Lexicographic features
			featureVec.addAll(l.genfeat(pa1, pa2));
			
			//15(+6) SDSM features
			featureVec.addAll(SDSMfeatures.genfeat(pa1, pa2,tsq1));
			
			//System.out.println("pair done.");
			writer.print(aux);
			for(Double d:featureVec){
				writer.format("|%6.6f",d);
			}
			writer.println();
		}
		writer.close();
	}
}

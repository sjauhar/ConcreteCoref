package edu.cmu.cs.lti.edvisees.eventcoref.features;

import java.util.ArrayList;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.*;


public class SennaSimilarity implements Feature{

	public SennaSimilarity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double computeVal(PredicateArgument i, PredicateArgument j) {
		return Senna.cosineSim(i.getAction(),j.getAction());
	}
	
	public ArrayList<Double> getfeats(PredicateArgument i, PredicateArgument j){
		ArrayList<Double> senna_feats = new ArrayList<Double>();
		
		if (i.getAgent().equals("-")||j.getAgent().equals("-"))
			senna_feats.add(-1.0);
		else
			senna_feats.add(Senna.cosineSim(i.getAgent(),j.getAgent()));
		//if (i.getPatient().equals("-")||j.getPatient().equals("-"))
		//	senna_feats.add(-1.0);
		//else
		//	senna_feats.add(Senna.cosineSim(i.getPatient(),j.getPatient()));
		if (i.getAgent().equals("-")||j.getPatient().equals("-"))
			senna_feats.add(-1.0);
		else
			senna_feats.add(Math.max(Senna.cosineSim(i.getAgent(),j.getPatient()),Senna.cosineSim(i.getPatient(),j.getAgent())));

		return senna_feats;
	}
	
}

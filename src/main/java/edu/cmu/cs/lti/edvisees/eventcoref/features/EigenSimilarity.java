package edu.cmu.cs.lti.edvisees.eventcoref.features;

import java.util.ArrayList;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.*;


public class EigenSimilarity implements Feature{

	public EigenSimilarity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double computeVal(PredicateArgument i, PredicateArgument j) {
		return Eigen.cosineSim(i.getAction(),j.getAction());
	}
	

	public ArrayList<Double> getfeats(PredicateArgument i, PredicateArgument j){
		ArrayList<Double> eigen_feats = new ArrayList<Double>();
		
		if (i.getAgent().equals("-")||j.getAgent().equals("-"))
			eigen_feats.add(-1.0);
		else
			eigen_feats.add(Eigen.cosineSim(i.getAgent(),j.getAgent()));
		if (i.getPatient().equals("-")||j.getPatient().equals("-"))
			eigen_feats.add(-1.0);
		else
			eigen_feats.add(Eigen.cosineSim(i.getPatient(),j.getPatient()));
		if (i.getAgent().equals("-")||j.getPatient().equals("-"))
			eigen_feats.add(-1.0);
		else
			eigen_feats.add(Math.max(Eigen.cosineSim(i.getAgent(),j.getPatient()),Eigen.cosineSim(i.getPatient(),j.getAgent())));

		return eigen_feats;
	}
	
}

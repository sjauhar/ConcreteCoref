package edu.cmu.cs.lti.edvisees.eventcoref.features;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.*;


public class SennaSimilarity implements Feature{

	public SennaSimilarity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double computeVal(PredicateArgument i, PredicateArgument j) {
		return Senna.cosineSim(i.getAction(),j.getAction());
	}
	
}

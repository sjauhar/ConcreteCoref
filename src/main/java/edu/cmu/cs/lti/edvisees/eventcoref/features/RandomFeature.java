package edu.cmu.cs.lti.edvisees.eventcoref.features;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;


public class RandomFeature implements Feature {

  public double computeVal(PredicateArgument i, PredicateArgument j) {
    return Math.random();
  }

}

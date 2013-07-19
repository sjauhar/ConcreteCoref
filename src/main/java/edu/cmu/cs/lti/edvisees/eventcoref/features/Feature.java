package edu.cmu.cs.lti.edvisees.eventcoref.features;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;


public interface Feature {
  
  public double computeVal (PredicateArgument predicateArgument, PredicateArgument predicateArgument2);
  
}

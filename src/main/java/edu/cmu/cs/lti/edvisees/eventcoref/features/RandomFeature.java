package edu.cmu.cs.lti.edvisees.eventcoref.features;

import edu.jhu.hlt.concrete.Concrete.Situation.Justification;


public class RandomFeature implements Feature {

  public double computeVal(Justification i, Justification j) {
    return Math.random();
  }

}

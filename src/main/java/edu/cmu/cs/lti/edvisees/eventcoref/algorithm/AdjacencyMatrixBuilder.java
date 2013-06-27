package edu.cmu.cs.lti.edvisees.eventcoref.algorithm;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.edvisees.eventcoref.features.Feature;
import edu.cmu.cs.lti.edvisees.eventcoref.features.RandomFeature;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;

import edu.ucla.sspace.matrix.*;

public class AdjacencyMatrixBuilder {
	
  private List<Feature> features;
  
  public AdjacencyMatrixBuilder() {
    //super();
    this.features = new ArrayList<Feature>();
    //keep adding new instances of features to the feature list
    //as and when they are implemented
    //any feature not in this list will not be computed
    this.features.add(new RandomFeature());
  }


  public Matrix build(ArrayList<Justification> justificationSet) {
    SymmetricMatrix adjacencyMatrix = new SymmetricMatrix(justificationSet.size(), justificationSet.size());
    
    /*Loop over pairs of Justifications (event mentions)*/
    for (int i=0; i<justificationSet.size();i++){
      for (int j=0; j<=i;j++){
        
    	//Compute Feature Vector Justification pair
        List<Double> featureVector = new ArrayList<Double>();
        for (Feature f : features) {
          featureVector.add(f.computeVal(justificationSet.get(i), justificationSet.get(j)));
        }
        
        //Feed feature vector to a Classifier, and generate an adjacency matrix entry
        adjacencyMatrix.set(i, j, classify(featureVector));
      }
    }
    
    return adjacencyMatrix;
  }
  
  
  private double classify (List<Double> featureScores) {
    return Math.random();
  }
  
}

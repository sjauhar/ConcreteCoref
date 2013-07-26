package edu.cmu.cs.lti.edvisees.eventcoref.algorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import edu.cmu.cs.lti.edvisees.eventcoref.features.Feature;
import edu.cmu.cs.lti.edvisees.eventcoref.features.RandomFeature;
import edu.cmu.cs.lti.edvisees.eventcoref.features.SDSMfeatures;
import edu.cmu.cs.lti.edvisees.eventcoref.features.SennaSimilarity;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.SqlHandle;
import edu.ucla.sspace.matrix.*;

public class AdjacencyMatrixBuilder_old {
	
  //private List<Feature> features;
  
  public AdjacencyMatrixBuilder_old() {
    //super();
    //this.features = new ArrayList<Feature>();
    //keep adding new instances of features to the feature list
    //as and when they are implemented
    //any feature not in this list will not be computed
    //this.features.add(new RandomFeature());
  }


  public Matrix build(ArrayList<PredicateArgument> predicateArgumentSet, SqlHandle tsq1, Boolean fast) throws Exception {
	
    SymmetricMatrix adjacencyMatrix = new SymmetricMatrix(predicateArgumentSet.size(), predicateArgumentSet.size());
    SennaSimilarity s = new SennaSimilarity();
    
    String modelLocation;
    String emptyArffLocation;
    //boolean test= true;
    if(fast){
    	modelLocation = "src/main/resources/smallModel.model";
    	emptyArffLocation = "src/main/resources/smallHeader.arff";
    }
    else{
    	modelLocation = "src/main/resources/bigModel.model";
        emptyArffLocation = "src/main/resources/header.arff";
    }
    
    DataSource source = new DataSource(emptyArffLocation);
	Instances testInst = source.getDataSet();
	testInst.setClassIndex(testInst.numAttributes() - 1);
	
	Classifier cls = (Classifier) weka.core.SerializationHelper.read(modelLocation);
    
    /*Loop over pairs of Justifications (event mentions)*/
    for (int i=0; i<predicateArgumentSet.size();i++){
      for (int j=0; j<i;j++){
          //System.out.println("Event pairs: i="+i+" j="+j);
    	  ArrayList<Double> featureVec = new ArrayList<Double>();
    	  
    	  PredicateArgument pa1 = predicateArgumentSet.get(i);
    	  PredicateArgument pa2 = predicateArgumentSet.get(j);
          //System.out.println("Created predicate arguments: Actions: "+pa1.getAction()+" "+pa2.getAction()+" Agents:" +pa1.getAgent()+pa2.getAgent()+"Patients:"+pa1.getPatient()+" "+pa2.getPatient());
          
          //System.out.println("Creating Senna feature..");
    	  featureVec.add(s.computeVal(pa1, pa2));
    	  if(!fast){
    	  //System.out.println("Creating DB features..");
    	  featureVec.addAll(SDSMfeatures.genfeat(pa1, pa2,tsq1));
    	  //System.out.println("All features made.");
    	  }
    	  
    	//Compute Feature Vector Justification pair
        double[] featureVector = new double[featureVec.size()+1];
        for (int id =0;id<featureVec.size();id++) {
          featureVector[id] = featureVec.get(id);
        }
        featureVector[featureVec.size()] = 0;
        
        //Feed feature vector to a Classifier, and generate an adjacency matrix entry
        //System.out.println("Adding classification..");
        double clss = classify(source,cls, testInst,featureVector,modelLocation,emptyArffLocation);
        /*if(clss > 0.6){
        	Scanner scan = new Scanner(System.in);   // Creates a new object of scanner class, this will take in Raw input from the user.


            System.out.println("Classification 1.");
            scan.nextLine(); 
        }*/
        adjacencyMatrix.set(i, j, clss);
      }
      adjacencyMatrix.set(i, i, 1.0);
    }
    
    return adjacencyMatrix;
  }
  
  
  private double classify(DataSource source,Classifier cls, Instances testInst,double[] featureScores,String modelLocation,String emptyArffLocation){
	  double classification = 0;
	try {
		double instVals[] = new double[featureScores.length+1];
		System.arraycopy(featureScores, 0, instVals, 0, featureScores.length);
		instVals[featureScores.length] = 0;
		
		Instance inst = new Instance(1,instVals);
		
		inst.setDataset(testInst);
		classification = 1-cls.classifyInstance(inst);
		//System.out.println("Classification is:"+classification);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return classification;
  } 
}

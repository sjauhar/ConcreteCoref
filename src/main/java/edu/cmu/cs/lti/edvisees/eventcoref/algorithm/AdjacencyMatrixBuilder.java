package edu.cmu.cs.lti.edvisees.eventcoref.algorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import edu.cmu.cs.lti.edvisees.eventcoref.features.EigenSimilarity;
import edu.cmu.cs.lti.edvisees.eventcoref.features.Feature;
import edu.cmu.cs.lti.edvisees.eventcoref.features.Lexicographicfeatures;
import edu.cmu.cs.lti.edvisees.eventcoref.features.RandomFeature;
import edu.cmu.cs.lti.edvisees.eventcoref.features.SDSMfeatures;
import edu.cmu.cs.lti.edvisees.eventcoref.features.SennaSimilarity;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.SqlHandle;
import edu.ucla.sspace.matrix.*;

public class AdjacencyMatrixBuilder {

	//private List<Feature> features;

	public AdjacencyMatrixBuilder() {
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
		EigenSimilarity e = new EigenSimilarity();
		Lexicographicfeatures l = new Lexicographicfeatures();

		String fullLocation,filterLocation;
		String fullArffLocation,filterArffLocation;
		//boolean test= true;

		
		fullLocation = "src/main/resources/bigModel.model";
		fullArffLocation = "src/main/resources/header.arff";
		//filterLocation = "src/main/resources/prelim.model";
		//filterArffLocation = "src/main/resources/prelim.arff";
		filterLocation = "src/main/resources/firestone.model";
		filterArffLocation = "src/main/resources/firestoneheader.arff";
		
		DataSource filtersource = new DataSource(filterArffLocation);
		DataSource fullsource = new DataSource(fullArffLocation);

		Instances filtertestInst = filtersource.getDataSet();
		Instances fulltestInst = fullsource.getDataSet();

		filtertestInst.setClassIndex(filtertestInst.numAttributes() - 1);
		fulltestInst.setClassIndex(fulltestInst.numAttributes() - 1);

		Classifier filtercls = (Classifier) weka.core.SerializationHelper.read(filterLocation);
		Classifier fullcls = (Classifier) weka.core.SerializationHelper.read(fullLocation);

		/*Loop over pairs of Justifications (event mentions)*/
		for (int i=0; i<predicateArgumentSet.size();i++){
			for (int j=0; j<i;j++){
				//System.out.println("Event pairs: i="+i+" j="+j);
				ArrayList<Double> featureVec = new ArrayList<Double>();

				PredicateArgument pa1 = predicateArgumentSet.get(i);
				PredicateArgument pa2 = predicateArgumentSet.get(j);
				//System.out.println("Created predicate arguments: Actions: "+pa1.getAction()+" "+pa2.getAction()+" Agents:" +pa1.getAgent()+pa2.getAgent()+"Patients:"+pa1.getPatient()+" "+pa2.getPatient());

				//System.out.println("Creating Filter features..");
				featureVec.add(s.computeVal(pa1, pa2));
				featureVec.addAll(s.getfeats(pa1, pa2));
				//featureVec.add(e.computeVal(pa1, pa2));
				//featureVec.addAll(e.getfeats(pa1, pa2));				
				featureVec.addAll(l.genfeat(pa1, pa2));

				double clss[] = classify(filtersource,filtercls, filtertestInst,featureVec,filterLocation,filterArffLocation);
				if(fast || (clss[1] > 0.75));
				else {
					//System.out.println("Need SDSM , confidence is "+clss[1]);
					featureVec.addAll(SDSMfeatures.genfeat(pa1, pa2,tsq1));

					clss = classify(fullsource,fullcls, fulltestInst,featureVec,fullLocation,fullArffLocation);
				}
				adjacencyMatrix.set(i, j, clss[0]);
			}
			adjacencyMatrix.set(i, i, 1.0);
		}

		return adjacencyMatrix;
	}


	private double[] classify(DataSource source,Classifier cls, Instances testInst,ArrayList<Double>featureVec,String modelLocation,String emptyArffLocation){
		//System.out.println("InClassifyFeatureVecSize: "+featureVec.size());
		double[] featureScores = new double[featureVec.size()+1];
		for (int id =0;id<featureVec.size();id++) {
			featureScores[id] = featureVec.get(id);
		}
		featureScores[featureVec.size()]=0;
		//System.out.println("c0 "+featureScores.length);
		double retArray[]= new double[2];
		double confid[] = new double[2];
		try {
			Instance inst = new Instance(1,featureScores);
			//System.out.println("c1 "+inst.numAttributes());
			//System.out.println("c2 "+testInst.numAttributes());
			if(inst.numAttributes()!=testInst.numAttributes()) System.err.println("Model Arff and feature size should match.");
			inst.setDataset(testInst);

			//double classification = 1-cls.classifyInstance(inst);
			confid = cls.distributionForInstance(inst);
			double classification=0;
			if(confid[0]>0.80)
				classification=1;
			
			//System.out.println("Classification is:"+classification);
			retArray[0] = confid[0]; //classification;
			retArray[1] = Math.max(confid[0], confid[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retArray;
	} 
}

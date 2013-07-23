package edu.cmu.cs.lti.edvisees.eventcoref.algorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import edu.jhu.hlt.concrete.Concrete.Situation.Justification;

import edu.ucla.sspace.clustering.Assignment;
import edu.ucla.sspace.clustering.Assignments;
import edu.ucla.sspace.clustering.HardAssignment;
import edu.ucla.sspace.matrix.Matrix;


public class ClusterJustificationBasic {
	public static Instances createInstances(Matrix adjacencyMatrix){
		FastVector fvWekaAttributes = new FastVector(adjacencyMatrix.columns());
		for(int i =0;i<adjacencyMatrix.columns();i++){
			Attribute newAtt = new Attribute("Feature"+i);
			fvWekaAttributes.addElement(newAtt);
		}
		// Declare the class attribute along with its values
		FastVector fvClassVal = new FastVector(2);
		fvClassVal.addElement("POS");
		fvClassVal.addElement("NEG");
		//Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
		//fvWekaAttributes.addElement(ClassAttribute);

		//Create instances
		Instances inst = new Instances("Rel", fvWekaAttributes, 1000000);           
		// Set class index
		//inst.setClassIndex(adjacencyMatrix.columns());

		//Add data from Matrix
		for(int i =0;i<adjacencyMatrix.columns();i++){
			Instance instance = new Instance(1,adjacencyMatrix.getColumn(i));
			inst.add(instance);
		}

		return inst;
	}

	public static int findBestClustering(int numClusters, Map<Integer,Double> clusterScores){
		int bestPos = 0;
		double bestQuality = -99999999;
		for(int i = 1;i<= numClusters;i++){
			if(clusterScores.get(i)>bestQuality){
				bestQuality = clusterScores.get(i);
				bestPos = i;
			}
		}
		return bestPos;
	}

	public static Assignments getBasicClusteringAssignments(Matrix adjacencyMatrix){
		int[] asst = new int[adjacencyMatrix.columns()];
		ArrayList<Set<Integer>> clusters = new ArrayList<Set<Integer>>();
		for(int i = 0;i<adjacencyMatrix.columns();i++){
			HashSet<Integer> h = new HashSet<Integer>();
			h.add(i);
			clusters.add(h);
			asst[i]=i;
		}
		for(int i = 0;i<adjacencyMatrix.columns();i++){
			for(int j=0;j<i;j++){
				if(adjacencyMatrix.get(i, j) == 1){
					//Coreferent, do something
					int asst_i = asst[i];
					int asst_j = asst[j];
					if(asst_i!=asst_j){
						Set<Integer> c_i = clusters.get(asst_i);
						Set<Integer> c_j = clusters.get(asst_j);
						c_j.addAll(c_i);
						
						//Update assts for all elements in cluster i to j
						for(Integer in:c_i) asst[in] = asst_j;

						clusters.remove(asst_i);
						clusters.remove(asst_j);
						clusters.add(asst_j, c_j);

						//Update assts for all elements in all clusters after asst_i
						for(int in = asst_i;in<clusters.size();in++){
							Set<Integer> cluster_to_change = clusters.get(in);
							for(Integer in1:cluster_to_change) asst[in1]--;
						}
					}
				}
			}
		}
		Assignment ass[] = new Assignment[adjacencyMatrix.columns()];
		for(int i = 0;i<adjacencyMatrix.columns();i++){
			ass[i] = new HardAssignment(asst[i]);
		}
		Assignments assignments = new Assignments(clusters.size(), ass);
		return assignments;
	}

	public static ArrayList< ArrayList<Justification>> cluster(Matrix adjacencyMatrix, ArrayList<Justification> justificationSet) {
		ArrayList< ArrayList<Justification> > bestJustificationClustering = new ArrayList< ArrayList<Justification> >();
		//System.out.println("Justification size is:"+justificationSet.size());
		if(justificationSet.size()>0){
			//Run Basic
			try {

				Assignments assignments = getBasicClusteringAssignments(adjacencyMatrix);
				//Evaluate Quality				  
				int bestK = assignments.numClusters();
				System.out.println("\tSituationClusters:  "+bestK+"\n");
				//System.out.println("In best clustering, number of clusters: " + bestK + "\nBest Clustering");
				Assignments bestCluster = assignments;

				//System.out.println("In best clustering, number of clusters: " +bestCluster.numClusters());
				int pos=0;
				/*
				for (Set<Integer> s : bestCluster.clusters()) {
					System.out.println(pos++);
					System.out.println(s.toString());
				}
				*/
				for (Set<Integer> currentCluster : bestCluster.clusters()) {
					ArrayList<Justification> currentJustificationCluster = new ArrayList<Justification>();
					for (Integer i : currentCluster) {
						currentJustificationCluster.add(justificationSet.get(i));
					}
					bestJustificationClustering.add(currentJustificationCluster);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bestJustificationClustering;

		}
		else return new ArrayList<ArrayList<Justification>>();
	}


	private static double clusterScore (Matrix adjacencyMatrix, Assignments assignments) {
		double cumulator = 0.0;
		//loop through each cluster
		for (Set<Integer> currentCluster : assignments.clusters()) {

			//get a set consisting of the union of all other clusters
			Set<Integer> allOtherClusters = getAllOtherClusters(assignments, currentCluster);

			//get lists of intra-cluster and inter-cluster pairs
			ArrayList< ArrayList<Integer>> intraClusterPairs = getIntraClusterPairs(currentCluster);
			ArrayList< ArrayList<Integer>> interClusterPairs = getInterClusterPairs(currentCluster,allOtherClusters);

			//get intra and inter-cluster scores
			double intraClusterScore = getClusterScore(adjacencyMatrix, intraClusterPairs);
			double interClusterScore = getClusterScore(adjacencyMatrix, interClusterPairs);

			//add difference to accumulator
			cumulator += (intraClusterScore - interClusterScore);
		}
		cumulator /= (double) assignments.clusters().size();

		return cumulator;
	}



	private static Assignments findBestClustering (Map<Assignments,Double> clusterScores) {
		Entry<Assignments,Double> maxEntry = null;
		for (Entry<Assignments,Double> entry : clusterScores.entrySet()) {
			if (maxEntry == null || (entry.getValue() > maxEntry.getValue())) {
				maxEntry = entry;
			}
		}

		return maxEntry.getKey();
	}


	private static Set<Integer> getAllOtherClusters (Assignments assignments, Set<Integer> currentCluster) {
		Set<Integer> allOtherClusters = new HashSet<Integer>();
		for (Set<Integer> s : assignments.clusters()) {
			for (Integer i : s) {
				if (currentCluster.contains(i) == false) {
					allOtherClusters.add(i);
				}
			}
		}
		return allOtherClusters;
	}


	private static ArrayList< ArrayList<Integer> > getIntraClusterPairs (Set<Integer> currentCluster) {
		ArrayList< ArrayList<Integer> > intraClusterPairs = new ArrayList< ArrayList<Integer> >();
		for (Integer i : currentCluster) {
			for (Integer j : currentCluster) {
				if (i.equals(j) == false) {
					ArrayList<Integer> tmp = new ArrayList<Integer>();
					tmp.add(i); tmp.add(j);
					intraClusterPairs.add(tmp);
				}
			}
		}
		return intraClusterPairs;
	}


	private static ArrayList< ArrayList<Integer> > getInterClusterPairs (Set<Integer> currentCluster, Set<Integer> allOtherClusters) {
		ArrayList< ArrayList<Integer> > interClusterPairs = new ArrayList< ArrayList<Integer> >();
		for (Integer i : currentCluster) {
			for (Integer j : allOtherClusters) {
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				tmp.add(i); tmp.add(j);
				interClusterPairs.add(tmp);
			}
		}
		return interClusterPairs;
	}

	private static double getClusterScore (Matrix adjacencyMatrix, ArrayList< ArrayList<Integer> > clusterPairs) {
		double clusterScore = 0.0;
		if (clusterPairs.size() == 0) {
			return clusterScore;
		} else {

			for (ArrayList<Integer> pair : clusterPairs) {
				clusterScore += adjacencyMatrix.get(pair.get(0), pair.get(1));
			}
			clusterScore /= (double) clusterPairs.size();

			return clusterScore;
		}
	}

}



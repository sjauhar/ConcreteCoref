package edu.cmu.cs.lti.edvisees.eventcoref.algorithm;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;
import edu.ucla.sspace.clustering.Assignment;
import edu.ucla.sspace.clustering.Assignments;
import edu.ucla.sspace.clustering.HardAssignment;
import edu.ucla.sspace.matrix.Matrix;


public class ClusterJustificationBasic {
	
	static boolean verbose = false;
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
				if(adjacencyMatrix.get(i, j) >= 0.8){
					//Coreferent, do something
					int asst_i = asst[i];
					int asst_j = asst[j];
					//System.out.println("i="+i+" j="+j+" Asst i="+asst_i+" Asst_j="+asst_j);
					if(asst_i!=asst_j){
						int max=Math.max(asst_i, asst_j);
						Set<Integer> c_i = clusters.get(asst_i);
						Set<Integer> c_j = clusters.get(asst_j);
						if(asst_i>asst_j){
							c_j.addAll(c_i);
							for(Integer in:c_i) asst[in] = asst_j;
							clusters.remove(asst_i);
							clusters.remove(asst_j);
							clusters.add(asst_j, c_j);
						}
						else{
							c_i.addAll(c_j);
							for(Integer in:c_j) asst[in] = asst_i;
							clusters.remove(asst_j);
							clusters.remove(asst_i);
							clusters.add(asst_i,c_i);
						}
						//System.out.println("cluster size"+clusters.size());

						//Update assts for all elements in all clusters after max

						for(int in = max;in<clusters.size();in++){
							Set<Integer> cluster_to_change = clusters.get(in);
							for(Integer in1:cluster_to_change){
								//System.out.println("cluster no="+in+" point="+in1);
								asst[in1]--;
							}
						}
						//System.out.println("Merged "+i+" "+j);
					}

					/*
					System.out.println("Printing clusters:");
					for(Set<Integer> cl:clusters){
						System.out.print("[");
						for(Integer inte:cl){
							System.out.print(inte+",");
						}
						System.out.println("]");
					}

					System.out.println("Printing assignments:");
					System.out.print("[");

					for(int id=0;id<asst.length;id++){
						System.out.print(asst[id]+",");
					}
					System.out.println("]");
					*/
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

	public static ArrayList< ArrayList<Justification>> cluster(ArrayList<PredicateArgument> predicateArgumentSet, Matrix adjacencyMatrix, ArrayList<Justification> justificationSet){
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

				System.out.println("In best clustering, number of clusters: " +bestCluster.numClusters());
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
				
				if(verbose){
					System.out.println("Printing clusters:");
					for(Set<Integer> cl:bestCluster.clusters()){
						System.out.print("[");
						for(Integer inte:cl){
							System.out.print("("+predicateArgumentSet.get(inte).getAgent()+","+predicateArgumentSet.get(inte).getAction()+","+predicateArgumentSet.get(inte).getPatient()+"),");
						}
						System.out.println("]");
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bestJustificationClustering;

		}
		else{
			System.out.println("\tSituationClusters:  0\n");
			return new ArrayList<ArrayList<Justification>>();
		}
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



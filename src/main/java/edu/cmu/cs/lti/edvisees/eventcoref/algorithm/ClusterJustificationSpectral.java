package edu.cmu.cs.lti.edvisees.eventcoref.algorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.jhu.hlt.concrete.Concrete.Situation.Justification;

import edu.ucla.sspace.clustering.Assignments;
import edu.ucla.sspace.clustering.CKVWSpectralClustering03;
import edu.ucla.sspace.matrix.Matrix;


public class ClusterJustificationSpectral {
  
  public static ArrayList< ArrayList<Justification> > cluster(Matrix adjacencyMatrix, ArrayList<Justification> justificationSet, Boolean useKMeans) {
    
    CKVWSpectralClustering03 ckvw = new CKVWSpectralClustering03();
    Properties props = new Properties();
    if (useKMeans) props.setProperty("USE_KMEANS", "edu.ucla.sspace.clustering.CKVWSpectralClustering03.useKMeans");
    
    Map<Assignments,Double> clusterScores = new HashMap<Assignments,Double>();
    for (int numberOfClusters = 1; numberOfClusters <= justificationSet.size(); numberOfClusters++) {
      for (int randomRestarts = 1; randomRestarts <= 3; randomRestarts++) {
        Assignments assignments = ckvw.cluster(adjacencyMatrix, numberOfClusters, props);
        clusterScores.put(assignments, clusterScore(adjacencyMatrix,assignments));
      }
    }
    Assignments bestCluster = findBestClustering(clusterScores);
    System.out.println("In best clustering, number of clusters: " + bestCluster.numClusters() + "\nBest Clustering");
    for (Set<Integer> s : bestCluster.clusters()) {
  	  System.out.println(s.toString());
    }
    
    ArrayList< ArrayList<Justification> > bestJustificationClustering = new ArrayList< ArrayList<Justification> >();
    for (Set<Integer> currentCluster : bestCluster.clusters()) {
      ArrayList<Justification> currentJustificationCluster = new ArrayList<Justification>();
      for (Integer i : currentCluster) {
        currentJustificationCluster.add(justificationSet.get(i));
      }
      bestJustificationClustering.add(currentJustificationCluster);
    }
    
    return bestJustificationClustering;
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


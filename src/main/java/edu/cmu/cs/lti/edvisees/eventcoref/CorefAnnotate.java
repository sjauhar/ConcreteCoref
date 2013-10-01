package edu.cmu.cs.lti.edvisees.eventcoref;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.AdjacencyMatrixBuilder;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustification;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustificationBasic;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustificationKMeans;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustificationSpectral;
import edu.cmu.cs.lti.edvisees.eventcoref.features.EigenSimilarity;
import edu.cmu.cs.lti.edvisees.eventcoref.features.Lexicographicfeatures;
import edu.cmu.cs.lti.edvisees.eventcoref.features.LexicographicfeaturesNew;
import edu.cmu.cs.lti.edvisees.eventcoref.features.SennaSimilarity;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.ConcreteReader;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.FanseParse;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.Senna;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.SqlHandle;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.WrapperUtils;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.SituationMention;
import edu.jhu.hlt.concrete.Concrete.UUID;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;
import edu.jhu.hlt.concrete.Concrete.SituationMention.Argument;
import edu.jhu.hlt.concrete.Concrete.Situation.Argument.Role;
import edu.jhu.hlt.concrete.Concrete.Situation.Type;
import edu.jhu.hlt.concrete.Concrete.*;
import edu.jhu.hlt.concrete.io.ProtocolBufferReader;
import edu.jhu.hlt.concrete.util.IdUtil;
import edu.ucla.sspace.matrix.*;

public class CorefAnnotate {

  public static ArrayList<Communication> execute(ArrayList<Communication> cList,Boolean fast,String IP) throws Exception {
    
	SqlHandle tsq1 = null;
	if (!fast) tsq1= new SqlHandle("src/main/resources/simplewikidata/bklsimplewiki_lemma_sql0.db");
	ArrayList<Communication> annotatedCList = new ArrayList<Communication>();
	
    int count=0;    
    for (Communication c : cList) {

      c = FanseParse.addToCommunication(c,IP);
      
      System.out.print("Communication: " + count++);
      //System.out.println("c.getText() " + c.getText());
      
      ////System.out.println("Number of section segmentations: "+ c.getSectionSegmentationCount());
      ////System.out.println("Number of sections in segmentation0: "+ c.getSectionSegmentation(0).getSectionCount());
      //System.out.println("c.getSituationSetCount() " + c.getSituationSetCount());
      //System.out.println("c.getSituationMentionSetCount() " + c.getSituationMentionSetCount());
      System.out.println("\tSituationMentions: "+c.getSituationMentionSet(0).getMentionCount());   
      //System.out.print("\tSituations: "+c.getSituationSet(0).getSituationCount());
      
      //Wrap the mentions into justifications
      System.out.println("Wrapping mentions into justifications");
      ArrayList<Justification> justificationSet = WrapperUtils.justificationWrapper(c.getSituationMentionSet(0));
      System.out.println("Creating PredicateArguments...");
      ArrayList<PredicateArgument> predicateArgumentSet = WrapperUtils.predicateArgumentWrapper(c.getSituationMentionSet(0), c);
      
      //Coref engine goes here
      //Produces an adjacency matrix (by pair-wise predictions over whole justification set)
      System.out.println("Producing adjacency matrix...");
      AdjacencyMatrixBuilder amb = new AdjacencyMatrixBuilder();
      Matrix adjacencyMatrix = amb.build(predicateArgumentSet,tsq1,fast);
      
      System.out.println("Clustering...");
      ArrayList< ArrayList<Justification>> justificationClusterList = ClusterJustificationBasic.cluster(predicateArgumentSet, adjacencyMatrix, justificationSet);
      
      //Loop through each justification cluster and create a situation out of it
      ArrayList<Situation> situationSet = new ArrayList<Situation>();
      for (ArrayList<Justification> justificationCluster : justificationClusterList) {
        situationSet.add(WrapperUtils.situationWrapper(justificationCluster));
      }
      
      //Finally wrap the newly created situation set into the communication
      c = WrapperUtils.situationSetWrapper(c, situationSet);
      
      {

    	  HashMap<UUID,SituationMention> hmap = new HashMap<UUID,SituationMention>();
    	  for(SituationMention sm:c.getSituationMentionSet(0).getMentionList()){
    		  hmap.put(sm.getUuid(), sm);
    	  }
    	
    	  /*Write Predicate-arguments by event-ids in text file*/
    	  /*
    	  {
    		  PrintWriter writer1 = new PrintWriter(new BufferedWriter(new FileWriter("/Users/shashans/Desktop/Firestone/Data.txt", true)));
    		  
    		  SennaSimilarity s = new SennaSimilarity();
    		  EigenSimilarity e = new EigenSimilarity();
    		  LexicographicfeaturesNew l = new LexicographicfeaturesNew();
    			
    		  
    		  for (int ki=0; ki<predicateArgumentSet.size();ki++){
    				for (int kj=0; kj<ki;kj++){
    					
    					SituationMention e1 = hmap.get(justificationSet.get(ki).getMention());
    					PredicateArgument pa1 = predicateArgumentSet.get(ki);
    					
    					SituationMention e2 = hmap.get(justificationSet.get(kj).getMention());
    					PredicateArgument pa2 = predicateArgumentSet.get(kj);
    					
    					//System.out.println("GENDATA Agent1:"+pa1.getAgent()+":Action1:"+pa1.getAction()+":Patient1:"+pa1.getPatient()+":Agent2:"+pa2.getAgent()+":Action2:"+pa2.getAction()+":Patient2:"+pa2.getPatient());
    					
    					ArrayList<Double> featureVec = new ArrayList<Double>();
    					//Four Features each from Senna/Eigenwords
    					featureVec.add(s.computeVal(pa1, pa2));
    					featureVec.addAll(s.getfeats(pa1, pa2));
    					featureVec.add(e.computeVal(pa1, pa2));
    					featureVec.addAll(e.getfeats(pa1, pa2));
    					//Six Lexicographic features
    					featureVec.addAll(l.genfeat(pa1, pa2));
    					
    					String coref = "NEG";
    					if(e1.getSituationKindLemma().equals(e2.getSituationKindLemma()))
    						coref = "POS";
    					
    					writer1.print(e1.getSituationKindLemma()+","+e2.getSituationKindLemma()+","+pa1.getAction()+","+pa1.getAgent()+","+pa1.getPatient()+","+pa2.getAction()+","+pa2.getAgent()+","+pa2.getPatient()+",");
    					for(int kk=0; kk<featureVec.size(); kk++){
    						writer1.print(featureVec.get(kk)+",");
    					}
    					writer1.print(coref+",");
    					writer1.printf("%6.4f\n",adjacencyMatrix.get(ki,kj));   					
    				}
    			}
    		  writer1.close();
    	  }*/
    	  
    	  /*Add second situationSet with pairwise annotations, and write confidence scores to text file*/
    	  /*
    	  {
    		  PrintWriter writer = new PrintWriter("/Users/shashans/Desktop/Firestone/FirestoneCombineCoref/confidenceScoresNew.txt", "UTF-8");
    		  ArrayList<Situation> situationSet1 = new ArrayList<Situation>();
    		  for(int i=0; i<adjacencyMatrix.rows(); i++){
    			  for(int j=0; j<adjacencyMatrix.columns(); j++){
    				  float conf = (float) adjacencyMatrix.get(i, j);
    				  Situation s = Situation.newBuilder().setUuid(IdUtil.generateUUID()).addJustification(justificationSet.get(i)).addJustification(justificationSet.get(j)).setConfidence(conf).build();
    				  situationSet1.add(s);
    				  writer.print(hmap.get(justificationSet.get(i).getMention()).getSituationKindLemma()+" ");
    				  writer.print(hmap.get(justificationSet.get(j).getMention()).getSituationKindLemma()+" ");
    				  writer.printf("%6.4f", adjacencyMatrix.get(i, j));
    				  writer.println();
    			  } 
    		  }  
    		  writer.close();
    		  c = WrapperUtils.situationSetWrapper(c, situationSet1);
    	  }
    	  */
      }
      
      
      //Add to annotatedcList
      annotatedCList.add(c);
      //*/
    }     
    return annotatedCList;
  }
  
  private static String getEventText(Communication c, SituationMention sm){
	  for (Section section : c.getSectionSegmentation(0).getSectionList() ){
		  for ( Sentence sentence : section.getSentenceSegmentation(0).getSentenceList() ){
			  for ( Tokenization tokenizn : sentence.getTokenizationList()){
				  if (tokenizn.getUuid().equals(sm.getTokens().getTokenizationId() )){
					  	return tokenizn.getToken(sm.getTokens().getAnchorTokenIndex()).getText();
				  }
			  }
		  }
	  }
  	  return "";
  }
  
}

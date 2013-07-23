package edu.cmu.cs.lti.edvisees.eventcoref;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.AdjacencyMatrixBuilder;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustification;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustificationBasic;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustificationKMeans;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.ConcreteReader;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.FanseParse;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.PredicateArgument;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.Senna;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.SqlHandle;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.WrapperUtils;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.DependencyParse.Dependency;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;
import edu.jhu.hlt.concrete.Concrete.SituationMention.Argument;
import edu.jhu.hlt.concrete.Concrete.Situation.Argument.Role;
import edu.jhu.hlt.concrete.Concrete.Situation.Type;
import edu.jhu.hlt.concrete.Concrete.*;
import edu.jhu.hlt.concrete.io.ProtocolBufferReader;
import edu.jhu.hlt.concrete.util.IdUtil;
import edu.ucla.sspace.matrix.*;

public class Test {

  public static void execute(String fileName,Boolean fast) throws Exception {
    
	//System.out.println(Senna.getVector("cat"));
	SqlHandle tsq1= new SqlHandle("src/main/resources/simplewikidata/bklsimplewiki_lemma_sql0.db");
    System.out.println("Reading concrete object from file...");
    
    ProtocolBufferReader<Communication> pbr = new ProtocolBufferReader<Communication>(fileName, Communication.class);
    ArrayList<Communication> cList = new ArrayList<Communication>();
    while(pbr.hasNext()){
    	cList.add((Communication) pbr.next());
    }
    pbr.close();
    //System.out.println("Size of cList is "+cList.size());
       
    int count=0;    
    for (Communication c : cList) {
      //Add fanseparse annotation layer
      c = FanseParse.addToCommunication(c);
      
      System.out.print("Communication: " + count++);
      //System.out.println("c.getText() " + c.getText());
      
      ////System.out.println("Number of section segmentations: "+ c.getSectionSegmentationCount());
      ////System.out.println("Number of sections in segmentation0: "+ c.getSectionSegmentation(0).getSectionCount());
      //System.out.println("c.getSituationSetCount() " + c.getSituationSetCount());
      //System.out.println("c.getSituationMentionSetCount() " + c.getSituationMentionSetCount());
      System.out.print("\t"+c.getSituationMentionSet(0).getMentionCount());   
      System.out.print("\t"+c.getSituationSet(0).getSituationCount());

      /*
      for(int i=0;i<c.getSituationMentionSet(0).getMentionCount();i++){
    	  System.out.println("Anchor token for situationMention "+ i + " is: " +  getEventText(c,c.getSituationMentionSet(0).getMention(i)));
    	  //c.getSituationMentionSet(0).getMention(i).
    	  outerloop:
    	  for (Section section : c.getSectionSegmentation(0).getSectionList() ){
    		  System.out.println("Number of sentences: " + section.getSentenceSegmentation(0).getSentenceCount());
    		  for ( Sentence sentence : section.getSentenceSegmentation(0).getSentenceList() ){
    			  for ( Tokenization tokenizn : sentence.getTokenizationList()){
    				  if (tokenizn.getUuid().equals(c.getSituationMentionSet(0).getMention(i).getTokens().getTokenizationId() )){
    					  //System.out.println("Situation Mention "+i);
    					  ////System.out.println("Match "+tokenizn.getUuid().toString() + " with "+ c.getSituationMentionSet(0).getMention(0).getTokens().getTokenizationId().toString());			   
    					  ////System.out.println("Span "+c.getSituationMentionSet(0).getMention(i).getTokens().getTokenIndexList().get(0) + " " + c.getSituationMentionSet(0).getMention(i).getTokens().getTokenIndexList().get(c.getSituationMentionSet(0).getMention(i).getTokens().getTokenIndexCount()-1));
    					  ////System.out.println("Token Count in tokenizn " + tokenizn.getTokenCount());
    					  ////System.out.println("Token Count in c(i) "+ c.getSituationMentionSet(0).getMention(i).getTokens().getTokenIndexList());
    					  System.out.println("Anchor token for situationMention "+ i + " is: " + tokenizn.getToken(c.getSituationMentionSet(0).getMention(i).getTokens().getAnchorTokenIndex()).getText());
    					  break outerloop;
    				  }
    			  }
    		  }
    	  }
      }
      */
      
      //Wrap the mentions into justifications
      ArrayList<Justification> justificationSet = WrapperUtils.justificationWrapper(c.getSituationMentionSet(0));
      ArrayList<PredicateArgument> predicateArgumentSet = WrapperUtils.predicateArgumentWrapper(c.getSituationMentionSet(0), c);
      
      //Coref engine goes here
      //Produces an adjacency matrix (by pair-wise predictions over whole justification set)
      //System.out.println("Calling ambbuilder");
      AdjacencyMatrixBuilder amb = new AdjacencyMatrixBuilder();
      //System.out.println("Calling ambbuilder.build");
      Matrix adjacencyMatrix = amb.build(predicateArgumentSet,tsq1,fast);
      
      ///*
      //Chain builder goes here
      //Produces a list of list of justifications
      //System.out.println("Clustering..");
      //ArrayList< ArrayList<Justification>> justificationClusterList = ClusterJustificationSpectral.cluster(adjacencyMatrix, justificationSet, true);
      ArrayList< ArrayList<Justification>> justificationClusterList = ClusterJustificationBasic.cluster(adjacencyMatrix, justificationSet);
      //ArrayList< ArrayList<Justification>> justificationClusterList = ClusterJustificationEM.cluster(adjacencyMatrix, justificationSet);

      //ArrayList< ArrayList<Justification> > justificationClusterList = ClusterJustification.cluster(adjacencyMatrix, justificationSet, true);
      //System.out.println("\"Clustered");
      
      //Loop through each justification cluster and create a situation out of it
      //System.out.println("Wrappin.");
      ArrayList<Situation> situationSet = new ArrayList<Situation>();
      for (ArrayList<Justification> justificationCluster : justificationClusterList) {
        situationSet.add(WrapperUtils.situationWrapper(justificationCluster));
      }
      
      //Finally wrap the newly created situation set into the communication
      c = WrapperUtils.situationSetWrapper(c, situationSet);
      //*/
    }     

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

package edu.cmu.cs.lti.edvisees.eventcoref;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.AdjacencyMatrixBuilder;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustification;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.ConcreteReader;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.FanseParse;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.Senna;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.WrapperUtils;

import edu.jhu.hlt.concrete.Concrete.DependencyParse.Dependency;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;
import edu.jhu.hlt.concrete.Concrete.SituationMention.Argument;
import edu.jhu.hlt.concrete.Concrete.Situation.Argument.Role;
import edu.jhu.hlt.concrete.Concrete.Situation.Type;
import edu.jhu.hlt.concrete.Concrete.*;
import edu.jhu.hlt.concrete.util.IdUtil;

import edu.ucla.sspace.matrix.*;

public class Test {

  public static void main(String[] args) throws Exception {
    
	System.out.println(Senna.getVector("cat"));
	System.out.println(Senna.getVector("oogagogadf"));
	System.out.println(Senna.getVector("UNKNOWN"));
	Thread.sleep(1000);
	
    File f = new File(args[0]);
    System.out.println("Reading concrete object from file...");
    ArrayList<Communication> cList = ConcreteReader.getCommunicationsFrom(f);
    System.out.println("Finished reading concrete object from file");
    
    for (Communication c : cList) {
      //Add fanseparse annotation layer
      c = FanseParse.addToCommunication(c);
      //Generate a dummy set of mentions
      c = generateMentionSet(c);
      //Wrap the mentions into justifications
      ArrayList<Justification> justificationSet = WrapperUtils.justificationWrapper(c.getSituationMentionSet(0));
      
      //Coref engine goes here
      //Produces an adjacency matrix (by pair-wise predictions over whole justification set)
      AdjacencyMatrixBuilder amb = new AdjacencyMatrixBuilder();
      Matrix adjacencyMatrix = amb.build(justificationSet);
      
      //Chain builder goes here
      //Produces a list of list of justifications
      ArrayList< ArrayList<Justification> > justificationClusterList = ClusterJustification.cluster(adjacencyMatrix, justificationSet, true);
      
      //Loop through each justification cluster and create a situation out of it
      ArrayList<Situation> situationSet = new ArrayList<Situation>();
      for (ArrayList<Justification> justificationCluster : justificationClusterList) {
        situationSet.add(WrapperUtils.situationWrapper(justificationCluster));
      }
      
      //Finally wrap the newly created situation set into the communication
      c = WrapperUtils.situationSetWrapper(c, situationSet);
    }
    
  }
  
  
  //code to generate a set of dummy mentions based on root of parse trees
  public static Communication generateMentionSet(Communication c) {
    
    List<Sentence> sentList = c.getSectionSegmentation(0).getSection(0).getSentenceSegmentation(0).getSentenceList();
    List<SituationMention> situationMentionList = new ArrayList<SituationMention>();
    for (Sentence s : sentList) {
      DependencyParse d = s.getDependencyParse(3);
      TokenRef dRoot = getRoot(d);
      List<EntityMention> dRootDependencies = new ArrayList<EntityMention>();
      for (Dependency dp : d.getDependencyList()) {
        if (dp.getGov().getTokenId() == dRoot.getTokenId()) {
          dRootDependencies.add(EntityMention.newBuilder().setUuid(IdUtil.generateUUID()).build());
        }
      }
      
      List<Argument> argumentList = new ArrayList<Argument>();
      for (EntityMention e : dRootDependencies) {
        argumentList.add(Argument.newBuilder().setRole(Role.AGENT_ROLE).setValue(e.getUuid()).build());
      }
      
      situationMentionList.add(SituationMention.newBuilder().addAllArgument(argumentList).setUuid(IdUtil.generateUUID()).setSituationType(Type.EVENT).build());
    }
    
    return Communication.newBuilder(c).addSituationMentionSet(SituationMentionSet.newBuilder().setUuid(IdUtil.generateUUID()).addAllMention(situationMentionList).build()).build();
  }
  
  //get the root of a dependency parse tree
  public static TokenRef getRoot(DependencyParse d) {
    TokenRef root = null;
    Boolean flag = true;
    for (Dependency dp : d.getDependencyList()) {

      if (flag) {
        root = dp.getGov();
        flag = false;
      } else if (dp.getDep().getTokenId() == root.getTokenId()) {
        root = dp.getGov();
      }    
    }
    return root;
  }
  
}

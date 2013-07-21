package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.jhu.hlt.concrete.Concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.DependencyParse;
import edu.jhu.hlt.concrete.Concrete.DependencyParse.Dependency;
import edu.jhu.hlt.concrete.Concrete.Sentence;
import edu.jhu.hlt.concrete.Concrete.Situation;
import edu.jhu.hlt.concrete.Concrete.SituationSet;
import edu.jhu.hlt.concrete.Concrete.Tokenization;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification.Type;
import edu.jhu.hlt.concrete.Concrete.SituationMention;
import edu.jhu.hlt.concrete.Concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.util.IdUtil;


public class WrapperUtils {
  
  public static ArrayList<Justification> justificationWrapper (SituationMentionSet sms) {
    ArrayList<Justification> justificationSet = new ArrayList<Justification>();
    for (SituationMention sm : sms.getMentionList()) {
      justificationSet.add(Justification.newBuilder().setJustificationType(Type.DIRECT_MENTION).
                                                      setMention(sm.getUuid()).
                                                      addTokens(sm.getTokens())
                                                      .build());
    }
    return justificationSet;
  }
  
  
  public static Situation situationWrapper (ArrayList<Justification> justificationCorefSet) {
    return Situation.newBuilder().setUuid(IdUtil.generateUUID()).
            setSituationType(edu.jhu.hlt.concrete.Concrete.Situation.Type.EVENT).
            addAllJustification(justificationCorefSet).
            build();
  }
  
  
  public static Communication situationSetWrapper (Communication commPrototype, ArrayList<Situation> situationSet) {
    return Communication.newBuilder(commPrototype).
                         addSituationSet(SituationSet.newBuilder().setUuid(IdUtil.generateUUID()).
                                                      setMetadata(AnnotationMetadata.newBuilder().setTool("edu.cmu.cs.lti.edvisees.corefengine")).
                                                      addAllSituation(situationSet).
                                                      build()).
                         build();
  }
  
  
  public static ArrayList<PredicateArgument> predicateArgumentWrapper(SituationMentionSet sms, Communication c) {
    ArrayList<PredicateArgument> predicateArgumentSet = new ArrayList<PredicateArgument>();
    for (SituationMention sm : sms.getMentionList()) {
      predicateArgumentSet.add(getPredicateArgumentFromSituationMention(c,sm));
    }
    return predicateArgumentSet;
  }
  
  
  private static PredicateArgument getPredicateArgumentFromSituationMention(Communication c, SituationMention sm){
    
    List<String> agentRelations = Arrays.asList("nsubj", "dobj", "pobj", "amod", "prep", "partmod");
    List<String> patientRelations = Arrays.asList("dobj", "nsubj", "ccomp", "prep", "xcomp", "partmod", "pobj");
    
    for (Sentence sentence : c.getSectionSegmentation(0).getSection(0).getSentenceSegmentation(0).getSentenceList() ){
      Tokenization tokenizn = sentence.getTokenization(0);
      if (tokenizn.getUuid().equals(sm.getTokens().getTokenizationId() )){
        int actionIndex = sm.getTokens().getAnchorTokenIndex();
        
        String action = tokenizn.getToken(actionIndex).getText();
        String actionPOS = tokenizn.getPosTags(0).getTaggedToken(actionIndex).getTag();
        String context = c.getText().substring(sentence.getTextSpan().getStart(), sentence.getTextSpan().getEnd());
        
        DependencyParse fanseparse = tokenizn.getDependencyParse(3);
        HashMap<String, ArrayList<Integer> > candidateRoles = new HashMap<String, ArrayList<Integer> >();
        for (Dependency d : fanseparse.getDependencyList()) {
          if (d.getGov() == actionIndex && patientRelations.contains(d.getEdgeType())) {
            ArrayList<Integer> clone = new ArrayList<Integer>();
            if (candidateRoles.containsKey(d.getEdgeType())) {
              clone.addAll(candidateRoles.get(d.getEdgeType()));
            }
            clone.add(d.getDep());
            candidateRoles.put(d.getEdgeType(), clone);
          }
        }
        
        String agent = "";
        String agentPOS = "";
        String agentRelation = "";
        for (String relation : agentRelations) {
          if (candidateRoles.containsKey(relation)) {
            ArrayList<Integer> clone = candidateRoles.get(relation);
            agent = tokenizn.getToken(clone.get(0)).getText();
            agentPOS = tokenizn.getPosTags(0).getTaggedToken(clone.get(0)).getTag();
            agentRelation = relation;
            clone.remove(0);
            if (clone.isEmpty()) {
              candidateRoles.remove(relation);
            } else {
              candidateRoles.put(relation, clone);
            }
            break;
          }
        }
        
        String patient = "";
        String patientPOS = "";
        String patientRelation = "";
        for (String relation : patientRelations) {
          if (candidateRoles.containsKey(relation)) {
            ArrayList<Integer> clone = candidateRoles.get(relation);
            patient = tokenizn.getToken(clone.get(0)).getText();
            patientPOS = tokenizn.getPosTags(0).getTaggedToken(clone.get(0)).getTag();
            patientRelation = relation;
            clone.remove(0);
            candidateRoles.put(relation, clone);
            break;
          }
        }
        
        return new PredicateArgument(action,agent,patient,actionPOS,agentPOS,patientPOS,agentRelation,patientRelation,context);
        
      }
    }
    return null;
  }
  
}

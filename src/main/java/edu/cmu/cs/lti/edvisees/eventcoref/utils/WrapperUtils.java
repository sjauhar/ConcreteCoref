package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.util.ArrayList;

import edu.jhu.hlt.concrete.Concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.Situation;
import edu.jhu.hlt.concrete.Concrete.SituationSet;
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
                                                      addAllTokens(sm.getTokensList())
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
  
}

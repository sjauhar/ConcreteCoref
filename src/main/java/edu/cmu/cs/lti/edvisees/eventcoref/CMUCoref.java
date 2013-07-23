package edu.cmu.cs.lti.edvisees.eventcoref;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.AdjacencyMatrixBuilder;
import edu.cmu.cs.lti.edvisees.eventcoref.algorithm.ClusterJustification;
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

public class CMUCoref {

  public static void main(String[] args) throws Exception {
    Boolean test=true;
    String inputFile = "src/main/resources/eecb-docs-annotations-concrete.pb";
    
    System.out.println("Reading concrete object from file...");
    ProtocolBufferReader<Communication> pbr = new ProtocolBufferReader<Communication>(inputFile, Communication.class);
    ArrayList<Communication> cList = new ArrayList<Communication>();
    while(pbr.hasNext()){
    	cList.add((Communication) pbr.next());
    }
    pbr.close();
    
    //CorefAnnotate t = new CorefAnnotate();
    ArrayList<Communication> cListFinal = CorefAnnotate.execute(cList,test);
    
  }
  
}

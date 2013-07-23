package edu.cmu.cs.lti.edvisees.eventcoref;

import java.util.ArrayList;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.io.ProtocolBufferReader;

public class CMUCoref {

  public static void main(String[] args) throws Exception {
    Boolean fast=true;//The system runs in fast mode if set to true. To be used for testing!
    
    String inputFile = args[0];//This is the location of the data pb file
    //inputFile="src/main/resources/eecb-docs-annotations-concrete.pb";
    
    String parseServerIP = args[1];//This is the IP of the Fanseparser server
    
    System.out.println("Reading concrete object from file...");
    ProtocolBufferReader<Communication> pbr = new ProtocolBufferReader<Communication>(inputFile, Communication.class);
    ArrayList<Communication> cList = new ArrayList<Communication>();
    while(pbr.hasNext()){
    	cList.add((Communication) pbr.next());
    }
    pbr.close();
    
    //Fetch the annotated communication list
    ArrayList<Communication> cListFinal = CorefAnnotate.execute(cList,fast,parseServerIP);
    
  }
  
}

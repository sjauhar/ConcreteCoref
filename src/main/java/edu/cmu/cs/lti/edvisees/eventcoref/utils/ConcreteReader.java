package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import edu.jhu.hlt.concrete.Concrete.*;


public class ConcreteReader {
  
  public static ArrayList<Communication> getCommunicationsFrom (File f) throws IOException {
    //System.out.println("[ConcreteWrapper getCommunicationsFrom] reading from " + f.getPath());
    FileInputStream is = new FileInputStream(f);
    //@SuppressWarnings("unused")
    //KnowledgeGraph kg = KnowledgeGraph.parseDelimitedFrom(is);
    ArrayList<Communication> docs = new ArrayList<Communication>();
    while (is.available() > 0) {
      Communication c = Communication.parseDelimitedFrom(is);
      if (c != null) {
        if(c.getGuid() == null) throw new RuntimeException("1");
        if(c.getGuid().getCorpusName() == null) throw new RuntimeException("2");
        if(c.getGuid().getCommunicationId() == null) throw new RuntimeException("3");
        docs.add(c);
      }
    }
    is.close();
    return docs;
  }
  
}

package edu.cmu.cs.lti.edvisees.eventcoref.utils;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.Socket;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.DependencyParse;
import edu.jhu.hlt.concrete.Concrete.Section;
import edu.jhu.hlt.concrete.Concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Concrete.DependencyParse.Dependency;
import edu.jhu.hlt.concrete.Concrete.Token;
import edu.jhu.hlt.concrete.Concrete.TokenRef;
import edu.jhu.hlt.concrete.util.IdUtil;

import tratz.parse.FullSystemWrapper.FullSystemResult;
import tratz.parse.SimpleParseServer;
import tratz.parse.types.Arc;
import tratz.parse.types.Parse;
import tratz.parse.types.Sentence;
import tratz.pos.PosTagger;

public class FanseParse {

  private Socket mSocket;
  private ObjectInputStream mInputStream;
  private ObjectOutputStream mOutputStream;

  public FanseParse(String address, int portNumber) throws IOException {
    mSocket = new Socket(address, portNumber);
    mInputStream = new ObjectInputStream(mSocket.getInputStream());
    mOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
  }

  public void closeClient() throws IOException {
	  mSocket.close();
  }
  
  private SimpleParseServer.ParseResult sendRequest(SimpleParseServer.ParseRequest request) throws IOException, ClassNotFoundException {
    mOutputStream.writeObject(request);
    mOutputStream.flush();
    // Call reset() to avoid memory leak
    mOutputStream.reset();

    SimpleParseServer.ParseResult result = (SimpleParseServer.ParseResult)mInputStream.readObject();

    return result;
  }
  
  private static Parse processLine(edu.jhu.hlt.concrete.Concrete.Sentence inpSentence, FanseParse client) throws Exception {
    
    List<Token> tokList = inpSentence.getTokenization(0).getTokenList();
    List<String> tokTextList = new ArrayList<String>();
    for (Token t : tokList) {
      tokTextList.add(t.getText());
    }
    String[] tokTextArray = new String[tokTextList.size()];
    tokTextArray = tokTextList.toArray(tokTextArray);
    
    Sentence sentence = new Sentence(PosTagger.makeMeSomeTokens(tokTextArray));
    SimpleParseServer.ParseRequest request = null;
    request = new SimpleParseServer.ParseRequest(sentence);
    SimpleParseServer.ParseResult result = client.sendRequest(request);
    
    if(result.getException() != null) {
      System.err.println("Error in parsing...");
      result.getException().printStackTrace();
      return null;
    }
    else {
      FullSystemResult fullResult = result.getResult();
      Parse syntacticParse = fullResult.getParse();
      
      return syntacticParse;
    }
    
  }

  
  public static Communication addToCommunication (Communication c) throws Exception {
    //create a fanseparser client object
    FanseParse client = new FanseParse("128.2.209.222", 5776);
    
    //int start = 0;
    //int end;
    //String cText = c.getText();
    List<edu.jhu.hlt.concrete.Concrete.Sentence> sentList = c.getSectionSegmentation(0).getSection(0).getSentenceSegmentation(0).getSentenceList();
    List<edu.jhu.hlt.concrete.Concrete.Sentence> modifiedSentList = new ArrayList<edu.jhu.hlt.concrete.Concrete.Sentence>();
    //loop through the sentences in this communication, i.e. document
    for (edu.jhu.hlt.concrete.Concrete.Sentence sent : sentList) {
      //end = start + sent.getTextSpan().getEnd();
      //get fanseparse of the sentence
      Parse sentParse = processLine(sent, client);
      
      List<Arc> parseArcs = sentParse.getArcs();
      List<Dependency> parseDeps = new ArrayList<Dependency>();
      //loop through the dependency arcs
      for (Arc a : parseArcs) {
       
        //convert from tratz Arc object to concrete Dependency object and add to list
        if (a.getDependency().equals("ROOT")) {
          continue;
        }
        parseDeps.add(Dependency.newBuilder().setDep(TokenRef.newBuilder().
                                                     setTokenization(sent.getTokenization(0).getUuid()).
                                                     setTokenId(a.getChild().getIndex()-1).build()).
                                              setGov(TokenRef.newBuilder().
                                                     setTokenization(sent.getTokenization(0).getUuid()).
                                                     setTokenId(a.getHead().getIndex()-1).build()).
                                              setEdgeType(a.getDependency()).build());
      }
      //add to the list of modified sentence objects
      modifiedSentList.add(edu.jhu.hlt.concrete.Concrete.Sentence.newBuilder(sent).
                                                                  addDependencyParse(DependencyParse.newBuilder().
                                                                          setUuid(IdUtil.generateUUID()).
                                                                          addAllDependency(parseDeps).build()).
                                                                  build());
      
      //start = end;
    }
    
    client.closeClient();
    return Communication.newBuilder(c).clearSectionSegmentation().
                                       addSectionSegmentation(SectionSegmentation.newBuilder(c.getSectionSegmentation(0)).
                                               clearSection().addSection(Section.newBuilder(c.getSectionSegmentation(0).getSection(0)).
                                                       clearSentenceSegmentation().addSentenceSegmentation(SentenceSegmentation.newBuilder(c.getSectionSegmentation(0).getSection(0).getSentenceSegmentation(0)).
                                                               clearSentence().addAllSentence(modifiedSentList).build()).
                                                       build()).
                                               build()).
                                       build();
  }
  
}

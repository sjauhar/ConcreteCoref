package edu.cmu.cs.lti.edvisees.eventcoref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.tools.JavaFileObject.Kind;

import com.google.protobuf.Message;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.CommunicationGUID;
import edu.jhu.hlt.concrete.Concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Concrete.Situation;
import edu.jhu.hlt.concrete.Concrete.Situation.Argument;
import edu.jhu.hlt.concrete.Concrete.Section;
import edu.jhu.hlt.concrete.Concrete.Sentence;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;
import edu.jhu.hlt.concrete.Concrete.SituationMention;
import edu.jhu.hlt.concrete.Concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.Concrete.SituationMentionSetOrBuilder;
import edu.jhu.hlt.concrete.Concrete.Token;
import edu.jhu.hlt.concrete.Concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.Concrete.Tokenization;
import edu.jhu.hlt.concrete.Concrete.UUID;
import edu.jhu.hlt.concrete.io.ProtocolBufferReader;
import edu.jhu.hlt.concrete.io.ProtocolBufferWriter;
import edu.jhu.hlt.concrete.util.IdUtil;
import edu.jhu.hlt.concrete.util.ProtoFactory;
import static org.junit.Assert.assertEquals;

public class FirestoneReader {


	public static void main(String[] args) throws Exception {

		//inputFile="src/main/resources/eecb-docs-annotations-concrete.pb";
		String parseServerIP = args[1];//This is the IP of the Fanseparser server

		Boolean test = false;

			  
		try{
			File directory = new File(args[0]);
			File[] listOfFiles = directory.listFiles(); 		 
			for (int fi = 0; fi < listOfFiles.length; fi++) 
			{
				if (listOfFiles[fi].isFile()) 
				{
					String file = listOfFiles[fi].getName();
					if (file.endsWith("event.txt"))
					{
						ArrayList<Communication> cList = new ArrayList<Communication>();	
						//System.out.println("Reading filename"+(path+"/"+file));
						//readFile(path,file,eventMap); 
						String filename = args[0]+"/"+file;


						FileReader fr = new FileReader(new File(filename));
						BufferedReader buf = new BufferedReader(fr);
						String line = "";
						String[] arr;

						ArrayList<Sentence> sentList = new ArrayList<Sentence>();
						ArrayList<SituationMention> smList = new ArrayList<SituationMention>();

						while((line = buf.readLine())!= null && !line.isEmpty()){
							arr = line.trim().split(" ");

							ArrayList<Token> tokList = new ArrayList<Token>();

							for(int i = 0; i<arr.length;i++){
								String word= arr[i];
								if(word.startsWith("<Event>")){

									word = word.replace("<Event>", "");
									word = word.replace("<\\Event>", "");
									word = word.split(":")[1];
								}		

								Token tok = Token.newBuilder().setText(word).setTokenIndex(i).build();
								tokList.add(tok);
							}
							Tokenization tokenize = Tokenization.newBuilder().setUuid(IdUtil.generateUUID()).addAllToken(tokList).build();
							Sentence sent = Sentence.newBuilder().setUuid(IdUtil.generateUUID()).addTokenization(0, tokenize).build();
							sentList.add(sent);

							for(int i = 0; i<arr.length;i++){
								if(arr[i].startsWith("<Event>")){
									arr[i] = arr[i].replace("<Event>", "");	arr[i] = arr[i].replace("<\\Event>", "");
									String id = arr[i].split(":")[0];
									arr[i] = arr[i].split(":")[1];
									TokenRefSequence trs = TokenRefSequence.newBuilder().addTokenIndex(i).setAnchorTokenIndex(i).setTokenizationId(tokenize.getUuid()).build();
									SituationMention sm = SituationMention.newBuilder().setUuid(IdUtil.generateUUID()).setText(arr[i]).setTokens(trs).setSituationKindLemma(id).build();
									smList.add(sm);
								}
							}
							//System.out.println("Here 3");
						}

						SituationMentionSet smSet = SituationMentionSet.newBuilder().setUuid(IdUtil.generateUUID()).addAllMention(smList).build();
						SentenceSegmentation sentSeg = SentenceSegmentation.newBuilder().setUuid(IdUtil.generateUUID()).addAllSentence(sentList).build();
						Section sec = Section.newBuilder().setUuid(IdUtil.generateUUID()).addSentenceSegmentation(0, sentSeg).setKind(edu.jhu.hlt.concrete.Concrete.Section.Kind.OTHER).build();
						SectionSegmentation secSeg = SectionSegmentation.newBuilder().setUuid(IdUtil.generateUUID()).addSection(0, sec).build();
						Communication c = Communication.newBuilder().setUuid(IdUtil.generateUUID()).setGuid(CommunicationGUID.newBuilder().build()).addSectionSegmentation(secSeg).addSituationMentionSet(smSet).build();
						cList.add(c);

						System.out.println("Corpus read into Communication object.");
						System.out.println("SituationSetCount(): " + c.getSituationSetCount());
						System.out.println("SituationMentionSetCount(): " + c.getSituationMentionSetCount());
						System.out.println("SituationMentions in Set 0: "+c.getSituationMentionSet(0).getMentionCount());   
						ArrayList<Communication> cListFinal = CorefAnnotate.execute(cList,test,parseServerIP);

						Hashtable<String, String> ht = formatClusters(cListFinal.get(0));
						writeOutput(ht, filename);

						/*Write to new Concrete object*/
						/*
			UUID id;
			CommunicationGUID guid;
			File output = new File("src/main/resources/test-out.pb");
	        FileOutputStream fos = new FileOutputStream(output);
	        ProtocolBufferWriter pbw = new ProtocolBufferWriter(fos);
	        for(int i=0;i<cListFinal.size();i++){
	        	Communication comm = cListFinal.get(0);
		        id = comm.getUuid();
		        guid = comm.getGuid();
		        pbw.write(comm);
	        }
	        pbw.close();
						 */

						buf.close(); 
					}
				}
			}
		}catch(Exception e){
			System.err.println(e);
		}
	}

	private static void writeOutput(Hashtable<String, String> ht, String fName) {
		// TODO Auto-generated method stub
		try{
			System.out.println("In writeOutput, filename:"+ fName);
			FileReader fr = new FileReader(new File(fName));
			BufferedReader buf = new BufferedReader(fr);
			String line = "";
			String[] arr;
			
			PrintWriter writer = new PrintWriter(fName+".bigannotated", "UTF-8");
			
			while((line = buf.readLine())!= null && !line.isEmpty()){
				arr = line.trim().split(" ");
				for(int i = 0; i<arr.length;i++){
					String word= arr[i];
					String id = "";
					if(word.startsWith("<Event>")){
						word = word.replace("<Event>", "");
						word = word.replace("<\\Event>", "");
						id = word.split(":")[0];
						word = word.split(":")[1];
						writer.print("<Event_id="+id+",Cluster_id="+ht.get(id)+">"+word+"</Event> ");
					}
					else writer.print(word+" ");
				}
				writer.println();
			}
			buf.close();
			writer.close();
		}catch(Exception e){	
		}
	}

	private static Hashtable<String, String> formatClusters(Communication c) {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		System.out.println("Printing Clustering");
		HashMap<UUID,SituationMention> hmap = new HashMap<UUID,SituationMention>();
		for(SituationMention sm:c.getSituationMentionSet(0).getMentionList()){
			hmap.put(sm.getUuid(), sm);
		}
		// TODO Auto-generated method stub
		int counter = 0;
		for(Situation s:c.getSituationSet(0).getSituationList()){
			for (Justification j:s.getJustificationList()){
				SituationMention sm = hmap.get(j.getMention());
				System.out.print(sm.getSituationKindLemma()+ " "+ c.getText()+" ");
				ht.put(sm.getSituationKindLemma(), ""+counter);
			}
			counter++;
			System.out.println();
		}
		return ht;
	}
}

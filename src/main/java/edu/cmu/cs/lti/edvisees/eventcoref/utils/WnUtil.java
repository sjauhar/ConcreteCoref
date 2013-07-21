package edu.cmu.cs.lti.edvisees.eventcoref.utils;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import edu.mit.jwi.morph.*;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
//import edu.mit.jwi.item.IIndexWord;
//import edu.mit.jwi.item.IWord;
//import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.*;
import rita.wordnet.RiWordnet;
public class WnUtil {
	private static String wnhome = "src/main/resources/WordNet-3.0";
	private static String path = wnhome + File.separator + "dict";
	
	public static String findlemma(String q, String pos) throws IOException{
		RiWordnet wordnet = new RiWordnet(null);
		//String[] l = wordnet.getSynset("bank", "n");
		String ret=q.toLowerCase();
		String postag = "v";
		if (pos.startsWith("N")|| pos.startsWith("n")){
			postag ="n";
		}
		else if(pos.startsWith("J")|| pos.startsWith("j")){
			postag="a";
		}
		//IIndexWord idxWord = dict.getIndexWord(q,postag);
		//IWordID wordID = idxWord.getWordIDs().get(0) ;
		//IWord word = dict.getWord(wordID) ;
		//System.out.println(q+postag);
		String[] ret1=wordnet.getStems(q, postag);
		//System.out.println(ret==null);
		
		if (ret1==(null)){
			ret=q.toLowerCase();
		}
		else{
			ret=ret1[0].toLowerCase();
		}
		if (pos.equals("NNP")||pos.equals("NNPS")){
			ret=q;
		}
		return ret;
	}
	
	public static String findlemmajwi(String q, String pos) throws IOException{
		IDictionary dict = getDict();
		dict.open() ;
		WordnetStemmer wns= new WordnetStemmer(dict);
		POS postag = POS.VERB;
		if (pos.startsWith("N")|| pos.startsWith("n")){
			postag = POS.NOUN;
		}
		else if(pos.startsWith("J")|| pos.startsWith("j")){
			postag=POS.ADJECTIVE;
		}
		String ret="O";
		List<String> ret1 = wns.findStems(q, postag);
		
		if (ret1.size()==0){
			ret=q.toLowerCase();
		}
		else{
			ret=ret1.get(0).toLowerCase();
		}
		return ret;
	}
	public static String findsst(String q,String pos) throws IOException{
		String ret= "O";
		IDictionary dict = getDict();
		dict.open() ;
		POS postag = POS.VERB;
		if (pos.startsWith("N")|| pos.startsWith("n")){
			postag = POS.NOUN;
		}
		else if(pos.startsWith("J")|| pos.startsWith("j")){
			postag=POS.ADJECTIVE;
		}
		String ret1=findlemmajwi(q, pos);
		IIndexWord idxWord = dict.getIndexWord(ret1,postag);
		if (idxWord==null){
			ret="O";
			return ret;
		}
		IWordID wordID = idxWord.getWordIDs().get(0) ;
		IWord word = dict.getWord(wordID);
		
		
		
		
		ret=word.getSynset().getLexicalFile().getName();
		if (ret==null){
			ret="O";
		}
		return ret.toLowerCase();
	}
	
	public static IDictionary getDict() throws MalformedURLException{
		URL url = new URL("file",null,path ) ;
		
		// construct the dictionary object and open it
		IDictionary dict = new Dictionary ( url ) ;
		return dict;
	}
	public static void main(String[] args) throws IOException{
		//RiWordnet wordnet = new RiWordnet(null);
		//String[] l = wordnet.getSynset("bank", "n");
		
		
		//URL url = new URL("file",null,path ) ;
		
		// construct the dictionary object and open it
		
		
		
		//System.out.println("Id = "+ wordID ) ;
		//System.out.println("Lemma = "+ wordnet.getStems("cities","n")[0]);
		System.out.println("Lemma = "+ findlemma("-","n"));
		System.out.println("sst = "+ findsst("-","n"));


	}
}

package edu.cmu.cs.lti.edvisees.eventcoref.features;
import java.io.IOException;
import java.util.*;

import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

import com.google.common.collect.*;
import com.google.common.collect.Table.Cell;

import edu.cmu.cs.lti.edvisees.eventcoref.utils.*;


public class SDSMfeatures {
	public static ArrayList<Double> genfeat(PredicateArgument set1, PredicateArgument set2,SqlHandle tsq1) throws Exception{
		Boolean nofill=true;
		ArrayList<Double> ret= new ArrayList<Double>();
		
		//SqlHandle tsq2= new SqlHandle("src/main/resources/simplewikidata/repaired_bklsimplewiki_word_lemma_sst_sql0.db");
		//SqlHandle tsq3= new SqlHandle("src/main/resources/simplewikidata/bklsimplewiki_sst_sql0.db");
		//lemma arguments
		String event1= WnUtil.findlemma(set1.getAction(),set1.getActionPOS())+"_root";
		String event2= WnUtil.findlemma(set2.getAction(),set2.getActionPOS())+"_root";
		
		String agent1= WnUtil.findlemma(set1.getAgent(),set1.getAgentPOS())+"_root";
		String agent2= WnUtil.findlemma(set2.getAgent(),set2.getAgentPOS())+"_root";
		
		String patient1= WnUtil.findlemma(set1.getPatient(),set1.getPatientPOS())+"_root";
		String patient2= WnUtil.findlemma(set2.getPatient(),set2.getPatientPOS())+"_root";
		
		//sst arguments
		
		/*String eventsst1= WnUtil.findsst(set1.getAction(),set1.getActionPOS())+"_sst";
		String eventsst2= WnUtil.findsst(set2.getAction(),set2.getActionPOS())+"_sst";
		
		String agentsst1= WnUtil.findsst(set1.getAgent(),set1.getAgentPOS())+"_sst";
		String agentsst2= WnUtil.findsst(set2.getAgent(),set2.getAgentPOS())+"_sst";
		
		String patientsst1= WnUtil.findsst(set1.getPatient(),set1.getPatientPOS())+"_sst";
		String patientsst2= WnUtil.findsst(set2.getPatient(),set2.getPatientPOS())+"_sst";*/
		String eventsst1="";
		String eventsst2="";
		//lemma gold arguments
		GoldArgm ev_ag_lem1 = new GoldArgm(event1,agent1,"A");
		GoldArgm ev_ag_lem2 = new GoldArgm(event2,agent2,"A");
		
		GoldArgm ev_pa_lem1 = new GoldArgm(event1,patient1,"P");
		GoldArgm ev_pa_lem2 = new GoldArgm(event2,patient2,"P");
		//sst gold aruments
		/*GoldArgm ev_ag_sst1 = new GoldArgm(eventsst1,agentsst1,"A");
		GoldArgm ev_ag_sst2 = new GoldArgm(eventsst2,agentsst2,"A");
		
		GoldArgm ev_pa_sst1 = new GoldArgm(eventsst1,patientsst1,"P");
		GoldArgm ev_pa_sst2 = new GoldArgm(eventsst2,patientsst2,"P");
		
		//sst1 gold aruments
		GoldArgm ev_ag_sst11 = new GoldArgm(event1,agentsst1,"A");
		GoldArgm ev_ag_sst12 = new GoldArgm(event2,agentsst2,"A");
				
		GoldArgm ev_pa_sst11 = new GoldArgm(event1,patientsst1,"P");
		GoldArgm ev_pa_sst12 = new GoldArgm(event2,patientsst2,"P");
		
		//sst2 gold aruments
		GoldArgm ev_ag_sst21 = new GoldArgm(eventsst1,agent1,"A");
		GoldArgm ev_ag_sst22 = new GoldArgm(eventsst2,agent2,"A");
						
		GoldArgm ev_pa_sst21 = new GoldArgm(eventsst1,patient1,"P");
		GoldArgm ev_pa_sst22 = new GoldArgm(eventsst2,patient2,"P");*/
		
		//TODO:Handle the cases when events are nouns!
		
		
		
		//if (!(agent1.equals("-_root")||agent2.equals("-_root")||patient1.equals("-_root")||patient2.equals("-_root"))){
		//Simple Level3
			//Lemma
			//System.out.println("Making Simple lemma features");
			ArrayList<Double> Eventfeats= finddist(event1,event2,tsq1,"normal");
			ArrayList<Double> Agentfeats= Lists.newArrayList();
			ArrayList<Double> ev_ag_lem_feats= Lists.newArrayList();
			ArrayList<Double> Patientfeats= Lists.newArrayList();
			ArrayList<Double> ev_pa_lem_feats= Lists.newArrayList();
			
		if(!(agent1.equals("-_root"))&&!(agent2.equals("-_root"))){
			Agentfeats= finddist(agent1,agent2,tsq1,"normal");
			ev_ag_lem_feats= finddist(ev_ag_lem1,ev_ag_lem2,tsq1,"normal");
		}
		if(!(patient1.equals("-_root"))&&!(patient2.equals("-_root"))){
			Patientfeats= finddist(patient1,patient2,tsq1,"normal");
		
			ev_pa_lem_feats= finddist(ev_pa_lem1,ev_pa_lem2,tsq1,"normal");
		}
			//pure sst
	/*		System.out.println("Making Simple pure sst features");
			ArrayList<Double> Eventsstfeats= finddist(eventsst1,eventsst2,tsq3,"sst");
		ArrayList<Double> Agentsstfeats= finddist(agentsst1,agentsst2,tsq3,"sst");
		ArrayList<Double> Patientsstfeats= finddist(patientsst1,patientsst2,tsq3,"sst");
			//mixed sst
			System.out.println("Making Simple mixed sst features");
			ArrayList<Double> Eventsst1feats= finddist(eventsst1,eventsst2,tsq2,"sst");
		ArrayList<Double> Agentsst1feats= finddist(agentsst1,agentsst2,tsq2,"sst");
		ArrayList<Double> Patientsst1feats= finddist(patientsst1,patientsst2,tsq2,"sst");*/
		
		//Level 2 features
			//Lemma
			//System.out.println("Making level2 lemma features");
		
//@@@@@@Here is the code to revert to -1.0 default values
		if(nofill){
			//System.out.println("nofill is true");
		if (agent1.equals("-_root")||agent2.equals("-_root")){
			//SparseDoubleMatrix2D m1= new SparseDoubleMatrix2D(1,1);
			//SparseDoubleMatrix2D m2= new SparseDoubleMatrix2D(1,1);
			Agentfeats= Lists.newArrayList(0.23122829779151968, 1.2512233042402923, 0.5353173744699655);//calcdist(m1,m2,"default");
			ev_ag_lem_feats= Lists.newArrayList(0.9389374553003531, 0.9765789241166078, 0.9036256846289756);//calcdist(m1,m2,"default");
		}
		
		if (patient1.equals("-_root")||patient2.equals("-_root")){
			//SparseDoubleMatrix2D m1= new SparseDoubleMatrix2D(1,1);
			//SparseDoubleMatrix2D m2= new SparseDoubleMatrix2D(1,1);
			Patientfeats=Lists.newArrayList(0.21432281972552883, 1.33989803033053, 0.5507771073850622);//calcdist(m1,m2,"default");
			ev_pa_lem_feats= Lists.newArrayList(0.74923933945639, 0.9389621960271494, 0.7649554668126036); //calcdist(m1,m2,"default");
		}
		}
	//@@@@@TILL HERE FOR DEFAULT -1.0
		
	/*		//pure sst
		//	System.out.println("Making level2 pure sst features");
		ArrayList<Double> ev_ag_sst_feats= finddist(ev_ag_sst1,ev_ag_sst2,tsq3,"sst");
		ArrayList<Double> ev_pa_sst_feats= finddist(ev_pa_sst1,ev_pa_sst2,tsq3,"sst");
			//sst1
			System.out.println("Making level2 sst1 features");
		ArrayList<Double> ev_ag_sst1_feats= finddist(ev_ag_sst11,ev_ag_sst12,tsq2,"sst1");
		ArrayList<Double> ev_pa_sst1_feats= finddist(ev_pa_sst11,ev_pa_sst12,tsq2,"sst1");
			//sst2
			System.out.println("Making level2 sst2 features");
		ArrayList<Double> ev_ag_sst2_feats= finddist(ev_ag_sst21,ev_ag_sst22,tsq2,"sst2");
		ArrayList<Double> ev_pa_sst2_feats= finddist(ev_pa_sst21,ev_pa_sst22,tsq2,"sst2");
		//}*/
		//Handling empty agents, patients
		
		//@@@@ Here is the code to fill with 1 value
		if(!nofill){
			//System.out.println("nofill is false");
		if (agent1.equals("-_root")){
			//System.out.println("Agent1 Missing");
			HashMap<String,Double> ag1_map= CompositionUtils.fill_lem(event1,"A",tsq1);
			//HashMap<String,Double> ag1_sst_map= CompositionUtils.fill_lem(event1,"A",tsq2);
			
			if(agent2.equals("-_root")){
				//System.out.println("Agent2 also Missing");
				HashMap<String,Double> ag2_map= CompositionUtils.fill_lem(event2,"A",tsq1);
				//HashMap<String,Double> ag2_sst_map= CompositionUtils.fill_lem(event2,"A",tsq2);
				Agentfeats= finddist(ag1_map,ag2_map,tsq1,"single","A","normal",event1,event2,eventsst1,eventsst2);
				//Agentsstfeats=finddist(ag1_sst_map, ag2_sst_map,tsq3,"single","A","sst",event1,event2,eventsst1,eventsst2);
				//Agentsst1feats=finddist(ag1_sst_map, ag2_sst_map,tsq2,"single","A","sst1",event1,event2,eventsst1,eventsst2);
				
				ev_ag_lem_feats=finddist(ag1_map, ag2_map,tsq1,"comp","A","normal",event1,event2,eventsst1,eventsst2);
				//ev_ag_sst_feats=finddist(ag1_sst_map, ag2_sst_map,tsq3,"comp","A","sst",event1,event2,eventsst1,eventsst2);
				//ev_ag_sst1_feats=finddist(ag1_sst_map, ag2_sst_map,tsq2,"comp","A","sst1",event1,event2,eventsst1,eventsst2);
				//ev_ag_sst2_feats=finddist(ag1_map, ag2_map,tsq2,"comp","A","sst2",event1,event2,eventsst1,eventsst2);
				
			}
			else{
				//System.out.println("Only Agent1 Missing");
				Agentfeats= finddist(ag1_map,agent2,tsq1,"single","A","normal",event1,event2,eventsst1,eventsst2);
				//Agentsstfeats=finddist(ag1_sst_map, agentsst2,tsq3,"single","A","sst",event1,event2,eventsst1,eventsst2);
				//Agentsst1feats=finddist(ag1_sst_map, agentsst2,tsq2,"single","A","sst1",event1,event2,eventsst1,eventsst2);
				
				ev_ag_lem_feats=finddist(ag1_map, agent2,tsq1,"comp","A","normal",event1,event2,eventsst1,eventsst2);
				//ev_ag_sst_feats=finddist(ag1_sst_map, agentsst2,tsq3,"comp","A","sst",event1,event2,eventsst1,eventsst2);
				//ev_ag_sst1_feats=finddist(ag1_sst_map, agentsst2,tsq2,"comp","A","sst1",event1,event2,eventsst1,eventsst2);
				//ev_ag_sst2_feats=finddist(ag1_map, agent2,tsq2,"comp","A","sst2",event1,event2,eventsst1,eventsst2);
			}
		}
		
		if (agent2.equals("-_root")&&!(agent1.equals("-_root"))){
			//System.out.println("Only Agent2 Missing");
			HashMap<String,Double> ag2_map= CompositionUtils.fill_lem(event2,"A",tsq1);
			//HashMap<String,Double> ag2_sst_map= CompositionUtils.fill_lem(event2,"A",tsq2);
			Agentfeats= finddist(ag2_map,agent1,tsq1,"single","A","normal",event1,event2,eventsst1,eventsst2);
			//Agentsstfeats=finddist(ag2_sst_map, agentsst1,tsq3,"single","A","sst",event1,event2,eventsst1,eventsst2);
			//Agentsst1feats=finddist(ag2_sst_map, agentsst1,tsq2,"single","A","sst1",event1,event2,eventsst1,eventsst2);
			
			ev_ag_lem_feats=finddist(ag2_map, agent1,tsq1,"comp","A","normal",event1,event2,eventsst1,eventsst2);
			//ev_ag_sst_feats=finddist(ag2_sst_map, agentsst1,tsq3,"comp","A","sst",event1,event2,eventsst1,eventsst2);
			//ev_ag_sst1_feats=finddist(ag2_sst_map, agentsst1,tsq2,"comp","A","sst1",event1,event2,eventsst1,eventsst2);
			//ev_ag_sst2_feats=finddist(ag2_map, agentsst1,tsq2,"comp","A","sst2",event1,event2,eventsst1,eventsst2);
		}
		
		if (patient1.equals("-_root")){
			//System.out.println("Patient1 Missing");
			HashMap<String,Double> pa1_map= CompositionUtils.fill_lem(event1,"P",tsq1);
			//HashMap<String,Double> pa1_sst_map= CompositionUtils.fill_lem(event1,"P",tsq2);
			if(patient2.equals("-_root")){
				//System.out.println("Patient2 also Missing");
				HashMap<String,Double> pa2_map= CompositionUtils.fill_lem(event2,"P",tsq1);
				//HashMap<String,Double> pa2_sst_map= CompositionUtils.fill_lem(event2,"P",tsq2);
				Patientfeats= finddist(pa1_map,pa2_map,tsq1,"single","P","normal",event1,event2,eventsst1,eventsst2);
				//Patientsstfeats=finddist(pa1_sst_map, pa2_sst_map,tsq3,"single","P","sst",event1,event2,eventsst1,eventsst2);
				//Patientsst1feats=finddist(pa1_sst_map, pa2_sst_map,tsq2,"single","P","sst1",event1,event2,eventsst1,eventsst2);
				
				ev_pa_lem_feats=finddist(pa1_map, pa2_map,tsq1,"comp","P","normal",event1,event2,eventsst1,eventsst2);
				//ev_pa_sst_feats=finddist(pa1_sst_map, pa2_sst_map,tsq3,"comp","P","sst",event1,event2,eventsst1,eventsst2);
				//ev_pa_sst1_feats=finddist(pa1_sst_map, pa2_sst_map,tsq2,"comp","P","sst1",event1,event2,eventsst1,eventsst2);
				//ev_pa_sst2_feats=finddist(pa1_map, pa2_map,tsq2,"comp","P","sst2",event1,event2,eventsst1,eventsst2);
				
			}
			else{
				//System.out.println("Onlt Patient1 Missing");
				Patientfeats= finddist(pa1_map,patient2,tsq1,"single","P","normal",event1,event2,eventsst1,eventsst2);
				//Patientsstfeats=finddist(pa1_sst_map, patientsst2,tsq3,"single","P","sst",event1,event2,eventsst1,eventsst2);
				//Patientsst1feats=finddist(pa1_sst_map, patientsst2,tsq2,"single","P","sst1",event1,event2,eventsst1,eventsst2);
				
				ev_pa_lem_feats=finddist(pa1_map, patient2,tsq1,"comp","P","normal",event1,event2,eventsst1,eventsst2);
				//ev_pa_sst_feats=finddist(pa1_sst_map, patientsst2,tsq3,"comp","P","sst",event1,event2,eventsst1,eventsst2);
				//ev_pa_sst1_feats=finddist(pa1_sst_map, patientsst2,tsq2,"comp","P","sst1",event1,event2,eventsst1,eventsst2);
				//ev_pa_sst2_feats=finddist(pa1_map, patient2,tsq2,"comp","P","sst2",event1,event2,eventsst1,eventsst2);
			}
		}
		
				
		if (patient2.equals("-_root")&&!(patient1.equals("-_root"))){
			//System.out.println("Onlt Patient2 Missing");
			HashMap<String,Double> pa2_map= CompositionUtils.fill_lem(event2,"P",tsq1);
			//HashMap<String,Double> pa2_sst_map= CompositionUtils.fill_lem(event2,"P",tsq2);
			Patientfeats= finddist(pa2_map,patient1,tsq1,"single","P","normal",event1,event2,eventsst1,eventsst2);
			//Patientsstfeats=finddist(pa2_sst_map, patientsst2,tsq3,"single","P","sst",event1,event2,eventsst1,eventsst2);
			//Patientsst1feats=finddist(pa2_sst_map, patientsst2,tsq2,"single","P","sst1",event1,event2,eventsst1,eventsst2);
			
			ev_pa_lem_feats=finddist(pa2_map, patient1,tsq1,"comp","P","normal",event1,event2,eventsst1,eventsst2);
			//ev_pa_sst_feats=finddist(pa2_sst_map, patientsst1,tsq3,"comp","P","sst",event1,event2,eventsst1,eventsst2);
			//ev_pa_sst1_feats=finddist(pa2_sst_map, patientsst1,tsq2,"comp","P","sst1",event1,event2,eventsst1,eventsst2);
			//ev_pa_sst2_feats=finddist(pa2_map, patient1,tsq2,"comp","P","sst2",event1,event2,eventsst1,eventsst2);
		}
		}
		//@@@@TILL HERE THE CODE TO FILL WITH 1 ILLER VALUE
		ret.addAll(Eventfeats);
		ret.addAll(Agentfeats);
		ret.addAll(Patientfeats);
		ret.addAll(ev_ag_lem_feats);
		ret.addAll(ev_pa_lem_feats);
		/*ret.addAll(Eventsstfeats);
		ret.addAll(Agentsstfeats);
		ret.addAll(Patientsstfeats);
		ret.addAll(Eventsst1feats);
		ret.addAll(Agentsst1feats);
		ret.addAll(Patientsst1feats);
		
		ret.addAll(ev_ag_sst_feats);
		ret.addAll(ev_pa_sst_feats);
		ret.addAll(ev_ag_sst1_feats);
		ret.addAll(ev_pa_sst1_feats);
		ret.addAll(ev_ag_sst2_feats);
		ret.addAll(ev_pa_sst2_feats);*/
		return ret;
	}
	
	public static ArrayList<Double> finddist(String cat1, String cat2,SqlHandle tsq,String type) throws Exception{
		ArrayList<Double> ret= new ArrayList<Double>();
		Table<String,String,Multiset<String>> tab1= HashBasedTable.create();
		Table<String,String,Multiset<String>> tab2= HashBasedTable.create();
		if (type.equals("sst")){
			tab1= CompositionUtils.Singledictsst(cat1, tsq);
			tab2= CompositionUtils.Singledictsst(cat2, tsq);
		}
		else{
			tab1= CompositionUtils.Singledict(cat1, tsq);
			tab2= CompositionUtils.Singledict(cat2, tsq);
		}
		List<Table<String,String,Multiset<String>>> dictlist= Lists.newArrayList(tab1,tab2);
		//System.out.print("Dictionaries made");
		List<SparseDoubleMatrix2D> matrices = MatrixUtils.convert2mat(dictlist);
		//System.out.print("..........Matrices made");
		ret= calcdist(matrices.get(0),matrices.get(1),"normal");
		//System.out.println(".......Distances calculated");
		return ret;
	}
	
	public static ArrayList<Double> finddist(GoldArgm cat1, GoldArgm cat2,SqlHandle tsq,String type) throws Exception{
		ArrayList<Double> ret= new ArrayList<Double>();
		
		Table<String,String,Multiset<String>> tab1= HashBasedTable.create();
		Table<String,String,Multiset<String>> tab2= HashBasedTable.create();
		if (type.equals("sst")){
			tab1= Gold.goldsst(cat1, tsq);
			tab2= Gold.goldsst(cat2, tsq);
		}
		else if(type.equals("sst1")){
			tab1= Gold.goldsst1(cat1, tsq);
			tab2= Gold.goldsst1(cat2, tsq);
		}
		else if (type.equals("sst2")){
			tab1= Gold.goldsst2(cat1, tsq);
			tab2= Gold.goldsst2(cat2, tsq);
		}
		else{
			tab1= Gold.golds(cat1, tsq);
			tab2= Gold.golds(cat2, tsq);
		}
		List<Table<String,String,Multiset<String>>> dictlist= Lists.newArrayList(tab1,tab2);
		//System.out.print("Dictionaries made");
		List<SparseDoubleMatrix2D> matrices = MatrixUtils.convert2mat(dictlist);
		//System.out.print("..........Matrices made");
		ret= calcdist(matrices.get(0),matrices.get(1),"normal");
		//System.out.println("......Distances calculated");
		return ret;
	}
	
	public static ArrayList<Double> finddist(HashMap<String,Double> cat1,String cat2,SqlHandle tsq, String type,String rel,String mode,String event1,String event2,String eventsst1,String eventsst2) throws Exception{
		//System.out.println("Calculating..");
		ArrayList<Double> ret= new ArrayList<Double>();
		List<Table<String,String,Multiset<String>>> dictlist= Lists.newArrayList();
		List<Double> weights =Lists.newArrayList();
		int count1=0;
		for(String k:cat1.keySet()){
			if(type.equals("single")){
				dictlist.add(CompositionUtils.Singledict(k, tsq));
			}
			if(type.equals("comp")){
				if (mode.equals("normal")){
					GoldArgm argm1 = new GoldArgm(event1,k,rel);
					dictlist.add(Gold.golds(argm1, tsq));
				}
				else if(mode.equals("sst2")){
					GoldArgm argm1 = new GoldArgm(eventsst1,k,rel);
					dictlist.add(Gold.golds(argm1, tsq));
				}
			}
			weights.add(cat1.get(k));
			count1 +=1;
		}
		if (type.equals("single")){
		dictlist.add(CompositionUtils.Singledict(cat2, tsq));
		}
		if(type.equals("comp")){
			if (mode.equals("normal")){
				GoldArgm argm2 = new GoldArgm(event2,cat2,rel);
				dictlist.add(Gold.golds(argm2, tsq));
			}
			else if(mode.equals("sst2")){
				GoldArgm argm2 = new GoldArgm(eventsst2,cat2,rel);
				dictlist.add(Gold.golds(argm2, tsq));
			}
				
		}
		//System.out.println("building matrices");
		List<SparseDoubleMatrix2D> matrices = MatrixUtils.convert2mat(dictlist);
		List<SparseDoubleMatrix2D> scaledmats=Lists.newArrayList();
		for(int i=0;i<count1;i+=1){
			scaledmats.add(MatrixUtils.scale(matrices.get(i),weights.get(i)));
		}
		SparseDoubleMatrix2D matrix1 = MatrixUtils.addn(scaledmats);
		SparseDoubleMatrix2D matrix2 = matrices.get(count1);
		ret= calcdist(matrix1,matrix2,"normal");
		//System.out.println("Calculated..");
		return ret;
	}
	
	public static ArrayList<Double> finddist(HashMap<String,Double> cat1,HashMap<String,Double> cat2,SqlHandle tsq, String type,String rel,String mode,String event1,String event2,String eventsst1,String eventsst2) throws Exception{
		//System.out.println("Calculating..");
		ArrayList<Double> ret= new ArrayList<Double>();
		List<Table<String,String,Multiset<String>>> dictlist= Lists.newArrayList();
		List<Double> weights =Lists.newArrayList();
		int count1=0;
		for(String k:cat1.keySet()){
			if(type.equals("single")){
			dictlist.add(CompositionUtils.Singledict(k, tsq));
			}
			else if(type.equals("comp")){
				if (mode.equals("normal")){
					GoldArgm argm1 = new GoldArgm(event1,k,rel);
					dictlist.add(Gold.golds(argm1, tsq));
				}
				else if(mode.equals("sst2")){
					GoldArgm argm1 = new GoldArgm(eventsst1,k,rel);
					dictlist.add(Gold.golds(argm1, tsq));
				}
			}
			weights.add(cat1.get(k));
			count1 +=1;
		}
		int count2=count1;
		for(String k:cat2.keySet()){
			if(type.equals("single")){
			dictlist.add(CompositionUtils.Singledict(k, tsq));
			}
			else if(type.equals("comp")){
				if (mode.equals("normal")){
					GoldArgm argm2 = new GoldArgm(event2,k,rel);
					dictlist.add(Gold.golds(argm2, tsq));
				}
				else if(mode.equals("sst2")){
					GoldArgm argm2 = new GoldArgm(eventsst2,k,rel);
					dictlist.add(Gold.golds(argm2, tsq));
				}
			}
			weights.add(cat2.get(k));
			count2+=1;
		}
		//System.out.println("building matrices");
		List<SparseDoubleMatrix2D> matrices = MatrixUtils.convert2mat(dictlist);
		List<SparseDoubleMatrix2D> scaledmats1 = Lists.newArrayList();
		List<SparseDoubleMatrix2D> scaledmats2 = Lists.newArrayList();
		for(int i=0;i<count1;i+=1){
			scaledmats1.add(MatrixUtils.scale(matrices.get(i),weights.get(i)));
		}
		for(int i=count1;i<count2;i+=1){
			scaledmats2.add(MatrixUtils.scale(matrices.get(i),weights.get(i)));
		}
		SparseDoubleMatrix2D matrix1 = MatrixUtils.addn(scaledmats1);
		SparseDoubleMatrix2D matrix2 = MatrixUtils.addn(scaledmats2);
		ret= calcdist(matrix1,matrix2,"normal");
		//System.out.println("Calculated..");
		return ret;
	}
	
	public static ArrayList<Double> calcdist(SparseDoubleMatrix2D m1, SparseDoubleMatrix2D m2,String type){
		ArrayList<Double> ret= new ArrayList<Double>();
		ArrayList<String> dists= Lists.newArrayList("E","M","co");
		
		int i=0;
		for(String dis:dists){
			
			
			if (type.equals("default")){
				ret.add(-1.0);
				//ret.add(-1.0);
				//ret.add(-1.0);
			}
			else{
				//System.out.println("Dvec norm");
				double x=MatrixUtils.DVecdist(m1,m2,dis);
				if(Double.isNaN(x)||(x==0.0 && !(m1.equals(m2)))){
					x=1.0;
				}
				ret.add(x);
				//System.out.println("SDvec norm");
				//ret.add(MatrixUtils.SdVecdist(m1, m2, dis));
				//System.out.println("Normal norm");
				//ret.add(MatrixUtils.dist(m1, m2, dis));
			}
		}
		return ret;
	}
}

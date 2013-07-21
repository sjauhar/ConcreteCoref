package edu.cmu.cs.lti.edvisees.eventcoref.utils;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.CompositionUtils;
import edu.cmu.cs.lti.edvisees.eventcoref.utils.Gold;
import cern.colt.matrix.tdouble.algo.DoubleStatistic;
import cern.colt.matrix.tdouble.algo.DoubleStatistic.VectorVectorFunction;





//import cern.colt.matrix.doublealgo.Statistic
import java.util.*;

//import cern.colt.matrix.DoubleMatrix1D;
//import cern.colt.matrix.DoubleMatrix2D;
//import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix1D;

import com.google.common.collect.*;
import com.google.common.collect.Table.Cell;
public class MatrixUtils {
	
	private static final double Nan = 0;

	//private DoubleMatrix2D matrix;
	public static List<SparseDoubleMatrix2D> convert2mat(List<Table<String,String,Multiset<String>>> dictlist){
		List<SparseDoubleMatrix2D> retlist= new ArrayList<SparseDoubleMatrix2D>();
		Set<String> rowset=new HashSet<String>();
		Set<String> columnset= new HashSet<String>();
		for (Table<String,String,Multiset<String>> curtab:dictlist){
			columnset.addAll(curtab.columnKeySet());
			rowset.addAll(curtab.rowKeySet());
		}
		HashMap<String,Integer> colmap= new HashMap<String,Integer>();
		HashMap<String,Integer> rowmap= new HashMap<String,Integer>();
		int colcount=0;
		int rowcount=0;
		for (String col:columnset){
			colmap.put(col,colcount);
			colcount +=1;
		}
		for (String row:rowset){
			rowmap.put(row,rowcount);
			rowcount +=1;
		}
		for (Table<String,String,Multiset<String>> curtab:dictlist){
			SparseDoubleMatrix2D mat= new SparseDoubleMatrix2D(rowset.size(),columnset.size());
			for (Cell<String,String,Multiset<String>> cell:curtab.cellSet()){
				String r= cell.getRowKey();
				String c= cell.getColumnKey();
				int count= cell.getValue().size();
				mat.set(rowmap.get(r),colmap.get(c), count);
			}
			retlist.add(mat);
			
		}
		return retlist;
	}
	public static double dist(SparseDoubleMatrix1D m1, SparseDoubleMatrix1D m2, String type){
		SparseDoubleMatrix2D inp = new SparseDoubleMatrix2D((int)m1.size(), 2);
		SparseDoubleMatrix1D m11=(SparseDoubleMatrix1D) m1.copy();
		SparseDoubleMatrix1D m21=(SparseDoubleMatrix1D) m2.copy();
		
		//DoubleMatrix2D inp = new DenseDoubleMatrix2D((int)m1.size(), 2);
		//DoubleMatrix1D m11= m1.copy();
		//DoubleMatrix1D m21= m2.copy();
		//System.out.println(m1);
		//System.out.println(m2);
		
		for (int i = 0; i < (int)m1.size(); i++) {
			inp.set(i,0,m1.get(i));
			inp.set(i, 1, m2.get(i));
		}
		
		if (type.equals("E")){
			return DoubleStatistic.distance(inp, DoubleStatistic.EUCLID).get(0,1);
		}
		else if (type.equals("M")){
			return DoubleStatistic.distance(inp, DoubleStatistic.MANHATTAN).get(0,1);
		}
		else if (type.equals("C")){
			return 1.0-DoubleStatistic.correlation(DoubleStatistic.covariance(inp)).get(0,1);
		}
		else{
			double norm_prev1 = m11.aggregate(cern.jet.math.tdouble.DoubleFunctions.plus,cern.jet.math.tdouble.DoubleFunctions.square);
			//System.out.println("Norm1prev is"+norm_prev1);
			double norm1 = Math.sqrt(norm_prev1);
			//System.out.println("Norm1 is"+norm1);
			double norm2= Math.sqrt(m21.aggregate(cern.jet.math.tdouble.DoubleFunctions.plus,cern.jet.math.tdouble.DoubleFunctions.square));
			//System.out.println("Norm2 is"+norm2);
			m11.assign(m21, cern.jet.math.tdouble.DoubleFunctions.mult);
			//System.out.println(m11);
			if(norm1<0.000001 || norm2<0.000001||Double.isNaN(norm1)||Double.isNaN(norm2)){
				return 1.0;
			}
			
			double ret= 1.0-(((m11.aggregate(cern.jet.math.tdouble.DoubleFunctions.plus,cern.jet.math.tdouble.DoubleFunctions.identity))/norm1)/norm2);
			if(Double.isNaN(ret))
				return 1.0;
			return ret;
		}			
	}
	
	public static double dist(SparseDoubleMatrix2D m1, SparseDoubleMatrix2D m2, String type){
		//System.out.println("flattening..");
		return dist((SparseDoubleMatrix1D)m1.viewDice().vectorize(),(SparseDoubleMatrix1D)m2.viewDice().vectorize(),type);
	}
	
	public static double SdVecdist(SparseDoubleMatrix2D m1, SparseDoubleMatrix2D m2, String type){
		//System.out.println("normalizing..overall");
		SparseDoubleMatrix1D m11 = (SparseDoubleMatrix1D)m2.viewDice().vectorize();
		SparseDoubleMatrix1D m21 = (SparseDoubleMatrix1D)m1.viewDice().vectorize();
		m11.normalize();
		m21.normalize();
		//System.out.println("normalized");
		return dist(m11,m21,type);
	}
	
	public static double DVecdist(SparseDoubleMatrix2D m1, SparseDoubleMatrix2D m2, String type){
		SparseDoubleMatrix1D m11= new SparseDoubleMatrix1D(m1.columns());
		SparseDoubleMatrix1D m21= new SparseDoubleMatrix1D(m2.columns());
		//System.out.println("normalizing--collapsing rows");
		for (int i = 0; i < (int)m1.columns(); i++) {
			double sum1 = m1.viewColumn(i).aggregate(cern.jet.math.tdouble.DoubleFunctions.plus,cern.jet.math.tdouble.DoubleFunctions.identity);
			double sum2 = m2.viewColumn(i).aggregate(cern.jet.math.tdouble.DoubleFunctions.plus,cern.jet.math.tdouble.DoubleFunctions.identity);
			m11.set(i, sum1);
			m21.set(i, sum2);
		}
		//SparseDoubleMatrix1D m11 = (SparseDoubleMatrix1D)m2.viewDice().vectorize();
		//SparseDoubleMatrix1D m21 = (SparseDoubleMatrix1D)m1.viewDice().vectorize();
		m11.normalize();
		m21.normalize();
		//System.out.println("normalized");
		return dist(m11,m21,type);
	}
	
	public static SparseDoubleMatrix2D addn(List<SparseDoubleMatrix2D> m1){
		SparseDoubleMatrix2D ret= (SparseDoubleMatrix2D)m1.get(0).copy();
		int count=0;
		for (SparseDoubleMatrix2D m: m1){
			if(!(count==0)){
			ret.assign(m, cern.jet.math.tdouble.DoubleFunctions.plus);
			}
			count += 1;
		}
		//ret.assign(m3, cern.jet.math.tdouble.DoubleFunctions.plus);
		return ret;
	}
	
	public static SparseDoubleMatrix2D scale(SparseDoubleMatrix2D m1,double cst){
		SparseDoubleMatrix2D ret= (SparseDoubleMatrix2D)m1.copy();
		ret.assign(cern.jet.math.tdouble.DoubleFunctions.mult(cst));
		return ret;
	}
	
	public static void main(String[] args) throws Exception{
		GoldArgm argm1 = new GoldArgm("he_root","eat_root","A");
		GoldArgm argm2 = new GoldArgm("people_root","eat_root","A");
		GoldArgm argm3 = new GoldArgm("they_root","eat_root","A");
		SqlHandle tsq= new SqlHandle("src/main/resources/simplewikidata/bklsimplewiki_lemma_sql0.db");
		Table<String, String, Multiset<String>> gold1 = Gold.golds(argm1,tsq);
		Table<String, String, Multiset<String>> gold2 = Gold.golds(argm2,tsq);
		Table<String, String, Multiset<String>> gold3 = Gold.golds(argm3,tsq);
		List<Table<String,String,Multiset<String>>> dictlist= new ArrayList<Table<String,String,Multiset<String>>>();
		dictlist.add(gold1);
		dictlist.add(gold2);
		dictlist.add(gold3);
		List<SparseDoubleMatrix2D> matlist= convert2mat(dictlist);
		//System.out.println(matlist.get(2));
		SparseDoubleMatrix2D mt= new SparseDoubleMatrix2D(2,2);
		mt.set(0,0,1);
		mt.set(1,1,4);
		mt.set(0,1,2);
		mt.set(1, 0, 3);
		
		SparseDoubleMatrix2D mt1= new SparseDoubleMatrix2D(2,2);
		mt1.set(0,0,1);
		mt1.set(1,1,4);
		mt1.set(0,1,2);
		mt1.set(1, 0, 3);
		
		SparseDoubleMatrix2D mt2= new SparseDoubleMatrix2D(2,2);
		mt2.set(0,0,6);
		mt2.set(1,1,2);
		mt2.set(0,1,3);
		mt2.set(1, 0, 6);
		//System.out.println(mt);
		DoubleMatrix1D bt = mt.viewDice().vectorize();
		//DoubleMatrix1D bt1=new SparseDoubleMatrix2D(2,2).viewDice().vectorize();
		//bt.normalize();
		DoubleMatrix1D bt1 = mt1.viewDice().vectorize();
		//System.out.println(mt.viewColumn(0));
		/*System.out.println(dist(matlist.get(0),matlist.get(1),"Co"));
		System.out.println(SdVecdist(matlist.get(0),matlist.get(1),"Co"));
		System.out.println(DVecdist(matlist.get(0),matlist.get(1),"Co"));
		//System.out.println(Math.sqrt(bt1.aggregate(cern.jet.math.tdouble.DoubleFunctions.plus,cern.jet.math.tdouble.DoubleFunctions.square)));
		//System.out.println(addn(Lists.newArrayList(mt,mt1,mt2)));
		//System.out.println(addn(matlist));
		//System.out.println(bt1);
		System.out.println(scale(mt2,3.0));*/
		SparseDoubleMatrix2D spl1= new SparseDoubleMatrix2D(2,2);
		//System.out.println(spl1);
		spl1.set(0, 0, 1);
		SparseDoubleMatrix2D spl2= new SparseDoubleMatrix2D(2,2);
		spl2.set(0,0,0);
		SparseDoubleMatrix1D spl12 = (SparseDoubleMatrix1D)spl2.viewDice().vectorize();
		spl12.normalize();
		
		SparseDoubleMatrix1D spl11 = (SparseDoubleMatrix1D)spl1.viewDice().vectorize();
		spl11.normalize();
		List<Table<String,String,Multiset<String>>> lis= Lists.newArrayList();
		Table<String,String,Multiset<String>> rettable1= HashBasedTable.create();
		Table<String,String,Multiset<String>> rettable2= HashBasedTable.create();
		
		
		Multiset<String> mu= HashMultiset.create();
		mu.add("444");
		mu.add("432",3);
		mu.add("54");
		
		rettable2.put("nsub", "eat", mu);
		lis.add(rettable1);
		lis.add(rettable2);
		List<SparseDoubleMatrix2D> mat = convert2mat(lis);
		Double x=DVecdist(mat.get(0),mat.get(1),"E");
		
		if(x==0.0 && !(mat.get(0).equals(mat.get(1))))
			System.out.println("1.0");
		else
			System.out.println(x);
		//System.out.println(spl11);
		
		
		
	}

}

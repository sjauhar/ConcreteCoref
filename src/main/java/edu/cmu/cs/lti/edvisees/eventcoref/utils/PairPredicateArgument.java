package edu.cmu.cs.lti.edvisees.eventcoref.utils;

public class PairPredicateArgument {
	PredicateArgument pa1;
	PredicateArgument pa2;
	String entireLine;
	
	public String getEntireLine() {
		return entireLine;
	}

	public void setEntireLine(String entireLine) {
		this.entireLine = entireLine;
	}

	public PairPredicateArgument(PredicateArgument pa1, PredicateArgument pa2,
			String entireLine) {
		super();
		this.pa1 = pa1;
		this.pa2 = pa2;
		this.entireLine = entireLine;
	}

	public PairPredicateArgument(PredicateArgument pa1, PredicateArgument pa2) {
		super();
		this.pa1 = pa1;
		this.pa2 = pa2;
	}
	
	public PredicateArgument getPa1() {
		return pa1;
	}
	public void setPa1(PredicateArgument pa1) {
		this.pa1 = pa1;
	}
	public PredicateArgument getPa2() {
		return pa2;
	}
	public void setPa2(PredicateArgument pa2) {
		this.pa2 = pa2;
	}
	
	public static void main(String args[]){
		//Used only for testing
		//Do nothing
	}
}

package edu.cmu.cs.lti.edvisees.eventcoref.utils;

public class GoldArgm {
	
	private String w1;
	private String w2;
	private String rel;
	public GoldArgm(String w1, String w2, String rel) {
		super();
		this.w1 = w1;
		this.w2 = w2;
		this.rel = rel;
	}
	public String getW1() {
		return w1;
	}
	public void setW1(String w1) {
		this.w1 = w1;
	}
	public String getW2() {
		return w2;
	}
	public void setW2(String w2) {
		this.w2 = w2;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
}

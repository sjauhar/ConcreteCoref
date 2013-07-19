package edu.cmu.cs.lti.edvisees.eventcoref.utils;

public class PredicateArgument {
  
  private String action;
  private String agent;
  private String patient;
  private String actionPOS;
  private String agentPOS;
  private String patientPOS;
  private String agentRelation;
  private String patientRelation;
  private String context;
  
  public PredicateArgument(String action, String agent, String patient, String actionPOS, String agentPOS, 
          String patientPOS, String agentRelation, String patientRelation, String context) {
    super();
    this.action = action;
    this.agent = agent;
    this.patient = patient;
    this.actionPOS = actionPOS;
    this.agentPOS = agentPOS;
    this.patientPOS = patientPOS;
    this.agentRelation = agentRelation;
    this.patientRelation = patientRelation;
    this.context = context;
  }

  /**
   * @return the action
   */
  public String getAction() {
    return action;
  }

  /**
   * @param action the action to set
   */
  public void setAction(String action) {
    this.action = action;
  }

  /**
   * @return the agent
   */
  public String getAgent() {
    return agent;
  }

  /**
   * @param agent the agent to set
   */
  public void setAgent(String agent) {
    this.agent = agent;
  }

  /**
   * @return the patient
   */
  public String getPatient() {
    return patient;
  }

  /**
   * @param patient the patient to set
   */
  public void setPatient(String patient) {
    this.patient = patient;
  }

  /**
   * @return the agentRelation
   */
  public String getAgentRelation() {
    return agentRelation;
  }

  /**
   * @param agentRelation the agentRelation to set
   */
  public void setAgentRelation(String agentRelation) {
    this.agentRelation = agentRelation;
  }

  /**
   * @return the patientRelation
   */
  public String getPatientRelation() {
    return patientRelation;
  }

  /**
   * @param patientRelation the patientRelation to set
   */
  public void setPatientRelation(String patientRelation) {
    this.patientRelation = patientRelation;
  }

  /**
   * @return the context
   */
  public String getContext() {
    return context;
  }

  /**
   * @param context the context to set
   */
  public void setContext(String context) {
    this.context = context;
  }

  /**
   * @return the actionPOS
   */
  public String getActionPOS() {
    return actionPOS;
  }

  /**
   * @param actionPOS the actionPOS to set
   */
  public void setActionPOS(String actionPOS) {
    this.actionPOS = actionPOS;
  }

  /**
   * @return the agentPOS
   */
  public String getAgentPOS() {
    return agentPOS;
  }

  /**
   * @param agentPOS the agentPOS to set
   */
  public void setAgentPOS(String agentPOS) {
    this.agentPOS = agentPOS;
  }

  /**
   * @return the patientPOS
   */
  public String getPatientPOS() {
    return patientPOS;
  }

  /**
   * @param patientPOS the patientPOS to set
   */
  public void setPatientPOS(String patientPOS) {
    this.patientPOS = patientPOS;
  }
  
}

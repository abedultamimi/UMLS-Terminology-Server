/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.services.rest;

import java.util.List;

import com.wci.umls.server.UserRole;
import com.wci.umls.server.helpers.ChecklistList;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.helpers.TrackingRecordList;
import com.wci.umls.server.helpers.WorklistList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.worfklow.WorkflowBinDefinitionJpa;
import com.wci.umls.server.jpa.worfklow.WorkflowConfigJpa;
import com.wci.umls.server.jpa.worfklow.WorkflowEpochJpa;
import com.wci.umls.server.model.workflow.Checklist;
import com.wci.umls.server.model.workflow.WorkflowAction;
import com.wci.umls.server.model.workflow.WorkflowBin;
import com.wci.umls.server.model.workflow.WorkflowBinDefinition;
import com.wci.umls.server.model.workflow.WorkflowBinType;
import com.wci.umls.server.model.workflow.WorkflowConfig;
import com.wci.umls.server.model.workflow.WorkflowEpoch;
import com.wci.umls.server.model.workflow.Worklist;

/**
 * Represents a workflow service REST API.
 */
public interface WorkflowServiceRest {

  /**
   * Adds the workflow config.
   *
   * @param projectId the project id
   * @param config the config
   * @param authToken the auth token
   * @return the workflow config
   * @throws Exception the exception
   */
  public WorkflowConfig addWorkflowConfig(Long projectId,
    WorkflowConfigJpa config, String authToken) throws Exception;

  /**
   * Update workflow config.
   *
   * @param projectId the project id
   * @param config the config
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateWorkflowConfig(Long projectId, WorkflowConfigJpa config,
    String authToken) throws Exception;

  /**
   * Removes the workflow config.
   *
   * @param projectId the project id
   * @param id the workflow config id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeWorkflowConfig(Long projectId, Long id, String authToken)
    throws Exception;

  /**
   * Returns the workflow config.
   *
   * @param projectId the project id
   * @param id the workflow config id
   * @param authToken the auth token
   * @return the workflow config
   * @throws Exception the exception
   */
  public WorkflowConfig getWorkflowConfig(Long projectId, Long id,
    String authToken) throws Exception;

  /**
   * Adds the workflow bin definition.
   *
   * @param projectId the project id
   * @param binDefinition the bin definition
   * @param authToken the auth token
   * @return the workflow bin definition
   * @throws Exception the exception
   */
  public WorkflowBinDefinition addWorkflowBinDefinition(Long projectId,
    WorkflowBinDefinitionJpa binDefinition, String authToken) throws Exception;

  /**
   * Update workflow bin definition.
   * @param projectId project id
   * @param definition the definition
   * @param authToken auth token
   *
   * @throws Exception the exception
   */
  public void updateWorkflowBinDefinition(Long projectId,
    WorkflowBinDefinitionJpa definition, String authToken) throws Exception;

  /**
   * Removes the workflow bin definition.
   *
   * @param projectId the project id
   * @param id the workflow bin definition id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeWorkflowBinDefinition(Long projectId, Long id,
    String authToken) throws Exception;

  /**
   * Removes the workflow bin.
   *
   * @param projectId the project id
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeWorkflowBin(Long projectId, Long id, String authToken)
    throws Exception;

  /**
   * Returns the workflow bin definition.
   *
   * @param projectId the project id
   * @param id the workflow bin definition id
   * @param authToken the auth token
   * @return the workflow bin definition
   * @throws Exception the exception
   */
  public WorkflowBinDefinition getWorkflowBinDefinition(Long projectId,
    Long id, String authToken) throws Exception;

  /**
   * Clear bins.
   *
   * @param projectId the project id
   * @param type the type
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void clearBins(Long projectId, WorkflowBinType type, String authToken)
    throws Exception;

  /**
   * Regenerate bins.
   *
   * @param projectId the project id
   * @param type the type
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void regenerateBins(Long projectId, WorkflowBinType type,
    String authToken) throws Exception;

  /**
   * Find assigned work.
   *
   * @param projectId the project id
   * @param userName the user name
   * @param role the role
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tracking record list
   * @throws Exception the exception
   */
  public TrackingRecordList findAssignedWork(Long projectId, String userName,
    UserRole role, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find available work.
   *
   * @param projectId the project id
   * @param role the role
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tracking record list
   * @throws Exception the exception
   */
  public TrackingRecordList findAvailableWork(Long projectId, UserRole role,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find tracking records for checklist.
   *
   * @param projectId the project id
   * @param checklistId the checklist id
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tracking record list
   * @throws Exception the exception
   */
  public TrackingRecordList findTrackingRecordsForChecklist(Long projectId,
    Long checklistId, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find tracking records for worklist.
   *
   * @param projectId the project id
   * @param worklistId the worklist id
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tracking record list
   * @throws Exception the exception
   */
  public TrackingRecordList findTrackingRecordsForWorklist(Long projectId,
    Long worklistId, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find tracking records for workflow bin.
   *
   * @param projectId the project id
   * @param workflowBinId the workflow bin id
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tracking record list
   * @throws Exception the exception
   */
  public TrackingRecordList findTrackingRecordsForWorkflowBin(Long projectId,
    Long workflowBinId, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find assigned worklists.
   *
   * @param projectId the project id
   * @param userName the user name
   * @param role the role
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the worklist list
   * @throws Exception the exception
   */
  public WorklistList findAssignedWorklists(Long projectId, String userName,
    UserRole role, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find checklists.
   *
   * @param projectId the project id
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the checklist list
   * @throws Exception the exception
   */
  public ChecklistList findChecklists(Long projectId, String query,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Returns the workflow paths defined by the supported listeners.
   * 
   * @param authToken the auth token
   * @return the workflow paths
   * @throws Exception the exception
   */
  public StringList getWorkflowPaths(String authToken) throws Exception;

  /**
   * Perform workflow action.
   *
   * @param projectId the project id
   * @param worklistId the worklist id
   * @param userName the user name
   * @param role the role
   * @param action the action
   * @param authToken the auth token
   * @return the tracking record
   * @throws Exception the exception
   */
  public Worklist performWorkflowAction(Long projectId, Long worklistId,
    String userName, UserRole role, WorkflowAction action, String authToken)
    throws Exception;

  /**
   * Find available worklists.
   *
   * @param projectId the project id
   * @param role the role
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the worklist list
   * @throws Exception the exception
   */
  public WorklistList findAvailableWorklists(Long projectId, UserRole role,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Creates the checklist.
   *
   * @param projectId the project id
   * @param workflowBinId the workflow bin id
   * @param name the name
   * @param randomize the randomize
   * @param excludeOnWorklist the exclude on worklist
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the checklist
   * @throws Exception the exception
   */
  public Checklist createChecklist(Long projectId, Long workflowBinId,
    String name, Boolean randomize, Boolean excludeOnWorklist, String query,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Adds the workflow epoch.
   *
   * @param projectId the project id
   * @param epoch the epoch
   * @param authToken the auth token
   * @return the workflow epoch
   * @throws Exception the exception
   */
  public WorkflowEpoch addWorkflowEpoch(Long projectId, WorkflowEpochJpa epoch,
    String authToken) throws Exception;

  /**
   * Removes the workflow epoch.
   *
   * @param projectId the project id
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeWorkflowEpoch(Long projectId, Long id, String authToken)
    throws Exception;

  /**
   * Creates the worklist.
   *
   * @param projectId the project id
   * @param workflowBinId the workflow bin id
   * @param clusterType the cluster type
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the worklist
   * @throws Exception the exception
   */
  public Worklist createWorklist(Long projectId, Long workflowBinId,
    String clusterType, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Removes the worklist.
   *
   * @param projectId the project id
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeWorklist(Long projectId, Long id, String authToken)
    throws Exception;

  /**
   * Removes the checklist.
   *
   * @param projectId the project id
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeChecklist(Long projectId, Long id, String authToken)
    throws Exception;

  /**
   * Find worklists.
   *
   * @param projectId the project id
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the worklist list
   * @throws Exception the exception
   */
  public WorklistList findWorklists(Long projectId, String query,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Returns the workflow bin stats.
   *
   * @param projectId the project id
   * @param type the type
   * @param authToken the auth token
   * @return the workflow bin stats
   * @throws Exception the exception
   */
  public List<WorkflowBin> getWorkflowBins(Long projectId,
    WorkflowBinType type, String authToken) throws Exception;

  /**
   * 
   * @param projectId the project id
   * 
   * @param workflowBinId the workflow bin id
   * 
   * @param authToken the auth token
   * 
   * @throws Exception the exception
   */
  public void clearBin(Long projectId, Long workflowBinId, String authToken)
    throws Exception;

  /**
   * Regenerate bin.
   *
   * @param projectId the project id
   * @param workflowBinId the workflow bin id
   * @param type the type
   * @param authToken the auth token
   * @return the workflow bin
   * @throws Exception the exception
   */
  public WorkflowBin regenerateBin(Long projectId, Long workflowBinId,
    WorkflowBinType type, String authToken) throws Exception;

  /**
   * Generate concept report.
   *
   * @param projectId the project id
   * @param worklistId the worklist id
   * @param delay the delay
   * @param sendEmail the send email
   * @param conceptReportType the concept report type
   * @param relationshipCt the relationship ct
   * @param authToken the auth token
   * @return the string
   * @throws Exception the exception
   */
  public String generateConceptReport(Long projectId, Long worklistId,
    Long delay, Boolean sendEmail, String conceptReportType,
    Integer relationshipCt, String authToken) throws Exception;

  /**
   * Find generated concept reports.
   *
   * @param projectId the project id
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the string list
   * @throws Exception the exception
   */
  public StringList findGeneratedConceptReports(Long projectId, String query,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Returns the generated concept report.
   *
   * @param projectId the project id
   * @param fileName the file name
   * @param authToken the auth token
   * @return the generated concept report
   * @throws Exception the exception
   */
  public String getGeneratedConceptReport(Long projectId, String fileName,
    String authToken) throws Exception;

  /**
   * Removes the generated concept report.
   *
   * @param projectId the project id
   * @param fileName the file name
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeGeneratedConceptReport(Long projectId, String fileName,
    String authToken) throws Exception;

  /**
   * Returns the worklist stats.
   *
   * @param projectId the project id
   * @param worklistId the worklist id
   * @param authToken the auth token
   * @return the worklist stats
   * @throws Exception the exception
   */
  public Worklist getWorklist(Long projectId, Long worklistId, String authToken)
    throws Exception;
}
/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.rest.client;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.helpers.ChecklistList;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.helpers.TrackingRecordList;
import com.wci.umls.server.helpers.WorklistList;
import com.wci.umls.server.jpa.helpers.ChecklistListJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TrackingRecordListJpa;
import com.wci.umls.server.jpa.helpers.WorklistListJpa;
import com.wci.umls.server.jpa.services.rest.WorkflowServiceRest;
import com.wci.umls.server.jpa.worfklow.ChecklistJpa;
import com.wci.umls.server.jpa.worfklow.WorkflowBinDefinitionJpa;
import com.wci.umls.server.jpa.worfklow.WorkflowBinJpa;
import com.wci.umls.server.jpa.worfklow.WorkflowConfigJpa;
import com.wci.umls.server.jpa.worfklow.WorkflowEpochJpa;
import com.wci.umls.server.jpa.worfklow.WorklistJpa;
import com.wci.umls.server.model.workflow.Checklist;
import com.wci.umls.server.model.workflow.WorkflowAction;
import com.wci.umls.server.model.workflow.WorkflowBin;
import com.wci.umls.server.model.workflow.WorkflowBinDefinition;
import com.wci.umls.server.model.workflow.WorkflowBinType;
import com.wci.umls.server.model.workflow.WorkflowConfig;
import com.wci.umls.server.model.workflow.WorkflowEpoch;
import com.wci.umls.server.model.workflow.Worklist;

/**
 * A client for connecting to a content REST service.
 */
public class WorkflowClientRest extends RootClientRest implements
    WorkflowServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link WorkflowClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public WorkflowClientRest(Properties config) {
    this.config = config;
  }

  /* see superclass */
  @Override
  public WorkflowConfig addWorkflowConfig(Long projectId,
    WorkflowConfigJpa workflowConfig, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - add workflow config " + projectId + ", "
            + workflowConfig.toString() + ", " + projectId);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/config/add?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .post(Entity.json(workflowConfig));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        WorkflowConfigJpa.class);
  }

  /* see superclass */
  @Override
  public void updateWorkflowConfig(Long projectId,
    WorkflowConfigJpa workflowConfig, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - update workflow config " + projectId + ", "
            + workflowConfig.toString() + ", " + projectId);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/config/update?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .post(Entity.json(workflowConfig));

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public void removeWorkflowConfig(Long projectId, Long id, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - remove workflow config " + id + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/config/" + id
            + "/remove?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete();

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public WorkflowConfig getWorkflowConfig(Long projectId, Long id,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - get workflow config " + id + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/config/" + id
            + "?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        WorkflowConfigJpa.class);
  }

  /* see superclass */
  @Override
  public void removeWorklist(Long projectId, Long id, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - remove worklist " + id + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/worklist/"
            + id + "/remove?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete();

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public void removeChecklist(Long projectId, Long id, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - remove checklist " + id + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/checklist/"
            + id + "/remove?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete();

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public WorkflowBinDefinition addWorkflowBinDefinition(Long projectId,
    WorkflowBinDefinitionJpa binDefinition, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - add workflow bin definition " + projectId + ", "
            + binDefinition.toString() + ", " + authToken);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/definition/add?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .post(Entity.json(binDefinition));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        WorkflowBinDefinitionJpa.class);
  }

  /* see superclass */
  @Override
  public void updateWorkflowBinDefinition(Long projectId,
    WorkflowBinDefinitionJpa definition, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - update workflow config " + projectId + ", "
            + definition.toString() + ", " + projectId);

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/definition/update?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.json(definition));

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public void removeWorkflowBinDefinition(Long projectId, Long id,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - remove workflow bin definition " + id + ", "
            + authToken);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/definition/"
            + id + "/remove?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete();

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public void removeWorkflowBin(Long projectId, Long id, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - remove workflow bin " + id + ", " + authToken);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/bin/" + id
            + "/remove?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete();

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public WorkflowBinDefinition getWorkflowBinDefinition(Long projectId,
    Long id, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - get workflow bin definition " + id + ", "
            + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/definition/"
            + id + "?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        WorkflowBinDefinitionJpa.class);

  }

  /* see superclass */
  @Override
  public void regenerateBins(Long projectId, WorkflowBinType type,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - regenerate bins " + projectId + ", "
            + type.toString() + ", " + projectId);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/bin/regenerate/all?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.json(type));

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public TrackingRecordList findAssignedWork(Long projectId, String userName,
    UserRole role, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass())
        .debug(
            "Workflow Client - find assigned work - " + projectId + ", "
                + userName);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(userName, "userName");
    validateNotEmpty(role + "", "role");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/record/assigned" + "?projectId=" + projectId
            + "&userName=" + userName + "&role=" + role);
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        TrackingRecordListJpa.class);
  }

  /* see superclass */
  @Override
  public TrackingRecordList findTrackingRecordsForChecklist(Long projectId,
    Long id, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find tracking records for checklist " + id + ", "
            + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/checklist/"
            + id + "/records?projectId=" + projectId);
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        TrackingRecordListJpa.class);
  }

  /* see superclass */
  @Override
  public TrackingRecordList findTrackingRecordsForWorklist(Long projectId,
    Long id, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find tracking records for worklist " + id + ", "
            + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/worklist/"
            + id + "/records?projectId=" + projectId);
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        TrackingRecordListJpa.class);
  }

  /* see superclass */
  @Override
  public TrackingRecordList findTrackingRecordsForWorkflowBin(Long projectId,
    Long id, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find tracking records for workflow bin " + id + ", "
            + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "id");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/bin/" + id
            + "/records?projectId=" + projectId);
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        TrackingRecordListJpa.class);
  }

  /* see superclass */
  @Override
  public WorklistList findAssignedWorklists(Long projectId, String userName,
    UserRole role, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find assigned worklists - " + projectId + ", "
            + userName);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(userName, "userName");
    validateNotEmpty(role + "", "role");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/worklist/assigned" + "?projectId=" + projectId
            + "&userName=" + userName + "&role=" + role);
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, WorklistListJpa.class);
  }

  /* see superclass */
  @Override
  public ChecklistList findChecklists(Long projectId, String query,
    PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find checklists - " + projectId + ", " + query);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/checklist"
            + "?projectId="
            + projectId
            + "&query="
            + URLEncoder.encode(query == null ? "" : query, "UTF-8")
                .replaceAll("\\+", "%20"));
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility
        .getGraphForString(resultString, ChecklistListJpa.class);
  }

  /* see superclass */
  @Override
  public WorklistList findWorklists(Long projectId, String query,
    PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find worklists - " + projectId + ", " + query);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/worklist"
            + "?projectId="
            + projectId
            + "&query="
            + URLEncoder.encode(query == null ? "" : query, "UTF-8")
                .replaceAll("\\+", "%20"));
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, WorklistListJpa.class);
  }

  /* see superclass */
  @Override
  public StringList getWorkflowPaths(String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("Workflow Client - get workflow paths");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/paths");

    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, StringList.class);
  }

  /* see superclass */
  @Override
  public Worklist performWorkflowAction(Long projectId, Long worklistId,
    String userName, UserRole role, WorkflowAction action, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - perform workflow action " + projectId + ", "
            + userName + ", " + action);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(worklistId, "worklistId");
    validateNotEmpty(userName, "userName");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/worklist/action" + "?projectId=" + projectId
            + "&worklistId=" + worklistId + "&action=" + action + "&userName="
            + userName + "&userRole=" + role);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, WorklistJpa.class);
  }

  /* see superclass */
  @Override
  public TrackingRecordList findAvailableWork(Long projectId, UserRole role,
    PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find available work - " + projectId + ", " + role);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(role.getValue(), "role");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/record/available" + "?projectId=" + projectId
            + "&userRole=" + role);
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString,
        TrackingRecordListJpa.class);
  }

  /* see superclass */
  @Override
  public WorklistList findAvailableWorklists(Long projectId, UserRole role,
    PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find available worklists - " + projectId + ", "
            + role);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(role.getValue(), "role");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/worklist/available" + "?projectId=" + projectId
            + "&userRole=" + role);
    final String pfsStr =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.xml(pfsStr));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, WorklistListJpa.class);
  }

  /* see superclass */
  @Override
  public void clearBins(Long projectId, WorkflowBinType type, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - clear bins " + projectId + ", " + type.toString()
            + ", " + projectId);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/bin/clear/all?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.json(type));

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public Checklist createChecklist(Long projectId, Long workflowBinId,
    String name, Boolean randomize, Boolean excludeOnWorklist, String query,
    PfsParameterJpa pfs, String authToken) throws Exception {

    Logger.getLogger(getClass()).debug(
        "Workflow Client - create checklist " + projectId + ", " + workflowBinId
            + ", " + name + ", " + randomize + ", " + excludeOnWorklist + ", "
            + query + ", " + projectId);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/checklist/add?projectId="
            + projectId
            + "&workflowBinId="
            + workflowBinId
            + "&name="
            + name
            + (randomize != null ? ("&randomize=" + randomize) : "")
            + (excludeOnWorklist != null
                ? ("&excludeOnWorklist=" + excludeOnWorklist) : "")
            + "&query="
            + URLEncoder.encode(query == null ? "" : query, "UTF-8")
                .replaceAll("\\+", "%20"));

    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.json(pfs));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, ChecklistJpa.class);
  }

  /* see superclass */
  @Override
  public WorkflowEpoch addWorkflowEpoch(Long projectId, WorkflowEpochJpa epoch,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - add workflow epoch " + projectId + ", "
            + epoch.toString() + ", " + projectId);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/epoch/add?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.json(epoch));

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    return ConfigUtility
        .getGraphForString(resultString, WorkflowEpochJpa.class);
  }

  /* see superclass */
  @Override
  public void removeWorkflowEpoch(Long projectId, Long id, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - remove workflow epoch " + id + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(id, "epochId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/epoch/" + id
            + "/remove?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete();

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public Worklist createWorklist(Long projectId, Long workflowBinId,
    String clusterType, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - create worklist " + projectId + ", " + workflowBinId
            + ", " + clusterType + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(workflowBinId, "workflowBinId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/worklist/add?projectId=" + projectId
            + "&workflowBinId=" + workflowBinId
            + (clusterType == null ? "" : "&clusterType=" + clusterType));
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.json(pfs));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, WorklistJpa.class);
  }

  /* see superclass */
  @Override
  public List<WorkflowBin> getWorkflowBins(Long projectId,
    WorkflowBinType type, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - get workflow bins " + projectId + ", " + type);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/bin/all?projectId=" + projectId + "&type=" + type);
    final Response response =
        target.request(MediaType.APPLICATION_JSON)
            .header("Authorization", authToken).get();

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    final List<WorkflowBinJpa> list =
        ConfigUtility.getGraphForJson(resultString,
            new TypeReference<List<WorkflowBinJpa>>() {
              // n/a
            });
    return new ArrayList<WorkflowBin>(list);
  }

  /* see superclass */
  @Override
  public Worklist getWorklist(Long projectId, Long worklistId, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - get worklist " + projectId + ", " + worklistId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(worklistId, "worklistId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/worklist/"
            + worklistId + "?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, WorklistJpa.class);
  }

  /* see superclass */
  @Override
  public void clearBin(Long projectId, Long workflowBinId, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - clear bin " + projectId + ", " + workflowBinId
            + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(workflowBinId, "workflowBinId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/bin/"
            + workflowBinId + "/clear?projectId=" + projectId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

  }

  /* see superclass */
  @Override
  public WorkflowBin regenerateBin(Long projectId, Long workflowBinId,
    WorkflowBinType type, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - regenerate bin " + projectId + ", " + workflowBinId
            + ", " + projectId);

    validateNotEmpty(projectId, "projectId");
    validateNotEmpty(workflowBinId, "workflowBinId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/bin/"
            + workflowBinId + "/regenerate?projectId=" + projectId
            + "&workflowBinId=" + workflowBinId);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();
    final String resultString = response.readEntity(String.class);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, WorkflowBinJpa.class);
  }

  /* see superclass */
  @Override
  public String generateConceptReport(Long projectId, Long worklistId,
    Long delay, Boolean sendEmail, String conceptReportType,
    Integer relationshipCt, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - generate concept report " + projectId + ", "
            + worklistId + ", " + sendEmail + ", " + conceptReportType + ", "
            + relationshipCt + ", " + projectId);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/worklist/"
            + worklistId + "/report/generate?projectId=" + projectId
            + "&worklistId=" + worklistId + "&delay=" + delay
            + (sendEmail != null ? ("&sendEmail=" + sendEmail) : "")
            + "&conceptReportType=" + conceptReportType + "&relationshipCt="
            + relationshipCt);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    return resultString;
  }

  /* see superclass */
  @Override
  public String getGeneratedConceptReport(Long projectId, String fileName,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - get generated concept report " + projectId + ", "
            + fileName);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/report/"
            + fileName + "?projectId=" + projectId + "&fileName=" + fileName);
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get();

    if (response.getStatus() == 204) {
      return null;
    }

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    return resultString;
  }

  /* see superclass */
  @Override
  public void removeGeneratedConceptReport(Long projectId, String fileName,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - remove generated concept report " + projectId);

    validateNotEmpty(projectId, "project id");
    validateNotEmpty(fileName, "fileName");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url") + "/workflow/report/"
            + URLEncoder.encode(fileName, "UTF-8") + "/remove?projectId="
            + projectId);

    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete();
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception(response.toString());
    }
  }

  /* see superclass */
  @Override
  public StringList findGeneratedConceptReports(Long projectId, String query,
    PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Workflow Client - find generated concept reports " + projectId + ", "
            + query);

    validateNotEmpty(projectId, "projectId");

    final Client client = ClientBuilder.newClient();
    final WebTarget target =
        client.target(config.getProperty("base.url")
            + "/workflow/report?projectId="
            + projectId
            + "&query="
            + URLEncoder.encode(query == null ? "" : query, "UTF-8")
                .replaceAll("\\+", "%20"));
    final Response response =
        target.request(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(Entity.json(pfs));

    final String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    return ConfigUtility.getGraphForString(resultString, StringList.class);
  }

}
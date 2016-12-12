/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.insert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractSourceInsertionAlgorithm;
import com.wci.umls.server.jpa.content.AtomRelationshipJpa;
import com.wci.umls.server.jpa.content.CodeRelationshipJpa;
import com.wci.umls.server.jpa.content.ComponentInfoRelationshipJpa;
import com.wci.umls.server.jpa.content.ConceptRelationshipJpa;
import com.wci.umls.server.jpa.content.DescriptorRelationshipJpa;
import com.wci.umls.server.model.content.Component;
import com.wci.umls.server.model.content.Relationship;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.handlers.IdentifierAssignmentHandler;

/**
 * Implementation of an algorithm to import relationships.
 */
public class RelationshipLoaderAlgorithm
    extends AbstractSourceInsertionAlgorithm {

  /** The handler. */
  private IdentifierAssignmentHandler handler = null;

  /** The add count. */
  private int addCount = 0;

  /** The update count. */
  private int updateCount = 0;

  /**
   * Instantiates an empty {@link RelationshipLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public RelationshipLoaderAlgorithm() throws Exception {
    super();
    setActivityId(UUID.randomUUID().toString());
    setWorkId("RELATIONSHIPLOADER");
    setLastModifiedBy("admin");
  }

  /**
   * Check preconditions.
   *
   * @return the validation result
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public ValidationResult checkPreconditions() throws Exception {

    ValidationResult validationResult = new ValidationResultJpa();

    if (getProject() == null) {
      throw new Exception("Relationship Loading requires a project to be set");
    }

    // Check the input directories

    String srcFullPath =
        ConfigUtility.getConfigProperties().getProperty("source.data.dir")
            + File.separator + getProcess().getInputPath();

    setSrcDirFile(new File(srcFullPath));
    if (!getSrcDirFile().exists()) {
      throw new Exception("Specified input directory does not exist");
    }

    return validationResult;
  }

  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void compute() throws Exception {
    logInfo("Starting RELATIONSHIPLOADING");

    // No molecular actions will be generated by this algorithm
    setMolecularActionFlag(false);

    // Set up the handler for identifier assignment
    handler = newIdentifierAssignmentHandler(getProject().getTerminology());
    handler.setTransactionPerOperation(false);
    handler.beginTransaction();

    // Count number of added and updated Relationships, for logging
    addCount = 0;
    updateCount = 0;

    try {

      logInfo("[RelationshipLoader] Checking for new/updated Relationships");

      //
      // Load the relationships.src file
      //
      List<String> lines = loadFileIntoStringList(getSrcDirFile(),
          "relationships.src", null, null);

      //
      // Load the contexts.src file
      //
      // Only keep "PAR" relationship rows.
      List<String> lines2 = loadFileIntoStringList(getSrcDirFile(),
          "contexts.src", "[0-9]+?\\|PAR(.*)", null);

      // There will be many duplicated lines in the contexts.src file, since the
      // main
      // distinguishing field "parent_treenum" is ignored for these purposes.
      // Remove the dups.
      lines = removeDups(lines);

      // Set the number of steps to the number of relationships to be processed
      setSteps(lines.size() + lines2.size());

      //
      // Process relationships.src lines
      //
      String fields[] = new String[18];
      for (String line : lines) {

        // Check for a cancelled call once every 100 lines
        if (getStepsCompleted() % 100 == 0) {
          checkCancel();
        }

        FieldedStringTokenizer.split(line, "|", 18, fields);

        // Fields:
        // 0 src_relationship_id (Not used)
        // 1 level
        // 2 id_1
        // 3 relationship_name
        // 4 relationship_attribute
        // 5 id_2
        // 6 source
        // 7 source_of_label
        // 8 status
        // 9 tobereleased
        // 10 released
        // 11 suppressible
        // 12 id_type_1
        // 13 id_qualifier_1
        // 14 id_type_2
        // 15 id_qualifier_2
        // 16 source_rui
        // 17 relationship_group

        // e.g.
        // 40|S|C17260|RT|Gene_Plays_Role_In_Process|C29949|NCI_2016_05E|
        // NCI_2016_05E|R|Y|N|N|SOURCE_CUI|NCI_2016_05E|SOURCE_CUI|NCI_2016_05E|||

        //
        // Relationship based on input line.
        //

        final String fromTermId = fields[5];
        final String fromTermAndVersion = fields[15];
        final String fromClassIdType = fields[14];
        final String toTermId = fields[2];
        final String toTermAndVersion = fields[13];
        final String toClassIdType = fields[12];
        final String additionalRelType = fields[4];
        final String group = fields[17];
        final String publishable = fields[9];
        final String published = fields[10];
        final String relType = fields[3];
        final String suppresible = fields[11];
        final String sourceTermAndVersion = fields[6];
        final String sourceTermId = fields[16];
        final String workflowStatusStr = fields[8];

        handleRelationships(line, fromTermId, fromTermAndVersion,
            fromClassIdType, toTermId, toTermAndVersion, toClassIdType,
            additionalRelType, group, publishable, published, relType,
            suppresible, sourceTermAndVersion, sourceTermId, workflowStatusStr,
            false);
      }

      //
      // Process contexts.src lines
      //
      String fields2[] = new String[17];
      for (final String line : lines2) {

        // Check for a cancelled call once every 100 relationships (doing it
        // every time
        // makes things too slow)
        if (getStepsCompleted() % 100 == 0) {
          checkCancel();
        }

        FieldedStringTokenizer.split(line, "|", 17, fields2);

        // Fields:
        // 0 source_atom_id_1
        // 1 relationship_name
        // 2 relationship_attribute
        // 3 source_atom_id_2
        // 4 source
        // 5 source_of_label
        // 6 hcd
        // 7 parent_treenum
        // 8 release mode
        // 9 source_rui
        // 10 relationship_group
        // 11 sg_id_1
        // 12 sg_type_1
        // 13 sg_qualifier_1
        // 14 sg_id_2
        // 15 sg_type_2
        // 16 sg_qualifier_2

        // e.g.
        // 362241646|PAR|isa|362239326|NCI_2016_05E|NCI_2016_05E||
        // 31926003.362204588.362250568.362172407.362239326|00|||C90893|
        // SOURCE_CUI|NCI_2016_05E|C29696|SOURCE_CUI|NCI_2016_05E|

        //
        // Relationship based on input line.
        //

        final String fromTermId = fields2[11];
        final String fromTermAndVersion = fields2[13];
        final String fromClassIdType = fields2[12];
        final String toTermId = fields2[14];
        final String toTermAndVersion = fields2[16];
        final String toClassIdType = fields2[15];
        final String additionalRelType = fields2[2];
        final String group = fields2[10];
        final String publishable = "Y";
        final String published = "N";
        // Note: relType and additionalRelType are swapped in file. We're only
        // keeping "PAR" rows, so we hard-code relType as "CHD"
        final String relType = "CHD";
        final String suppresible = "N";
        final String sourceTermAndVersion = fields2[4];
        final String sourceTermId = fields2[9];
        final String workflowStatusStr = "R";

        handleRelationships(line, fromTermId, fromTermAndVersion,
            fromClassIdType, toTermId, toTermAndVersion, toClassIdType,
            additionalRelType, group, publishable, published, relType,
            suppresible, sourceTermAndVersion, sourceTermId, workflowStatusStr,
            true);

      }

      commitClearBegin();
      handler.commitClearBegin();

      logInfo("[RelationshipLoader] Added " + addCount + " new Relationships.");
      logInfo("[RelationshipLoader] Updated " + updateCount
          + " existing Relationships.");

      logInfo("  project = " + getProject().getId());
      logInfo("  workId = " + getWorkId());
      logInfo("  activityId = " + getActivityId());
      logInfo("  user  = " + getLastModifiedBy());
      logInfo("Finished RELATIONSHIPLOADING");

    } catch (

    Exception e) {
      logError("Unexpected problem - " + e.getMessage());
      throw e;
    }

  }

  /**
   * Removes the dups.
   *
   * @param lineList the line list
   * @return the list
   */
  @SuppressWarnings("static-method")
  private List<String> removeDups(List<String> lineList) {
    // Make a set of the rela, ID1, and ID2, so you don't create duplicate
    // relationships.
    Set<String> seenLines = new HashSet<>();
    List<String> lines = new ArrayList<>();

    String fields[] = new String[17];

    for (String line : lineList) {

      FieldedStringTokenizer.split(line, "|", 17, fields);
      String concatedFields = fields[2] + fields[14] + fields[14];

      if (!seenLines.contains(concatedFields)) {
        lines.add(line);
        seenLines.add(concatedFields);
      }
    }

    return lines;
  }

  /**
   * Handle relationships.
   *
   * @param line the line
   * @param fromTermId the from term id
   * @param fromTermAndVersion the from term and version
   * @param fromClassIdType the from class id type
   * @param toTermId the to term id
   * @param toTermAndVersion the to term and version
   * @param toClassIdType the to class id type
   * @param additionalRelType the additional rel type
   * @param group the group
   * @param publishable the publishable
   * @param published the published
   * @param relType the rel type
   * @param suppresible the suppresible
   * @param sourceTermAndVersion the source term and version
   * @param sourceTermId the source term id
   * @param workflowStatusStr the workflow status str
   * @param fromContextsSrcFile the from contexts src file
   * @throws Exception the exception
   */
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  private void handleRelationships(String line, String fromTermId,
    String fromTermAndVersion, String fromClassIdType, String toTermId,
    String toTermAndVersion, String toClassIdType, String additionalRelType,
    String group, String publishable, String published, String relType,
    String suppresible, String sourceTermAndVersion, String sourceTermId,
    String workflowStatusStr, Boolean fromContextsSrcFile) throws Exception {

    // For the contexts.src file relationships only, if to and from ClassTypes
    // don't match, fire a
    // warning and skip the line.
    if (fromContextsSrcFile && !fromClassIdType.equals(toClassIdType)) {
      logWarnAndUpdate(line, "Warning - type 1: " + fromClassIdType
          + " does not equals type 2: " + toClassIdType + ".");
      return;
    }

    // Load the containing objects based on type
    Component fromComponent = getComponent(fromClassIdType, fromTermId,
        getCachedTerminology(fromTermAndVersion).getTerminology(), null);
    if (fromComponent == null) {
      logWarnAndUpdate(line,
          "Warning - could not find from Component for this line.");
      return;
    }

    Component toComponent = getComponent(toClassIdType, toTermId,
        getCachedTerminology(toTermAndVersion).getTerminology(), null);
    if (toComponent == null) {
      logWarnAndUpdate(line,
          "Warning - could not find to Component for this line.");
      return;
    }

    // For the contexts.src file relationships only, if either
    // the from or to component has a terminology = 'SRC', skip it.
    if (fromContextsSrcFile && (toComponent.getTerminology().equals("SRC")
        || fromComponent.getTerminology().equals("SRC"))) {
      updateProgress();
      return;
    }

    // Create the relationship.
    // If id_type_1 equals id_type_2, the relationship is of that type.
    // If they are not equal, it's a Component Info Relationship
    Relationship newRelationship = null;
    Class relClass = null;

    if (!fromClassIdType.equals(toClassIdType)) {
      relClass = ComponentInfoRelationshipJpa.class;
      newRelationship = new ComponentInfoRelationshipJpa();
    } else if (fromClassIdType.equals("SOURCE_CUI")) {
      relClass = ConceptRelationshipJpa.class;
      newRelationship = new ConceptRelationshipJpa();
    } else if (fromClassIdType.equals("SOURCE_DUI")) {
      relClass = DescriptorRelationshipJpa.class;
      newRelationship = new DescriptorRelationshipJpa();
    } else if (fromClassIdType.equals("CODE_SOURCE")) {
      relClass = CodeRelationshipJpa.class;
      newRelationship = new CodeRelationshipJpa();
    } else if (fromClassIdType.equals("SRC_ATOM_ID")) {
      relClass = AtomRelationshipJpa.class;
      newRelationship = new AtomRelationshipJpa();
    } else {
      throw new Exception("Error - unhandled class type: " + fromClassIdType);
    }

    newRelationship.setAdditionalRelationshipType(additionalRelType);
    newRelationship.setBranch(Branch.ROOT);
    newRelationship.setFrom(fromComponent);
    newRelationship.setGroup(group);
    newRelationship.setObsolete(false);
    newRelationship.setPublishable(publishable.equals("Y"));
    newRelationship.setPublished(published.equals("Y"));
    newRelationship.setRelationshipType(lookupRelationshipType(relType));
    // When creating "CHD" relationship, set the "hierarchical" field
    // to true.
    newRelationship.setHierarchical(
        newRelationship.getRelationshipType().equals("CHD") ? true : false);
    newRelationship.setStated(true);
    newRelationship.setInferred(true);
    newRelationship.setSuppressible(suppresible.equals("Y"));
    Terminology term = getCachedTerminology(sourceTermAndVersion);
    if (term == null) {
      throw new Exception("ERROR: lookup for " + sourceTermAndVersion
          + " returned no terminology");
    } else {
      newRelationship.setAssertedDirection(term.isAssertsRelDirection());
      newRelationship.setTerminology(term.getTerminology());
      newRelationship.setVersion(term.getVersion());
    }
    newRelationship.setTerminologyId(sourceTermId);
    newRelationship.setTo(toComponent);
    newRelationship.setWorkflowStatus(lookupWorkflowStatus(workflowStatusStr));

    // Calculate inverseRel and inverseAdditionalRel types, to use in the
    // RUI handler and the inverse relationship creation
    String inverseRelType =
        getRelationshipType(newRelationship.getRelationshipType(),
            getProject().getTerminology(), getProject().getVersion())
                .getInverse().getAbbreviation();

    String inverseAdditionalRelType = "";
    if (!newRelationship.getAdditionalRelationshipType().equals("")) {
      inverseAdditionalRelType = getAdditionalRelationshipType(
          newRelationship.getAdditionalRelationshipType(),
          getProject().getTerminology(), getProject().getVersion()).getInverse()
              .getAbbreviation();
    }

    // Create the inverse relationship
    Relationship newInverseRelationship =
        newRelationship.createInverseRelationship(newRelationship,
            inverseRelType, inverseAdditionalRelType);

    // Compute identity for relationship and its inverse
    // Note: need to pass in the inverse RelType and AdditionalRelType
    String newRelationshipRui = handler.getTerminologyId(newRelationship,
        inverseRelType, inverseAdditionalRelType);
    String newInverseRelationshipRui = handler.getTerminologyId(
        newInverseRelationship, newRelationship.getRelationshipType(),
        newRelationship.getAdditionalRelationshipType());

    // Check to see if relationship with matching RUI already exists in the
    // database
    Relationship oldRelationship = (Relationship) getComponent("RUI",
        newRelationshipRui,
        getCachedTerminology(sourceTermAndVersion).getTerminology(), relClass);
    Relationship oldInverseRelationship = (Relationship) getComponent("RUI",
        newInverseRelationshipRui,
        getCachedTerminology(sourceTermAndVersion).getTerminology(), relClass);

    // If no relationships with the same RUI exists, add this new
    // relationship
    if (oldRelationship == null) {
      newRelationship.getAlternateTerminologyIds()
          .put(getProject().getTerminology(), newRelationshipRui);
      newRelationship = addRelationship(newRelationship);

      addCount++;
      putComponent(newRelationship, newRelationshipRui);

      // No need to explicitly attach to component - will be done
      // automatically by addRelationship.

    }
    // If an existing relationship DOES exist, update it
    else {
      boolean oldRelChanged = false;

      // Update "alternateTerminologyIds" for the relationship
      if (!oldRelationship.getAlternateTerminologyIds()
          .containsKey(getProject().getTerminology())) {
        oldRelationship.getAlternateTerminologyIds()
            .put(getProject().getTerminology(), newRelationshipRui);
        oldRelChanged = true;
      }

      // Update the version
      if (!oldRelationship.getVersion().equals(newRelationship.getVersion())) {
        oldRelationship.setVersion(newRelationship.getVersion());
        oldRelChanged = true;
      }

      // If the existing relationship doesn't exactly equal the new one,
      // update obsolete, suppressible, and group as well
      if (!oldRelationship.equals(newRelationship)) {
        if (oldRelationship.isObsolete() != newRelationship.isObsolete()) {
          oldRelationship.setObsolete(newRelationship.isObsolete());
          oldRelChanged = true;
        }
        if (oldRelationship.isSuppressible() != newRelationship
            .isSuppressible()) {
          oldRelationship.setSuppressible(newRelationship.isSuppressible());
          oldRelChanged = true;
        }
        if (!oldRelationship.getGroup().equals(newRelationship.getGroup())) {
          oldRelationship.setGroup(newRelationship.getGroup());
          oldRelChanged = true;
        }
      }

      if (oldRelChanged) {
        updateCount++;
        updateRelationship(oldRelationship);
      }
    }

    // If no inverse relationships with the same RUI exists, add the new
    // inverse relationship
    if (oldInverseRelationship == null) {
      newInverseRelationship.getAlternateTerminologyIds()
          .put(getProject().getTerminology(), newInverseRelationshipRui);
      newInverseRelationship = addRelationship(newInverseRelationship);

      addCount++;
      putComponent(newInverseRelationship, newInverseRelationshipRui);

      // No need to explicitly attach to component - will be done
      // automatically by addRelationship.

    }
    // If an existing inverse relationship DOES exist, update it
    else {
      boolean oldInverseRelChanged = false;

      // Update "alternateTerminologyIds" for the atom
      if (!oldInverseRelationship.getAlternateTerminologyIds()
          .containsKey(getProject().getTerminology())) {
        oldInverseRelationship.getAlternateTerminologyIds()
            .put(getProject().getTerminology(), newInverseRelationshipRui);
        oldInverseRelChanged = true;
      }

      // Update the version
      if (!oldInverseRelationship.getVersion()
          .equals(newInverseRelationship.getVersion())) {
        oldInverseRelationship.setVersion(newInverseRelationship.getVersion());
        oldInverseRelChanged = true;
      }

      // If the existing inverse relationship doesn't exactly equal the new
      // one,
      // update obsolete, suppressible, and group as well
      if (!oldInverseRelationship.equals(newInverseRelationship)) {
        if (oldInverseRelationship.isObsolete() != newInverseRelationship
            .isObsolete()) {
          oldInverseRelationship
              .setObsolete(newInverseRelationship.isObsolete());
          oldInverseRelChanged = true;
        }
        if (oldInverseRelationship.isSuppressible() != newInverseRelationship
            .isSuppressible()) {
          oldInverseRelationship
              .setSuppressible(newInverseRelationship.isSuppressible());
          oldInverseRelChanged = true;
        }
        if (!oldInverseRelationship.getGroup()
            .equals(newInverseRelationship.getGroup())) {
          oldInverseRelationship.setGroup(newInverseRelationship.getGroup());
          oldInverseRelChanged = true;
        }
      }

      if (oldInverseRelChanged) {
        updateCount++;
        updateRelationship(oldInverseRelationship);
      }
    }

    // Update the progress
    updateProgress();
    handler.silentIntervalCommit(getStepsCompleted(), RootService.logCt,
        RootService.commitCt);

  }

  /**
   * Reset.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void reset() throws Exception {
    // n/a - No reset
  }

  /* see superclass */
  @Override
  public void checkProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /**
   * Returns the parameters.
   *
   * @return the parameters
   */
  /* see superclass */
  @Override
  public List<AlgorithmParameter> getParameters() {
    final List<AlgorithmParameter> params = super.getParameters();

    return params;
  }

  @Override
  public String getDescription() {
    return "Loads and processes relationships.src";
  }

}
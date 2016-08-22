/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.content.AtomJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.content.ConceptRelationshipJpa;
import com.wci.umls.server.jpa.content.SemanticTypeComponentJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.ConceptRelationship;
import com.wci.umls.server.model.content.SemanticTypeComponent;
import com.wci.umls.server.model.meta.RelationshipType;
import com.wci.umls.server.model.workflow.WorkflowStatus;

/**
 * A molecular action for splitting a concept into two concepts.
 */
public class SplitMolecularAction extends AbstractMolecularAction {

  /** The atom ids. */
  private List<Long> atomIds;

  /** The move atoms. */
  private List<Atom> moveAtoms;

  /** The copy semantic types. */
  private boolean copySemanticTypes;

  /** The copy relationships. */
  private boolean copyRelationships;

  /** The relationship type abbr. */
  private String relationshipType;

  /** The originating concept pre updates. */
  private Concept originatingConceptPreUpdates;

  /** The originating concept post updates. */
  private Concept originatingConceptPostUpdates;

  /** The created concept post updates. */
  private Concept createdConceptPostUpdates;

  /** The created concept. */
  private Concept createdConcept;

  /**
   * Instantiates an empty {@link SplitMolecularAction}.
   *
   * @throws Exception the exception
   */
  public SplitMolecularAction() throws Exception {
    super();
    // n/a
  }

  /**
   * Sets the atom ids.
   *
   * @param atomIds the atom ids
   */
  public void setAtomIds(List<Long> atomIds) {
    this.atomIds = atomIds;
  }

  /**
   * Sets the copy semantic types.
   *
   * @param copySemanticTypes the copy semantic types
   */
  public void setCopySemanticTypes(boolean copySemanticTypes) {
    this.copySemanticTypes = copySemanticTypes;
  }

  /**
   * Sets the copy relationships.
   *
   * @param copyRelationships the copy relationships
   */
  public void setCopyRelationships(boolean copyRelationships) {
    this.copyRelationships = copyRelationships;
  }

  /**
   * Sets the relationship type
   *
   * @param relationshipType the relationship type
   */
  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
  }

  /**
   * Returns the originating concept.
   *
   * @return the originating concept
   */
  public Concept getFromConcept() {
    return getConcept();
  }

  /**
   * Returns the created concept.
   *
   * @return the created concept
   */
  public Concept getToConcept() {
    return createdConcept;
  }

  /**
   * Returns the originating concept pre updates.
   *
   * @return the originating concept pre updates
   */
  public Concept getOriginatingConceptPreUpdates() {
    return originatingConceptPreUpdates;
  }

  /**
   * Returns the originating concept post updates.
   *
   * @return the originating concept post updates
   */
  public Concept getOriginatingConceptPostUpdates() {
    return originatingConceptPostUpdates;
  }

  /**
   * Returns the created concept post updates.
   *
   * @return the created concept post updates
   */
  public Concept getCreatedConceptPostUpdates() {
    return createdConceptPostUpdates;
  }

  /* see superclass */
  @Override
  public ValidationResult checkPreconditions() throws Exception {
    final ValidationResult validationResult = new ValidationResultJpa();
    // Perform action specific validation - n/a

    // Metadata referential integrity checking

    // Populate move-atom list, and exists check
    moveAtoms = new ArrayList<Atom>();
    for (final Atom atm : getFromConcept().getAtoms()) {
      if (atomIds.contains(atm.getId())) {
        moveAtoms.add(atm);
      }
    }
    if (!(moveAtoms.size() == atomIds.size())) {
      throw new LocalException("Atom to split out not found in Concept");
    }

    // Exists check on relationship Type, and populate
    final RelationshipType type =
        getRelationshipType(relationshipType, getTerminology(), getVersion());

    if (type == null) {
      throw new LocalException(
          "RelationshipType " + relationshipType + " not found.");
    }

    // Check preconditions
    validationResult.merge(super.checkPreconditions());

    return validationResult;
  }

  /* see superclass */
  @Override
  public void compute() throws Exception {
    //
    // Perform the action (contentService will create atomic actions for CRUD
    // operations)
    //

    // Make copy of originating Concept before changes, to pass into
    // change event
    originatingConceptPreUpdates =
        new ConceptJpa(getFromConcept(), false);

    //
    // Make a copy of each object in the originating Concept to be moved
    //
    List<Atom> moveAtomsCopies = new ArrayList<>();
    for (final Atom atom : moveAtoms) {
      moveAtomsCopies.add(new AtomJpa(atom));
    }

    List<SemanticTypeComponent> fromStysCopies = new ArrayList<>();
    if (copySemanticTypes) {
      for (final SemanticTypeComponent sty : getFromConcept()
          .getSemanticTypes()) {
        fromStysCopies.add(new SemanticTypeComponentJpa(sty));
      }
    }

    List<ConceptRelationship> fromRelationshipsCopies = new ArrayList<>();
    List<ConceptRelationship> inverseRelationshipsCopies = new ArrayList<>();
    if (copyRelationships) {
      for (final ConceptRelationship rel : getFromConcept()
          .getRelationships()) {
        fromRelationshipsCopies.add(new ConceptRelationshipJpa(rel, false));
      }
      // Also make copies of the inverse relationships
      for (final ConceptRelationship rel : getFromConcept()
          .getRelationships()) {
        inverseRelationshipsCopies.add(new ConceptRelationshipJpa(
            (ConceptRelationship) findInverseRelationship(rel), false));
      }
    }

    //
    // Remove objects from the Concept
    //
    // Only done for atoms - semantic types and relationships are kept in
    // originating Concept
    for (final Atom atom : moveAtomsCopies) {
      getFromConcept().getAtoms().remove(atom);
    }

    //
    // Update originatingConcept
    //
    updateConcept(getFromConcept());

    //
    // Remove the objects from the database
    //
    // Not done for Atoms

    //
    // Create and add the new concept
    //
    createdConcept = new ConceptJpa(getConcept(), false);
    getToConcept().setId(null);
    getToConcept().setTerminologyId("");
    createdConcept = new ConceptJpa(addConcept(createdConcept), false);
    getToConcept().setTerminologyId(createdConcept.getId().toString());

    // Add newly created concept and conceptId to the molecular action (undo
    // action uses
    // this)
    getMolecularAction().setComponentId2(getToConcept().getId());
    updateMolecularAction(getMolecularAction());

    //
    // Prepare copies of component objects to be created into new objects
    //
    for (SemanticTypeComponent sty : fromStysCopies) {
      sty.setId(null);
    }
    for (final ConceptRelationship rel : fromRelationshipsCopies) {
      rel.setId(null);
      rel.setFrom(getToConcept());
    }
    for (final ConceptRelationship rel : inverseRelationshipsCopies) {
      rel.setId(null);
      rel.setTo(getToConcept());
    }

    //
    // Change status of the components to be added
    //
    if (getChangeStatusFlag()) {
      for (final Atom atom : moveAtomsCopies) {
        atom.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
      }
      for (SemanticTypeComponent sty : fromStysCopies) {
        sty.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
      }
      for (final ConceptRelationship rel : fromRelationshipsCopies) {
        rel.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
      }
      for (final ConceptRelationship rel : inverseRelationshipsCopies) {
        rel.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
      }
    }

    //
    // construct relationship between originating and created relationship
    //
    ConceptRelationship newBetweenRel = new ConceptRelationshipJpa();
    newBetweenRel.setBranch(Branch.ROOT);
    newBetweenRel.setRelationshipType(relationshipType);
    newBetweenRel.setAdditionalRelationshipType("");
    newBetweenRel.setFrom(getFromConcept());
    newBetweenRel.setTo(getToConcept());
    newBetweenRel.setTerminology(getTerminology());
    newBetweenRel.setTerminologyId("");
    newBetweenRel.setVersion(getVersion());
    newBetweenRel.setTimestamp(new Date());
    newBetweenRel.setPublishable(true);
    newBetweenRel.setAssertedDirection(false);
    newBetweenRel.setWorkflowStatus(WorkflowStatus.READY_FOR_PUBLICATION);

    // construct relationship as well
    ConceptRelationshipJpa inverseBetweenRel =
        (ConceptRelationshipJpa) createInverseConceptRelationship(
            newBetweenRel);

    //
    // Create the new components to be added, and update modified objects
    //
    for (final Atom atom : moveAtomsCopies) {
      updateAtom(atom);
    }

    List<SemanticTypeComponent> newStys = new ArrayList<>();
    for (SemanticTypeComponent sty : fromStysCopies) {
      newStys.add(addSemanticTypeComponent(sty, getToConcept()));
    }

    List<ConceptRelationship> newRels = new ArrayList<>();
    for (final ConceptRelationship rel : fromRelationshipsCopies) {
      newRels.add((ConceptRelationshipJpa) addRelationship(rel));
    }

    List<ConceptRelationship> newInverseRels = new ArrayList<>();
    for (final ConceptRelationship rel : inverseRelationshipsCopies) {
      newInverseRels.add((ConceptRelationshipJpa) addRelationship(rel));
    }

    newBetweenRel = (ConceptRelationshipJpa) addRelationship(newBetweenRel);
    inverseBetweenRel =
        (ConceptRelationshipJpa) addRelationship(inverseBetweenRel);

    //
    // Add the components to the created concept (and related concepts)
    //
    for (final Atom atom : moveAtomsCopies) {
      getToConcept().getAtoms().add(atom);
    }
    for (SemanticTypeComponent sty : newStys) {
      getToConcept().getSemanticTypes().add(sty);
    }
    for (final ConceptRelationship rel : newRels) {
      getToConcept().getRelationships().add(rel);
    }
    for (final ConceptRelationship rel : newInverseRels) {
      rel.getFrom().getRelationships().add(rel);
    }
    getFromConcept().getRelationships().add(newBetweenRel);
    getToConcept().getRelationships().add(inverseBetweenRel);

    //
    // Change status of the concepts
    //
    if (getChangeStatusFlag()) {
      getFromConcept().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
      getToConcept().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
    }

    //
    // update the to and from Concepts, and all concepts a relationship has been
    // added to
    //
    updateConcept(getFromConcept());
    updateConcept(getToConcept());
    for (final ConceptRelationship rel : newInverseRels) {
      if (!rel.getFrom().getId().equals(getToConcept().getId())
          && !rel.getFrom().getId().equals(getFromConcept().getId())) {
        updateConcept(rel.getFrom());
      }
    }

    // log the REST calls
    addLogEntry(getUserName(), getProject().getId(),
        getFromConcept().getId(), getActivityId(), getWorkId(),
        getName() + " from concept " + getFromConcept().getId()
            + " into concept " + getToConcept().getId());

    addLogEntry(getUserName(), getProject().getId(),
        getToConcept().getId(), getActivityId(), getWorkId(),
        getName() + " into concept " + getToConcept().getId()
            + " from concept " + getFromConcept().getId());

    // Make copy of to and fromConcept to pass into change event
    originatingConceptPostUpdates =
        new ConceptJpa(getFromConcept(), false);
    createdConceptPostUpdates = new ConceptJpa(getToConcept(), false);

  }

}
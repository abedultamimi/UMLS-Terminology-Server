/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.action;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Code;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.Descriptor;
import com.wci.umls.server.model.workflow.WorkflowStatus;

/**
 * A molecular action for removing an atom.
 */
public class RemoveAtomMolecularAction extends AbstractMolecularAction {

  /** The atom. */
  private Atom atom;

  /** The atom id. */
  private Long atomId;

  /**
   * Instantiates an empty {@link RemoveAtomMolecularAction}.
   *
   * @throws Exception the exception
   */
  public RemoveAtomMolecularAction() throws Exception {
    super();
    // n/a
  }

  /**
   * Returns the atom.
   *
   * @return the atom
   */
  public Atom getAtom() {
    return atom;
  }

  /**
   * Returns the atom id.
   *
   * @return the atom id
   */
  public Long getAtomId() {
    return atomId;
  }

  /**
   * Sets the atom id.
   *
   * @param atomId the atom id
   */
  public void setAtomId(Long atomId) {
    this.atomId = atomId;
  }

  /* see superclass */
  @Override
  public ValidationResult checkPreconditions() throws Exception {
    final ValidationResult validationResult = new ValidationResultJpa();

    // Perform action specific validation - n/a

    // Exists check
    for (final Atom atm : getConcept().getAtoms()) {
      if (atm.getId().equals(atomId)) {
        atom = atm;
      }
    }
    if (atom == null) {
      rollback();
      throw new LocalException("Atom to remove does not exist");
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

    // Handle codeId, descriptorId, conceptId
    handleCode(atom);
    handleConcept(atom);
    handleDescriptor(atom);

    // Remove the atom from the concept
    getConcept().getAtoms().remove(atom);

    // Update Concept
    updateConcept(getConcept());

    // Remove the atom
    removeAtom(atom.getId());

    // Change status of concept
    if (getChangeStatusFlag()) {
      getConcept().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
    }
    // Update Concept
    updateConcept(getConcept());

    // log the REST call
    addLogEntry(getLastModifiedBy(), getProject().getId(), getConcept().getId(),
        getActivityId(), getWorkId(),
        getName() + " from concept " + getConcept().getId() + " " + atom);

    // Log for the molecular action report
    addLogEntry(getLastModifiedBy(), getProject().getId(),
        getMolecularAction().getId(), getActivityId(), getWorkId(),
        "\nACTION  " + getName() + "\n  concept = " + getConcept().getId() + " "
            + getConcept().getName() + "\n  atom = " + atom.getName() + ", "
            + atom.getTerminology() + "/" + atom.getTermType() + ","
            + atom.getCodeId());
  }

  /**
   * Handle code.
   *
   * @param atom the atom
   * @throws Exception
   */
  private void handleCode(Atom atom) throws Exception {
    Code code = getCode(atom.getCodeId(), atom.getTerminology(),
        atom.getVersion(), Branch.ROOT);
    if (code != null) {
      // Note: a code can contain multiple atoms that are identical except for
      // ID, so make sure we remove the correct one
      int index = 0;
      for (Atom codeAtom : code.getAtoms()) {
        if (!codeAtom.getId().equals(atom.getId())) {
          index++;
          continue;
        }
        code.getAtoms().remove(index);
        updateCode(code);
        break;
      }
    }
  }

  /**
   * Handle concept.
   *
   * @param atom the atom
   * @throws Exception
   */
  private void handleConcept(Atom atom) throws Exception {
    Concept concept = getConcept(atom.getConceptId(), atom.getTerminology(),
        atom.getVersion(), Branch.ROOT);
    if (concept != null) {
      // Note: a concept can contain multiple atoms that are identical except
      // for ID, so make sure we remove the correct one
      int index = 0;
      for (Atom conceptAtom : concept.getAtoms()) {
        if (!conceptAtom.getId().equals(atom.getId())) {
          index++;
          continue;
        }
        concept.getAtoms().remove(index);
        updateConcept(concept);
        break;
      }
    }
  }

  /**
   * Handle descriptor.
   *
   * @param atom the atom
   * @throws Exception the exception
   */
  private void handleDescriptor(Atom atom) throws Exception {
    Descriptor descriptor = getDescriptor(atom.getDescriptorId(),
        atom.getTerminology(), atom.getVersion(), Branch.ROOT);
    if (descriptor != null) {
      // Note: a descriptor can contain multiple atoms that are identical except
      // for ID, so make sure we remove the correct one
      int index = 0;
      for (Atom descriptorAtom : descriptor.getAtoms()) {
        if (!descriptorAtom.getId().equals(atom.getId())) {
          index++;
          continue;
        }
        descriptor.getAtoms().remove(index);
        updateDescriptor(descriptor);
        break;
      }
    }
  }
}

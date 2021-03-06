/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.model.workflow;

import com.wci.umls.server.helpers.HasLastModified;
import com.wci.umls.server.helpers.HasProject;

/**
 * Represents an editing cycle during which the workflow system operates.
 * Typical naming convention is the (2-digit) year followed by a letter
 * designation for the release. For example, 16a.
 */
public interface WorkflowEpoch extends HasLastModified, HasProject {

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName();

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name);

  /**
   * Checks if is active.
   *
   * @return true, if is active
   */
  public boolean isActive();

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(boolean active);


}
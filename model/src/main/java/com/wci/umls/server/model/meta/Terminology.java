/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.model.meta;

import java.util.Date;
import java.util.List;

import com.wci.umls.server.helpers.HasLastModified;
import com.wci.umls.server.model.content.AtomClass;
import com.wci.umls.server.model.content.Code;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.Descriptor;

/**
 * Represents a terminology of data with version information.
 */
public interface Terminology extends HasLastModified {

  /**
   * Returns the terminology.
   * 
   * @return the terminology
   */
  public String getTerminology();

  /**
   * Sets the terminology.
   * 
   * @param terminology the terminology
   */
  public void setTerminology(String terminology);

  /**
   * Returns the terminology version.
   * 
   * @return the terminology version
   */
  public String getTerminologyVersion();

  /**
   * Sets the terminology version.
   * 
   * @param terminologyVersion the terminology version
   */
  public void setTerminologyVersion(String terminologyVersion);

  /**
   * Indicates whether or not this terminology asserts the direction of its
   * relations.
   * 
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isAssertsRelDirection();

  /**
   * Sets the "asserts rel direction" flag.
   * 
   * @param assertsRelDirection the "asserts rel direction" flag
   */
  public void setAssertsRelDirection(boolean assertsRelDirection);

  /**
   * Returns the start date at which this terminology is valid and the current
   * version.
   * 
   * @return the start date
   */
  public Date getStartDate();

  /**
   * Sets the start date.
   * 
   * @param startDate the start date
   */
  public void setStartDate(Date startDate);

  /**
   * Returns the end date at which this terminology is no longer valid or
   * current.
   * 
   * @return the end date
   */
  public Date getEndDate();

  /**
   * Sets the end date.
   * 
   * @param endDate the end date
   */
  public void setEndDate(Date endDate);

  /**
   * Returns the citation.
   * 
   * @return the citation
   */
  public Citation getCitation();

  /**
   * Sets the citation.
   * 
   * @param citation the citation
   */
  public void setCitation(Citation citation);

  /**
   * Indicates whether or not the terminology is the current version.
   * 
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isCurrent();

  /**
   * Returns the root terminology.
   * 
   * @return the root terminology
   */
  public RootTerminology getRootTerminology();

  /**
   * Sets the root terminology.
   * 
   * @param rootTerminology the root terminology
   */
  public void setRootTerminology(RootTerminology rootTerminology);

  /**
   * Returns the preferred name.
   * 
   * @return the preferred name
   */
  public String getPreferredName();

  /**
   * Sets the preferred name.
   * 
   * @param preferredName the preferred name
   */
  public void setPreferredName(String preferredName);

  /**
   * Returns the synonymous names.
   * 
   * @return the synonymous names
   */
  public List<String> getSynonymousNames();

  /**
   * Sets the synonymous names.
   * 
   * @param synonymousNames the synonymous names
   */
  public void setSynonymousNames(List<String> synonymousNames);

  /**
   * Returns the organizing class type, which is an indicator of the
   * {@link AtomClass} subtype which is the primary organizing principle of the
   * terminology. This includes the level at which the hierarchy exists as well
   * as attributes and relationships. Another way to think about it is whether
   * the terminology code field is a {@link Concept}, a {@link Descriptor}, or
   * simply a {@link Code} without any further specification.
   *
   * @return the organizing class type
   */
  public IdentifierType getOrganizingClassType();

  /**
   * Sets the organizing class type.
   *
   * @param organizingClassType the organizing class type
   */
  public void setOrganizingClassType(IdentifierType organizingClassType);

}
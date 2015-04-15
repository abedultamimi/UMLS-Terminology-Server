/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.content;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.AtomRelationship;

/**
 * JPA-enabled implementation of {@link AtomRelationship}.
 */
@Entity
@Table(name = "atom_relationships", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "terminologyVersion", "id"
}))
@Audited
@XmlRootElement(name = "atomRelationship")
public class AtomRelationshipJpa extends AbstractRelationship<Atom, Atom>
    implements AtomRelationship {

  /** The from concept. */
  @ManyToOne(targetEntity = AtomJpa.class, optional = false)
  @JoinColumn(nullable = true)
  private Atom from;

  /** the to concept. */
  @ManyToOne(targetEntity = AtomJpa.class, optional = false)
  @JoinColumn(nullable = true)
  private Atom to;

  /**
   * Instantiates an empty {@link AtomRelationshipJpa}.
   */
  public AtomRelationshipJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link AtomRelationshipJpa} from the specified parameters.
   *
   * @param relationship the concept relationship
   * @param deepCopy the deep copy
   */
  public AtomRelationshipJpa(AtomRelationship relationship, boolean deepCopy) {
    super(relationship, deepCopy);
    to = relationship.getTo();
    from = relationship.getFrom();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.model.content.Relationship#getFrom()
   */
  @Override
  @XmlTransient
  public Atom getFrom() {
    return from;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.Relationship#setFrom(com.wci.umls.server
   * .model.content.Component)
   */
  @Override
  public void setFrom(Atom component) {
    this.from = component;
  }

  /**
   * Returns the from id. For JAXB.
   *
   * @return the from id
   */
  public Long getFromId() {
    return from == null ? null : from.getId();
  }
  
  /**
   * Sets the from id.
   *
   * @param id the from id
   */
  public void setFromId(Long id) {
    if (from == null) {
      from = new AtomJpa();
    }
    from.setId(id);
  }

  /**
   * Returns the from term. For JAXB.
   *
   * @return the from term
   */
  public String getFromTerm() {
    return from == null ? null : from.getTerm();
  }

  /**
   * Sets the from term.
   *
   * @param term the from term
   */
  public void setFromTerm(String term) {
    if (from == null) {
      from = new AtomJpa();
    }
    from.setTerm(term);
  }

  
  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.model.content.Relationship#getTo()
   */
  @Override
  @XmlTransient
  public Atom getTo() {
    return to;
  }

  /**
   * Returns the to id.
   *
   * @return the to id
   */
  public Long getToId() {
    return to == null ? null : to.getId();
  }
  
  /**
   * Returns the to term.
   *
   * @return the to term
   */
  public String getToTerm() {
    return to == null ? null : to.getTerm();
  }
  
  /**
   * Sets the to id.
   *
   * @param id the to id
   */
  public void setToId(Long id) {
    if (to == null) {
      to = new AtomJpa();
    }
    to.setId(id);
  }
  
  /**
   * Sets the to term.
   *
   * @param term the to term
   */
  public void setToTerm(String term) {
    if (to == null) {
      to = new AtomJpa();
    }
    to.setTerm(term);
  }
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.Relationship#setTo(com.wci.umls.server
   * .model.content.Component)
   */
  @Override
  public void setTo(Atom component) {
    this.to = component;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.jpa.content.AbstractRelationship#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + ((to == null) ? 0 : to.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.jpa.content.AbstractRelationship#equals(java.lang.Object
   * )
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AtomRelationshipJpa other = (AtomRelationshipJpa) obj;
    if (from == null) {
      if (other.from != null)
        return false;
    } else if (!from.equals(other.from))
      return false;
    if (to == null) {
      if (other.to != null)
        return false;
    } else if (!to.equals(other.to))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.jpa.content.AbstractRelationship#toString()
   */
  @Override
  public String toString() {
    return "AtomRelationshipJpa [from=" + from + ", to=" + to + "]";
  }

}
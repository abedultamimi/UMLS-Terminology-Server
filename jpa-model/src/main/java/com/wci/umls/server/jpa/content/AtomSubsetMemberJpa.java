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
import com.wci.umls.server.model.content.AtomSubset;
import com.wci.umls.server.model.content.AtomSubsetMember;
import com.wci.umls.server.model.content.SubsetMember;

/**
 * Abstract JPA-enabled implementation of an {@link Atom} {@link SubsetMember}.
 */
@Entity
@Table(name = "atom_subset_members", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "terminologyVersion", "id"
}))
@Audited
@XmlRootElement(name = "atomMember")
public class AtomSubsetMemberJpa extends AbstractSubsetMember<Atom> implements
    AtomSubsetMember {

  /** The member. */
  @ManyToOne(targetEntity = AtomJpa.class, optional = false)
  @JoinColumn(nullable = false, name = "atom_id")
  private Atom member;

  /** The subset. */
  @ManyToOne(targetEntity = AtomSubsetJpa.class, optional = false)
  @JoinColumn(nullable = false, name = "subset_id")
  private AtomSubset subset;

  /**
   * Instantiates an empty {@link AtomSubsetMemberJpa}.
   */
  public AtomSubsetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link AtomSubsetMemberJpa} from the specified parameters.
   *
   * @param member the subset
   * @param deepCopy the deep copy
   */
  public AtomSubsetMemberJpa(AtomSubsetMember member, boolean deepCopy) {
    super(member, deepCopy);
    subset = member.getSubset();
    this.member = member.getMember();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.model.content.SubsetMember#getMember()
   */
  @Override
  @XmlTransient
  public Atom getMember() {
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.SubsetMember#setMember(java.lang.Object)
   */
  @Override
  public void setMember(Atom member) {
    this.member = member;
  }

  /**
   * Returns the member id. For JAXB.
   *
   * @return the member id
   */
  public Long getMemberId() {
    return member == null ? null : member.getId();
  }

  /**
   * Sets the member id. For JAXB.
   *
   * @param id the member id
   */
  public void setMemberId(Long id) {
    if (member == null) {
      member = new AtomJpa();
    }
    member.setId(id);
  }

  /**
   * Returns the member terminology id. For JAXB.
   *
   * @return the member terminology id
   */
  public String getMemberTerminologyId() {
    return member == null ? null : member.getTerminologyId();
  }

  /**
   * Sets the member terminology id. For JAXB.
   *
   * @param terminologyId the member terminology id
   */
  public void setMemberTerminologyId(String terminologyId) {
    if (member == null) {
      member = new AtomJpa();
    }
    member.setTerminologyId(terminologyId);
  }
  
  /**
   * Returns the member name. For JAXB.
   *
   * @return the member name
   */
  public String getMemberName() {
    return member == null ? null : member.getName();
  }

  /**
   * Sets the member name. For JAXB.
   *
   * @param name the member name
   */
  public void setMemberName(String name) {
    if (member == null) {
      member = new AtomJpa();
    }
    member.setName(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.model.content.AtomSubsetMember#getSubset()
   */
  @XmlTransient
  @Override
  public AtomSubset getSubset() {
    return subset;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.AtomSubsetMember#setSubset(com.wci.umls
   * .server.model.content.AtomSubset)
   */
  @Override
  public void setSubset(AtomSubset subset) {
    this.subset = subset;
  }

  /**
   * Returns the subset id. For JAXB.
   *
   * @return the subset id
   */
  public Long getSubsetId() {
    return subset == null ? null : subset.getId();
  }

  /**
   * Sets the subset id. For JAXB.
   *
   * @param id the subset id
   */
  public void setSubsetId(Long id) {
    if (subset == null) {
      subset = new AtomSubsetJpa();
    }
    subset.setId(id);
  }

  /**
   * Returns the subset terminology id. For JAXB.
   *
   * @return the subset terminology id
   */
  public String getSubsetTerminologyId() {
    return subset == null ? null : subset.getTerminologyId();
  }

  /**
   * Sets the subset terminology id. For JAXB.
   *
   * @param terminologyId the subset terminology id
   */
  public void setSubsetTerminologyId(String terminologyId) {
    if (subset == null) {
      subset = new AtomSubsetJpa();
    }
    subset.setTerminologyId(terminologyId);
  }

  /**
   * Returns the subset name. For JAXB.
   *
   * @return the subset name
   */
  public String getSubsetName() {
    return subset == null ? null : subset.getName();
  }

  /**
   * Sets the subset name. For JAXB.
   *
   * @param name the subset name
   */
  public void setSubsetName(String name) {
    if (subset == null) {
      subset = new AtomSubsetJpa();
    }
    subset.setName(name);
  }

  /**
   * CUSTOM equals method for subset/member.getTerminologyId()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((member == null) ? 0 : member.hashCode());
    result =
        prime
            * result
            + ((member == null || member.getTerminologyId() == null) ? 0
                : member.getTerminologyId().hashCode());
    result =
        prime
            * result
            + ((subset == null || subset.getTerminologyId() == null) ? 0
                : subset.getTerminologyId().hashCode());
    return result;
  }

  /**
   * CUSTOM equals method for subset/member.getTerminologyId()
   *
   * @param obj the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AtomSubsetMemberJpa other = (AtomSubsetMemberJpa) obj;
    if (member == null) {
      if (other.member != null)
        return false;
    } else if (member.getTerminologyId() == null) {
      if (other.member != null && other.member.getTerminologyId() != null)
        return false;
    } else if (!member.getTerminologyId().equals(
        other.member.getTerminologyId()))
      return false;
    if (subset == null) {
      if (other.subset != null)
        return false;
    } else if (subset.getTerminologyId() == null) {
      if (other.subset != null && other.subset.getTerminologyId() != null)
        return false;
    } else if (!subset.getTerminologyId().equals(
        other.subset.getTerminologyId()))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.jpa.content.AbstractComponent#toString()
   */
  @Override
  public String toString() {
    return "AtomSubsetMemberJpa [" + super.toString() + ", member=" + member + ", subset=" + subset + "]";
  }

}

/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.services;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.wci.umls.server.model.meta.AtomIdentity;
import com.wci.umls.server.model.meta.AttributeIdentity;
import com.wci.umls.server.model.meta.LexicalClassIdentity;
import com.wci.umls.server.model.meta.RelationshipIdentity;
import com.wci.umls.server.model.meta.SemanticTypeComponentIdentity;
import com.wci.umls.server.model.meta.StringClassIdentity;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.services.UmlsIdentityService;

/**
 * JPA and JAXB enabled implementation of {@link UmlsIdentityService}.
 */
public class UmlsIdentityServiceJpa extends MetadataServiceJpa
    implements UmlsIdentityService {

  /**
   * Instantiates an empty {@link UmlsIdentityServiceJpa}.
   *
   * @throws Exception the exception
   */
  public UmlsIdentityServiceJpa() throws Exception {
    super();

  }

  /* see superclass */
  @Override
  public AttributeIdentity getAttributeIdentity(Long id) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get attribute identity " + id);
    return getObject(id, AttributeIdentity.class);
  }

  /* see superclass */
  @Override
  public Long getNextAttributeId() throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get next attribute id");

    Long attId = 0L;
    Long styId = 0L;
    try {
      final javax.persistence.Query query =
          manager.createQuery("select max(a.id) from AttributeIdentityJpa a ");
      Long attId2 = (Long) query.getSingleResult();
      attId = attId2 != null ? attId2 : attId;
    } catch (NoResultException e) {
      attId = 0L;
    }
    try {
      final javax.persistence.Query query = manager.createQuery(
          "select max(a.id) from SemanticTypeComponentIdentityJpa a ");
      Long styId2 = (Long) query.getSingleResult();
      styId = styId2 != null ? styId2 : styId;
    } catch (NoResultException e) {
      styId = 0L;
    }
    // Return the max
    return Math.max(attId, styId) + 1;
  }

  /* see superclass */
  @Override
  public AttributeIdentity getAttributeIdentity(AttributeIdentity identity)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get attribute identity " + identity);

    try {
      final javax.persistence.Query query =
          manager.createQuery("select a from AttributeIdentityJpa a "
              + "where terminology = :terminology "
              + "and terminologyId = :terminologyId "
              + "and componentId = :componentId "
              + "and componentType = :componentType "
              + "and componentTerminology = :componentTerminology "
              + "and name = :name " + "and hashcode = :hashcode");
      query.setParameter("terminology", identity.getTerminology());
      query.setParameter("terminologyId", identity.getTerminologyId());
      query.setParameter("componentId", identity.getComponentId());
      query.setParameter("componentType", identity.getComponentType());
      query.setParameter("componentTerminology",
          identity.getComponentTerminology());
      query.setParameter("name", identity.getName());
      query.setParameter("hashcode", identity.getHashcode());

      return (AttributeIdentity) query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public AttributeIdentity addAttributeIdentity(
    AttributeIdentity attributeIdentity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - add attribute identity "
            + attributeIdentity.toString());
    return addObject(attributeIdentity);
  }

  /* see superclass */
  @Override
  public void updateAttributeIdentity(AttributeIdentity attributeIdentity)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - update attribute identity "
            + attributeIdentity.toString());

    updateObject(attributeIdentity);
  }

  /* see superclass */
  @Override
  public void removeAttributeIdentity(Long attributeIdentityId)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - remove attribute identity "
            + attributeIdentityId);

    AttributeIdentity identity = getAttributeIdentity(attributeIdentityId);
    removeObject(identity, AttributeIdentity.class);
  }

  /* see superclass */
  @Override
  public SemanticTypeComponentIdentity getSemanticTypeComponentIdentity(Long id)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Umls Identity Service - get semanticTypeComponent identity " + id);
    return getObject(id, SemanticTypeComponentIdentity.class);
  }

  /* see superclass */
  @Override
  public Long getNextSemanticTypeComponentId() throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get next semanticTypeComponent id");

    Long attId = 0L;
    Long styId = 0L;
    try {
      final javax.persistence.Query query =
          manager.createQuery("select max(a.id) from AttributeIdentity a ");
      Long attId2 = (Long) query.getSingleResult();
      attId = attId2 != null ? attId2 : attId;
    } catch (NoResultException e) {
      attId = 0L;
    }
    try {
      final javax.persistence.Query query = manager.createQuery(
          "select max(a.id) from SemanticTypeComponentIdentityJpa a ");
      styId = (Long) query.getSingleResult();
    } catch (NoResultException e) {
      styId = 0L;
    }
    // Return the max
    return Math.max(attId, styId) + 1;
  }

  /* see superclass */
  @Override
  public SemanticTypeComponentIdentity getSemanticTypeComponentIdentity(
    SemanticTypeComponentIdentity identity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get semanticTypeComponent identity "
            + identity);

    try {
      final javax.persistence.Query query = manager
          .createQuery("select a from SemanticTypeComponentIdentityJpa a "
              + "where terminology = :terminology "
              + "and conceptTerminologyId = :conceptTerminologyId"
              + "and semanticType = :semanticType");

      query.setParameter("terminology", identity.getTerminology());
      query.setParameter("concdeptTerminologyId",
          identity.getConceptTerminologyId());
      query.setParameter("semanticType", identity.getSemanticType());
      return (SemanticTypeComponentIdentity) query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public SemanticTypeComponentIdentity addSemanticTypeComponentIdentity(
    SemanticTypeComponentIdentity semanticTypeComponentIdentity)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - add semanticTypeComponent identity "
            + semanticTypeComponentIdentity.toString());
    return addObject(semanticTypeComponentIdentity);
  }

  /* see superclass */
  @Override
  public void updateSemanticTypeComponentIdentity(
    SemanticTypeComponentIdentity semanticTypeComponentIdentity)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - update semanticTypeComponent identity "
            + semanticTypeComponentIdentity.toString());

    updateObject(semanticTypeComponentIdentity);
  }

  /* see superclass */
  @Override
  public void removeSemanticTypeComponentIdentity(
    Long semanticTypeComponentIdentityId) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - remove semanticTypeComponent identity "
            + semanticTypeComponentIdentityId);

    SemanticTypeComponentIdentity identity =
        getSemanticTypeComponentIdentity(semanticTypeComponentIdentityId);
    removeObject(identity, SemanticTypeComponentIdentity.class);
  }

  /* see superclass */
  @Override
  public AtomIdentity getAtomIdentity(Long id) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get atom identity " + id);
    return getObject(id, AtomIdentity.class);
  }

  /* see superclass */
  @Override
  public Long getNextAtomId() throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get next atom id");

    Long atomId = 0L;
    try {
      final javax.persistence.Query query =
          manager.createQuery("select max(a.id) from AtomIdentityJpa a ");
      Long atomId2 = (Long) query.getSingleResult();
      atomId = atomId2 != null ? atomId2 : atomId;
    } catch (NoResultException e) {
      atomId = 0L;
    }
    // Return the max
    return atomId + 1;
  }

  /* see superclass */
  @Override
  public AtomIdentity getAtomIdentity(AtomIdentity identity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get atom identity " + identity);

    try {
      final javax.persistence.Query query =
          manager.createQuery("select a from AtomIdentityJpa a "
              + "where stringClassId = :stringClassId "
              + "and terminology = :terminology "
              + "and terminologyId = :terminologyId "
              + "and termType = :termType " + "and codeId = :codeId "
              + "and conceptId = :conceptId "
              + "and descriptorId = :descriptorId ");
      query.setParameter("stringClassId", identity.getStringClassId());
      query.setParameter("terminology", identity.getTerminology());
      query.setParameter("terminologyId", identity.getTerminologyId());
      query.setParameter("termType", identity.getTermType());
      query.setParameter("codeId", identity.getCodeId());
      query.setParameter("conceptId", identity.getConceptId());
      query.setParameter("descriptorId", identity.getDescriptorId());

      return (AtomIdentity) query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public AtomIdentity addAtomIdentity(AtomIdentity atomIdentity)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Umls Identity Service - add atom identity " + atomIdentity.toString());
    return addObject(atomIdentity);
  }

  /* see superclass */
  @Override
  public void updateAtomIdentity(AtomIdentity atomIdentity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - update atom identity "
            + atomIdentity.toString());

    updateObject(atomIdentity);
  }

  /* see superclass */
  @Override
  public void removeAtomIdentity(Long atomIdentityId) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Umls Identity Service - remove atom identity " + atomIdentityId);

    AtomIdentity identity = getAtomIdentity(atomIdentityId);
    removeObject(identity, AtomIdentity.class);
  }

  /* see superclass */
  @Override
  public StringClassIdentity getStringClassIdentity(Long id) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get string identity " + id);
    return getObject(id, StringClassIdentity.class);
  }

  /* see superclass */
  @Override
  public Long getNextStringClassId() throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get next string id");

    Long stringId = 0L;
    try {
      final javax.persistence.Query query = manager
          .createQuery("select max(a.id) from StringClassIdentityJpa a ");
      Long stringId2 = (Long) query.getSingleResult();
      stringId = stringId2 != null ? stringId2 : stringId;
    } catch (NoResultException e) {
      stringId = 0L;
    }
    // Return the max
    return stringId + 1;
  }

  /* see superclass */
  @Override
  public StringClassIdentity getStringClassIdentity(
    StringClassIdentity identity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get string identity " + identity);

    try {
      final javax.persistence.Query query =
          manager.createQuery("select a from StringClassIdentityJpa a "
              + "where name = :name " + "and language = :language");
      query.setParameter("name", identity.getName());
      query.setParameter("language", identity.getLanguage());

      return (StringClassIdentity) query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public StringClassIdentity addStringClassIdentity(
    StringClassIdentity stringClassIdentity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - add string class identity "
            + stringClassIdentity.toString());
    return addObject(stringClassIdentity);
  }

  /* see superclass */
  @Override
  public void updateStringClassIdentity(StringClassIdentity stringClassIdentity)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - update string identity "
            + stringClassIdentity.toString());

    updateObject(stringClassIdentity);
  }

  /* see superclass */
  @Override
  public void removeStringClassIdentity(Long stringClassIdentityId)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - remove string identity "
            + stringClassIdentityId);

    StringClassIdentity identity =
        getStringClassIdentity(stringClassIdentityId);
    removeObject(identity, StringClassIdentity.class);
  }

  /* see superclass */
  @Override
  public LexicalClassIdentity getLexicalClassIdentity(Long id)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get lexical class identity " + id);
    return getObject(id, LexicalClassIdentity.class);
  }

  /* see superclass */
  @Override
  public Long getNextLexicalClassId() throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get next lexicalClass id");

    Long lexicalClassId = 0L;
    try {
      final javax.persistence.Query query = manager
          .createQuery("select max(a.id) from LexicalClassIdentityJpa a ");
      Long lexicalClassId2 = (Long) query.getSingleResult();
      lexicalClassId =
          lexicalClassId2 != null ? lexicalClassId2 : lexicalClassId;
    } catch (NoResultException e) {
      lexicalClassId = 0L;
    }
    // Return the max
    return lexicalClassId + 1;
  }

  /* see superclass */
  @Override
  public LexicalClassIdentity getLexicalClassIdentity(
    LexicalClassIdentity identity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get lexicalClass identity " + identity);

    try {
      final javax.persistence.Query query =
          manager.createQuery("select a from LexicalClassIdentityJpa a "
              + "where normalizedName = :normalizedName ");
      query.setParameter("normalizedName", identity.getNormalizedName());

      return (LexicalClassIdentity) query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public LexicalClassIdentity addLexicalClassIdentity(
    LexicalClassIdentity lexicalClassIdentity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - add lexicalClass identity "
            + lexicalClassIdentity.toString());
    return addObject(lexicalClassIdentity);
  }

  /* see superclass */
  @Override
  public void updateLexicalClassIdentity(
    LexicalClassIdentity lexicalClassIdentity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - update lexicalClass identity "
            + lexicalClassIdentity.toString());

    updateObject(lexicalClassIdentity);
  }

  /* see superclass */
  @Override
  public void removeLexicalClassIdentity(Long lexicalClassIdentityId)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - remove lexicalClass identity "
            + lexicalClassIdentityId);

    LexicalClassIdentity identity =
        getLexicalClassIdentity(lexicalClassIdentityId);
    removeObject(identity, LexicalClassIdentity.class);
  }

  /* see superclass */
  @Override
  public RelationshipIdentity getRelationshipIdentity(Long id)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get relationship identity " + id);
    return getObject(id, RelationshipIdentity.class);
  }

  /* see superclass */
  @Override
  public RelationshipIdentity createInverseRelationshipIdentity(RelationshipIdentity identity)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - creating inverse of relationship identity " + identity);

    //Create an inverse of the relationship    
    Terminology term = getObject(Long.parseLong(identity.getTerminologyId()),Terminology.class);
    
    RelationshipIdentity inverseIdentity = identity;
    inverseIdentity.setId(null);
    inverseIdentity.setFromId(identity.getFromId());
    inverseIdentity.setFromTerminology(identity.getFromTerminology());
    inverseIdentity.setFromType(identity.getFromType());
    inverseIdentity.setToId(identity.getToId());
    inverseIdentity.setToTerminology(identity.getToTerminology());
    inverseIdentity.setToType(identity.getToType());
    inverseIdentity.setRelationshipType(getRelationshipType(identity.getRelationshipType(), term.getTerminology(), term.getVersion()).getInverse().getAbbreviation());
    //TODO same thing for additionalRelationshipType
    
    return inverseIdentity;
  }  
  
  /* see superclass */
  @Override
  public Long getNextRelationshipId() throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get next relationship id");

    Long relationshipId = 0L;
    try {
      final javax.persistence.Query query = manager
          .createQuery("select max(a.id) from RelationshipIdentityJpa a ");
      Long relationshipId2 = (Long) query.getSingleResult();
      relationshipId =
          relationshipId2 != null ? relationshipId2 : relationshipId;
    } catch (NoResultException e) {
      relationshipId = 0L;
    }
    // Return the max
    return relationshipId + 1;
  }

  /* see superclass */
  @Override
  public RelationshipIdentity getRelationshipIdentity(
    RelationshipIdentity identity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - get relationship identity " + identity);

    try {
      final javax.persistence.Query query =
          manager.createQuery("select a from RelationshipIdentityJpa a "
              + "where id = :id  " + "and terminology = :terminology  "
              + "and terminologyId = :terminologyId  "
              + "and relationshipType = :relationshipType  "
              + "and additionalRelationshipType = :additionalRelationshipType  "
              + "and fromId  = :fromId   " + "and fromType = :fromType  "
              + "and fromTerminology = :fromTerminology  "
              + "and toId = :toId  " + "and toType = :toType  "
              + "and toTerminology = :toTerminology  "
              + "and inverseId = :inverseId  ");
      query.setParameter("id", identity.getId());
      query.setParameter("terminology", identity.getTerminology());
      query.setParameter("terminologyId", identity.getTerminologyId());
      query.setParameter("relationshipType", identity.getRelationshipType());
      query.setParameter("additionalRelationshipType",
          identity.getAdditionalRelationshipType());
      query.setParameter("fromId ", identity.getFromId());
      query.setParameter("fromType", identity.getFromType());
      query.setParameter("fromTerminology", identity.getFromTerminology());
      query.setParameter("toId", identity.getToId());
      query.setParameter("toType", identity.getToType());
      query.setParameter("toTerminology", identity.getToTerminology());
      query.setParameter("inverseId", identity.getInverseId());

      return (RelationshipIdentity) query.getSingleResult();

    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public RelationshipIdentity addRelationshipIdentity(
    RelationshipIdentity relationshipIdentity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - add relationship identity "
            + relationshipIdentity.toString());
    return addObject(relationshipIdentity);
  }

  /* see superclass */
  @Override
  public void updateRelationshipIdentity(
    RelationshipIdentity relationshipIdentity) throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - update relationship identity "
            + relationshipIdentity.toString());

    updateObject(relationshipIdentity);
  }

  /* see superclass */
  @Override
  public void removeRelationshipIdentity(Long relationshipIdentityId)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Umls Identity Service - remove relationship identity "
            + relationshipIdentityId);

    RelationshipIdentity identity =
        getRelationshipIdentity(relationshipIdentityId);
    removeObject(identity, RelationshipIdentity.class);
  }

}
package com.wci.umls.server.mojo.processes;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wci.umls.server.helpers.SearchResult;
import com.wci.umls.server.helpers.SearchResultList;
import com.wci.umls.server.helpers.content.ConceptList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.mojo.analysis.matching.rules.neoplasm.AbstractNeoplasmICD11MatchingRule;
import com.wci.umls.server.mojo.model.ICD11MatcherSctConcept;
import com.wci.umls.server.mojo.model.SctNeoplasmDescription;
import com.wci.umls.server.mojo.model.ICD11MatcherRelationship;
import com.wci.umls.server.rest.client.ContentClientRest;

public class FindingSiteUtility {

  private ICD11MatcherConceptSearcher conceptSearcher;

  /** The finding site potential terms map cache. */
  protected Map<String, Map<ICD11MatcherSctConcept, Set<String>>> findingSitePotentialTermsMapCache =
      new HashMap<>();

  /** The non finding site strings. */
  final static protected List<String> nonFindingSiteStrings =
      Arrays.asList("of", "part", "structure", "system", "and/or", "and", "region", "area", "or",
          "the", "in", "cavity", "organ", "genitalia", "canal", "genital", "adnexa", "duct", "tract");// ,
                                                                                             // "male",
                                                                                             // "female");

  /** The top level body structure ids. */
  final protected List<String> topLevelBodyStructureIds =
      Arrays.asList("86762007", "20139000", "39937001", "81745001", "387910009", "127882003",
          "64033007", "117590005", "21514008", "76752008", "113331007", "363667005", "31610004",
          "421663001", "416319003", "21483005", "774007", "771314001", "119194000");

  protected ContentClientRest client;

  protected String sourceTerminology;

  protected String sourceVersion;

  protected String targetTerminology;

  protected String targetVersion;

  protected String authToken;

  protected PfsParameterJpa pfsLimitless = new PfsParameterJpa();

  private int jesse = 0;

  public FindingSiteUtility(ContentClientRest contentClient, String st, String sv, String tt,
      String tv, String token) {
    client = contentClient;
    sourceTerminology = st;
    sourceVersion = sv;
    targetTerminology = tt;
    targetVersion = tv;
    authToken = token;
  }

  /**
   * Based on a finding site, identify all the finding site's ancestors up to
   * levels specified by topLevelBodyStructureIds.
   *
   * @param findingSites the finding sites
   * @return the sets the
   * @throws Exception the exception
   */
  public Set<ICD11MatcherSctConcept> identifyPotentialFSConcepts(Set<ICD11MatcherSctConcept> findingSites,
    PrintWriter devWriter) throws Exception {
    Set<ICD11MatcherSctConcept> retConcepts = new HashSet<>();

    if (findingSites != null) {
      for (ICD11MatcherSctConcept fsConcept : findingSites) {
        // Get the finding site as a concept
        retConcepts.add(fsConcept);

        if (findingSitePotentialTermsMapCache.containsKey(fsConcept.getConceptId())) {
          retConcepts.add(fsConcept);
        } else {

          Map<ICD11MatcherSctConcept, Set<String>> potentialFSConTerms = new HashMap<>();
          findingSitePotentialTermsMapCache.put(fsConcept.getConceptId(), potentialFSConTerms);

          if (topLevelBodyStructureIds.contains(fsConcept.getConceptId())) {
            ICD11MatcherSctConcept mapCon = null;
            if (ICD11MatcherConceptSearcher.canPopulateFromFiles) {
              mapCon = conceptSearcher.populateSctConcept(fsConcept.getConceptId(), null);
            } else {
              Concept c = client.getConcept(fsConcept.getConceptId(), sourceTerminology,
                  sourceVersion, null, authToken);
              mapCon = conceptSearcher.populateSctConcept(c.getTerminologyId(), null);
            }

            Set<String> bucket = new HashSet<>();
            potentialFSConTerms.put(mapCon, bucket);
          } else {
            // Get all fsCon's ancestors
            String topLevelSctId = null;
            final ConceptList ancestorResults =
                client.findAncestorConcepts(fsConcept.getConceptId(), sourceTerminology,
                    sourceVersion, false, pfsLimitless, authToken);

            // Find the body structure hierarchy it falls under
            Set<String> ancestorIds = new HashSet<>();
            for (Concept ancestor : ancestorResults.getObjects()) {
              if (!ancestorIds.contains(ancestor.getTerminologyId())) {
                ancestorIds.add(ancestor.getTerminologyId());
                if (AbstractNeoplasmICD11MatchingRule.topLevelConcepts
                    .contains(ancestor.getTerminologyId())) {
                  continue;
                }

                if (jesse == 0) {
                  ICD11MatcherSctConcept mapCon = null;
                  try {
                    mapCon = conceptSearcher.populateSctConcept(ancestor.getTerminologyId(), null);
                  } catch (Exception e) {
                    Concept c = client.getConcept(ancestor.getTerminologyId(), sourceTerminology,
                        sourceVersion, null, authToken);
                    mapCon = conceptSearcher.populateSctConcept(c.getTerminologyId(), null);
                  }
                  potentialFSConTerms.put(mapCon, new HashSet<>());
                } else {
                  if (topLevelBodyStructureIds.contains(ancestor.getTerminologyId())) {
                    topLevelSctId = ancestor.getTerminologyId();
                    break;
                  }
                }
              }
            }

            if (jesse != 0) {
              // Have list of possibleFindingSites. Test them for matches
              if (topLevelSctId == null) {
                String errorString =
                    "ERROR ERROR ERROR: Found a finding site without an identified top level BS ancestor: "
                        + fsConcept.getConceptId() + "---" + fsConcept.getName();
                System.out.println(errorString);
                if (devWriter != null) {
                  devWriter.println(errorString + "\n\n");
                }

                return null;
              }

              // TODO: Because can't do ancestors via ECL, need this work around
              // Identify all descendants of top level bodyStructure concept
              pfsLimitless.setExpression("<< " + topLevelSctId);
              final SearchResultList descendentResults = client.findConcepts(sourceTerminology,
                  sourceVersion, null, pfsLimitless, authToken);
              pfsLimitless.setExpression(null);

              // Create a list of concepts that are both ancestors of fsConcept
              // and
              // descendents of topLevelBodyStructure Concept
              // TODO: This could be a Rest Call in of itself
              for (Concept ancestor : ancestorResults.getObjects()) {

                for (SearchResult potentialFindingSite : descendentResults.getObjects()) {
                  if (ancestor.getTerminologyId().equals(potentialFindingSite.getTerminologyId())) {
                    ICD11MatcherSctConcept mapCon = null;
                    if (ICD11MatcherConceptSearcher.canPopulateFromFiles) {
                      mapCon =
                          conceptSearcher.populateSctConcept(ancestor.getTerminologyId(), null);
                    } else {
                      Concept c = client.getConcept(ancestor.getTerminologyId(), sourceTerminology,
                          sourceVersion, null, authToken);
                      mapCon = conceptSearcher.populateSctConcept(c.getTerminologyId(), null);
                    }

                    potentialFSConTerms.put(mapCon, new HashSet<>());
                    break;
                  }
                }
              }
            }
          }

          for (ICD11MatcherSctConcept testCon : potentialFSConTerms.keySet()) {
            for (SctNeoplasmDescription desc : testCon.getDescs()) {
              String normalizedStr = desc.getDescription().toLowerCase();

              if (!potentialFSConTerms.get(testCon).contains(normalizedStr)) {
                potentialFSConTerms.get(testCon).add(normalizedStr);
              }
            }
          }
        }
      }
    }

    return retConcepts;
  }

  public void setConceptSearcher(ICD11MatcherConceptSearcher searcher) {
    conceptSearcher = searcher;
  }

  public List<String> getNonFindingSiteStrings() {
    return nonFindingSiteStrings;
  }

  public Map<String, Map<ICD11MatcherSctConcept, Set<String>>> getFindingSitePotentialTermsMapCache() {
    return findingSitePotentialTermsMapCache;
  }

  /**
   * Identify finding sites related to associated morphology relationships.
   *
   * @param sctCon the sct con
   * @return the sets the
   * @throws Exception
   */
  public Set<ICD11MatcherSctConcept> identifyAssociatedMorphologyBasedFindingSites(
    ICD11MatcherSctConcept sctCon) throws Exception {
    Set<ICD11MatcherSctConcept> targets = new HashSet<>();

    Set<ICD11MatcherRelationship> amRels = conceptSearcher.getDestRels(sctCon, "Associated morphology");
    Set<ICD11MatcherRelationship> findingSites = conceptSearcher.getDestRels(sctCon, "Finding site");

    for (ICD11MatcherRelationship morphology : amRels) {
      for (ICD11MatcherRelationship site : findingSites) {
        if (site.getRoleGroup() == morphology.getRoleGroup()) {
          ICD11MatcherSctConcept fsConcept =
              conceptSearcher.getSctConceptFromDesc(site.getRelationshipDestination());
          targets.add(fsConcept);
        }
      }
    }

    return targets;
  }

  public String cleanNonFindingSiteString(String origSiteString) {
    String site = origSiteString.toLowerCase();
    for (String s : nonFindingSiteStrings) {
      site = site.replaceAll("\\b" + s + "s" + "\\b", " ").trim();
      site = site.replaceAll("\\b" + s + "\\b", " ").trim();
    }
    site = site.replaceAll(" {2,}", " ").trim();

    /*
     * if (site.matches(".*\\bgenital\\w{0,}\\b.*") &&
     * (site.matches(".*\\bmale\\b.*") || site.matches(".*\\bfemale\\b.*"))) {
     * site = site.replaceAll("\\bgenital\\w{0,}\\b", ""); }
     */

    return site;
  }
}

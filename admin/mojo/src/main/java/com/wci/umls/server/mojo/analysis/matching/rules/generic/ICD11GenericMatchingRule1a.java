/**
 * Copyright 2018 West Coast Informatics, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wci.umls.server.mojo.analysis.matching.rules.generic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wci.umls.server.helpers.SearchResult;
import com.wci.umls.server.helpers.SearchResultList;
import com.wci.umls.server.mojo.analysis.matching.ICD11MatcherConstants;
import com.wci.umls.server.mojo.model.ICD11MatcherSctConcept;
import com.wci.umls.server.rest.client.ContentClientRest;

public class ICD11GenericMatchingRule1a extends AbstractGenericICD11MatchingRule {

  public ICD11GenericMatchingRule1a(ContentClientRest client, String st, String sv, String tt,
      String tv, String authToken) {
    super(client, st, sv, tt, tv, authToken);
  }

  @Override
  public String getRuleId() {
    return "rule1a";
  }

  @Override
  public String getDescription() {
    return "ECL Based: All descendents of 'Human immunodeficiency virus infection' connecting them to the ICD11 'Human immunodeficiency virus disease' i.e. andything starting with '1C6*', contains 'HIV', or contains 'Human immunodeficiency virus'";
  }

  @Override
  public String getEclExpression() {
    return "<< 86406008";
  }

  @Override
  public Map<String, ICD11MatcherSctConcept> getConceptMap() {
    return null;
  }

  @Override
  public String getDefaultTarget() {
    return "1C62.Z\tHuman immunodeficiency virus disease without mention of associated disease or condition, clinical stage unspecified";
  }

  @Override
  protected ICD11MatcherSctConcept getTopLevelConcept() {
    return conceptSearcher.getSctConcept("86406008");
  }

  @Override
  protected String getRuleQueryString() {
    return "(atoms.codeId: 1C6*)";
  }

  @Override
  protected boolean printIcd11Targets() {
    return true;
  }
  
  @Override
  public String getEclTopLevelDesc() {
    // TODO: Need to fix import of ICD11 to enable importing blocks and chapters if going to do this properly 
    //return "Human immunodeficiency virus disease";
    return null;
  }

  @Override
  public String getDefaultSkinMatch() {
    return null;
  }

  @Override
  protected boolean isRuleMatch(SearchResult result) {
    if (result.getCodeId().startsWith("1C6")
        && result.isLeafNode()) {
      return true;
    }

    return false;
  }

  @Override
  protected Set<String> getRuleBasedNonMatchTerms() {
    Set<String> retSet = new HashSet<>();
    
    retSet.add("hiv");
    retSet.add("human immunodeficiency virus");
    retSet.add("infection");

    return retSet;
  }

  /**
   * Test rule 1 finding site.
   *
   * @param queryPortion the query portion
   * @return the search result list
   * @throws Exception the exception
   */
  protected SearchResultList testMatchingFindingSite(String queryPortion) throws Exception {
    if (!findingSiteCache.containsKey(queryPortion)) {
      final SearchResultList straightMatch = client.findConcepts(targetTerminology, targetVersion,
          getRuleQueryString() + queryPortion, pfsLimited, authToken);

      findingSiteCache.put(queryPortion, straightMatch);
    }

    return findingSiteCache.get(queryPortion);
  }
  

  @Override
  public Set<String> executeRule(ICD11MatcherSctConcept sctCon, int counter) throws Exception {

    Set<String> results = new HashSet<>();
    matchNextConcept(sctCon, counter);

    results = matchApproachBaseMatch(sctCon, results, icd11Targets, ICD11MatcherConstants.FILTERED_RULE_TYPE);
    
    if (results.isEmpty()) {
      results = matchApproachBaseSearch(sctCon, results, icd11Targets, ICD11MatcherConstants.FILTERED_RULE_TYPE);
    }
    
    return results;
  }
}
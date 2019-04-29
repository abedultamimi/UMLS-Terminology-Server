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
package com.wci.umls.server.mojo.analysis.matching.rules.neoplasm;

import java.util.Map;

import com.wci.umls.server.helpers.SearchResult;
import com.wci.umls.server.mojo.model.ICD11MatcherSctConcept;
import com.wci.umls.server.rest.client.ContentClientRest;

public class ICD11NeoplasmMatchingRule2 extends AbstractNeoplasmICD11MatchingRule {

  public ICD11NeoplasmMatchingRule2(ContentClientRest client, String st, String sv, String tt,
      String tv, String authToken) {
    super(client, st, sv, tt, tv, authToken);
  }

  @Override
  public String getRuleId() {
    return "rule2";
  }

  @Override
  public String getRuleName() {
    return "Melanoma In Situ";
  }

  @Override
  public String getDescription() {
    return "ECL Based: All descendents of 'Neoplastic disease' where the assocMorph is 'Melanoma in situ'";
  }

  @Override
  public String getEclExpression() {
    return "<< 55342001 : 116676008 = 77986002";
  }

  @Override
  public Map<String, ICD11MatcherSctConcept> getConceptMap() {
    return null;
  }

  @Override
  public String getDefaultTarget() {
    return "2E63.Y\tOther specified melanoma in situ neoplasms\t1576650184/other";
  }

  @Override
  protected ICD11MatcherSctConcept getTopLevelConcept() {
    return conceptSearcher.getSctConcept("55342001");
  }

  @Override
  protected String getRuleQueryString() {
    return "(atoms.codeId: 2* OR (\"Melanoma\" AND \"in situ\"))";
  }

  @Override
  public String getDefaultSkinMatch() {
    return "2E63.0Z\tMelanoma in situ of skin, unspecified\t1538263896/unspecified";
  }

  @Override
  public boolean isRuleMatch(SearchResult result) {
    if (!result.getCodeId().startsWith("X")
        && result.getValue().toLowerCase().matches(".*\\bmelanoma\\b.*")
        && result.getValue().toLowerCase().matches(".*\\bin situ\\b.*")
        && !result.getTerminologyId().equals("2E63.Z") && result.isLeafNode()) {
      return true;
    }

    return false;
  }
}
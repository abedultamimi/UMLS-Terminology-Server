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

import java.io.IOException;
import java.util.Map;

import com.wci.umls.server.helpers.SearchResult;
import com.wci.umls.server.mojo.model.ICD11MatcherSctConcept;
import com.wci.umls.server.mojo.processes.SctNeoplasmDescriptionParser;
import com.wci.umls.server.mojo.processes.SctRelationshipParser;
import com.wci.umls.server.rest.client.ContentClientRest;

public class ICD11NeoplasmMatchingRule7 extends AbstractNeoplasmICD11MatchingRule {

  public ICD11NeoplasmMatchingRule7(ContentClientRest client, String st, String sv, String tt,
      String tv, String authToken) {
    super(client, st, sv, tt, tv, authToken);
  }

  @Override
  public String getRuleId() {
    return "rule7";
  }

  @Override
  public String getDescription() {
    return "Description Based: All descendents of 'cyst (disorder)'";
  }

  @Override
  public String getEclExpression() {
    return "<< 441457006";
  }

  @Override
  public Map<String, ICD11MatcherSctConcept> getConceptMap() {
    return null;
  }

  @Override
  public String getDefaultTarget() {
    return "2F7Y";
  }

  @Override
  protected ICD11MatcherSctConcept getTopLevelConcept() {
    return conceptSearcher.getSctConcept("441457006");
  }

  @Override
  protected String getRuleQueryString() {
    return "(\"cyst\")";
  }

  @Override
  public String getDefaultSkinMatch() {
    return "2F72.Y";
  }

  @Override
  protected boolean isRuleMatch(SearchResult result) {
    if (!result.getCodeId().startsWith("X")
        && result.getValue().toLowerCase().matches(".*\\bcyst.*")
        && result.isLeafNode()) {
      return true;
    }

    return false;
  }
  
  @Override
  public boolean executeContentParsers(String matcherName, SctNeoplasmDescriptionParser descParser, SctRelationshipParser relParser) throws IOException {

    // Finding Sites
    boolean populatedFromFiles = descParser.readAllFindingSitesFromFile();
    populatedFromFiles = populatedFromFiles && relParser.readAllFindingSitesFromFile();

    try {
      populatedFromFiles = descParser.readDescsFromFile(getRulePath(matcherName));
      populatedFromFiles =
          populatedFromFiles && relParser.readRelsFromFile(getRulePath(matcherName));
    } catch (Exception e) {

    }
    
    return populatedFromFiles;
  }
}
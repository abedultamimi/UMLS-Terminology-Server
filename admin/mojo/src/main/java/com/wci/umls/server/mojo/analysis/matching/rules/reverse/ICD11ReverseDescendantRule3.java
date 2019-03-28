package com.wci.umls.server.mojo.analysis.matching.rules.reverse;

import com.wci.umls.server.helpers.SearchResult;
import com.wci.umls.server.rest.client.ContentClientRest;

public class ICD11ReverseDescendantRule3 extends AbstractReverseDescendantICD11MatchingRule {
  public ICD11ReverseDescendantRule3(ContentClientRest client, String st, String sv, String tt,
      String tv, String authToken) {
    super(client, st, sv, tt, tv, authToken);
  }

  @Override
  public String getRuleId() {
    return "rule3";
  }

  @Override
  public String getDescription() {
    return "Reverse Rule for Predominantly sexually transmitted infections";
  }

  @Override
  protected String getRuleQueryString() {
    return "(atoms.codeId: 1A6* OR atoms.codeId: 1A7* OR atoms.codeId: 1A8* OR atoms.codeId: 1A9*)";
  }

  @Override
  protected boolean isRuleMatch(SearchResult result) {
    if ((result.getCodeId().startsWith("1C6") || result.getCodeId().startsWith("1C7")
        || result.getCodeId().startsWith("1C8") || result.getCodeId().startsWith("1C9"))
        && result.isLeafNode()) {
      return true;
    }

    return false;
  }
}

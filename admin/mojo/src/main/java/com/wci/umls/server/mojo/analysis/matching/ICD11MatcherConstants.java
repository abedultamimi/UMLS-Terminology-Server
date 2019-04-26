package com.wci.umls.server.mojo.analysis.matching;

import java.util.Arrays;
import java.util.List;

public class ICD11MatcherConstants {
  public static final String NON_MATCH_HEADER = "\n\n\nCouldn't Match the following: ";

  public static final String SNOMED_ROOT_CONCEPT = "138875005";

  public static final String SEX_STRUCTURE_CONCEPT = "57312000";

  public static final String BODY_STRUCTURE = "123037004";

  public static final String FILTERED_RULE_TYPE = "filtered";

  public static final String ALL_LEAFS_RULE_TYPE = "all leaf nodes";

  public static final Integer PARENT_CONCEPTS = 1;

  public static final Integer ANCESTOR_CONCEPTS = 2;

  public static final int ICD_COLUMN = 0;

  public static final int SNOMED_COLUMN = 1;

  public static final int DEPTH_LOCATION_NEOPLASM = 4;

  public static final int DEPTH_LOCATION_GENERIC = 5;

  public static final int PRINT_WRITER_DEV_TYPE = 0;

  public static final int PRINT_WRITER_TERM_TYPE = 1;

  final public static List<String> NON_MATCHING_TERMS =
      Arrays.asList("of", "part", "structure", "system", "and/or", "and", "region", "area", "or",
          "the", "in", "cavity", "tract", "organ", "duct", "canal", "genitalia", "genital",
          "adnexa", "due", "to", "disease", "by", "caused", "left", "right", "upper", "lower",
          "tissue", "body", "gland", "cell", "site", "with", "co-occurrent", "acquired", "in");// ,
  // "male",
  // "female");

  final public static List<String> NEOPLASM_SYNONYMS = Arrays.asList("neoplasm", "neoplasms",
      "neoplastic", "tumor", "tumorous", "tumoru", "tumour", "tumoural", "tumours", "cancer",
      "cancerous", "cancerphobia", "carcinoma", "carcinomas", "carcinomatosis", "carcinomatous",
      "carcinoma-induced", "carcinomaphobia", "adenocarcinoma", "adenoma", "chondromatosis",
      "chromaffinoma", "glioma", "neoplasia", "pheochromocytoma", "proliferating pilar cyst",
      "thymoma", "melanoma", "melanocytic", "lipoma", "mesothelioma", "sarcoma", "fibroma",
      "papilloma", "lymphoma", "chondroma", "squamous");

  public static final int SNOMED_TO_ICD11 = 0;

  public static final int ICD11_TO_SNOMED = 1;

}
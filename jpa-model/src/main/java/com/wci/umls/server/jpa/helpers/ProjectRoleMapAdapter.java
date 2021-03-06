/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.helpers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.wci.umls.server.Project;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.jpa.ProjectJpa;

/**
 * A map adapter for Project, UserRole maps.
 */
public class ProjectRoleMapAdapter extends
    XmlAdapter<HashMap<Long, String>, Map<Project, UserRole>> {

  /* see superclass */
  @Override
  public Map<Project, UserRole> unmarshal(HashMap<Long, String> v)
    throws Exception {
    final HashMap<Project, UserRole> map = new HashMap<Project, UserRole>();

    for (final Map.Entry<Long, String> entry : v.entrySet()) {
      Project project = new ProjectJpa();
      project.setId(entry.getKey());
      map.put(project, UserRole.valueOf(entry.getValue()));
    }
    return map;
  }

  /* see superclass */
  @Override
  public HashMap<Long, String> marshal(Map<Project, UserRole> v)
    throws Exception {
    final HashMap<Long, String> map = new HashMap<Long, String>();

    for (final Map.Entry<Project, UserRole> entry : v.entrySet()) {
      map.put(entry.getKey().getId(), entry.getValue().toString());
    }
    return map;
  }

}
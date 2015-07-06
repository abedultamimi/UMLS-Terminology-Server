/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.rest.impl;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.sun.jersey.api.model.AbstractResourceModelContext;
import com.sun.jersey.api.model.AbstractResourceModelListener;

/**
 * The listener interface for receiving initialization events. The class that is
 * interested in processing a initialization event implements this interface,
 * and the object created with that class is registered with a component using
 * the component's <code>addInitializationListener<code> method. When
 * the initialization event occurs, that object's appropriate
 * method is invoked.
 */
@Provider
public class InitializationListener implements AbstractResourceModelListener {

  /** The timer. */
  Timer timer;

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sun.jersey.api.model.AbstractResourceModelListener#onLoaded(com.sun
   * .jersey.api.model.AbstractResourceModelContext)
   */
  @SuppressWarnings("unused")
  @Override
  public void onLoaded(AbstractResourceModelContext modelContext) {
    // Set up a timer task to run at 2AM every day
    TimerTask task = new SampleTask();
    timer = new Timer();
    Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR_OF_DAY, 2);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    // FOR NOW: do nothing
    // timer.scheduleAtFixedRate(task, today.getTime(), 24 * 60 * 60 * 1000);

    // Cache the "guest" user.
    // SecurityService service;
    // try {
    // service = new SecurityServiceJpa();
    // Properties config = ConfigUtility.getConfigProperties();
    // if (config.getProperty("security.handler").equals("DEFAULT")) {
    // service.authenticate("guest", "guest");
    // }
    // } catch (Exception e) {
    // try {
    // ExceptionHandler.handleException(e, "Cacheing guest user info");
    // } catch (Exception e1) {
    // // do nothing
    // e1.printStackTrace();
    // }
    // }
  }

  /**
   * A sample task.
   */
  class SampleTask extends TimerTask {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
      try {

        // For now, the timer is not doing anything
        // TODO: consider making some kind of DB request once per hour to stay logged in.
        
      } catch (Exception e) {
        e.printStackTrace();
        Logger.getLogger(getClass()).error("Error running the process to xxx.");
      }
    }
  }

}

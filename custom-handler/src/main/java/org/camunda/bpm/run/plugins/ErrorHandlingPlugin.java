package org.camunda.bpm.run.plugins;


import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.incident.IncidentHandler;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.run.handlers.GenericExceptionHandler;
import org.springframework.stereotype.Component;


@Component
public class ErrorHandlingPlugin implements ProcessEnginePlugin {
	@Override
	public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
	    List<IncidentHandler> customIncidentHandlers = new ArrayList<IncidentHandler>();
	    customIncidentHandlers.add(new GenericExceptionHandler(Incident.FAILED_JOB_HANDLER_TYPE));
	    processEngineConfiguration.setCustomIncidentHandlers(customIncidentHandlers);
	    processEngineConfiguration.setCreateIncidentOnFailedJobEnabled(true); 
	}

	@Override
	public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
	}

	@Override
	public void postProcessEngineBuild(ProcessEngine processEngine) {
	}

}

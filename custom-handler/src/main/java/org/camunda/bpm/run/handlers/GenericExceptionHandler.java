package org.camunda.bpm.run.handlers;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.impl.incident.DefaultIncidentHandler;
import org.camunda.bpm.engine.impl.incident.IncidentContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.model.bpmn.instance.FlowNode;


public class GenericExceptionHandler extends DefaultIncidentHandler  {
	
	public GenericExceptionHandler(String type) {
		super(type);
	}

	@Override
	public Incident handleIncident(IncidentContext context, String message) {	
		
		try {
			ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
			ExecutionEntity execution = (ExecutionEntity) engine.getRuntimeService().createExecutionQuery().executionId(context.getExecutionId()).singleResult();
	        ActivityImpl act = execution.getActivity();
            String waitStateTaskId="";
            
            for (PvmTransition transition : act.getOutgoingTransitions()) {
            	  waitStateTaskId =  transition.getSource().getId();
			}	
            
            FlowNode waitStateElement =  execution.getProcessInstance().getBpmnModelInstance().getModelElementById(waitStateTaskId);
	        
            if (waitStateElement.isCamundaAsyncAfter()) {
			    engine.getRuntimeService().createProcessInstanceModification(execution.getProcessInstanceId())
	    	    .cancelAllForActivity(context.getActivityId())
	    	    .startBeforeActivity("Event_0vdbr16")
	    	    .setVariable("TargetTask", waitStateTaskId)
	    	    .setVariable("IsAsyncAfter", true)
	    	    .execute();
	        }
	        else {
	    	    engine.getRuntimeService().createProcessInstanceModification(execution.getProcessInstanceId())
	    	    .cancelAllForActivity(context.getActivityId())
	    	    .startBeforeActivity("Event_0vdbr16")
	    	    .setVariable("TargetTask", waitStateTaskId)
	    	    .setVariable("IsAsyncAfter", false)
	    	    .execute();
	        }
	        return null;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
			
	}
}

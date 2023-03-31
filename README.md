# camunda-custom-incident-handler
An incident handler for Camunda. Handling incidents from Java and divert flow to the another element.


When incident occurs on the engine, "GenericExceptionHandler.class" is handling incident and diverts flow to the another element. Handler saves as variable id of last wait state and if desired process can be continued from last wait state with another execution listener.


First of all we need to get execution and cast to ExecutionEntity and get activity. After than task id of last wait state can be accessible
```Java
ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
			ExecutionEntity execution = (ExecutionEntity) engine.getRuntimeService().createExecutionQuery().executionId(context.getExecutionId()).singleResult();
	        ActivityImpl act = execution.getActivity();
            String waitStateTaskId="";
            
            for (PvmTransition transition : act.getOutgoingTransitions()) {
            	  waitStateTaskId =  transition.getSource().getId();
			}	
```

The incident task can be canceled and divert flow to another element if desired. When asynchronous continuous is selected(before/after) on a task, last wait state updates as that task. With code down below saving wait state task id and asynchronous continuous state as variable. Process continues from "Event_0vdbr16" element.

```Java
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
```

Custom incident handler must be set to process engine before engine start. Custom incident handler can be set with code down below.

```Java
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
```

# How to add plugin to Process Engine

Clean and install project and copy-paste to camunda_path/configuration/userlib
![image](https://user-images.githubusercontent.com/72344057/229051255-63c7fb3a-3a3e-49f1-b934-8a2ee9b14211.png)

Before process engine start, add plugin to default.yml or production.yml like down below.
![image](https://user-images.githubusercontent.com/72344057/229050801-9f90b9a2-ba38-48db-b553-623d62bddc43.png)

package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.SMModelParser;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

public class NoInterventionAction extends SimpleAction {
    private static final Logger logger = LoggerFactory.getLogger(NoInterventionAction.class);

    private PersonalDecisionMaker pdm;

    public NoInterventionAction(String name, Domain domain, PersonalDecisionMaker pdm) {
        super(name, domain);
        this.pdm = pdm;
    }

    @Override
    protected State performActionHelper(State s, GroundedAction groundedAction) {
        // wait for the new PatientState for determining the next RL state
        logger.debug("No int action will wait for patient state");
        pdm.waitForPatientState();
        logger.debug("No int action got patient state");

        SMState nextState = pdm.getLastState();
        nextState.printState();
        return nextState;
    }
}
package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.action;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.SMState;

public class DeliverInterventionAction extends SMAction {
    private static final Logger logger = LoggerFactory.getLogger(DeliverInterventionAction.class);

    public DeliverInterventionAction(String name, Domain domain, PersonalDecisionMaker pdm) {
        super(name, domain, pdm);
    }

    @Override
    protected State performActionHelper(State s, GroundedAction groundedAction) {
        // wait for the new PatientState for determining the next RL state
        logger.debug("Deliver int action will wait for patient state");
        pdm.waitForPatientState();
        logger.debug("Deliver int action got patient state");

        SMState nextState = pdm.getEnvironment().getCurrentObservation();
        nextState.printState();
        return nextState;
    }
}
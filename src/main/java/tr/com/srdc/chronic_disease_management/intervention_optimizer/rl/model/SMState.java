package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.*;

public class SMState extends MutableState {
    private static final Logger logger = LoggerFactory.getLogger(DeliverInterventionAction.class);

    private boolean terminal;

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public void printState() {
        ObjectInstance o = this.getObjectsOfClass(CLASS_STATE_DATA).get(0);
        int goalAchivement = o.getIntValForAttribute(ATT_GOAL_ACHIEVEMENT);
        logger.debug("State: Achivement: " + goalAchivement + " Terminal: " + isTerminal());
    }
}

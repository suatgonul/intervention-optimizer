package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;

import java.time.LocalDateTime;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.ATT_GOAL_ACHIEVEMENT;
import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.CLASS_STATE_DATA;

public class SMState extends MutableState {
    private static final Logger logger = LoggerFactory.getLogger(SMState.class);

    private boolean terminal;
    private LocalDateTime stateTime;
    private Goal associatedGoal;

    public SMState() {
    }

    public SMState(SMState s) {
        super(s);
        this.terminal = s.terminal;
        this.stateTime = s.stateTime;
        this.associatedGoal = s.associatedGoal;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public LocalDateTime getStateTime() {
        return stateTime;
    }

    public void setStateTime(LocalDateTime stateTime) {
        this.stateTime = stateTime;
    }

    public Goal getAssociatedGoal() {
        return associatedGoal;
    }

    public void setAssociatedGoal(Goal associatedGoal) {
        this.associatedGoal = associatedGoal;
    }

    @Override
    public SMState copy() {
        return new SMState(this);
    }

    public void printState() {
        ObjectInstance o = this.getObjectsOfClass(CLASS_STATE_DATA).get(0);
        int goalAchivement = o.getIntValForAttribute(ATT_GOAL_ACHIEVEMENT);
        logger.debug("State: Achivement: " + goalAchivement + " Terminal: " + isTerminal());
    }
}

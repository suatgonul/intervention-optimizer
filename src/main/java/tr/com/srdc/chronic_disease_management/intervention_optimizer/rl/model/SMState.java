package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;

import java.time.LocalDateTime;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.*;

public class SMState extends MutableState {
    private static final Logger logger = LoggerFactory.getLogger(SMState.class);

    private boolean terminal;
    private String pid;
    private LocalDateTime stateTime;
    private Goal associatedGoal;

    public SMState() {
    }

    public SMState(SMState s) {
        super(s);
        this.terminal = s.terminal;
        this.pid = s.pid;
        this.stateTime = s.stateTime;
        this.associatedGoal = s.associatedGoal;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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
        int totalNumberOfInterventions = o.getIntValForAttribute(ATT_TOTAL_NUMBER_OF_INTERVENTIONS_SENT);
        int timeSinceLast = o.getIntValForAttribute(ATT_TIME_SINCE_LAST_INTERVENTION);
        int timeSinceSimilarLast = o.getIntValForAttribute(ATT_TIME_SINCE_LAST_SAME_TYPE_INTERVENTION);
        int goalAchievement = o.getIntValForAttribute(ATT_GOAL_ACHIEVEMENT);
        int habituation = o.getIntValForAttribute(ATT_HABITUATION);
        int timeOfDay = o.getIntValForAttribute(ATT_TIME_OF_DAY);
        int typeOfDay = o.getIntValForAttribute(ATT_TYPE_OF_DAY);

        logger.debug("State: Pid: {}, Goal: {}-{}, State time: {}, Terminal: {}", pid, associatedGoal.getBehaviour(), associatedGoal.getPeriod(),stateTime, isTerminal());
        logger.debug("State Model: Total: {}, Time Last: {}, Time Last Similar: {}, Achievement: {}, Habituation: {}, Time: {}, Type: {}", totalNumberOfInterventions, timeSinceLast, timeSinceSimilarLast, goalAchievement, habituation, timeOfDay, typeOfDay);

    }
}

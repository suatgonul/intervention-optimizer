package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import org.joda.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.*;

public class SMEnvironment extends SimulatedEnvironment {
    private SMState lastState;
    private GroundedAction lastAction;
    private LocalDateTime lastInterventionTime;
    private Map<String, LocalDateTime> lastInterventionTimesByBehaviour = new HashMap<>();
    private int totalNumberOfDeliveredInterventionsInEpisode = 0;

    public SMEnvironment(Domain domain, RewardFunction rf, TerminalFunction tf) {
        super(domain, rf, tf);
    }

    public SMState getLastState() {
        return lastState;
    }

    public void setLastState(SMState lastState) {
        this.lastState = lastState;
    }

    public GroundedAction getLastAction() {
        return lastAction;
    }

    public void setLastAction(GroundedAction lastAction) {
        this.lastAction = lastAction;
    }

    public LocalDateTime getLastInterventionTime() {
        return lastInterventionTime;
    }

    public void setLastInterventionTime(LocalDateTime lastInterventionTime) {
        this.lastInterventionTime = lastInterventionTime;
    }

    public Map<String, LocalDateTime> getLastInterventionTimesByBehaviour() {
        return lastInterventionTimesByBehaviour;
    }

    public void setLastInterventionTimesByBehaviour(Map<String, LocalDateTime> lastInterventionTimesByBehaviour) {
        this.lastInterventionTimesByBehaviour = lastInterventionTimesByBehaviour;
    }

    public int getTotalNumberOfDeliveredInterventionsInEpisode() {
        return totalNumberOfDeliveredInterventionsInEpisode;
    }

    public void setTotalNumberOfDeliveredInterventionsInEpisode(int totalNumberOfDeliveredInterventionsInEpisode) {
        this.totalNumberOfDeliveredInterventionsInEpisode = totalNumberOfDeliveredInterventionsInEpisode;
    }

    @Override
    public GoalPerformanceRewardFunction getRf() {
        return (GoalPerformanceRewardFunction) super.getRf();
    }

    public void updateEnvironmentForDeliveredAction(GroundedAction action) {
        lastAction = action;
        if (action.action.getName().equals(ACTION_DELIVER_INTERVENTION)) {
            lastInterventionTime = new LocalDateTime();
            totalNumberOfDeliveredInterventionsInEpisode++;
            lastInterventionTimesByBehaviour.put(lastState.getAssociatedGoal().getBehaviour(), lastInterventionTime);
        }
    }
}
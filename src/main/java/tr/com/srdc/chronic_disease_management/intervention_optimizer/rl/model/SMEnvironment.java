package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.ACTION_DELIVER_INTERVENTION;

public class SMEnvironment extends SimulatedEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(SMEnvironment.class);

    private GroundedAction lastAction;
    private LocalDateTime lastInterventionTime;
    private Map<String, LocalDateTime> lastInterventionTimesByBehaviour = new HashMap<>();
    private int totalNumberOfDeliveredInterventionsInEpisode = 0;

    public SMEnvironment(Domain domain, RewardFunction rf, TerminalFunction tf) {
        super(domain, rf, tf);
    }

    public GroundedAction getLastAction() {
        return lastAction;
    }

    public LocalDateTime getLastInterventionTime() {
        return lastInterventionTime;
    }

    public Map<String, LocalDateTime> getLastInterventionTimesByBehaviour() {
        return lastInterventionTimesByBehaviour;
    }

    public int getTotalNumberOfDeliveredInterventionsInEpisode() {
        return totalNumberOfDeliveredInterventionsInEpisode;
    }

    @Override
    public GoalPerformanceRewardFunction getRf() {
        return (GoalPerformanceRewardFunction) super.getRf();
    }

    @Override
    public SMState getCurrentObservation() {
        SMState copy = new SMState((SMState) this.curState);
        return copy;
    }

    @Override
    public void resetEnvironment() {
        super.resetEnvironment();
        lastAction = null;
        lastInterventionTime = null;
        lastInterventionTimesByBehaviour = new HashMap<>();
        totalNumberOfDeliveredInterventionsInEpisode = 0;
    }

    public void updateEnvironmentForDeliveredAction(GroundedAction action) {
        lastAction = action;
        if (action.action.getName().equals(ACTION_DELIVER_INTERVENTION)) {
            //lastInterventionTime = LocalDateTime.now();
            lastInterventionTime = getCurrentObservation().getStateTime();
            totalNumberOfDeliveredInterventionsInEpisode++;
            SMState lastObservedState = getCurrentObservation();
            lastInterventionTimesByBehaviour.put(lastObservedState.getAssociatedGoal().getBehaviour(), lastInterventionTime);
        }
    }
}
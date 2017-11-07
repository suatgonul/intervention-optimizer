package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class GoalPerformanceRewardFunction implements RewardFunction {
    @Override
    public double reward(State s, GroundedAction a, State sprime) {
        return 0;
    }
}

package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;

import java.util.List;

public class GoalPerformanceRewardFunction implements RewardFunction {

    private PersonalDecisionMaker pdm;

    @Override
    public double reward(State s, GroundedAction a, State sprime) {
        //TODO
        return 0;
    }

    public void rewardForAchievedGoal(List<EpisodeAnalysis> previousEpisodes) {
        Goal goal = pdm.getEnvironment().getLastState().getAssociatedGoal();
        //TODO
    }
}

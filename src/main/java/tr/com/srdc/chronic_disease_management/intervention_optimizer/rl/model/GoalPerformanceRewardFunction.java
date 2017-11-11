package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.*;

public class GoalPerformanceRewardFunction implements RewardFunction {

    private PersonalDecisionMaker pdm;

    @Override
    public double reward(State s, GroundedAction a, State sprime) {
        SMState sms = (SMState) s;
        SMState smsprime = (SMState) sprime;

        ObjectInstance sData = s.getObject(CLASS_STATE_DATA);
        ObjectInstance sprimeData = sprime.getObject(CLASS_STATE_DATA);
        int sHabitutation = sData.getIntValForAttribute(ATT_HABITUATION);
        int sprimeHabituation = sprimeData.getIntValForAttribute(ATT_HABITUATION);
        int sGoalAchievement = sData.getIntValForAttribute(ATT_GOAL_ACHIEVEMENT);
        int sprimeGoalAchievement = sprimeData.getIntValForAttribute(ATT_GOAL_ACHIEVEMENT);

        double reward = 0;
        if(sprimeHabituation > sHabitutation || sprimeGoalAchievement > sGoalAchievement) {
            reward = 10;
        }

        return reward;
    }

    public double getRewardForPastEpisode(String period) {
        double reward = 0;
        if(period.contentEquals("DAY")) {
            reward = 5;
        } else if(period.contentEquals("WEEK")) {
            reward = 2;
        } else if(period.contentEquals("MONTH")) {
            reward = 1;
        }
        return reward;
    }
}

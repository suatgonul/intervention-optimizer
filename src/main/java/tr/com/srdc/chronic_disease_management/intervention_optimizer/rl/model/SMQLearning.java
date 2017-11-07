package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.options.Option;
import burlap.behavior.singleagent.options.support.EnvironmentOptionOutcome;
import burlap.behavior.valuefunction.QValue;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.environment.Environment;
import burlap.oomdp.singleagent.environment.EnvironmentOutcome;
import burlap.oomdp.statehashing.HashableState;
import burlap.oomdp.statehashing.HashableStateFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;

public class SMQLearning extends QLearning {
    private PersonalDecisionMaker pdm;

    public SMQLearning(Domain domain, double gamma, HashableStateFactory hashingFactory, double qInit, double learningRate, Policy learningPolicy, int maxEpisodeSize, PersonalDecisionMaker pdm) {
        super(domain, gamma, hashingFactory, qInit, learningRate, learningPolicy, maxEpisodeSize);
        this.pdm = pdm;
    }

    @Override
    public EpisodeAnalysis runLearningEpisode(Environment env, int maxSteps) {

        this.toggleShouldAnnotateOptionDecomposition(shouldAnnotateOptions);

        State initialState = env.getCurrentObservation();

        EpisodeAnalysis ea = new EpisodeAnalysis(initialState);
        HashableState curState = this.stateHash(initialState);
        eStepCounter = 0;

        maxQChangeInLastEpisode = 0.;
        while(!env.isInTerminalState() && (eStepCounter < maxSteps || maxSteps == -1)){

            GroundedAction action = (GroundedAction)learningPolicy.getAction(curState.s);
            QValue curQ = this.getQ(curState, action);

            pdm.notifyWithSelectedAction(action);

            EnvironmentOutcome eo = action.executeIn(env);


            HashableState nextState = this.stateHash(eo.op);
            double maxQ = 0.;

            if(!eo.terminated){
                maxQ = this.getMaxQ(nextState);
            }

            //manage option specifics
            double r = eo.r;
            double discount = eo instanceof EnvironmentOptionOutcome ? ((EnvironmentOptionOutcome)eo).discount : this.gamma;
            int stepInc = eo instanceof EnvironmentOptionOutcome ? ((EnvironmentOptionOutcome)eo).numSteps : 1;
            eStepCounter += stepInc;

            if(action.action.isPrimitive() || !this.shouldAnnotateOptions){
                ea.recordTransitionTo(action, nextState.s, r);
            }
            else{
                ea.appendAndMergeEpisodeAnalysis(((Option)action.action).getLastExecutionResults());
            }



            double oldQ = curQ.q;

            //update Q-value
            curQ.q = curQ.q + this.learningRate.pollLearningRate(this.totalNumberOfSteps, curState.s, action) * (r + (discount * maxQ) - curQ.q);

            double deltaQ = Math.abs(oldQ - curQ.q);
            if(deltaQ > maxQChangeInLastEpisode){
                maxQChangeInLastEpisode = deltaQ;
            }

            //move on polling environment for its current state in case it changed during processing
            curState = this.stateHash(env.getCurrentObservation());
            this.totalNumberOfSteps++;


        }

        if(episodeHistory.size() >= numEpisodesToStore){
            episodeHistory.poll();
        }
        episodeHistory.offer(ea);

        return ea;

    }
}

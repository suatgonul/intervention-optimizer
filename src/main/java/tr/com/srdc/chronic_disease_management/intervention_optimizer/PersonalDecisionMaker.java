package tr.com.srdc.chronic_disease_management.intervention_optimizer;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.*;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.ACTION_DELIVER_INTERVENTION;

public class PersonalDecisionMaker {
    private static final Logger logger = LoggerFactory.getLogger(PersonalDecisionMaker.class);

    private SMEnvironment environment;

    private PatientStateLock patientStateLock = new PatientStateLock();
    private BooleanLock learnerInitializedLock = new BooleanLock();
    private BooleanLock selectedActionLock = new BooleanLock();

    public SMEnvironment getEnvironment() {
        return environment;
    }

    public boolean isInterventionDeliverySuitable(Goal goal, SMState smState) {
        notifyWithSetPatientState(smState);
        logger.debug("Will wait for selected action");
        waitForSelectedAction();
        logger.debug("Selected action available");
        GroundedAction lastAction = environment.getLastAction();
        logger.debug("Last action: " + lastAction);
        if(lastAction.action.getName().equals(ACTION_DELIVER_INTERVENTION)) {
            return true;
        } else {
            return false;
        }
    }

    public void startLearning() {
        Thread learningThread = new Thread(() -> {
            InterventionDecisionMakerDomainGenerator dg = new InterventionDecisionMakerDomainGenerator(this);
            SADomain domain = dg.generateDomain();
            TerminalFunction tf = new DayTerminalFunction();
            RewardFunction rf = new GoalPerformanceRewardFunction();
            environment = new SMEnvironment(domain, rf, tf);
            LearningAgent learningAgent = getLearningAlgorithm(domain);
            synchronized (learnerInitializedLock) {
                learnerInitializedLock.notify();
            }

            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // wait for the new request coming from Communication Engine at the beginning of each episode
                logger.debug("Wait at the beginning of episode");
                waitForPatientState();
                learningAgent.runLearningEpisode(environment);
                environment.resetEnvironment();

                selectedActionLock.setCondition(false);

                logger.debug("Episode completed");
            }
        });
        learningThread.start();
    }

    private LearningAgent getLearningAlgorithm(final Domain domain) {
        final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        final PersonalDecisionMaker pdm = this;
        return (new LearningAgentFactory() {
            @Override
            public String getAgentName() {
                return "SMQ-Learning Lambda_0.8 Gamma_0.1 LR_0.1";
            }

            @Override
            public LearningAgent generateAgent() {
                EpsilonGreedy epsilonGreedy = new EpsilonGreedy(0.1);
                SMQLearning qLearning = new SMQLearning(domain, 0.1, hashingFactory, 0, 0.1, epsilonGreedy, Integer.MAX_VALUE, pdm);
                epsilonGreedy.setSolver(qLearning);
                return qLearning;
            }
        }).generateAgent();
    }

    public void checkLearningThreadStarted() throws DecisionMakerException {
        synchronized (learnerInitializedLock) {

            if (learnerInitializedLock.getCondition() == false) {
                try {
                    logger.debug("Will wait for the learning environment to be established");
                    learnerInitializedLock.wait();
                    logger.debug("Learning environment established");

                } catch (InterruptedException e) {
                    String msg = "Interrupted while waiting for the learning thread to start";
                    logger.error(msg, e);
                    throw new DecisionMakerException(msg, e);

                }
            }
        }
    }

    public void notifyWithSetPatientState(SMState smState) {
        synchronized (patientStateLock) {
            environment.setCurStateTo(smState);
            patientStateLock.setPatientState(smState);
            patientStateLock.notify();
            logger.debug("Patient state set");
            smState.printState();
        }
    }

    public void waitForPatientState() {
        synchronized (patientStateLock) {
            if(patientStateLock.getPatientState() == null) {
                try {
                    logger.debug("Condition false... Will wait for patient state");
                    patientStateLock.wait();
                    logger.debug("Patient state received");
                    patientStateLock.getPatientState().printState();

                } catch (InterruptedException e) {
                    String msg = "Learning thread interrupted while waiting patient state";
                    logger.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            } else {
                logger.debug("Non-null patient state");
                patientStateLock.getPatientState().printState();
            }
            patientStateLock.setPatientState(null);
        }
    }

    public void notifyWithSelectedAction(GroundedAction action) {
        synchronized (selectedActionLock) {
            environment.updateEnvironmentForDeliveredAction(action);
            selectedActionLock.setCondition(true);
            selectedActionLock.notify();
            logger.debug("Action selected. Action: " + action.actionName());
        }
    }

    public void waitForSelectedAction() {
        synchronized (selectedActionLock) {
            if(!selectedActionLock.getCondition()) {
                try {
                    logger.debug("Condition false... Will wait for selected action");
                    selectedActionLock.wait();
                    logger.debug("Patient state received. Learning continues");


                } catch (InterruptedException e) {
                    String msg = "Learning thread interrupted while waiting selected action";
                    logger.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            } else {
                logger.debug(environment.getLastAction() == null ? "null" : environment.getLastAction().actionName());
            }
            selectedActionLock.setCondition(false);
        }
    }

    public class PatientStateLock {
        private SMState patientState = null;

        public SMState getPatientState() {
            return patientState;
        }

        public void setPatientState(SMState patientState) {
            this.patientState = patientState;
        }
    }

    public class BooleanLock {
        private boolean condition = false;

        public boolean getCondition() {
            return condition;
        }

        public void setCondition(boolean condition) {
            this.condition = condition;
        }
    }
}

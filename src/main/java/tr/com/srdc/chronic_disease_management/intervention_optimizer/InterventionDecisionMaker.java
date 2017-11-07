package tr.com.srdc.chronic_disease_management.intervention_optimizer;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.SMState;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.SMModelParser;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InterventionDecisionMaker {
    private static final Logger logger = LoggerFactory.getLogger(PersonalDecisionMaker.class);

    private Map<String, PersonalDecisionMaker> decisionMakers = new HashMap();

    private static InterventionDecisionMaker instance;

    private InterventionDecisionMaker() {

    }

    public static InterventionDecisionMaker getInstance() {
        if (instance == null) {
            instance = new InterventionDecisionMaker();
        }
        return instance;
    }

    public static void main(String[] args) throws DecisionMakerException {

        InterventionDecisionMaker idm = InterventionDecisionMaker.getInstance();
        // episode
        for (int i = 0; i < 2; i++) {
            logger.debug("E: " + (i + 1));
            // action in episode
            for (int j = 0; j < 4; j++) {
                idm.isInterventionDeliverySuitable("bgm", "{\"patientId\" : \"pid1\", \"goalAchievement\" : " + j + "}", new LocalDateTime());

                logger.debug("suitability checked... e: " + (i + 1) + " a: " + (j + 1));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            idm.submitLastPatientState("{\n" +
                    "\t\"behaviour\" : \"bgm\", \n" +
                    "\t\"period\" : \"DAY\"\n" +
                    "}", "{\"patientId\" : \"pid1\"}");
            logger.debug("Last state sent");
        }
    }

    public boolean isInterventionDeliverySuitable(String goalJson, String patientStateJson, LocalDateTime localStateTime) throws DecisionMakerException {
        // parse patient state and goal
        PatientState patientState;
        Goal goal;
        try {
            patientState = SMModelParser.parsePatientState(patientStateJson);
            goal = SMModelParser.parseGoal(goalJson);
            patientState.setStateRetrievalTime(localStateTime);
            patientState.setRelatedBehaviour(goal.getBehaviour());
        } catch (IOException e) {
            throw new DecisionMakerException(e.getMessage(), e);
        }

        PersonalDecisionMaker pdm = decisionMakers.get(patientState.getPatientId());
        if (decisionMakers.containsKey(patientState.getPatientId()) == false) {
            pdm = new PersonalDecisionMaker();
            decisionMakers.put(patientState.getPatientId(), pdm);
            pdm.startLearning();

            // wait for the learning thread to start and be waiting for the patient state
            pdm.checkLearningThreadStarted();
        }

        SMState state = SMModelParser.createSMStateFromPatientState(goal, patientState);
        boolean decision = pdm.isInterventionDeliverySuitable(goal, state);
        return decision;
    }

    public void submitLastPatientState(String goalJson, String patientStateJson) throws DecisionMakerException {
        // parse patient state and goal
        PatientState patientState;
        Goal goal;
        try {
            patientState = SMModelParser.parsePatientState(patientStateJson);
            goal = SMModelParser.parseGoal(goalJson);
            patientState.setRelatedBehaviour(goal.getBehaviour());

        } catch (IOException e) {
            throw new DecisionMakerException(e.getMessage(), e);
        }

        SMState smState;
        try {
            patientState = SMModelParser.parsePatientState(patientStateJson);
            smState = SMModelParser.createSMStateFromPatientState(goal, patientState);
            smState.setTerminal(true);
        } catch (IOException e) {
            throw new DecisionMakerException(e.getMessage(), e);
        }

        PersonalDecisionMaker pdm = decisionMakers.get(patientState.getPatientId());
        pdm.notifyWithSetPatientState(smState);
    }

    public PersonalDecisionMaker getPdm(String patientId) {
        return decisionMakers.get(patientId);
    }
}

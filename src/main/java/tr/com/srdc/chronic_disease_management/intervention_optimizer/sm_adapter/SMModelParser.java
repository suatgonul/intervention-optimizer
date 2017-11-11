package tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter;

import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.InterventionDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.SMState;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

import java.io.IOException;
import java.time.LocalDateTime;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.*;
import static tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.SMModelCalculator.*;

public class SMModelParser {
    private static final Logger logger = LoggerFactory.getLogger(SMModelParser.class);

    private static ObjectMapper parser = new ObjectMapper();
    static {
        parser = new ObjectMapper();
        parser.findAndRegisterModules();
    }

    public static void main(String[] args) throws Exception {
        String jsonInString = "{\n" +
                "\t\"patientId\" : \"pid1\", \n" +
                "\t\"goalAchievement\" : 3,\n" +
                "\t\"recents\": [\n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0}, \n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 0.0},\n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0}, \n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0},\n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0}, \n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 0.0},\n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0}, \n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0},\n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0}, \n" +
                "\t\t{\"day\": \"2017-07-10\", \"value\": 1.0}\t\t\n" +
                "\t]\n" +
                "}";
        PatientState state = parsePatientState(jsonInString);
        logger.debug(state.getPatientId());
        logger.debug("" + state.getGoalAchievement());
        logger.debug("" + state.getLastDays().size());
    }

    public static PatientState parsePatientState(String patientStateJson) throws IOException {
        try {
            PatientState patientState = parser.readValue(patientStateJson, PatientState.class);
            return patientState;
        } catch (IOException e) {
            String msg = "Failed to parse patient state";
            logger.error(msg, e);
            throw e;
        }
    }

    public static Goal parseGoal(String goalJson) throws IOException {
        try {
            Goal goal = parser.readValue(goalJson, Goal.class);
            return goal;
        } catch (IOException e) {
            String msg = "Failed to parse goal";
            logger.error(msg, e);
            throw e;
        }
    }

    public static SMState createSMStateFromPatientState(Goal goal, PatientState patientState) {
        SMState smState = new SMState();
        smState.setAssociatedGoal(goal);
        smState.setStateTime(patientState.getStateRetrievalTime());

        PersonalDecisionMaker pdm = InterventionDecisionMaker.getInstance().getPdm(patientState.getPatientId());
        String relatedBehaviour = patientState.getRelatedBehaviour();
        LocalDateTime lastInterventionTime = pdm.getEnvironment().getLastInterventionTime();
        LocalDateTime lastSimilarInterventionTime = pdm.getEnvironment().getLastInterventionTimesByBehaviour().get(relatedBehaviour);
        LocalDateTime interventionTime = patientState.getStateRetrievalTime();

        ObjectInstance objectInstance = new MutableObjectInstance(pdm.getEnvironment().getDomain().getObjectClass(CLASS_STATE_DATA), CLASS_STATE_DATA);
        objectInstance.setValue(ATT_GOAL_ACHIEVEMENT, patientState.getGoalAchievement());
        objectInstance.setValue(ATT_HABITUATION, calculateHabituation(patientState) );
        objectInstance.setValue(ATT_TOTAL_NUMBER_OF_INTERVENTIONS_SENT, pdm.getEnvironment().getTotalNumberOfDeliveredInterventionsInEpisode());
        objectInstance.setValue(ATT_TIME_SINCE_LAST_INTERVENTION, getTimeSince(lastSimilarInterventionTime));
        objectInstance.setValue(ATT_TIME_SINCE_LAST_INTERVENTION, getTimeSince(lastInterventionTime));
        objectInstance.setValue(ATT_TYPE_OF_DAY, getDayType(interventionTime));
        objectInstance.setValue(ATT_TIME_OF_DAY, getTimeOfDay(interventionTime));

        smState.addObject(objectInstance);
        return smState;
    }


}

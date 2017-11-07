package tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter;

import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.InterventionDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.SMState;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.ActivityPerformance;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

import java.io.IOException;
import java.util.List;

import static tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.InterventionDecisionMakerDomainGenerator.*;

public class SMModelParser {
    private static final Logger logger = LoggerFactory.getLogger(SMModelParser.class);

    private static ObjectMapper parser = new ObjectMapper();

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

    public static SMState parseSMStateFromPatientState(String patientStateJson) throws IOException {
        PatientState patientState = parsePatientState(patientStateJson);
        SMState smState = createSMStateFromPatientState(patientState);
        return smState;
    }

    public static SMState createSMStateFromPatientState(Goal goal, PatientState patientState) {
        SMState smState = new SMState();
        PersonalDecisionMaker pdm = InterventionDecisionMaker.getInstance().getPdm(patientState.getPatientId());
        String relatedBehaviour = patientState.getRelatedBehaviour();
        LocalDateTime lastInterventionTime = pdm.getLastInterventionTime();
        LocalDateTime lastSimilartInterventionTime = pdm.getLastInterventionTimesByBehaviour().get(relatedBehaviour);
        LocalDateTime interventionTime = patientState.getStateRetrievalTime();

        ObjectInstance objectInstance = new MutableObjectInstance(pdm.getEnvironment().getDomain().getObjectClass(CLASS_STATE_DATA), CLASS_STATE_DATA);
        objectInstance.setValue(ATT_GOAL_ACHIEVEMENT, patientState.getGoalAchievement());
        objectInstance.setValue(ATT_HABITUATION, calculateHabituation(goal, patientState) );
        objectInstance.setValue(ATT_TOTAL_NUMBER_OF_INTERVENTIONS_SENT, pdm.getTotalNumberOfDeliveredInterventionsInEpisode());
        objectInstance.setValue(ATT_TIME_SINCE_LAST_INTERVENTION, getTimeSince(lastSimilartInterventionTime));
        objectInstance.setValue(ATT_TIME_SINCE_LAST_INTERVENTION, getTimeSince(lastInterventionTime));
        objectInstance.setValue(ATT_TYPE_OF_DAY, getDayType(interventionTime));
        objectInstance.setValue(ATT_TIME_OF_DAY, getTimeOfDay(interventionTime));

        // TODO add state parameters
        smState.addObject(objectInstance);
        return smState;
    }

    private static int calculateHabituation(Goal goal, PatientState patientState) {
        double habituation = 0;

        // daily goal
        if (goal.getPeriod() == 0) {
            List<ActivityPerformance> activityPerformances = patientState.getLastDays();
            int pastIntervals[] = new int[]{1, 3, 5, 10};
            int offset = 0;
            for (int t = 0; t < pastIntervals.length; t++) {
                double tempTotal = 0;
                for (int i = 0; i < pastIntervals[t] && offset < activityPerformances.size(); i++, offset++) {
                    tempTotal += activityPerformances.get(offset).getValue();
                }
                tempTotal /= pastIntervals.length;
                habituation += tempTotal;

                if (offset == activityPerformances.size()) {
                    break;
                }
            }

            // weekly goal
        } else {
            List<ActivityPerformance> activityPerformances = patientState.getLastWeeks();
            for (int i = 0; i < 3 && i < activityPerformances.size(); i++) {
                if (i == 0) {
                    habituation += activityPerformances.get(i).getValue() * 2;
                } else {
                    habituation += activityPerformances.get(i).getValue();
                }
            }
        }

        return (int) habituation;
    }

    private static int getTimeSince(LocalDateTime earlierTime) {
        int timeSinceInMinutes = Minutes.minutesBetween(earlierTime, new LocalDateTime()).getMinutes();
        if (timeSinceInMinutes <= 5) {
            return 0;
        } else if (timeSinceInMinutes > 5 & timeSinceInMinutes <= 15) {
            return 1;
        } else if (timeSinceInMinutes > 15 && timeSinceInMinutes <= 30) {
            return 2;
        } else if (timeSinceInMinutes > 30 && timeSinceInMinutes <= 60) {
            return 3;
        } else if (timeSinceInMinutes > 60 && timeSinceInMinutes <= 120) {
            return 4;
        } else {
            return 5;
        }
    }

    private static int getTimeOfDay(LocalDateTime time) {
        int hour = time.getHourOfDay();
        if (hour < 11) {
            return 0;
        } else if (hour >= 11 && hour < 15) {
            return 1;
        } else if (hour >= 11 && hour < 18) {
            return 2;
        } else if (hour >= 11 && hour < 21) {
            return 3;
        } else {
            return 4;
        }
    }

    private static int getDayType(LocalDateTime time) {
        int dayOfWeek = time.getDayOfWeek();
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            return 1;
        } else {
            return 0;
        }
    }
}

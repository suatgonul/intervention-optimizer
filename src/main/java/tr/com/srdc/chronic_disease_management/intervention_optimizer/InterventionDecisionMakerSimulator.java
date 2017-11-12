package tr.com.srdc.chronic_disease_management.intervention_optimizer;

import burlap.oomdp.statehashing.HashableState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.SMState;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.SMModelParser;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.ActivityPerformance;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class InterventionDecisionMakerSimulator {
    private static final Logger logger = LoggerFactory.getLogger(InterventionDecisionMakerSimulator.class);

    private static Goal dailyGoal = new Goal("bgm", "DAY");
    private static Goal weeklyGoal = new Goal("bgm", "WEEK");
    private static Goal monthlyGoal = new Goal("bgm", "MONTH");
    private static ObjectMapper parser = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        parser.findAndRegisterModules();
        simulateCommunicationEngine();
    }

    private static void simulateCommunicationEngine() throws Exception {
        InterventionDecisionMaker idm = InterventionDecisionMaker.getInstance();
        LocalDateTime stateTime = LocalDateTime.of(2017, 7, 1, 9, 1, 1);
        PatientState patientState = new PatientState();
        patientState.setRelatedBehaviour("bgm");
        patientState.setPatientId("pid1");
        patientState.setLastDays(new ArrayList<>());

        while (stateTime.getMonth().getValue() != 12) {
            // morning check
            patientState.setStateTime(stateTime);
            patientState.setGoalAchievement(0);
            logger.debug("Morning check: {} ", patientState.getStateTime());
            idm.isInterventionDeliverySuitable(parser.writeValueAsString(dailyGoal), parser.writeValueAsString(patientState), stateTime);
            logger.debug("Morning check done: {} ", patientState.getStateTime());

            // afternoon check
            stateTime = stateTime.plus(5, ChronoUnit.HOURS);
            patientState.setStateTime(stateTime);
            patientState.setGoalAchievement(0);
            logger.debug("Afternoon check: {} ", patientState.getStateTime());
            idm.isInterventionDeliverySuitable(parser.writeValueAsString(dailyGoal), parser.writeValueAsString(patientState), stateTime);
            logger.debug("Afternoon check done: {} ", patientState.getStateTime());

            // evening check
            stateTime = stateTime.plus(4, ChronoUnit.HOURS);
            patientState.setStateTime(stateTime);
            patientState.setGoalAchievement(1);
            logger.debug("Evening check: {} ", patientState.getStateTime());
            idm.isInterventionDeliverySuitable(parser.writeValueAsString(dailyGoal), parser.writeValueAsString(patientState), stateTime);
            logger.debug("Evening check done: {} ", patientState.getStateTime());

            // night check
            stateTime = stateTime.plus(4, ChronoUnit.HOURS);
            patientState.setStateTime(stateTime);
            patientState.setGoalAchievement(1);
            logger.debug("Night check: {} ", patientState.getStateTime());
            idm.isInterventionDeliverySuitable(parser.writeValueAsString(dailyGoal), parser.writeValueAsString(patientState), stateTime);
            logger.debug("Night check done: {} ", patientState.getStateTime());

            // send last state
            logger.debug("Last state: {}", patientState.getStateTime());
            stateTime = stateTime.plus(3, ChronoUnit.HOURS);
            patientState.setStateTime(stateTime);
            LocalDateTime lastOfMonth = stateTime.with(TemporalAdjusters.lastDayOfMonth());
            if(stateTime.get(ChronoField.DAY_OF_MONTH) == lastOfMonth.get(ChronoField.DAY_OF_MONTH)) {
                patientState.setGoalAchievement(1);
                idm.submitLastPatientState(parser.writeValueAsString(monthlyGoal), parser.writeValueAsString(patientState));

            } else if(stateTime.getDayOfWeek().getValue() == 7) {
                patientState.setGoalAchievement(1);
                idm.submitLastPatientState(parser.writeValueAsString(weeklyGoal), parser.writeValueAsString(patientState));

            } else {
                patientState.setGoalAchievement(1);
                idm.submitLastPatientState(parser.writeValueAsString(dailyGoal), parser.writeValueAsString(patientState));
            }

            logger.debug("Last state done: {}", patientState.getStateTime());
            patientState.getLastDays().add(new ActivityPerformance("", 0.75));

            // set time for the next day
            stateTime = stateTime.plus(8, ChronoUnit.HOURS);
        }

        idm.terminateLearning("pid1");
    }
}

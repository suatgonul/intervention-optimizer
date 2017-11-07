import org.junit.Test;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.SMModelCalculator;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.ActivityPerformance;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HabitCalculationTest {
    @Test
    public void testFullActivityForDailyActivity() {
        Goal goal = new Goal();
        goal.setBehaviour("bgm");
        goal.setPeriod("DAY");

        PatientState patientState = new PatientState();
        List<ActivityPerformance> lastDays = new ArrayList<>();
        patientState.setLastDays(lastDays);

        for(int i = 0; i<19; i++) {
            ActivityPerformance ap = new ActivityPerformance();
            ap.setValue(1.0);
            lastDays.add(ap);
        }

        int habituation = SMModelCalculator.calculateHabituation(goal, patientState);
        assertEquals(4, habituation);
    }

    @Test
    public void testFullActivityForDailyActivity_14Days() {
        Goal goal = new Goal();
        goal.setBehaviour("bgm");
        goal.setPeriod("DAY");

        PatientState patientState = new PatientState();
        List<ActivityPerformance> lastDays = new ArrayList<>();
        patientState.setLastDays(lastDays);

        for(int i = 0; i<14; i++) {
            ActivityPerformance ap = new ActivityPerformance();
            ap.setValue(1.0);
            lastDays.add(ap);
        }

        int habituation = SMModelCalculator.calculateHabituation(goal, patientState);
        assertEquals(4, habituation);
    }

    @Test
    public void testFullActivityForDailyActivity_13Days() {
        Goal goal = new Goal();
        goal.setBehaviour("bgm");
        goal.setPeriod("DAY");

        PatientState patientState = new PatientState();
        List<ActivityPerformance> lastDays = new ArrayList<>();
        patientState.setLastDays(lastDays);

        for(int i = 0; i<13; i++) {
            ActivityPerformance ap = new ActivityPerformance();
            ap.setValue(1.0);
            lastDays.add(ap);
        }

        int habituation = SMModelCalculator.calculateHabituation(goal, patientState);
        assertEquals(3, habituation);
    }

    @Test
    public void testFullActivityForDailyActivity_9Days() {
        Goal goal = new Goal();
        goal.setBehaviour("bgm");
        goal.setPeriod("DAY");

        PatientState patientState = new PatientState();
        List<ActivityPerformance> lastDays = new ArrayList<>();
        patientState.setLastDays(lastDays);

        for(int i = 0; i<9; i++) {
            ActivityPerformance ap = new ActivityPerformance();
            ap.setValue(1.0);
            lastDays.add(ap);
        }

        int habituation = SMModelCalculator.calculateHabituation(goal, patientState);
        assertEquals(3, habituation);
    }
}

package tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.ActivityPerformance;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.Goal;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

import java.util.List;

public class SMModelCalculator {
    public static int calculateHabituation(Goal goal, PatientState patientState) {
        double habituation = 0;

        // daily goal
        if (goal.getPeriod().equals(Goal.Period.DAY.name())) {
            List<ActivityPerformance> activityPerformances = patientState.getLastDays();
            int pastIntervals[] = new int[]{1, 3, 5, 10};
            int offset = 0;
            for (int t = 0; t < pastIntervals.length; t++) {
                double tempTotal = 0;
                for (int i = 0; i < pastIntervals[t] && offset < activityPerformances.size(); i++, offset++) {
                    tempTotal += activityPerformances.get(offset).getValue();
                }
                tempTotal /= pastIntervals[t];
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

        return (int) Math.round(habituation);
    }

    public static int getTimeSince(LocalDateTime earlierTime) {
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

    public static int getTimeOfDay(LocalDateTime time) {
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

    public static int getDayType(LocalDateTime time) {
        int dayOfWeek = time.getDayOfWeek();
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            return 1;
        } else {
            return 0;
        }
    }
}

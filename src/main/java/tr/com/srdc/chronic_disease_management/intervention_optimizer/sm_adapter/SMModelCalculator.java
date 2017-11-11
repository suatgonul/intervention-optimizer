package tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter;

import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.ActivityPerformance;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model.PatientState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SMModelCalculator {
    public static int calculateHabituation(PatientState patientState) {
        double habituation = 0;

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

        return (int) Math.round(habituation);
    }

    public static int getTimeSince(LocalDateTime earlierTime) {
        if(earlierTime == null) {
            return 0;
        }

        long timeSinceInMinutes = earlierTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
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
        int hour = time.getHour();
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
        int dayOfWeek = time.getDayOfWeek().getValue();
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            return 1;
        } else {
            return 0;
        }
    }
}

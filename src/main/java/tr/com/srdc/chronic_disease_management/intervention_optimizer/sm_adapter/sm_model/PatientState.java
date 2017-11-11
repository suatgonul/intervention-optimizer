package tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model;

import java.time.LocalDateTime;
import java.util.List;

public class PatientState {
    private String patientId;
    private String relatedBehaviour;
    private List<ActivityPerformance> lastDays;
    private int goalAchievement;
    private LocalDateTime stateRetrievalTime;
    private boolean terminal;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRelatedBehaviour() {
        return relatedBehaviour;
    }

    public void setRelatedBehaviour(String relatedBehaviour) {
        this.relatedBehaviour = relatedBehaviour;
    }

    public List<ActivityPerformance> getLastDays() {
        return lastDays;
    }

    public void setLastDays(List<ActivityPerformance> lastDays) {
        this.lastDays = lastDays;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public int getGoalAchievement() {
        return goalAchievement;
    }

    public void setGoalAchievement(int goalAchievement) {
        this.goalAchievement = goalAchievement;
    }

    public LocalDateTime getStateRetrievalTime() {
        return stateRetrievalTime;
    }

    public void setStateRetrievalTime(LocalDateTime stateRetrievalTime) {
        this.stateRetrievalTime = stateRetrievalTime;
    }
}

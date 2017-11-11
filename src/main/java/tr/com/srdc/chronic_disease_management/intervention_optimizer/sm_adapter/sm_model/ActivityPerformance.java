package tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model;

public class ActivityPerformance {
    private String day;
    private double value;

    public ActivityPerformance() {
    }

    public ActivityPerformance(String day, double value) {
        this.day = day;
        this.value = value;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

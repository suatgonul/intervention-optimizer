package tr.com.srdc.chronic_disease_management.intervention_optimizer.sm_adapter.sm_model;

public class Goal {
    private String behaviour;
    private String period;

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public enum Period {
        DAY, WEEK, MONTH
    }
}

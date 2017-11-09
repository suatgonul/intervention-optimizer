package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.singleagent.SADomain;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.action.DeliverInterventionAction;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.action.NoInterventionAction;

public class InterventionDecisionMakerDomainGenerator implements DomainGenerator {
    // Total of number of interventions in a day up to the moment of execution of the algoritm
    public static final String ATT_TOTAL_NUMBER_OF_INTERVENTIONS_SENT = "TotalNumberOfInterventionsSent";

    // An enumeration for the time elapsed since the last delivered intervention
    // 0-5 min: 0
    // 5-15 min: 1
    // 15-30 min: 2
    // 30-60 min: 3
    // 60-120 min: 4
    // > 120 min: 5
    public static final String ATT_TIME_SINCE_LAST_INTERVENTION = "TimeSinceLastIntervention";

    // The same enumeration with the previous attribute. This time for time elapsed since the last intervention from the
    // same intervention group.
    public static final String ATT_TIME_SINCE_LAST_SAME_TYPE_INTERVENTION = "TimeSinceLastSameTypeOfIntervention";

    // An enumeration for the goal achievement
    // UNKNOWN (-1)
    // ACHIEVED LESS THAN GOAL (0)
    // ALMOST ACHIEVED GOAL (1)
    // ABOUT TO ACHIEVE GOAL (2)
    // ACHIEVED GOAL (3)
    // ACHIEVED MORE THAN GOAL (4)
    public static final String ATT_GOAL_ACHIEVEMENT = "GoalAchievement";

    // A normalized score for habituation of performing the activity that the patient is supposed to perform
    // Habituation is calculated according to the
    public static final String ATT_HABITUATION = "Habituation";

    // An enumeration for the phase of the
    // Morning: 0
    // Noon: 1
    // Afternoon: 2
    // Evening: 3
    // Night: 4
    public static final String ATT_TIME_OF_DAY = "TimeOfDay";

    // An enumeration for the type of the day
    // Weekday: 0
    // Weekend: 1
    public static final String ATT_TYPE_OF_DAY = "TypeOfDay";

    public static final String CLASS_STATE_DATA = "StateData";
    public static final String ACTION_DELIVER_INTERVENTION = "DeliverIntervention";
    public static final String ACTION_NO_INTERVENTION = "NoIntervention";

    private PersonalDecisionMaker pdm;

    public InterventionDecisionMakerDomainGenerator(PersonalDecisionMaker pdm) {
        this.pdm = pdm;
    }

    public SADomain generateDomain() {
        SADomain domain = new SADomain();
        ObjectClass stateClass = new ObjectClass(domain, CLASS_STATE_DATA);

        Attribute attribute = new Attribute(domain, ATT_TOTAL_NUMBER_OF_INTERVENTIONS_SENT, Attribute.AttributeType.INT);
        attribute.setDiscValuesForRange(0, 10, 1);
        stateClass.addAttribute(attribute);

        attribute = new Attribute(domain, ATT_TIME_SINCE_LAST_INTERVENTION, Attribute.AttributeType.INT);
        attribute.setDiscValuesForRange(0, 5, 1);
        stateClass.addAttribute(attribute);

        attribute = new Attribute(domain, ATT_TIME_SINCE_LAST_SAME_TYPE_INTERVENTION, Attribute.AttributeType.INT);
        attribute.setDiscValuesForRange(0, 5, 1);
        stateClass.addAttribute(attribute);

        attribute = new Attribute(domain, ATT_GOAL_ACHIEVEMENT, Attribute.AttributeType.INT);
        attribute.setDiscValuesForRange(-1, 4, 1);
        stateClass.addAttribute(attribute);

        attribute = new Attribute(domain, ATT_HABITUATION, Attribute.AttributeType.INT);
        attribute.setDiscValuesForRange(0, 100, 10);
        stateClass.addAttribute(attribute);

        attribute = new Attribute(domain, ATT_TIME_OF_DAY, Attribute.AttributeType.INT);
        attribute.setDiscValuesForRange(0, 4, 1);
        stateClass.addAttribute(attribute);

        attribute = new Attribute(domain, ATT_TYPE_OF_DAY, Attribute.AttributeType.INT);
        attribute.setDiscValuesForRange(0, 1, 1);
        stateClass.addAttribute(attribute);

        new DeliverInterventionAction(ACTION_DELIVER_INTERVENTION, domain, pdm);
        new NoInterventionAction(ACTION_NO_INTERVENTION, domain, pdm);

        return domain;
    }
}

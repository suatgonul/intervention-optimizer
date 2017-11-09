package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model.action;

import burlap.oomdp.core.Domain;
import burlap.oomdp.singleagent.common.SimpleAction;
import tr.com.srdc.chronic_disease_management.intervention_optimizer.PersonalDecisionMaker;

public abstract class SMAction extends SimpleAction {
    protected PersonalDecisionMaker pdm;

    public SMAction(String name, Domain domain, PersonalDecisionMaker pdm) {
        super(name, domain);
        this.pdm = pdm;
    }

    public PersonalDecisionMaker getPdm() {
        return pdm;
    }
}

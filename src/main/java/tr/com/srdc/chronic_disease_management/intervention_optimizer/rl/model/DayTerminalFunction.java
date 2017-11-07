package tr.com.srdc.chronic_disease_management.intervention_optimizer.rl.model;

import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;

public class DayTerminalFunction implements TerminalFunction {
    @Override
    public boolean isTerminal(State s) {
        return ((SMState) s).isTerminal();
    }
}

/*
 *    
 *   Copyright 2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.taglibs.rdc.scxml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.LogFactory;

import org.apache.taglibs.rdc.scxml.model.Action;
import org.apache.taglibs.rdc.scxml.model.Assign;
import org.apache.taglibs.rdc.scxml.model.Cancel;
import org.apache.taglibs.rdc.scxml.model.Else;
import org.apache.taglibs.rdc.scxml.model.ElseIf;
import org.apache.taglibs.rdc.scxml.model.Exit;
import org.apache.taglibs.rdc.scxml.model.History;
import org.apache.taglibs.rdc.scxml.model.If;
import org.apache.taglibs.rdc.scxml.model.Initial;
import org.apache.taglibs.rdc.scxml.model.Log;
import org.apache.taglibs.rdc.scxml.model.ModelException;
import org.apache.taglibs.rdc.scxml.model.OnEntry;
import org.apache.taglibs.rdc.scxml.model.OnExit;
import org.apache.taglibs.rdc.scxml.model.Parallel;
import org.apache.taglibs.rdc.scxml.model.SCXML;
import org.apache.taglibs.rdc.scxml.model.Send;
import org.apache.taglibs.rdc.scxml.model.State;
import org.apache.taglibs.rdc.scxml.model.Transition;
import org.apache.taglibs.rdc.scxml.model.TransitionTarget;
import org.apache.taglibs.rdc.scxml.model.Var;

/**
 * This class encapsulates a particular SCXML semantics, that is a particular
 * semantic interpretation of Harel Statecharts. The possible semantic
 * interpretations are for example:
 * <ul>
 * <li>STATEMATE
 * <li>RHAPSODY
 * <li>ROOMCharts
 * <li>UML 1.5
 * <li>UML 2.0
 * </ul>
 * The purpose of this class is to separate the interpretation algorithm from
 * the SCXMLExecutor and make it therefore pluggable. Semantics agnostic utility
 * functions and common operators as defined in UML can be found in the
 * SCXMLHelper or attached directly to the SCXML model elements.
 * 
 * Specific semantics can be created by subclassing SCXMLSemantics, the current
 * implementation aligns mostly with W3C SCXML July 5 public draft (that is,
 * UML 1.5) however, certain aspects are taken from STATEMATE.
 * 
 * @author Jaroslav Gergic
 * @author Rahul Akolkar
 * 
 *  
 */
public class SCXMLSemantics {

    protected static org.apache.commons.logging.Log appLog = LogFactory
            .getLog("scxml.app.log");

    protected TransitionTargetComparator ttc = new TransitionTargetComparator();

    /**
     * @param input
     *            SCXML state machine
     * @return normalized SCXML state machine, pseudo states are removed, etc.
     * @param errRep
     *            ErrorReporter callback
     */
    public SCXML normalizeStateMachine(SCXML input, ErrorReporter errRep) {
        //it is a no-op for now
        return input;
    }

    /**
     * @param input
     *            SCXML state machine [in]
     * @param states
     *            a set of States to populate [out]
     * @param entryList
     *            a list of States and Parallels to enter [out]
     * @param errRep
     *            ErrorReporter callback [inout]
     * @throws ModelException
     *             in case there is a fatal SCXML object model problem.
     */
    public void determineInitialStates(SCXML input, Set states, List entryList,
            ErrorReporter errRep) throws ModelException {
        State tmp = input.getInitialState();
        if (tmp == null) {
            errRep.onError(ErrorReporter.NO_INITIAL,
                    "SCXML initialstate is missing!", input);
        } else {
            states.add(tmp);
            determineTargetStates(states, errRep);
            //set of ALL entered states (even if initialState is a jump-over)
            Set onEntry = SCXMLHelper.getAncestorClosure(states, null);
            // sort onEntry according state hierarchy
            Object oen[] = onEntry.toArray();
            onEntry.clear();
            Arrays.sort(oen, getTTComparator());
            // we need to impose reverse order for the onEntry list
            List entering = Arrays.asList(oen);
            Collections.reverse(entering);
            entryList.addAll(entering);

        }
    }

    /**
     * @param actions
     *            a list of actions to execute [in]
     * @param derivedEvents
     *            collection of internal events generated by the actions [out]
     * @param exec
     *            execution environment [inout]
     * @throws ModelException
     *             in case there is a fatal SCXML object model problem.
     * @throws SCXMLExpressionException
     * @see ErrorReporter
     * @see NotificationRegistry
     * @see EventDispatcher
     * @see Context
     * @see Evaluator
     */
    public void executeActionList(List actions, Collection derivedEvents,
            SCXMLExecutor exec, ErrorReporter errRep) throws ModelException,
            SCXMLExpressionException {
        // NOTE: "if" statement is a container - we may need to call this method
        // recursively and pass a sub-list of actions embedded in a particular
        // "if"
        Evaluator eval = exec.getEvaluator();
        for (Iterator i = actions.iterator(); i.hasNext(); ) {
            Action a = (Action) i.next();
            State parentState = a.getParentState();
            Context ctx = parentState.getContext();
            // NOTE: "elseif" and "else" do not appear here, since they are
            // always handled as a part of "if" as a container
            if (a instanceof Assign) {
                Assign asgn = (Assign) a;
                String varName = asgn.getName();
                if (!ctx.has(varName)) {
                    errRep.onError(ErrorReporter.UNDEFINED_VARIABLE, varName
                            + " = null", parentState);
                } else {
                    Object varObj = eval.eval(ctx, asgn.getExpr());
                    ctx.set(varName, varObj);
                    TriggerEvent ev = new TriggerEvent(varName + ".change",
                            TriggerEvent.CHANGE_EVENT);
                    derivedEvents.add(ev);
                }
            } else if (a instanceof Cancel) {
                Cancel cncl = (Cancel) a;
                exec.getEventdispatcher().cancel(cncl.getSendId());
            } else if (a instanceof Exit) {
                // Ignore; Exit instance holds other information that might 
                // be needed, and is not transformed at parse time. 
                //throw new ModelException("The <exit/> tag must be transformed"
                //        + " to an anonymous final state at the parse time!");
            } else if (a instanceof If) {
                //determine elseif/else separators evaluate conditions
                //extract a sub-list of If's actions and invoke
                // executeActionList()
                List todoList = new LinkedList();
                If ifo = (If) a;
                List subAct = ifo.getActions();
                boolean cnd = eval.evalCond(ctx, ifo.getCond()).booleanValue();
                for (Iterator ifiter = subAct.iterator(); ifiter.hasNext(); ) {
                    Action aa = (Action) ifiter.next();
                    if (cnd && !(aa instanceof ElseIf) && !(aa instanceof Else)) {
                        todoList.add(aa);
                    } else if (cnd
                            && (aa instanceof ElseIf || aa instanceof Else)) {
                        break;
                    } else if (aa instanceof ElseIf) {
                        cnd = eval.evalCond(ctx, ((ElseIf) aa).getCond())
                                .booleanValue();
                    } else if (aa instanceof Else) {
                        cnd = true;
                    } else {
                        //skip
                    }
                }
                if (!todoList.isEmpty()) {
                    executeActionList(todoList, derivedEvents, exec, errRep);
                }
                todoList.clear();
            } else if (a instanceof Log) {
                Log lg = (Log) a;
                Object exprRslt = eval.eval(ctx, lg.getExpr());
                appLog.info(lg.getLabel() + ": " + String.valueOf(exprRslt));
            } else if (a instanceof Send) {
                Send snd = (Send) a;
                Object hints = null;
                if (!SCXMLHelper.isStringEmpty(snd.getHints())) {
                    hints = eval.eval(ctx, snd.getHints());
                }
                Map params = null;
                if (!SCXMLHelper.isStringEmpty(snd.getNamelist())) {
                    StringTokenizer tkn = new StringTokenizer(snd.getNamelist());
                    params = new HashMap(tkn.countTokens());
                    while (tkn.hasMoreTokens()) {
                        String varName = tkn.nextToken();
                        Object varObj = ctx.get(varName);
                        if (varObj == null) {
                            //considered as a warning here
                            errRep.onError(ErrorReporter.UNDEFINED_VARIABLE,
                                    varName + " = null", parentState);
                        }
                        params.put(varName, varObj);
                    }
                }
                exec.getEventdispatcher().send(snd.getSendId(),
                        snd.getTarget(), snd.getTargetType(), snd.getEvent(),
                        params, hints, Long.parseLong(snd.getDelay()));
            } else if (a instanceof Var) {
                Var vr = (Var) a;
                String varName = vr.getName();
                Object varObj = eval.eval(ctx, vr.getExpr());
                ctx.setLocal(varName, varObj);
                TriggerEvent ev = new TriggerEvent(varName + ".change",
                        TriggerEvent.CHANGE_EVENT);
                derivedEvents.add(ev);
            } else {
                errRep.onError(ErrorReporter.UNKNOWN_ACTION,
                        "unsupported executable statement", a);
            }
        }
    }

    /**
     * Exectutes all OnExit/Transition/OnEntry transitional actions.
     * 
     * @param step
     *            [inout] provides EntryList, TransitList, ExitList gets updated
     *            its AfterStatus/Events
     * @param exec
     *            [inout] execution environment - SCXMLExecutor instance
     * @param errRep
     *            [out[ error reporter
     * @throws ModelException
     *             in case there is a fatal SCXML object model problem.
     */
    public void executeActions(Step step, SCXMLExecutor exec,
            ErrorReporter errRep) throws ModelException {
        SCXML sm = exec.getStateMachine();
        NotificationRegistry nr = sm.getNotificationRegistry();
        Collection internalEvents = step.getAfterStatus().getEvents();
        // ExecutePhaseActions / OnExit
        for (Iterator i = step.getExitList().iterator(); i.hasNext(); ) {
            TransitionTarget tt = (TransitionTarget) i.next();
            OnExit oe = tt.getOnExit();
            try {
                executeActionList(oe.getActions(), internalEvents, exec, errRep);
            } catch (SCXMLExpressionException e) {
                errRep.onError(ErrorReporter.EXPRESSION_ERROR, e.getMessage(),
                        oe);
            }
            nr.fireOnExit(tt, tt);
            nr.fireOnExit(sm, tt);
            TriggerEvent te = new TriggerEvent(tt.getId() + ".exit",
                    TriggerEvent.CHANGE_EVENT);
            internalEvents.add(te);
        }
        // ExecutePhaseActions / Transitions
        for (Iterator i = step.getTransitList().iterator(); i.hasNext(); ) {
            Transition t = (Transition) i.next();
            try {
                executeActionList(t.getActions(), internalEvents, exec, errRep);
            } catch (SCXMLExpressionException e) {
                errRep.onError(ErrorReporter.EXPRESSION_ERROR, e.getMessage(),
                        t);
            }
            nr.fireOnTransition(t, t.getParent(), t.getRuntimeTarget(), t);
            nr.fireOnTransition(sm, t.getParent(), t.getRuntimeTarget(), t);
        }
        // ExecutePhaseActions / OnEntry
        for (Iterator i = step.getEntryList().iterator(); i.hasNext(); ) {
            TransitionTarget tt = (TransitionTarget) i.next();
            OnEntry oe = tt.getOnEntry();
            try {
                executeActionList(oe.getActions(), internalEvents, exec, errRep);
            } catch (SCXMLExpressionException e) {
                errRep.onError(ErrorReporter.EXPRESSION_ERROR, e.getMessage(),
                        oe);
            }
            nr.fireOnEntry(tt, tt);
            nr.fireOnEntry(sm, tt);
            TriggerEvent te = new TriggerEvent(tt.getId() + ".entry",
                    TriggerEvent.CHANGE_EVENT);
            internalEvents.add(te);
            //3.2.1 and 3.4 (.done events)
            if (tt instanceof State) {
                State ts = (State)tt;
                if (ts.getIsFinal()) {
                    State parent = (State)ts.getParent();
                    String prefix = (parent == null) ? "" : parent.getId();
                    te = new TriggerEvent(prefix + ".done",
                            TriggerEvent.CHANGE_EVENT);
                    internalEvents.add(te);
                    if (parent != null) {
                        parent.setDone(true);
                    }
                    if (parent != null && parent.isRegion()) {
                        //3.4 we got a region, which is finalized
                        //let's check its siblings too
                        Parallel p = (Parallel)parent.getParent();
                        int finCount = 0;
                        int pCount = p.getStates().size();
                        Iterator regions = p.getStates().iterator();
                        while(regions.hasNext()) {
                            State reg = (State)regions.next();
                            if(reg.isDone()) {
                                finCount++;
                            }
                        }
                        if(finCount == pCount) {
                            te = new TriggerEvent(p.getId() + ".done",
                                        TriggerEvent.CHANGE_EVENT);
                            internalEvents.add(te);
                            te = new TriggerEvent(p.getParent().getId() + ".done",
                                    TriggerEvent.CHANGE_EVENT);
                            internalEvents.add(te);
                            //this is not in the specs, but is makes sense
                            p.getParentState().setDone(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param stateMachine
     *            a SM to traverse [in]
     * @param step
     *            with current status and list of transitions to populate
     *            [inout]
     * @param errRep
     *            ErrorReporter callback [inout]
     */
    public void enumerateReachableTransitions(SCXML stateMachine, Step step,
            ErrorReporter errRep) {
        // prevents adding the same transition multiple times
        Set transSet = new HashSet();
        // prevents visiting the same state multiple times
        Set stateSet = new HashSet(step.getBeforeStatus().getStates());
        // breath-first search to-do list
        LinkedList todoList = new LinkedList(stateSet);
        while (!todoList.isEmpty()) {
            State st = (State) todoList.removeFirst();
            for (Iterator i = st.getTransitionsList().iterator(); i.hasNext(); ) {
                Transition t = (Transition) i.next();
                if (!transSet.contains(t)) {
                    transSet.add(t);
                    step.getTransitList().add(t);
                }
            }
            State parent = st.getParentState();
            if (parent != null && !stateSet.contains(parent)) {
                stateSet.add(parent);
                todoList.addLast(parent);
            }
        }
        transSet.clear();
        stateSet.clear();
        todoList.clear();
    }

    /**
     * @param step
     *            [inout]
     * @param evaluator
     *            guard condition evaluator
     * @param errRep
     *            ErrorReporter callback [inout]
     */
    public void filterTransitionsSet(Step step, Evaluator evaluator,
            ErrorReporter errRep) {
        /*
         * - filter transition set by applying events (step/beforeStatus/events +
         * step/externalEvents) (local check) - evaluating guard conditions for
         * each transition (local check) - transition precedence (bottom-up) as
         * defined by SCXML specs
         */
        Set allEvents = new HashSet(step.getBeforeStatus().getEvents()
                .size()
                + step.getExternalEvents().size());
        //for now, we only match against event names
        for (Iterator ei = step.getBeforeStatus().getEvents().iterator();
                ei.hasNext(); ) {
            TriggerEvent te = (TriggerEvent) ei.next();
            allEvents.add(te.getName());
        }
        for (Iterator ei = step.getExternalEvents().iterator(); ei.hasNext(); ) {
            TriggerEvent te = (TriggerEvent) ei.next();
            allEvents.add(te.getName());
        }
        //remove list (filtered-out list)
        List removeList = new LinkedList();
        //iterate over non-filtered transition set
        for (Iterator iter = step.getTransitList().iterator(); iter.hasNext();) {
            Transition t = (Transition) iter.next();
            // event check
            String event = t.getEvent();
            if (!eventMatch(event, allEvents)) {
                // t has a non-empty event which is not triggered
                removeList.add(t);
                continue; //makes no sense to eval guard cond.
            }
            // guard condition check
            Boolean rslt;
            String expr = t.getCond();
            if (SCXMLHelper.isStringEmpty(expr)) {
                rslt = Boolean.TRUE;
            } else {
                try {
                    rslt = evaluator.evalCond(((State) t.getParent())
                            .getContext(), t.getCond());
                } catch (SCXMLExpressionException e) {
                    rslt = Boolean.FALSE;
                    errRep.onError(ErrorReporter.EXPRESSION_ERROR, e
                            .getMessage(), t);
                }
            }
            if (rslt.booleanValue() == false) {
                // guard condition has not passed
                removeList.add(t);
            }
        }
        // apply event + guard condition filter
        step.getTransitList().removeAll(removeList);
        // cleanup temporary structures
        allEvents.clear();
        removeList.clear();
        // optimization - global precedence potentially applies
        // only if there are multiple enabled transitions
        if (step.getTransitList().size() > 1) {
            // global transition precedence check
            Object trans[] = step.getTransitList().toArray();
            Set currentStates = step.getBeforeStatus().getStates();
            // non-determinism candidates
            Set nonDeterm = new HashSet();
            for (int i = 0; i < trans.length; i++) {
                Transition t = (Transition) trans[i];
                TransitionTarget tsrc = t.getParent();
                for (int j = i + 1; j < trans.length; j++) {
                    Transition t2 = (Transition) trans[j];
                    boolean conflict = SCXMLHelper.inConflict(t, t2,
                            currentStates);
                    if (conflict) {
                        //potentially conflicting transitions
                        TransitionTarget t2src = t2.getParent();
                        if (SCXMLHelper.isDescendant(t2src, tsrc)) {
                            //t2 takes precedence over t
                            removeList.add(t);
                            break; //it makes no sense to waste cycles with t
                        } else if (SCXMLHelper.isDescendant(tsrc, t2src)) {
                            //t takes precendence over t2
                            removeList.add(t2);
                        } else {
                            //add both to the non-determinism candidates
                            nonDeterm.add(t);
                            nonDeterm.add(t2);
                        }
                    }
                }
            }
            // check if all non-deterministic situations have been resolved
            nonDeterm.removeAll(removeList);
            if (nonDeterm.size() > 0) {
                errRep.onError(ErrorReporter.NON_DETERMINISTIC,
                        "Multiple conflicting transitions enabled.", nonDeterm);
            }
            // apply global transition filter
            step.getTransitList().removeAll(removeList);
            removeList.clear();
            nonDeterm.clear();
        }
    }

    /**
     * @param step
     *            transitional step
     * @param errRep
     *            ErrorReporter callback [inout]
     */
    public void seedTargetSet(Step step, ErrorReporter errRep) {
        Set sources = step.getBeforeStatus().getStates();
        Set targets = step.getAfterStatus().getStates();
        List transitions = step.getTransitList();
        /* populate the target set by taking targets of selected transitions */
        for (Iterator i = transitions.iterator(); i.hasNext(); ) {
            Transition t = (Transition) i.next();
            targets.add(t.getRuntimeTarget());
        }
        /* retain the source states, which are not affected by the transitions */
        for (Iterator i = sources.iterator(); i.hasNext(); ) {
            State s = (State) i.next();
            boolean retain = true;
            for (Iterator j = transitions.iterator(); j.hasNext(); ) {
                Transition t = (Transition) j.next();
                State ts = (State) t.getParent();
                if (s == ts || SCXMLHelper.isDescendant(s, ts)) {
                    retain = false;
                    break;
                }
            }
            if (retain) {
                targets.add(s);
            }
        }
    }

    /**
     * @param states
     *            a set seeded in previous step [inout]
     * @param errRep
     *            ErrorReporter callback [inout]
     * @see #seedTargetSet(Step, ErrorReporter)
     */
    public void determineTargetStates(Set states, ErrorReporter errRep)
            throws ModelException {
        LinkedList wrkSet = new LinkedList(states);
        // clear the seed-set - will be populated by leaf states
        states.clear();
        while (!wrkSet.isEmpty()) {
            TransitionTarget tt = (TransitionTarget) wrkSet.removeFirst();
            if (tt instanceof State) {
                State st = (State) tt;
                //state can either have parallel or substates w. initial
                //or it is a leaf state
                // NOTE: Digester has to verify this precondition!
                if (st.isSimple()) {
                    states.add(st); //leaf
                } else if (st.isOrthogonal()) {
                    wrkSet.addLast(st.getParallel()); //parallel
                } else {
                    // composite state
                    Initial ini = st.getInitial();
                    if (ini == null) {
                        errRep.onError(ErrorReporter.NO_INITIAL,
                            "Initial pseudostate is missing!", st);
                    }
                    // If we are here, transition target must be a State
                    // or History
                    TransitionTarget init = ini.getTransition().getTarget();
                    if (init == null
                        || !(init instanceof State || init instanceof History)) {
                        errRep.onError(ErrorReporter.ILLEGAL_INITIAL,
                            "Initial does not point to a State or a History!",
                            st);
                    } else {
                        wrkSet.addLast(init);
                    }
                }
            } else if (tt instanceof Parallel) {
                Parallel prl = (Parallel) tt;
                Iterator i = prl.getStates().iterator();
                while (i.hasNext()) {
                    //fork
                    wrkSet.addLast(i.next());
                }
            } else if (tt instanceof History) {
                History h = (History) tt;
                if (h.isEmpty()) {
                    wrkSet.addLast(h.getTransition().getRuntimeTarget());
                } else {
                    wrkSet.addAll(h.getLastConfiguration());
                }
            } else {
                throw new ModelException("Unknown TransitionTarget subclass:"
                        + tt.getClass().getName());
            }
        }
        if (!SCXMLHelper.isLegalConfig(states, errRep)) {
            throw new ModelException("Illegal state machine configuration!");
        }
    }

    /**
     * @param step
     *            [inout]
     * @param errRep
     *            ErrorReporter callback [inout]
     */
    public void buildOnExitOnEntryLists(Step step, ErrorReporter errRep) {
        //set of exited states
        Set onExit = SCXMLHelper.getAncestorClosure(step.getBeforeStatus()
                .getStates(), null);
        Set onExit2 = new HashSet(onExit);
        //set of entered states
        Set onEntry = SCXMLHelper.getAncestorClosure(step.getAfterStatus()
                .getStates(), null);
        //remove common sub-set
        onExit.removeAll(onEntry);
        onEntry.removeAll(onExit2);
        //explicitly add self-transitions
        for (Iterator i = step.getTransitList().iterator(); i.hasNext(); ) {
            Transition t = (Transition) i.next();
            if (t.getParent() == t.getTarget()) {
                onExit.add(t.getParent());
                onEntry.add(t.getTarget());
            }
        }
        // sort onEntry and onExit according state hierarchy
        Object oex[] = onExit.toArray();
        onExit.clear();
        Object oen[] = onEntry.toArray();
        onEntry.clear();
        Arrays.sort(oex, getTTComparator());
        Arrays.sort(oen, getTTComparator());
        step.getExitList().addAll(Arrays.asList(oex));
        // we need to impose reverse order for the onEntry list
        List entering = Arrays.asList(oen);
        Collections.reverse(entering);
        step.getEntryList().addAll(entering);
        // reset 'done' flag
        for (Iterator reset = entering.iterator(); reset.hasNext(); ) {
            Object o = reset.next();
            if(o instanceof State) {
                ((State)o).setDone(false);
            }
        }
    }

    /**
     * Go over the exit list and update history information for relevant states.
     * 
     * @param step
     *            [inout]
     * @param errRep
     *            ErrorReporter callback [inout]
     */
    public void updateHistoryStates(Step step, ErrorReporter errRep) {
        Set oldState = step.getBeforeStatus().getStates();
        for (Iterator i = step.getExitList().iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o instanceof State) {
                State s = (State) o;
                if (s.hasHistory()) {
                    Set shallow = null;
                    Set deep = null;
                    Iterator j = s.getHistory().iterator();
                    while (j.hasNext()) {
                        History h = (History) j.next();
                        if (h.isDeep()) {
                            if (deep == null) {
                                //calculate deep history for a given state once
                                deep = new HashSet();
                                Iterator k = oldState.iterator();
                                while (k.hasNext()) {
                                    State os = (State) k.next();
                                    if (SCXMLHelper.isDescendant(os, s)) {
                                        deep.add(os);
                                    }
                                }
                            }
                            h.setLastConfiguration(deep);
                        } else {
                            if (shallow == null) {
                                //calculate shallow history for a given state
                                // once
                                shallow = new HashSet();
                                shallow.addAll(s.getChildren().values());
                                shallow.retainAll(SCXMLHelper
                                        .getAncestorClosure(oldState, null));
                            }
                            h.setLastConfiguration(shallow);
                        }
                    }
                    shallow = null;
                    deep = null;
                }
            }
        }
    }

    /**
     * Implements prefix match, that is, if, for example, 
     * &quot;mouse.click&quot; is a member of eventOccurrences and a
     * transition is triggered by &quot;mouse&quot;, the method returns true.
     * 
     * @param transEvent
     *            a trigger event of a transition
     * @param eventOccurrences
     *            current events
     * @return true/false
     */
    public boolean eventMatch(String transEvent, Set eventOccurrences) {
        if (SCXMLHelper.isStringEmpty(transEvent)) {
            return true;
        } else {
            String transEventDot = transEvent + "."; //wildcard (prefix) event
            // support
            Iterator i = eventOccurrences.iterator();
            while (i.hasNext()) {
                String evt = (String) i.next();
                if (evt.equals(transEvent) || evt.startsWith(transEventDot)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     *  TransitionTargetComparator factory method 
     */
    final Comparator getTTComparator() {
        return ttc;
    }

}

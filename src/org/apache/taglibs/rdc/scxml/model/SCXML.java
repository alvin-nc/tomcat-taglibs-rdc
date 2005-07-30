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
package org.apache.taglibs.rdc.scxml.model;

import org.apache.taglibs.rdc.RDCUtils;
import org.apache.taglibs.rdc.scxml.Context;
import org.apache.taglibs.rdc.scxml.NotificationRegistry;
import org.apache.taglibs.rdc.scxml.Observable;
import org.apache.taglibs.rdc.scxml.SCXMLListener;

import java.util.HashMap;
import java.util.Map;

/**
 * The class in this SCXML object model that corresponds to the
 * &lt;scxml&gt; root element, and serves as the &quot;document
 * root&quot;. It is also associated with the root Context, via which
 * the SCXMLExecutor may access and the query state of the host 
 * environment.
 * 
 * @author Rahul Akolkar
 * @author Jaroslav Gergic
 */
public class SCXML implements Observable {

    /**
     * The SCXML XMLNS
     */
	public static final String XMLNS = "http://www.w3.org/2005/01/SCXML";
	
    /**
     * The xmlns attribute on the root &lt;smxml&gt; element.
     * This must match XMLNS above.
     */
	private String xmlns;
	
    /**
     * The SCXML version of this document.
     */
	private String version;

    /**
     * The initial State for the SCXML executor
     */
	private State initialState;

    /**
     * The initial state ID (used by XML Digester only)
     */
	private transient String initialstate;

	/**
     * The immediate child states of this SCXML document root
     */
    private Map states;
    
    /**
     * The notification registry
     */
    private NotificationRegistry notifReg;

	/**
     * A global map of all States and Parallels associated with this
     * state machine, keyed by their id 
     */
    private Map targets;

	/**
	 * The root Context which interfaces with the host environment
	 */
	private Context rootContext;
	
	/**
	 * Constructor
	 */
	public SCXML() { 
		this.states = new HashMap();
		this.notifReg = new NotificationRegistry();
		this.targets = new HashMap();
	}
	
	/**
	 * Get the initial State
	 * 
	 * @return Returns the initialstate.
	 */
	public State getInitialState() {
		return initialState;
	}
	
	/**
	 * Set the initial State
	 * 
	 * @param initialstate The initialstate to set.
	 */
	public void setInitialState(State initialState) {
		this.initialState = initialState;
	}
	
	/**
	 * Get the children states
	 * 
	 * @return Returns the states.
	 */
	public Map getStates() {
		return states;
	}
	
	/**
	 * Add a child state
	 * 
	 * @param state The state to be added to the states Map.
	 */
	public void addState(State state) {
		states.put(state.getId(), state);
	}
	
	/**
	 * Get the targets map, whichis a Map of all States and Parallels 
	 * associated with this state machine, keyed by their id
	 * 
	 * @return Returns the targets.
	 */
	public Map getTargets() {
		return targets;
	}
	
	/**
	 * Add a target to this SCXML document
	 * 
	 * @param target The target to be added to the targets Map.
	 */
	public void addTarget(TransitionTarget target) {
		String id = target.getId();
		if (!RDCUtils.isStringEmpty(id)) {
			// Target is not anonymous, so makes sense to map it
			targets.put(id, target);
		}
	}	
	
	/**
	 * Get the SCXML document version
	 * 
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Set the SCXML document version
	 * 
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * Get the xmlns of this SCXML document
	 * 
	 * @return Returns the xmlns.
	 */
	public String getXmlns() {
		return xmlns;
	}
	
	/**
	 * Set the xmlns of this SCXML document
	 * 
	 * @param xmlns The xmlns to set.
	 */
	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	/**
	 * Get the notification registry
	 * 
	 * @return Returns the notifReg.
	 */
	public NotificationRegistry getNotificationRegistry() {
		return notifReg;
	}
	
	/**
	 * Get the ID of the initial state
	 * 
	 * @return Returns the initial state ID (used by XML Digester only).
	 * @see #getInitialState()
	 */
	public String getInitialstate() {
		return initialstate;
	}
	
	/**
	 * Set the ID of the initial state
	 * 
	 * @param initialstate The initial state ID (used by XML Digester only).
	 * @see #setInitialState(State)
	 */
	public void setInitialstate(String initialstate) {
		this.initialstate = initialstate;
	}
	
	/**
	 * Get the root Context for this document
	 * 
	 * @return Returns the rootContext.
	 */
	public Context getRootContext() {
		return rootContext;
	}
	
	/**
	 * Set the root Context for this document
	 * 
	 * @param rootContext The rootContext to set.
	 */
	public void setRootContext(Context rootContext) {
		this.rootContext = rootContext;
	}

    /**
     * Register a listener to this document root
     * 
     * @param lst The SCXMLListener to add
     * Remarks: Only valid if StateMachine is non null!
     */
    public void addListener(SCXMLListener lst) {
        notifReg.addListener(this, lst);
    }

    /**
     * Deregister a listener from this document root
     * 
     * @param lst The SCXMLListener to remove
     * Remarks: Only valid if StateMachine is non null!
     */
    public void removeListener(SCXMLListener lst) {
    	notifReg.removeListener(this, lst);
    }
	
}

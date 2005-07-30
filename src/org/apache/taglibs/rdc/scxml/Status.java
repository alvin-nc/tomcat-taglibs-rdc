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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

import org.apache.taglibs.rdc.scxml.model.State;

/**
 * The encapsulation of the current state of a state machine.
 * 
 * @author Jaroslav Gergic
 * @author Rahul Akolkar
 */
public class Status {
	
	/**
	 * The states that are currently active.
	 */
	private Set states;

	/**
	 * The events that are currently queued.
	 */
	private Collection events;

	/**
	 * Have we reached a final configuration for this state machine.
	 * 
	 * True - if all the states are final and there are not events 
	 * pending from the last step. False - otherwise.
	 * 
	 * @return Whether a final configuration has been reached.
	 */
	public boolean isFinal() {
		boolean rslt = true;
		Iterator i = states.iterator();
		while (i.hasNext()) {
			State s = (State)i.next();
			if (!s.getIsFinal()) {
				rslt = false;
				break;
			}
			//the status is final only iff these are top-level states
			if(s.getParent() != null) {
				rslt = false;
				break;
			}
		}
		if (!events.isEmpty()) {
			rslt = false;
		}
		return rslt;
	}

	/**
	 * Constructor
	 */
	public Status() {
		states = new HashSet();
		events = new ArrayList();
	}

	/**
	 * Get the states configuration (leaf only).
	 * 
	 * @return Returns the states configuration - simple (leaf) states only.
	 */
	public Set getStates() {
		return states;
	}

	/**
	 * Get the events that are currently queued.
	 * 
	 * @return The events that are currently queued.
	 */
	public Collection getEvents() {
		return events;
	}

	/**
	 * Get the complete states configuration
	 * 
	 * @return complete states configuration including simple states and their
	 *         complex ancestors up to the root.
	 */
	public Set getAllStates() {
		return SCXMLHelper.getAncestorClosure(states, null);
	}
	
}

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

import org.apache.taglibs.rdc.scxml.model.Transition;
import org.apache.taglibs.rdc.scxml.model.TransitionTarget;

/**
 * Listener interface for Observable entities in the SCXML model.
 * 
 * @author Jaroslav Gergic
 */
public interface SCXMLListener {
	
	/**
	 * Handle the entry into a TransitionTarget
	 * 
	 * @param state The TransitionTarget entered
	 */
	public void onEntry(TransitionTarget state);

	/**
	 * Handle the exit out of a TransitionTarget
	 * 
	 * @param state The TransitionTarget exited
	 */
	public void onExit(TransitionTarget state);

	/**
	 * Handle the transition
	 * 
	 * @param from The source TransitionTarget
	 * @param to The destination TransitionTarget
	 * @param transition The Transition taken
	 */
	public void onTransition(TransitionTarget from, TransitionTarget to,
			Transition transition);

}

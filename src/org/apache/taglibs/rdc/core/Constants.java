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
 *
 */
/*$Id$*/
package org.apache.taglibs.rdc.core;

/**
 *
 * <p>Constants used by the RDC package.</p>
 * 
 * @author <a href="mailto:tvraman@almaden.ibm.com">T. V. Raman</a>
 * @author Rahul Akolkar
 * @version 1.0
 */

public final class Constants {
	
	// Atom States
	public static final int FSM_INITONLY = 76867680;
	public static final int FSM_INPUT = 76867681;
	public static final int FSM_VALIDATE = 76867682;
	public static final int FSM_DISAMBIGUATE = 76867683;
	public static final int FSM_CONFIRM = 76867684;
	public static final int FSM_DORMANT = 76867685;
	public static final int FSM_DONE = 76867686;

	// Getters for atom states
	public final int getFSM_INITONLY() { return FSM_INITONLY; }
	public final int getFSM_INPUT() { return FSM_INPUT; }
	public final int getFSM_VALIDATE() { return FSM_VALIDATE; }
	public final int getFSM_DISAMBIGUATE() { return FSM_DISAMBIGUATE; }
	public final int getFSM_CONFIRM() { return FSM_CONFIRM; }
	public final int getFSM_DORMANT() { return FSM_DORMANT; }
	public final int getFSM_DONE() { return FSM_DONE; }
		
	// Group states
	public static final int GRP_SOME_CHILD_RUNNING = 24803980;
	public static final int GRP_SOME_CHILD_DORMANT = 24803981;
	public static final int GRP_ALL_CHILDREN_DONE = 24803982;
	public static final int GRP_ALL_CHILDREN_DORMANT = 24803983;
	public static final int GRP_ALL_CHILDREN_INPUT = 24803984;
	public static final int GRP_STATE_RUNNING = 24803985;
	public static final int GRP_STATE_DONE = 24803986;
	
	// Only states needed in the JSP context
	public final int getGRP_STATE_RUNNING() { return GRP_STATE_RUNNING; }
	public final int getGRP_STATE_DONE() { return GRP_STATE_DONE; }
	
	// Group confirmation states
	public static final int CONF_STATE_UNEXPECTED = 43815030;
	public static final int CONF_STATE_INPUT = 43815031;
	public static final int CONF_STATE_CONFIRMATION = 43815032;
	public static final int CONF_STATE_IDENTIFICATION = 43815033;
	public static final int CONF_STATE_DONE = 43815034;

	// String constants
	public static final String STR_RDC_STACK = "rdcStack";
	public static final String STR_INIT_ONLY_FLAG = "initOnlyFlag";
	public static final String STR_EMPTY = "";
	public static final String STR_BOOLEAN = "boolean";
	public static final String STR_TRUE = "true";
	public static final String STR_FALSE = "false";

	// All XPath expressions
	public static final String XPATH_CONFIG_GRAMMAR = "/config/grammar";
	public static final String XPATH_COMPONENT_CONFIG =
		"/config/componentConfigList/component";
	public static final String XPATH_INITIAL_PROMPT = "/config/initial/prompt";

	public static final String XPATH_ATTR_ID = "@id";
	public static final String XPATH_ATTR_FILE = "@file";
	public static final String XPATH_ATTR_TEXT = "@text";
	public static final String XPATH_ATTR_SRC = "@src";

	// Dialog Management Strategies
	public static final String STRATEGY_DIRECTED_DIALOG = "DD";
	public static final String STRATEGY_MIXED_INITIATIVE = "MI";

	// VXML
	public static final String VXML_LAST_UTTERANCE =
		"application.lastresult$.utterance";
	public static final String VXML_BLOCK_BEGIN = "<block>";
	public static final String VXML_BLOCK_END = "</block>";
	public static final String VXML_GRAMMAR_BEGIN = "<grammar src=\"";
	public static final String VXML_GRAMMAR_END = "\" />";
	public static final String VXML_INITIAL_PROMPT_BEGIN = "<initial><prompt>";
	public static final String VXML_INITIAL_PROMPT_END = "</prompt></initial>";
	public static final String VXML_PROMPT_BEGIN = "<prompt>";
	public static final String VXML_PROMPT_END = "</prompt>";
	public static final String VXML_OPTION_BEGIN = "<option>";
	public static final String VXML_OPTION_END = "</option>";

	// Errors
	public static final String ERR_NO_RDC_STACK =
		"GroupTag ERROR: " + STR_RDC_STACK + " not found.\n";
	public static final String ERR_EMPTY_RDC_STACK =
		"GroupTag ERROR: " + STR_RDC_STACK + " empty.\n";
	public static final String ERR_NULL_DOCUMENT =
		"GroupTag ERROR: Parsed document is null.\n";
	public static final String ERR_NULL_NODELIST =
		"GroupTag ERROR: Nodelist is null.\n";
	public static final String ERR_NULL_DOC_BUILDER =
		"GroupTag ERROR: Document Builder is null.\n";
	public static final String ERR_NULL_XPATH_RESULT =
		"GroupTag ERROR: XObject is null.\n";
	public static final String ERR_XPATH_TRANSFORM =
		"GroupTag ERROR: Xpath trnsformation.\n";
		
	// Group Confirmation string constants
	public static final String STR_CONF_PROMPT_START = "You said ";
	public static final String STR_CONF_PROMPT_ENUMERATE =
		"<enumerate><value expr=\"_prompt\"/></enumerate>";
	public static final String STR_CONF_PROMPT_END = "Correct?";
	public static final String STR_CONF_PROMPT_SELECT =
		"Select one of <enumerate/>";
	
	// Location of the RDC jar file in the rdc-examples war
	public static final String RDC_JAR  = "/WEB-INF/lib/taglibs-rdc.jar";
	
	public Constants() {
	}
		
} // Constants

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
package org.apache.taglibs.rdc.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the book-keeping class for the RDC container
 * <b>group</b>.</p>
 * 
 * @author Abhishek Verma
 * @author Rahul Akolkar
 */

public class GroupModel extends BaseModel 
	implements Serializable{
	
	// The map of datamodel of child RDCs; keyed by id 
	protected Map localMap;
	// The object that group confirmation is outsourced to
	protected GroupConfirm groupConfirm;
	// The list of currently active children
	// An active child is defined as one that is neither DORMANT nor DONE
	protected List activeChildren;
	
	public GroupModel() {
		super();
		this.localMap = new LinkedHashMap();
		this.activeChildren = new ArrayList();
	} // GroupModel constructor
   
	
	/** 
	 * Get the map of child RDC datamodels
	 * 
	 * @return the map of child RDC datamodels
	 */
	public Map getLocalMap() {
		return localMap;
	}
    
    /**
     * Set the map of child RDC datamodels
     * 
     * @param localMap - the map of child RDC datamodels
     */
	public void setLocalMap(Map localMap) {
		this.localMap = localMap;
    }
  
	/**
	 * Get the GroupConfirm object
	 * 
	 * @return groupConfirm the group confirmation object
	 */
	public GroupConfirm getGroupConfirm() {
		return groupConfirm;
	}

	/**
	 * Set the GroupConfirm
	 * 
	 * @param confirm
	 */
	public void setGroupConfirm(GroupConfirm groupConfirm) {
		this.groupConfirm = groupConfirm;
	}

	/**
	 * Get the currently active children (not in dormant or done state)
	 * 
	 * @return activeChildren
	 */
	public List getActiveChildren() {
		return activeChildren;
	}

	/**
	 * Set the currently active children
	 * 
	 * @param list
	 */
	public void setActiveChildren(List list) {
		activeChildren = list;
	}
	
} 

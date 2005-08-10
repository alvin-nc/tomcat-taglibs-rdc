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

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class for containers of executable elements in SCXML,
 * such as &lt;onentry&gt; and &lt;onexit&gt;. 
 * 
 * @author Rahul Akolkar
 * @author Jaroslav Gergic
 */
public abstract class Executable {

    /**
     * The set of executable elements (those that inheriting from
     * Action) that are contained in this Executable.
     */
    private List actions;
    
    /**
     * The parent container, for traceability
     */
    protected TransitionTarget parent;
    
    /**
     * Constructor
     */
    public Executable() {
        super();
        this.actions = new ArrayList();
    }
    
    /**
     * Get the executable actions contained in this Executable
     * 
     * @return Returns the actions.
     */
    public List getActions() {
        return actions;
    }
    
    /**
     * Add an Action to the list of executable actions contained in
     * this Executable
     * 
     * @param action The action to add.
     */
    public void addAction(Action action) {
        if (action != null) {
            this.actions.add(action);
        }
    }
    
    /**
     * Get the TransitionTarget parent
     * 
     * @return Returns the parent.
     */
    public TransitionTarget getParent() {
        return parent;
    }
    
    /**
     * Set the TransitionTarget parent
     * 
     * @param parent The parent to set.
     */
    public void setParent(TransitionTarget parent) {
        this.parent = parent;
    }
    
}

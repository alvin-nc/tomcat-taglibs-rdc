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
import java.util.Map;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspContext;

/**
 * <p>This is the base class for component models
 * of composite RDCs.</p>
 * 
 * @author Abhishek Verma
 * @author Rahul Akolkar
 */
public class ComponentModel extends BaseModel 
implements Serializable{
	// The map of datamodel of child RDCs; keyed by id 
	protected Map localMap;
	// The map of non-default configs of child RDCs; keyed by id 
	protected Map configMap;
	// The map of datamodel of child RDCs; keyed by id 
	protected String config;
	// The map of datamodel of child RDCs; keyed by id 
	protected JspContext context;
	
	public ComponentModel() {
		super();
		this.localMap = new LinkedHashMap();
		this.configMap = new LinkedHashMap();
	} // ComponentModel constructor
   
	
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
	 * Get the map of configuration file URIs
	 *  
	 * @return the map of configuration file URI of child RDCs
	 */
	public Map getConfigMap() {
		return configMap;
	}
    
	/**
	 * Set the map of configuration file URI of child RDCs
	 * 
	 * @param configMap - the map of configuration file URI of child RDCs
	 */
	public void setConfigMap(Map configMap) {
		this.configMap = configMap;
	}
	
	/**
	 * Set the context in which this component is executing
	 * 
	 * @param ctx - the JspContext
	 */
	public void setContext(JspContext ctx) {
		this.context = ctx;
	}

	/**
	 * Set the configuration URI
	 * 
	 * @param config- the configuration file URI
	 */	
	public void setConfig(String newConfig) {
		Object[] argsArr = { context, newConfig	};
		Class[] argsClassArray = { javax.servlet.jsp.JspContext.class, java.lang.String.class };
		Method valueSetter = null;
		config = newConfig;
		if (config != null && !config.equals(Constants.STR_EMPTY)) {
			String configHandlerStr = "configHandler";
			try {
				Method configHandler = this.getClass().
					getMethod(configHandlerStr, argsClassArray);
				configHandler.invoke(this, argsArr);
			} catch (NoSuchMethodException nsme) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
} 

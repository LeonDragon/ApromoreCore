/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.logfilter.generic;

import java.util.Arrays;
import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.plugin.portal.generic.PluginInputParams;
import org.deckfour.xes.model.XLog;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public class LogFilterInputParams extends PluginInputParams {
    private XLog log;
    private String classifierAttribute;
    private List<LogFilterCriterion> filterCriteria;
    
    public LogFilterInputParams(Object...objects) throws LogFilterWrongInputException {
        super(objects);
        List<Object> objectList = Arrays.asList(objects);
        
        if (this.checkInputParamsValidity()) {
            this.log = (XLog)objectList.get(0);
            this.classifierAttribute = (String)objectList.get(1);
            this.filterCriteria = (List<LogFilterCriterion>)objectList.get(2);
        }
        else {
            throw new LogFilterWrongInputException("Wrong input parameters passed to LogFilter plugin");
        }
    }
    
    public XLog getLog() {
        return this.log;
    }
    
    public String getClassifierAttribute() {
        return this.classifierAttribute;
    }
    
    public List<LogFilterCriterion> getFilterCriteria() {
        return this.filterCriteria;
    }

    @Override
    public boolean checkInputParamsValidity() {
        if (this.size() == 3 && 
                this.get(0) instanceof XLog && 
                this.get(1) instanceof String &&
                this.get(2) instanceof List<?>) {
            return true;
        }
        else {
            return false;
        }
    }
    
}

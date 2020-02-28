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

package org.apromore.plugin.portal.generic;

public abstract class PluginOutputResult extends PluginParams {
    public static final int SUCCESS_CODE = 0;
    protected String resultMessage="";
    protected int resultCode = 0;
    
    public PluginOutputResult(Object...objects) {
        super(objects);
    }    
    
    public PluginOutputResult(int resultCode, String errorMessage) {
        super();
        this.resultMessage = errorMessage;
        this.resultCode = resultCode;
    }
    
    public String getMessage() {
        return this.resultMessage;
    }
    public int getResultCode() {
        return this.resultCode;
    }
    public boolean isSuccess() {
        return (this.resultCode == SUCCESS_CODE);
    }
}

/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import java.util.HashMap;

import org.yawlfoundation.yawl.elements.YCompositeTask;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;
import de.hbrs.oryx.yawl.util.MultiInstanceConverter;

/**
 * Converts a YAWL composite task with multiple instances to a Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class MultiInstanceCompositeTaskHandler extends CompositeTaskHandler {

	public MultiInstanceCompositeTaskHandler(YAWLConversionContext context, YCompositeTask netElement) {
		super(context, netElement);
		// Override setting of CompositeTaskHandler
		setTaskType("CompositeMultipleTask");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.AtomicTaskHandler#
	 * convertTaskProperties
	 * (de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
	 */
	@Override
	protected HashMap<String, String> convertTaskProperties(NetElementLayout layout) {
		HashMap<String, String> properties = super.convertTaskProperties(layout);

		YTask task = (YTask) getNetElement();
		properties.putAll(MultiInstanceConverter.convert(task));

		return properties;
	}

}
/*
 	Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 	
 	This file is part of OpenPnP.
 	
	OpenPnP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenPnP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenPnP.  If not, see <http://www.gnu.org/licenses/>.
 	
 	For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.machine.generic.driver;

import org.openpnp.Configuration;
import org.openpnp.Job;
import org.openpnp.LengthUnit;
import org.openpnp.Part;
import org.openpnp.machine.generic.GenericDriver;
import org.openpnp.machine.generic.GenericHead;
import org.openpnp.util.LengthUtil;
import org.w3c.dom.Node;

public class GcodeDriver implements GenericDriver {
	private double x, y, z, c;
	
	@Override
	public void configure(Node n) {
		
	}
	
	@Override
	public void prepareJob(Configuration configuration, Job job)
			throws Exception {
	}

	@Override
	public void actuate(GenericHead head, int index, boolean on) throws Exception {
	}

	@Override
	public void home(GenericHead head) throws Exception {
		moveTo(head, 0, 0, 0, 0);
	}

	@Override
	public void moveTo(GenericHead head, double x, double y, double z, double c) throws Exception {
		x = LengthUtil.convertLength(x, LengthUnit.Millimeters, LengthUnit.Inches);
		y = LengthUtil.convertLength(y, LengthUnit.Millimeters, LengthUnit.Inches);
		z = LengthUtil.convertLength(z, LengthUnit.Millimeters, LengthUnit.Inches);
		
		
		StringBuffer sb = new StringBuffer();
		if (x != this.x) {
			sb.append(String.format(" X%2.4f", x));
		}
		if (y != this.y) {
			sb.append(String.format(" Y%2.4f", y));
		}
		if (z != this.z) {
			sb.append(String.format(" Z%2.4f", z));
		}
		if (c != this.c) {
			sb.append(String.format(" A%2.4f", c));
		}
		if (sb.length() > 0) {
			System.out.println("G0 " + sb.toString());
		}
		this.x = x;
		this.y = y;
		this.z = z;
		this.c = c;
	}

	@Override
	public void pick(GenericHead head, Part part) throws Exception {
	}

	@Override
	public void place(GenericHead head) throws Exception {
	}
}

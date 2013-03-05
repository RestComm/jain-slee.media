/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.mgcp.demo.events;

import java.io.Serializable;
import java.util.Random;

import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
/**
 * CustomEvent to communicate between SBB Entities belonging to different Services
 * 
 * @author amit bhayani
 * @author yulian oifa 
 * 
 */
public class CustomEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;

	private EndpointIdentifier endpointID;
	private ConnectionIdentifier connectionID;
	private CallIdentifier callID;

	public CustomEvent(EndpointIdentifier endpointID,ConnectionIdentifier connectionID,CallIdentifier callID) {
		id = new Random().nextLong() ^ System.currentTimeMillis();
		this.endpointID = endpointID;
		this.connectionID=connectionID;
		this.callID = callID;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null)
			return false;
		return (o instanceof CustomEvent) && ((CustomEvent) o).id == id;
	}

	public int hashCode() {
		return (int) id;
	}

	public EndpointIdentifier getEndpointID() {
		return endpointID;
	}
	
	public ConnectionIdentifier getConnectionID() {
		return connectionID;
	}
	
	public CallIdentifier getCallID(){
		return this.callID;
	}

}

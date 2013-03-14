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

/*
 * CallSbb.java
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.mgcp.demo;

import jain.protocol.ip.mgcp.JainMgcpEvent;
import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.ModifyConnection;
import jain.protocol.ip.mgcp.message.CreateConnectionResponse;
import jain.protocol.ip.mgcp.message.ModifyConnectionResponse;
import jain.protocol.ip.mgcp.message.DeleteConnection;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.parms.CallIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.FactoryException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import javax.slee.facilities.Tracer;

import net.java.slee.resource.mgcp.JainMgcpProvider;
import net.java.slee.resource.mgcp.MgcpActivityContextInterfaceFactory;
import net.java.slee.resource.mgcp.MgcpConnectionActivity;
import net.java.slee.resource.mgcp.MgcpEndpointActivity;

import org.mobicents.mgcp.demo.events.CustomEvent;
import org.mobicents.protocols.mgcp.jain.pkg.AUPackage;

/**
 * 
 * @author amit bhayani
 * @author yulian oifa
 */
public abstract class ConfLegSbb implements Sbb {

	public final static String ENDPOINT_NAME = "mobicents/ivr/$";

	public final static String JBOSS_BIND_ADDRESS = System.getProperty("jboss.bind.address", "127.0.0.1");

	public static final int MGCP_PEER_PORT = 2427;
	public static final int MGCP_PORT = 2727;

	private SbbContext sbbContext;

	// MGCP
	private JainMgcpProvider mgcpProvider;
	private MgcpActivityContextInterfaceFactory mgcpAcif;

	private Tracer logger;

	/** Creates a new instance of CallSbb */
	public ConfLegSbb() {
	}

	public void onCallCreated(CustomEvent evt, ActivityContextInterface aci) {

		logger.info("Custom Event received Endpoint = " + evt.getEndpointID() + " Connection Id " + evt.getConnectionID().toString() + "Call Id = " + evt.getCallID().toString());
		this.setEndpointIdentifier(evt.getEndpointID());
		this.setConnectionIdentifier(evt.getConnectionID());
		this.setCallIdentifier(evt.getCallID());
		
		try {
			
		} catch (Exception e) {
			logger.severe("Error while receiving the Custom Event ", e);
		}
	}		

	public void onCallTerminated(CustomEvent evt, ActivityContextInterface aci) {
		logger.info("Conference Terminated " + this.getEndpointIdentifier() + " Connection Id " + this.getConnectionIdentifier().toString() + " callId = " + this.getCallIdentifier().toString());

		EndpointIdentifier endpointID = this.getEndpointIdentifier();
		ConnectionIdentifier connectionID=this.getConnectionIdentifier();
		
		DeleteConnection deleteConnection = new DeleteConnection(this, this.getCallIdentifier(), endpointID,connectionID);

		deleteConnection.setTransactionHandle(this.mgcpProvider.getUniqueTransactionHandler());
		mgcpProvider.sendMgcpEvents(new JainMgcpEvent[] { deleteConnection });
	}

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
		this.logger = sbbContext.getTracer(ConfLegSbb.class.getSimpleName());
		try {
			Context ctx = (Context) new InitialContext().lookup("java:comp/env");

			// initialize media api

			mgcpProvider = (JainMgcpProvider) ctx.lookup("slee/resources/jainmgcp/2.0/provider/demo");
			mgcpAcif = (MgcpActivityContextInterfaceFactory) ctx.lookup("slee/resources/jainmgcp/2.0/acifactory/demo");

		} catch (Exception ne) {
			logger.severe("Could not set SBB context:", ne);
		}
	}

	public abstract EndpointIdentifier getEndpointIdentifier();

	public abstract void setEndpointIdentifier(EndpointIdentifier endpointIdentifier);

	public abstract CallIdentifier getCallIdentifier();

	public abstract void setCallIdentifier(CallIdentifier callIdentifier);

	public abstract ConnectionIdentifier getConnectionIdentifier();
	
	public abstract void setConnectionIdentifier(ConnectionIdentifier connectionIdentifier);	
	
	public void unsetSbbContext() {
		this.sbbContext = null;
		this.logger = null;
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbRemove() {
	}

	public void sbbExceptionThrown(Exception exception, Object object, ActivityContextInterface activityContextInterface) {
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
	}
}

/*
* JBoss, Home of Professional Open Source.
* Copyright 2006, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors.
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
package org.jboss.as.domain.controller;

import java.util.NoSuchElementException;

import org.jboss.as.controller.TransactionalProxyController;
import org.jboss.as.controller.registry.ModelNodeRegistration;
import org.jboss.dmr.ModelNode;

/**
 * Proxy to the local host controller.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public interface HostControllerProxy extends TransactionalProxyController {

    /**
     * Gets the name of the host.
     * @return the host name. Will not be {@code null}
     */
    String getName();

    /**
     * Gets a copy of the host configuration model.
     *
     * @return the model. Will not be {@code null}
     */
    ModelNode getHostModel();

    /**
     * Gets the registry for host model resources.
     *
     * @return the registry. Will not be {@code null}
     */
    ModelNodeRegistration getRegistry();

    /**
     * Gets the name of the server group to which the given server belongs.
     *
     * @param serverName the name of the server
     * @return the name of the server group. Will not be {@code null}
     * @throws NoSuchElementException if the server does not exist
     */
    String getServerGroupName(String serverName);

    /**
     * Starts all the servers in the host configuration whose {@code start} attribute is {@code true}.
     *
     * @param domainController the DomainController the host controller should use for determining server
     *         configurations
     */
    void startServers(DomainController domainController);

    /**
     * Stops all the servers managed by the host controller.
     */
    void stopServers();
}

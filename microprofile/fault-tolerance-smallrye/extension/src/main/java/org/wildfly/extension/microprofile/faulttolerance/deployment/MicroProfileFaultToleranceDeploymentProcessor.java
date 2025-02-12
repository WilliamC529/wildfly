/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
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

package org.wildfly.extension.microprofile.faulttolerance.deployment;

import static org.wildfly.extension.microprofile.faulttolerance.MicroProfileFaultToleranceLogger.ROOT_LOGGER;

import java.util.Set;

import io.smallrye.faulttolerance.FaultToleranceExtension;
import io.smallrye.faulttolerance.metrics.MetricsIntegration;
import org.jboss.as.controller.capability.CapabilityServiceSupport;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.weld.Capabilities;
import org.jboss.as.weld.WeldCapability;

/**
 * This {@link DeploymentUnitProcessor} registers required CDI portable extension that adds support
 * for MP Fault Tolerance interceptor bindings. Moreover, it specifies which metrics provider to use according to
 * metrics integrations available at runtime.
 *
 * @author Radoslav Husar
 */
public class MicroProfileFaultToleranceDeploymentProcessor implements DeploymentUnitProcessor {

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        if (!MicroProfileFaultToleranceMarker.isMarked(deploymentUnit)) {
            return;
        }

        // Weld Extension
        CapabilityServiceSupport support = deploymentUnit.getAttachment(Attachments.CAPABILITY_SERVICE_SUPPORT);

        WeldCapability weldCapability;
        try {
            weldCapability = support.getCapabilityRuntimeAPI(Capabilities.WELD_CAPABILITY_NAME, WeldCapability.class);
        } catch (CapabilityServiceSupport.NoSuchCapabilityException e) {
            throw new IllegalStateException();
        }

        // Configure which metrics provider to use
        Set<String> registeredSubsystems = deploymentUnit.getAttachment(Attachments.REGISTERED_SUBSYSTEMS);

        MetricsIntegration metricsIntegration;

        if (registeredSubsystems.contains("microprofile-metrics-smallrye")) {
            metricsIntegration = MetricsIntegration.MICROPROFILE_METRICS;
        } else {
            metricsIntegration = MetricsIntegration.NOOP;
        }

        ROOT_LOGGER.metricsProvider(metricsIntegration.name());

        weldCapability.registerExtensionInstance(new FaultToleranceExtension(metricsIntegration), deploymentUnit);
    }
}

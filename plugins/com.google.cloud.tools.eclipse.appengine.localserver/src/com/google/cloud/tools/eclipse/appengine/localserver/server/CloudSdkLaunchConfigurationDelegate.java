/*******************************************************************************
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package com.google.cloud.tools.eclipse.appengine.localserver.server;

import com.google.cloud.tools.eclipse.appengine.localserver.Activator;
import com.google.cloud.tools.eclipse.appengine.localserver.GCloudCommandDelegate;
import com.google.common.collect.Lists;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cloud SDK server's launch configuration delegate.
 */
// TODO for https://github.com/GoogleCloudPlatform/gcloud-eclipse-tools/issues/242
// we want to rely on app-tools-lib-for-java here rather than shelling out to the cloud SDK.
public class CloudSdkLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {
  private static final String DEBUGGER_HOST = "localhost";

  @Override
  public void launch(ILaunchConfiguration configuration,
                     String mode,
                     ILaunch launch,
                     IProgressMonitor monitor) throws CoreException {
    final IServer server = ServerUtil.getServer(configuration);
    if (server == null) {
      return;
    }

    final IModule[] modules = server.getModules();
    if (modules == null || modules.length == 0) {
      return;
    }

    CloudSdkServerBehaviour serverBehaviour =
        (CloudSdkServerBehaviour) server.loadAdapter(CloudSdkServerBehaviour.class, null);
    List<String> runnables = Lists.newArrayList();
    for (IModule module : modules) {
      IPath deployPath = serverBehaviour.getModuleDeployDirectory(module);
      runnables.add(deployPath.toOSString());
    }

    IRuntime runtime = server.getRuntime();
    if (runtime == null) {
      return;
    }
    IPath sdkLocation = runtime.getLocation();

    serverBehaviour.setupLaunch(mode);

    final CloudSdkServer cloudSdkServer = CloudSdkServer.getCloudSdkServer(server);
    int debugPort = -1;
    if (ILaunchManager.DEBUG_MODE.equals(mode)) {
      final int port = getDebugPort();
      debugPort = port;

      try {
        runDebugTarget(cloudSdkServer, modules[0].getProject().getName(), port);
      } catch (CoreException e) {
        Activator.logError(e);
      }
    }

    try {
      String commands = GCloudCommandDelegate.createAppRunCommand(sdkLocation.toOSString(),
          runnables.toArray(new String[runnables.size()]), mode, cloudSdkServer.getApiHost(),
          cloudSdkServer.getApiPort(), debugPort);

      String additionalFlags =
          configuration.getAttribute(CloudSdkServer.SERVER_PROGRAM_FLAGS, (String) null);
      if (additionalFlags != null) {
        commands += " " + additionalFlags;
      }

      // FIXME: workaround bug when running on a Java8 JVM
      // https://github.com/GoogleCloudPlatform/gcloud-eclipse-tools/issues/181
      commands += " --jvm_flag=-Dappengine.user.timezone=UTC";

      Activator.logInfo("Executing: " + commands);

      Process process = Runtime.getRuntime().exec(commands, null);
      addProcessFactoryToLaunchConfiguration(configuration);
      // The DebugPlugin handles the streaming of the output to the console and
      // sends notifications of debug events
      DebugPlugin.newProcess(launch, process, commands);
      serverBehaviour.addProcessListener(launch.getProcesses()[0]);
    } catch (IOException | NullPointerException | IllegalStateException | InvalidPathException e) {
      Activator.logError(e);
      serverBehaviour.stop(true);
    }
  }

  /*
   * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=38016 The
   * custom IProcessFactory creates an IProcess object that calls the /quit
   * handler on the dev app server
   */
  private void addProcessFactoryToLaunchConfiguration(ILaunchConfiguration configuration) throws CoreException {
    ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
    workingCopy.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, DevAppServerProcessFactory.ID);
    workingCopy.doSave();
  }

  @Override
  public boolean preLaunchCheck(ILaunchConfiguration configuration,
                                String mode,
                                IProgressMonitor monitor) throws CoreException {
    IServer server = ServerUtil.getServer(configuration);
    if (server == null) {
      return false;
    }
    IModule[] modules = server.getModules();
    if (modules == null || modules.length == 0) {
      abort("No modules associated with this server instance.", null, 0);
      return false;
    } else if (ILaunchManager.DEBUG_MODE.equals(mode) && modules.length > 1) {
      abort("Can only debug a single module.", null, 0);
      return false;
    }
    return super.preLaunchCheck(configuration, mode, monitor);
  }

  @Override
  protected void abort(String message, Throwable exception, int code) throws CoreException {
    throw new CoreException(new Status(IStatus.ERROR,
                                       Activator.PLUGIN_ID,
                                       code,
                                       message,
                                       exception));
  }

  private int getDebugPort() throws CoreException {
    int port = SocketUtil.findFreePort();
    if (port == -1) {
      abort("Cannot find free port for remote debugger", null, IStatus.ERROR);
    }
    return port;
  }

  private void runDebugTarget(CloudSdkServer cloudSdkServer,
                              String projectName,
                              int port) throws CoreException {
    ILaunchConfigurationWorkingCopy remoteDebugLaunchConfig = createRemoteDebugLaunchConfiguration(cloudSdkServer,
                                                                                                   projectName,
                                                                                                   Integer.toString(port));
    remoteDebugLaunchConfig.launch(ILaunchManager.DEBUG_MODE, null);
  }

  private ILaunchConfigurationWorkingCopy createRemoteDebugLaunchConfiguration(CloudSdkServer cloudSdkServer,
                                                                               final String projectName,
                                                                               final String port) throws CoreException {
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_REMOTE_JAVA_APPLICATION);

    final ILaunchConfigurationWorkingCopy remoteDebugConfig = type.newInstance(null,
                                                                               "Remote debugger for "
                                                                                     + projectName
                                                                                     + " ("
                                                                                     + port
                                                                                     + ")");

    // Set project
    remoteDebugConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
                                   projectName);

    // Set JVM debugger connection parameters
    Map<String, String> connectionParameters = new HashMap<>();
    connectionParameters.put("hostname", DEBUGGER_HOST);
    connectionParameters.put("port", port);
    remoteDebugConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP,
                                   connectionParameters);
    remoteDebugConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
        "org.eclipse.jdt.launching.socketListenConnector");

    cloudSdkServer.setRemoteDebugLaunchConfig(remoteDebugConfig);

    return remoteDebugConfig;
  }
}

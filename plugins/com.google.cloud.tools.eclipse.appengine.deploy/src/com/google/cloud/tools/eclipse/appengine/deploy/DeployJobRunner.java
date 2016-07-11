package com.google.cloud.tools.eclipse.appengine.deploy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Runs a {@link AppEngineDeployJob} in an {@link IWorkspace} using a lock to prevent any changes to the project while
 * the operation is in progress.
 *
 */
// Main purpose of this class is to separate Eclipse' job execution from other logic to enhance testability
public class DeployJobRunner {

  public void runJob(AppEngineDeployJob deployJob, IProject project, IProgressMonitor monitor) throws CoreException {
    IWorkspace workspace = project.getWorkspace();
    workspace.run(
        deployJob,
        workspace.getRuleFactory().createRule(project),
        0 /* flags */,
        monitor);
  }

}

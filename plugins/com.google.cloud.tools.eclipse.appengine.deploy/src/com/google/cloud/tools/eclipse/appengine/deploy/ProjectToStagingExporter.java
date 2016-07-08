package com.google.cloud.tools.eclipse.appengine.deploy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.j2ee.internal.deployables.J2EEFlexProjDeployable;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.server.core.util.PublishHelper;

import com.google.common.base.Preconditions;

/**
 * Writes the exploded WAR file of a project to a staging directory.  
 */
public class ProjectToStagingExporter {

  /**
   * It does a smart export, i.e. considers the resources to be copied and
   * if the destination directory already contains resources those will be deleted if they are not part of the
   * exploded WAR.
   */
  public void writeProjectToStageDir(IProject project, String stageDir) throws CoreException {
    Preconditions.checkNotNull(project, 
        Messages.getString("argument.project.null")); //$NON-NLS-1$
    Preconditions.checkNotNull(stageDir, 
        Messages.getString("argument.stagedir.null")); //$NON-NLS-1$
    Preconditions.checkArgument(!stageDir.isEmpty(), 
        Messages.getString("argument.stagedir.empty")); //$NON-NLS-1$
    
    PublishHelper publishHelper = new PublishHelper(null);
    J2EEFlexProjDeployable deployable = new J2EEFlexProjDeployable(project, ComponentCore.createComponent(project));
    publishHelper.publishSmart(deployable.members(), new Path(stageDir), new NullProgressMonitor());
  }
}

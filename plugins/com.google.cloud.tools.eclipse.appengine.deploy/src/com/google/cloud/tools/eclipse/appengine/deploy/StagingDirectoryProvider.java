package com.google.cloud.tools.eclipse.appengine.deploy;

import org.eclipse.core.commands.ExecutionEvent;

/**
 * Common interface class to obtain the staging directory for App Engine Standard deploy.
 */
public interface StagingDirectoryProvider {

  /**
   * @param event the event that was triggered to run the App Engine Standard deploy
   * @return path to the directory to be used for staging
   * @see com.google.cloud.tools.eclipse.appengine.deploy.ProjectToStagingExporter#writeProjectToStageDir(IProject, String)
   * @see com.google.cloud.tools.eclipse.appengine.deploy.AppEngineStandardDeployCommandHandler
   */
  String getStagingDirectory(ExecutionEvent event);

}

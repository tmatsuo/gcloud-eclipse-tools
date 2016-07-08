package com.google.cloud.tools.eclipse.appengine.deploy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

import com.google.common.annotations.VisibleForTesting;

/**
 * Command handler to deploy an App Engine web application project to App Engine Standard.
 * <p>
 * It copies the project's exploded WAR to a staging directory and then executes staging and deploy operations
 * provided by the App Engine Plugins Core Library.
 */
public class AppEngineStandardDeployCommandHandler extends AbstractHandler {

  private ProjectToStagingExporter projectToStagingExporter;
  private StagingDirectoryProvider stagingDirectoryProvider;
  private FacetedProjectHelper facetedProjectHelper;
  
  public AppEngineStandardDeployCommandHandler() {
    this(new ProjectToStagingExporter(), new DialogStagingDirectoryProvider(), new FacetedProjectHelper());
  }
  
  @VisibleForTesting
  AppEngineStandardDeployCommandHandler(ProjectToStagingExporter exporter, 
                                        StagingDirectoryProvider stagingDirectoryProvider,
                                        FacetedProjectHelper facetedProjectHelper) {
    if (exporter == null) {
      projectToStagingExporter = new ProjectToStagingExporter();
    } else {
      projectToStagingExporter = exporter;
    }
    if (stagingDirectoryProvider == null) {
      this.stagingDirectoryProvider = new DialogStagingDirectoryProvider();
    } else {
      this.stagingDirectoryProvider = stagingDirectoryProvider;
    }
    if (facetedProjectHelper == null) {
      this.facetedProjectHelper = new FacetedProjectHelper();
    } else {
      this.facetedProjectHelper = facetedProjectHelper;
    }
  }
  
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      IEvaluationContext applicationContext = (IEvaluationContext)event.getApplicationContext();
      //TODO should we use the approach from http://www.programcreek.com/2013/02/eclipse-rcp-tutorial-add-a-menu-item-to-the-popup-menu-when-right-click-a-file/
      IStructuredSelection selection = 
          (IStructuredSelection) applicationContext.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
      if (selection.getFirstElement() instanceof IProject) {
        IProject project = (IProject) selection.getFirstElement();
        IFacetedProject facetedProject = facetedProjectHelper.getFacetedProject(project);
        // TODO replace with constant from AppEngineFacet (after possibly relocating that class)
        if (facetedProjectHelper.projectHasFacet(facetedProject, 
                                                 "com.google.cloud.tools.eclipse.appengine.facet")) {
          exportProjectToStageDir(event, project);
          //TODO run stage and deploy operations
        }
      }
      // must be null, reserved for future use
      return null;
    } catch (CoreException coreException) {
      throw new ExecutionException("Failed to export the project as exploded WAR", coreException);
    }
  }

  private void exportProjectToStageDir(ExecutionEvent event, IProject iProject) throws CoreException {
    String stageDir = getStagingDirectory(event);
    if (stageDir != null) {
      writeProjectToStageDir(iProject, stageDir);
      verifyProjectInStageDir(stageDir);
    }
  }

  private void writeProjectToStageDir(IProject project, String stageDir) throws CoreException {
    projectToStagingExporter.writeProjectToStageDir(project, stageDir);
  }

  private String getStagingDirectory(ExecutionEvent event) {
    return stagingDirectoryProvider.getStagingDirectory(event);
  }

  private void verifyProjectInStageDir(String stageDir) throws CoreException {
    // TODO verify if the export was successful by checking if appengine-web.xml exists
  }

  @Override
  public boolean isEnabled() {
    // TODO implement properly
    return true;
  }
}

package com.google.cloud.tools.eclipse.appengine.deploy;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AppEngineStandardDeployCommandHandlerTest {

  private static final String TEST_STAGING_DIR = "foo/bar";
  
  @Mock private StagingDirectoryProvider stagingDirectoryProvider;
  @Mock private ProjectToStagingExporter projectToStagingExporter;
  @Mock private FacetedProjectHelper facetedProjectHelper;
  
  @Test
  public void testIsEnabled() {
    assertTrue(new AppEngineStandardDeployCommandHandler().isEnabled());
  }
  
  @Test
  public void testExecute_selectionIsNotIProject() throws ExecutionException {
    AppEngineStandardDeployCommandHandler handler = 
        new AppEngineStandardDeployCommandHandler(projectToStagingExporter,
                                                  stagingDirectoryProvider,
                                                  facetedProjectHelper);
    
    ExecutionEvent event = getTestExecutionEvent(new Object());
    
    handler.execute(event);
    
    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
  }


  @Test
  public void testExecute_projectDoesNotHaveAppEngineFacet() throws Exception {
    AppEngineStandardDeployCommandHandler handler = 
        new AppEngineStandardDeployCommandHandler(projectToStagingExporter,
                                                  stagingDirectoryProvider,
                                                  facetedProjectHelper);
    
    IProject project = mock(IProject.class);
    ExecutionEvent event = getTestExecutionEvent(project);
    IFacetedProject facetedProject = mock(IFacetedProject.class);
    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(false);
    
    handler.execute(event);
    
    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
  }

  @Test(expected = ExecutionException.class)
  public void testExecute_facetedProjectCreationThrowsException() throws ExecutionException, CoreException {
    AppEngineStandardDeployCommandHandler handler = 
        new AppEngineStandardDeployCommandHandler(projectToStagingExporter,
                                                  stagingDirectoryProvider,
                                                  facetedProjectHelper);
    when(facetedProjectHelper.getFacetedProject(isA(IProject.class))).thenThrow(getFakeCoreException());
    
    ExecutionEvent event = getTestExecutionEvent(mock(IProject.class));
    
    handler.execute(event);
  }

  private Status getFakeErrorStatus() {
    return new Status(IStatus.ERROR, "fakePluginId", "test exception");
  }

  @Test
  public void testExecute_stagingDirectoryNull() throws Exception {
    AppEngineStandardDeployCommandHandler handler = 
        new AppEngineStandardDeployCommandHandler(projectToStagingExporter,
                                                  stagingDirectoryProvider,
                                                  facetedProjectHelper);
    
    IProject project = mock(IProject.class);
    ExecutionEvent event = getTestExecutionEvent(project);
    IFacetedProject facetedProject = mock(IFacetedProject.class);
    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(true);
    when(stagingDirectoryProvider.getStagingDirectory(isA(ExecutionEvent.class))).thenReturn(null);
    handler.execute(event);
    
    verifyZeroInteractions(projectToStagingExporter);
  }

  @Test(expected = ExecutionException.class)
  public void testExecute_exportThrowsException() throws Exception {
    AppEngineStandardDeployCommandHandler handler = 
        new AppEngineStandardDeployCommandHandler(projectToStagingExporter,
                                                  stagingDirectoryProvider,
                                                  facetedProjectHelper);
    
    IProject project = mock(IProject.class);
    ExecutionEvent event = getTestExecutionEvent(project);
    IFacetedProject facetedProject = mock(IFacetedProject.class);
    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(true);
    when(stagingDirectoryProvider.getStagingDirectory(isA(ExecutionEvent.class))).thenReturn(TEST_STAGING_DIR);
    doThrow(getFakeCoreException()).when(projectToStagingExporter).writeProjectToStageDir(project, TEST_STAGING_DIR);
    handler.execute(event);
  }

  @Test
  public void testExecute_exportSuccessful() throws Exception {
    AppEngineStandardDeployCommandHandler handler = 
        new AppEngineStandardDeployCommandHandler(projectToStagingExporter,
                                                  stagingDirectoryProvider,
                                                  facetedProjectHelper);
    
    IProject project = mock(IProject.class);
    ExecutionEvent event = getTestExecutionEvent(project);
    IFacetedProject facetedProject = mock(IFacetedProject.class);
    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(true);
    when(stagingDirectoryProvider.getStagingDirectory(isA(ExecutionEvent.class))).thenReturn(TEST_STAGING_DIR);
    Object result = handler.execute(event);
    // has to by null by API contract of org.eclipse.core.commands.IHandler.execute(ExecutionEvent)
    assertNull(result);
  }

  private CoreException getFakeCoreException() {
    return new CoreException(getFakeErrorStatus());
  }

  private ExecutionEvent getTestExecutionEvent(Object project) {
    IEvaluationContext context = mock(IEvaluationContext.class);
    IStructuredSelection selection = mock(IStructuredSelection.class);
    when(selection.getFirstElement()).thenReturn(project);
    when(context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME)).thenReturn(selection);
    return new ExecutionEvent(null /*command */, Collections.EMPTY_MAP, null /* trigger */, context);
  }
}

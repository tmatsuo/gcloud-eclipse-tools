package com.google.cloud.tools.eclipse.appengine.deploy;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.cloud.tools.eclipse.appengine.deploy.standard.ProjectToStagingExporter;
import com.google.cloud.tools.eclipse.appengine.deploy.standard.StagingDirectoryProvider;
import com.google.cloud.tools.eclipse.appengine.deploy.standard.StandardDeployCommandHandler;

@RunWith(MockitoJUnitRunner.class)
public class AppEngineStandardDeployCommandHandlerTest {

  private static final String TEST_STAGING_DIR = "foo/bar";
  
  @Mock private StagingDirectoryProvider stagingDirectoryProvider;
  @Mock private ProjectToStagingExporter projectToStagingExporter;
  @Mock private FacetedProjectHelper facetedProjectHelper;
  
  @Test
  public void testIsEnabled() {
    assertTrue(new StandardDeployCommandHandler().isEnabled());
  }
  
//  @Test
//  public void testExecute_noSelection() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    ExecutionEvent event = getTestExecutionEvent(project, null);
//    
//    handler.execute(event);
//    
//    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
//  }
//
//  @Test
//  public void testExecute_selectionIsNotIStructuredSelection() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    ExecutionEvent event = getTestExecutionEvent(project, new Object());
//    
//    handler.execute(event);
//    
//    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
//  }
//
//  @Test
//  public void testExecute_selectionIsEmpty() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    IStructuredSelection selection = mock(IStructuredSelection.class);
//    when(selection.size()).thenReturn(0);
//    ExecutionEvent event = getTestExecutionEvent(project, selection);
//    
//    handler.execute(event);
//    
//    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
//  }
//
//  @Test
//  public void testExecute_multipleSelection() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    IStructuredSelection selection = mock(IStructuredSelection.class);
//    when(selection.size()).thenReturn(2);
//    ExecutionEvent event = getTestExecutionEvent(project, selection);
//    
//    handler.execute(event);
//    
//    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
//  }
//
//  @Test
//  public void testExecute_selectionIsNotIProject() throws ExecutionException {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    ExecutionEvent event = getTestExecutionEvent(new Object());
//    
//    handler.execute(event);
//    
//    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
//  }
//
//  @Test
//  public void testExecute_projectDoesNotHaveAppEngineFacet() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    ExecutionEvent event = getTestExecutionEvent(project);
//    IFacetedProject facetedProject = mock(IFacetedProject.class);
//    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
//    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(false);
//    
//    handler.execute(event);
//    
//    verifyZeroInteractions(stagingDirectoryProvider, projectToStagingExporter);
//  }
//
//  @Test(expected = ExecutionException.class)
//  public void testExecute_facetedProjectCreationThrowsException() throws ExecutionException, CoreException {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    when(facetedProjectHelper.getFacetedProject(isA(IProject.class))).thenThrow(getFakeCoreException());
//    
//    ExecutionEvent event = getTestExecutionEvent(mock(IProject.class));
//    
//    handler.execute(event);
//  }
//
//  private Status getFakeErrorStatus() {
//    return new Status(IStatus.ERROR, "fakePluginId", "test exception");
//  }
//
//  @Test(expected = ExecutionException.class)
//  public void testExecute_stagingDirectoryNull() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    ExecutionEvent event = getTestExecutionEvent(project);
//    IFacetedProject facetedProject = mock(IFacetedProject.class);
//    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
//    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(true);
//    when(stagingDirectoryProvider.getStagingDirectory(isA(Shell.class))).thenReturn(null);
//    handler.execute(event);
//    
//    verifyZeroInteractions(projectToStagingExporter);
//  }
//
//  @Test(expected = ExecutionException.class)
//  public void testExecute_exportThrowsException() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    ExecutionEvent event = getTestExecutionEvent(project);
//    IFacetedProject facetedProject = mock(IFacetedProject.class);
//    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
//    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(true);
//    when(stagingDirectoryProvider.getStagingDirectory(isA(Shell.class))).thenReturn(TEST_STAGING_DIR);
//    doThrow(getFakeCoreException()).when(projectToStagingExporter).writeProjectToStageDir(project, TEST_STAGING_DIR);
//    handler.execute(event);
//  }
//
//  @Test
//  public void testExecute_exportSuccessful() throws Exception {
//    StandardDeployCommandHandler handler = 
//        new StandardDeployCommandHandler(projectToStagingExporter,
//                                                  stagingDirectoryProvider,
//                                                  facetedProjectHelper);
//    
//    IProject project = mock(IProject.class);
//    ExecutionEvent event = getTestExecutionEvent(project);
//    IFacetedProject facetedProject = mock(IFacetedProject.class);
//    when(facetedProjectHelper.getFacetedProject(project)).thenReturn(facetedProject);
//    when(facetedProjectHelper.projectHasFacet(eq(facetedProject), isA(String.class))).thenReturn(true);
//    when(stagingDirectoryProvider.getStagingDirectory(isA(Shell.class))).thenReturn(TEST_STAGING_DIR);
//    Object result = handler.execute(event);
//    // has to by null by API contract of org.eclipse.core.commands.IHandler.execute(ExecutionEvent)
//    assertNull(result);
//  }
//
//  private CoreException getFakeCoreException() {
//    return new CoreException(getFakeErrorStatus());
//  }
//
//  private ExecutionEvent getTestExecutionEvent(Object project) {
//    IEvaluationContext context = mock(IEvaluationContext.class);
//    IStructuredSelection selection = mock(IStructuredSelection.class);
//    when(selection.size()).thenReturn(1);
//    when(selection.getFirstElement()).thenReturn(project);
//    when(context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME)).thenReturn(selection);
//    when(context.getVariable(ISources.ACTIVE_SHELL_NAME)).thenReturn(mock(Shell.class));
//    return new ExecutionEvent(null /*command */, Collections.EMPTY_MAP, null /* trigger */, context);
//  }
//  
//  private ExecutionEvent getTestExecutionEvent(Object project, Object selection) {
//    IEvaluationContext context = mock(IEvaluationContext.class);
//    when(context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME)).thenReturn(selection);
//    when(context.getVariable(ISources.ACTIVE_SHELL_NAME)).thenReturn(mock(Shell.class));
//    return new ExecutionEvent(null /*command */, Collections.EMPTY_MAP, null /* trigger */, context);
//  }
}

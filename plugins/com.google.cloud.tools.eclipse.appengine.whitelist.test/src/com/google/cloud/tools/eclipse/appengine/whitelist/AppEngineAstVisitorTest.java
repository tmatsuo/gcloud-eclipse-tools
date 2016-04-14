package com.google.cloud.tools.eclipse.appengine.whitelist;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AppEngineAstVisitorTest {

  private AppEngineAstVisitor visitor;
  @SuppressWarnings("deprecation")
  private ASTParser parser = ASTParser.newParser(AST.JLS4); // Java 7

  @Before
  public void setUp() throws Exception {
    visitor = new AppEngineAstVisitor();
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    // todo use K_EXPRESSION or K_STATEMENT here instead?
    parser.setResolveBindings(true);
    parser.setBindingsRecovery(true);
    String unitName = "Foo.java";
    parser.setUnitName(unitName);

    Map options = JavaCore.getOptions();
    parser.setCompilerOptions(options);
 


    String[] sources = { "/Users/elharo/gcloud-eclipse-tools/plugins/com.google.cloud.tools.eclipse.appengine.whitelist.test/src" }; 
    String[] classpath = {"/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/rt.jar"};
 
    parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true); // include boot classpath to resolve bindings
  }

  @Test
  public void testEndVisit() throws JavaModelException {
    File file = new File("testdata/Foo.java");
    IWorkspace workspace = ResourcesPlugin.getWorkspace();    
    IPath location = Path.fromOSString(file.getAbsolutePath()); 
    IFile ifile = workspace.getRoot().getFileForLocation(location);
    IJavaModel javaModel = JavaCore.create(workspace.getRoot());
    IJavaProject projects[] = javaModel.getJavaProjects();

  //  Assert.assertTrue(projects.length > 0);
    
    ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
    // Returns null if unable to recognize the compilation unit.
    Assert.assertNotNull("test setup broken", icu);
     parser.setSource(icu);
    
  //  String program = "public class Foo{}";
  //  parser.setSource(program.toCharArray());
    
    CompilationUnit cu = (CompilationUnit) parser.createAST(null);
    Assert.assertTrue("not bound", cu.getAST().hasBindingsRecovery());
    
    ASTNode ast = parser.createAST(null);
    ast.accept(visitor);
    
    Assert.assertEquals(1, visitor.getProblems().size());
  }

}

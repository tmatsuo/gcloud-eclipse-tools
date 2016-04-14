package com.google.cloud.tools.eclipse.appengine.whitelist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
 
public class Main {
 
  //use ASTParse to parse string
  public static void parse(String str) throws JavaModelException {
    ASTParser parser = ASTParser.newParser(AST.JLS4);
    parser.setSource(str.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setResolveBindings(true);
    parser.setEnvironment(null, null, null, true);
    
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IJavaModel javaModel = JavaCore.create(workspace.getRoot());
    IJavaProject projects[] = javaModel.getJavaProjects();
    
    final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
 
    cu.accept(new ASTVisitor() {
      public boolean visit(SimpleName node) {
        
          System.out.println("Usage of '" + node.getFullyQualifiedName() + "' at line "
              + cu.getLineNumber(node.getStartPosition()));
        return true;
      }
    });
 
  }
 
  //read file content into a string
  public static String readFileToString(String filePath) throws IOException {
    StringBuilder fileData = new StringBuilder(1000);
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
    char[] buf = new char[10];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1) {
      System.out.println(numRead);
      String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
      buf = new char[1024];
    }
 
    reader.close();
 
    return  fileData.toString();  
  }
 
  //loop directory to get file list
  public static void ParseFilesInDir() throws IOException, JavaModelException {
    File dirs = new File(".");
    String dirPath = dirs.getCanonicalPath() + File.separator + "testdata" + File.separator;
 
    File root = new File(dirPath);
    File[] files = root.listFiles();
    String filePath = null;
 
     for (File f : files ) {
       filePath = f.getAbsolutePath();
       if(f.isFile()) {
         parse(readFileToString(filePath));
       }
     }
  }
 
  public static void main(String[] args) throws IOException, JavaModelException {
    ParseFilesInDir();
  }
}
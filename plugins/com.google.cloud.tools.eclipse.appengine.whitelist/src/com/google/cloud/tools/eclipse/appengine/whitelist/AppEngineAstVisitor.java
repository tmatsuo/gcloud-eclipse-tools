package com.google.cloud.tools.eclipse.appengine.whitelist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SimpleName;

class AppEngineAstVisitor extends ASTVisitor {

  private List<CategorizedProblem> problems = new ArrayList<>();
  
  List<CategorizedProblem> getProblems() {
    return problems;
  }
  
  @Override
  public void endVisit(SimpleName node) {
    IBinding binding = node.resolveBinding();
    if (binding == null) {
      // Ignore nodes for which there is no binding
      return;
    }

    if (binding.getKind() == IBinding.TYPE) {
      ITypeBinding iTypeBinding = (ITypeBinding) binding;
      String className = iTypeBinding.getQualifiedName();
      if (!AppEngineJreWhitelist.contains(className)) {
        CategorizedProblem problem = new AppEngineProblem();
        problems.add(problem);
      }
    }
  }

}

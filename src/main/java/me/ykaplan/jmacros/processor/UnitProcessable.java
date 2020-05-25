package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.tree.JCTree;

interface UnitProcessable {
  void processUnit(TreeElement<JCTree.JCCompilationUnit> compilationUnitTree);
}

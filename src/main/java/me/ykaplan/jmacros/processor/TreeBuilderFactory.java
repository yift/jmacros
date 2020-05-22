package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

class TreeBuilderFactory {
  private final TreeMaker treeMaker;
  private final Names names;

  TreeBuilderFactory(JavacProcessingEnvironment processingEnvironment) {
    var context = processingEnvironment.getContext();
    treeMaker = TreeMaker.instance(context);
    names = Names.instance(context);
  }

  TreeBuilder builder(JCTree element) {
    return new TreeBuilder(treeMaker.at(element.pos()), names);
  }
}

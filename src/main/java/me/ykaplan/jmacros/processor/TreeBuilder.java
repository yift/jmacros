package me.ykaplan.jmacros.processor;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

class TreeBuilder {
  private final TreeMaker treeMaker;
  private final Names names;

  TreeBuilder(JavacProcessingEnvironment processingEnvironment) {
    var context = processingEnvironment.getContext();
    treeMaker = TreeMaker.instance(context);
    names = Names.instance(context);
  }

  JCTree.JCLiteral createLiteral(Object value) {
    return treeMaker.Literal(value);
  }

  JCTree.JCExpression createIdent(String name) {
    var splitName = name.split("\\.");
    JCTree.JCExpression ret = null;
    for (int i = 0; i < splitName.length; ++i) {
      var shortName = names.fromString(splitName[i]);
      if (ret == null) {
        ret = treeMaker.Ident(shortName);
      } else {
        ret = treeMaker.Select(ret, shortName);
      }
    }
    return ret;
  }

  JCTree.JCMethodInvocation staticMethodInvoke(String method, Object... literalArgs) {
    List<JCTree.JCExpression> arguments = List.nil();
    for (var arg : literalArgs) {
      var argument = createLiteral(arg);
      arguments = arguments.append(argument);
    }
    var select = createIdent(method);

    return treeMaker.Apply(null, select, arguments);
  }
}

package me.ykaplan.jmacros.processor;

import static com.sun.tools.javac.tree.JCTree.Tag.PLUS;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

class TreeBuilder {
  private final TreeMaker treeMaker;
  private final Names names;

  TreeBuilder(TreeMaker treeMaker, Names names) {
    this.treeMaker = treeMaker;
    this.names = names;
  }

  JCTree.JCLiteral createLiteral(Object value) {
    if (value == null) {
      return treeMaker.Literal(TypeTag.BOT, null);
    } else {
      return treeMaker.Literal(value);
    }
  }

  JCTree.JCExpression createIdent(String name) {
    var splitName = name.split("\\.");
    JCTree.JCExpression ret = null;
    for (int i = 0; i < splitName.length; ++i) {
      var shortName = createName(splitName[i]);
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

  JCTree.JCExpression createByteArray(byte[] replacement) {
    List<JCTree.JCExpression> arguments = List.nil();
    for (byte b : replacement) {
      var argument = createLiteral((int) b);
      arguments = arguments.append(argument);
    }
    var identifier = treeMaker.TypeIdent(TypeTag.BYTE);

    return treeMaker.NewArray(identifier, List.nil(), arguments);
  }

  JCTree.JCExpression createAdd(JCTree.JCExpression left, JCTree.JCExpression right) {
    return treeMaker.Binary(PLUS, left, right);
  }

  public Name createName(String name) {
    return names.fromString(name);
  }

  public Name createName(Name name) {
    return createName(name.toString());
  }
}

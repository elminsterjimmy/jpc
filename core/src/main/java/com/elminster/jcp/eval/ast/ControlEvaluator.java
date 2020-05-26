package com.elminster.jcp.eval.ast;

import com.elminster.jcp.ast.Expression;
import com.elminster.jcp.ast.Node;
import com.elminster.jcp.eval.data.BooleanData;
import com.elminster.jcp.eval.data.Data;
import com.elminster.jcp.eval.Evaluable;
import com.elminster.jcp.eval.context.EvalContext;
import com.elminster.jcp.eval.factory.AstEvaluatorFactory;

abstract public class ControlEvaluator extends ExpressionEvaluator {

  public ControlEvaluator(Node astNode) {
    super(astNode);
  }

  protected boolean checkCondition(Expression expression, EvalContext context) throws Exception {
    Evaluable evaluable = AstEvaluatorFactory.getEvaluator(expression);
    Data<Boolean> condition = evaluable.eval(context);
    return condition.get();
  }
}

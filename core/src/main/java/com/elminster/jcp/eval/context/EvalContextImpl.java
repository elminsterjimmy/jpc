package com.elminster.jcp.eval.context;

import com.elminster.common.util.Assert;
import com.elminster.jcp.ast.Identifier;
import com.elminster.jcp.collection.FastStack;
import com.elminster.jcp.eval.data.Data;
import com.elminster.jcp.eval.data.DataType;
import com.elminster.jcp.eval.excpetion.AlreadyDeclaredException;
import com.elminster.jcp.ast.statement.Function;
import com.elminster.jcp.module.vb.ValueBuffer;
import com.elminster.jcp.util.ClassConverter;

import java.util.*;

public class EvalContextImpl implements EvalContext {

  private Map<String, Data> variables = new HashMap<>();
  private Map<String, Function> functions = new HashMap<>();
  private Map<String, DataType> dataTypes = new HashMap<>();
  private LoopContext loopContext;
  private FastStack<Data> functionVariableStack = new FastStack<>();

  public EvalContextImpl() {
    init();
  }

  private void init() {
    registerSystemDataTypes();
    registerSystemFunctions();
  }

  private void registerSystemFunctions() {
    ClassConverter.registerClass(ValueBuffer.class, this, "base");
  }

  private void registerSystemDataTypes() {
    for (DataType.SystemDataType systemDataType : DataType.SystemDataType.values()) {
      addDataType(systemDataType);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Data> getVariables() {
    return variables;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Data getVariable(String name) {
    return variables.get(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addVariable(Data variable) {
    Identifier identifier = variable.getIdentifier();
    String id = identifier.getId();
    if (variables.containsKey(id)) {
      AlreadyDeclaredException.throwVariableAlreadyDeclaredException(identifier);
    }
    variables.put(id, variable);
  }

  @Override
  public void setVariables(Map<String, Data> variables) {
    Assert.notNull(variables);
    this.variables = variables;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Function> getFunctions() {
    return Collections.unmodifiableMap(functions);
  }

  @Override
  public void addFunction(Function function) {
    String functionFullName = function.getFullName();
    if (functions.containsKey(functionFullName)) {
      AlreadyDeclaredException.throwFunctionAlreadyDeclaredException(function);
    }
    functions.put(functionFullName, function);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Function getFunction(String name) {
    return functions.get(name);
  }

  @Override
  public void addDataType(DataType dataType) {
    String name = dataType.getName();
    if (dataTypes.containsKey(name)) {
      AlreadyDeclaredException.throwDataTypeAlreadyDeclaredException(Identifier.fromName(name));
    }
    dataTypes.put(name, dataType);
  }

  @Override
  public DataType getDataType(String name) {
    String convertedName = convertSystemDataTypeName(name);
    return dataTypes.get(convertedName);
  }

  private String convertSystemDataTypeName(String name) {
    if ("Object".equals(name)) {
      return DataType.SystemDataType.ANY.getName();
    } else if ("int".equals(name)) {
      return DataType.SystemDataType.INT.getName();
    } else if ("boolean".equals(name)) {
      return DataType.SystemDataType.BOOLEAN.getName();
    } else if ("void".equals(name)) {
      return DataType.SystemDataType.VOID.getName();
    }
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LoopContext getLoopContext() {
    return loopContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setLoopContext(LoopContext loopContext) {
    this.loopContext = loopContext;
  }

  @Override
  public FastStack<Data> getFuncVariableStack() {
    return functionVariableStack;
  }

  @Override
  public String toString() {
    return "FlowContextImpl{" +
        ", variables=" + variables +
        ", functions=" + functions +
        ", loopContext=" + loopContext +
        '}';
  }
}

// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import java.util.Arrays;
import java.util.List;

@GeneratedBy(SLReadlnBuiltin.class)
@SuppressWarnings("unused")
public final class SLReadlnBuiltinFactory implements NodeFactory<SLReadlnBuiltin> {

    private static final SLReadlnBuiltinFactory INSTANCE = new SLReadlnBuiltinFactory();

    private SLReadlnBuiltinFactory() {
    }

    @Override
    public Class<SLReadlnBuiltin> getNodeClass() {
        return SLReadlnBuiltin.class;
    }

    @Override
    public List<Class<? extends Node>> getExecutionSignature() {
        return Arrays.asList();
    }

    @Override
    public List<List<Class<?>>> getNodeSignatures() {
        return Arrays.asList(Arrays.asList(SLExpressionNode[].class));
    }

    @Override
    public SLReadlnBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLReadlnBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLReadlnBuiltin create(SLExpressionNode[] arguments) {
        return new SLReadlnBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLReadlnBuiltin.class)
    public static final class SLReadlnBuiltinNodeGen extends SLReadlnBuiltin {

        private SLReadlnBuiltinNodeGen(SLExpressionNode[] arguments) {
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            return readln();
        }

        @Override
        public NodeCost getCost() {
            return NodeCost.MONOMORPHIC;
        }

    }
}

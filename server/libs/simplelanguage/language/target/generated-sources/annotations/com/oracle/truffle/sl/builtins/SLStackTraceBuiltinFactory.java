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

@GeneratedBy(SLStackTraceBuiltin.class)
@SuppressWarnings("unused")
public final class SLStackTraceBuiltinFactory implements NodeFactory<SLStackTraceBuiltin> {

    private static final SLStackTraceBuiltinFactory INSTANCE = new SLStackTraceBuiltinFactory();

    private SLStackTraceBuiltinFactory() {
    }

    @Override
    public Class<SLStackTraceBuiltin> getNodeClass() {
        return SLStackTraceBuiltin.class;
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
    public SLStackTraceBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLStackTraceBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLStackTraceBuiltin create(SLExpressionNode[] arguments) {
        return new SLStackTraceBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLStackTraceBuiltin.class)
    public static final class SLStackTraceBuiltinNodeGen extends SLStackTraceBuiltin {

        private SLStackTraceBuiltinNodeGen(SLExpressionNode[] arguments) {
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            return trace();
        }

        @Override
        public NodeCost getCost() {
            return NodeCost.MONOMORPHIC;
        }

    }
}

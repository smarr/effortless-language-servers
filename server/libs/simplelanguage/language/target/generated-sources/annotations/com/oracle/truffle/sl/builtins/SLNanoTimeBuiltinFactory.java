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

@GeneratedBy(SLNanoTimeBuiltin.class)
@SuppressWarnings("unused")
public final class SLNanoTimeBuiltinFactory implements NodeFactory<SLNanoTimeBuiltin> {

    private static final SLNanoTimeBuiltinFactory INSTANCE = new SLNanoTimeBuiltinFactory();

    private SLNanoTimeBuiltinFactory() {
    }

    @Override
    public Class<SLNanoTimeBuiltin> getNodeClass() {
        return SLNanoTimeBuiltin.class;
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
    public SLNanoTimeBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLNanoTimeBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLNanoTimeBuiltin create(SLExpressionNode[] arguments) {
        return new SLNanoTimeBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLNanoTimeBuiltin.class)
    public static final class SLNanoTimeBuiltinNodeGen extends SLNanoTimeBuiltin {

        private SLNanoTimeBuiltinNodeGen(SLExpressionNode[] arguments) {
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            return nanoTime();
        }

        @Override
        public NodeCost getCost() {
            return NodeCost.MONOMORPHIC;
        }

    }
}

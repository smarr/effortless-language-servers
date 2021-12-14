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

@GeneratedBy(SLWrapPrimitiveBuiltin.class)
public final class SLWrapPrimitiveBuiltinFactory implements NodeFactory<SLWrapPrimitiveBuiltin> {

    private static final SLWrapPrimitiveBuiltinFactory INSTANCE = new SLWrapPrimitiveBuiltinFactory();

    private SLWrapPrimitiveBuiltinFactory() {
    }

    @Override
    public Class<SLWrapPrimitiveBuiltin> getNodeClass() {
        return SLWrapPrimitiveBuiltin.class;
    }

    @Override
    public List<Class<? extends Node>> getExecutionSignature() {
        return Arrays.asList(SLExpressionNode.class);
    }

    @Override
    public List<List<Class<?>>> getNodeSignatures() {
        return Arrays.asList(Arrays.asList(SLExpressionNode[].class));
    }

    @Override
    public SLWrapPrimitiveBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLWrapPrimitiveBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLWrapPrimitiveBuiltin create(SLExpressionNode[] arguments) {
        return new SLWrapPrimitiveBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLWrapPrimitiveBuiltin.class)
    public static final class SLWrapPrimitiveBuiltinNodeGen extends SLWrapPrimitiveBuiltin {

        @Child private SLExpressionNode arguments0_;

        private SLWrapPrimitiveBuiltinNodeGen(SLExpressionNode[] arguments) {
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            Object arguments0Value_ = this.arguments0_.executeGeneric(frameValue);
            return doDefault(arguments0Value_);
        }

        @Override
        public NodeCost getCost() {
            return NodeCost.MONOMORPHIC;
        }

    }
}

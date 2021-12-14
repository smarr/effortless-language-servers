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

@GeneratedBy(SLHelloEqualsWorldBuiltin.class)
@SuppressWarnings("unused")
public final class SLHelloEqualsWorldBuiltinFactory implements NodeFactory<SLHelloEqualsWorldBuiltin> {

    private static final SLHelloEqualsWorldBuiltinFactory INSTANCE = new SLHelloEqualsWorldBuiltinFactory();

    private SLHelloEqualsWorldBuiltinFactory() {
    }

    @Override
    public Class<SLHelloEqualsWorldBuiltin> getNodeClass() {
        return SLHelloEqualsWorldBuiltin.class;
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
    public SLHelloEqualsWorldBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLHelloEqualsWorldBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLHelloEqualsWorldBuiltin create(SLExpressionNode[] arguments) {
        return new SLHelloEqualsWorldBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLHelloEqualsWorldBuiltin.class)
    public static final class SLHelloEqualsWorldBuiltinNodeGen extends SLHelloEqualsWorldBuiltin {

        private SLHelloEqualsWorldBuiltinNodeGen(SLExpressionNode[] arguments) {
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            return change();
        }

        @Override
        public NodeCost getCost() {
            return NodeCost.MONOMORPHIC;
        }

    }
}

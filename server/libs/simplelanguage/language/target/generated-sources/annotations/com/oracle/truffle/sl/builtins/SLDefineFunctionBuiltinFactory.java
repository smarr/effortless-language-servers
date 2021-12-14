// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import java.util.Arrays;
import java.util.List;

@GeneratedBy(SLDefineFunctionBuiltin.class)
public final class SLDefineFunctionBuiltinFactory implements NodeFactory<SLDefineFunctionBuiltin> {

    private static final SLDefineFunctionBuiltinFactory INSTANCE = new SLDefineFunctionBuiltinFactory();

    private SLDefineFunctionBuiltinFactory() {
    }

    @Override
    public Class<SLDefineFunctionBuiltin> getNodeClass() {
        return SLDefineFunctionBuiltin.class;
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
    public SLDefineFunctionBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLDefineFunctionBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLDefineFunctionBuiltin create(SLExpressionNode[] arguments) {
        return new SLDefineFunctionBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLDefineFunctionBuiltin.class)
    public static final class SLDefineFunctionBuiltinNodeGen extends SLDefineFunctionBuiltin {

        @Child private SLExpressionNode arguments0_;
        @CompilationFinal private int state_0_;

        private SLDefineFunctionBuiltinNodeGen(SLExpressionNode[] arguments) {
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            int state_0 = this.state_0_;
            Object arguments0Value_ = this.arguments0_.executeGeneric(frameValue);
            if (state_0 != 0 /* is-state_0 defineFunction(String) */ && arguments0Value_ instanceof String) {
                String arguments0Value__ = (String) arguments0Value_;
                return defineFunction(arguments0Value__);
            }
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return executeAndSpecialize(arguments0Value_);
        }

        private String executeAndSpecialize(Object arguments0Value) {
            int state_0 = this.state_0_;
            if (arguments0Value instanceof String) {
                String arguments0Value_ = (String) arguments0Value;
                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 defineFunction(String) */;
                return defineFunction(arguments0Value_);
            }
            throw new UnsupportedSpecializationException(this, new Node[] {this.arguments0_}, arguments0Value);
        }

        @Override
        public NodeCost getCost() {
            int state_0 = this.state_0_;
            if (state_0 == 0) {
                return NodeCost.UNINITIALIZED;
            } else {
                return NodeCost.MONOMORPHIC;
            }
        }

    }
}

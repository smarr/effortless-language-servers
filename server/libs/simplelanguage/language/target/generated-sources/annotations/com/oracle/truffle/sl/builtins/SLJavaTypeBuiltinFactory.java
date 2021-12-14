// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLJavaTypeBuiltin.class)
public final class SLJavaTypeBuiltinFactory implements NodeFactory<SLJavaTypeBuiltin> {

    private static final SLJavaTypeBuiltinFactory INSTANCE = new SLJavaTypeBuiltinFactory();
    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    private SLJavaTypeBuiltinFactory() {
    }

    @Override
    public Class<SLJavaTypeBuiltin> getNodeClass() {
        return SLJavaTypeBuiltin.class;
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
    public SLJavaTypeBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLJavaTypeBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLJavaTypeBuiltin create(SLExpressionNode[] arguments) {
        return new SLJavaTypeBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLJavaTypeBuiltin.class)
    public static final class SLJavaTypeBuiltinNodeGen extends SLJavaTypeBuiltin {

        @Child private SLExpressionNode arguments0_;
        @CompilationFinal private volatile int state_0_;
        @Child private InteropLibrary interop_;

        private SLJavaTypeBuiltinNodeGen(SLExpressionNode[] arguments) {
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            int state_0 = this.state_0_;
            Object arguments0Value_ = this.arguments0_.executeGeneric(frameValue);
            if (state_0 != 0 /* is-state_0 doLookup(Object, InteropLibrary) */) {
                return doLookup(arguments0Value_, this.interop_);
            }
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return executeAndSpecialize(arguments0Value_);
        }

        private Object executeAndSpecialize(Object arguments0Value) {
            Lock lock = getLock();
            boolean hasLock = true;
            lock.lock();
            try {
                int state_0 = this.state_0_;
                this.interop_ = super.insert((INTEROP_LIBRARY_.createDispatched(3)));
                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 doLookup(Object, InteropLibrary) */;
                lock.unlock();
                hasLock = false;
                return doLookup(arguments0Value, this.interop_);
            } finally {
                if (hasLock) {
                    lock.unlock();
                }
            }
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

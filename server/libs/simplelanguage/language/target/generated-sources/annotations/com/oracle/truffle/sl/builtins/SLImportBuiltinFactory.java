// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLImportBuiltin.class)
public final class SLImportBuiltinFactory implements NodeFactory<SLImportBuiltin> {

    private static final SLImportBuiltinFactory INSTANCE = new SLImportBuiltinFactory();
    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    private SLImportBuiltinFactory() {
    }

    @Override
    public Class<SLImportBuiltin> getNodeClass() {
        return SLImportBuiltin.class;
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
    public SLImportBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLImportBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLImportBuiltin create(SLExpressionNode[] arguments) {
        return new SLImportBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLImportBuiltin.class)
    public static final class SLImportBuiltinNodeGen extends SLImportBuiltin {

        @Child private SLExpressionNode arguments0_;
        @CompilationFinal private volatile int state_0_;
        @Child private InteropLibrary arrays_;

        private SLImportBuiltinNodeGen(SLExpressionNode[] arguments) {
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
        }

        @Override
        protected Object execute(VirtualFrame frameValue) {
            int state_0 = this.state_0_;
            Object arguments0Value_ = this.arguments0_.executeGeneric(frameValue);
            if (state_0 != 0 /* is-state_0 importSymbol(String, InteropLibrary) */ && arguments0Value_ instanceof String) {
                String arguments0Value__ = (String) arguments0Value_;
                return importSymbol(arguments0Value__, this.arrays_);
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
                if (arguments0Value instanceof String) {
                    String arguments0Value_ = (String) arguments0Value;
                    this.arrays_ = super.insert((INTEROP_LIBRARY_.createDispatched(3)));
                    this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 importSymbol(String, InteropLibrary) */;
                    lock.unlock();
                    hasLock = false;
                    return importSymbol(arguments0Value_, this.arrays_);
                }
                throw new UnsupportedSpecializationException(this, new Node[] {this.arguments0_}, arguments0Value);
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

// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.memory.MemoryFence;
import com.oracle.truffle.api.nodes.EncapsulatingNodeReference;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLIsNullBuiltin.class)
@SuppressWarnings("unused")
public final class SLIsNullBuiltinFactory implements NodeFactory<SLIsNullBuiltin> {

    private static final SLIsNullBuiltinFactory INSTANCE = new SLIsNullBuiltinFactory();
    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    private SLIsNullBuiltinFactory() {
    }

    @Override
    public Class<SLIsNullBuiltin> getNodeClass() {
        return SLIsNullBuiltin.class;
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
    public SLIsNullBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLIsNullBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLIsNullBuiltin create(SLExpressionNode[] arguments) {
        return new SLIsNullBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLIsNullBuiltin.class)
    public static final class SLIsNullBuiltinNodeGen extends SLIsNullBuiltin {

        @Child private SLExpressionNode arguments0_;
        @CompilationFinal private volatile int state_0_;
        @CompilationFinal private volatile int exclude_;
        @Child private IsExecutable0Data isExecutable0_cache;

        private SLIsNullBuiltinNodeGen(SLExpressionNode[] arguments) {
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
        }

        @ExplodeLoop
        @Override
        protected Object execute(VirtualFrame frameValue) {
            int state_0 = this.state_0_;
            Object arguments0Value_ = this.arguments0_.executeGeneric(frameValue);
            if (state_0 != 0 /* is-state_0 isExecutable(Object, InteropLibrary) || isExecutable(Object, InteropLibrary) */) {
                if ((state_0 & 0b1) != 0 /* is-state_0 isExecutable(Object, InteropLibrary) */) {
                    IsExecutable0Data s0_ = this.isExecutable0_cache;
                    while (s0_ != null) {
                        if ((s0_.values_.accepts(arguments0Value_))) {
                            return isExecutable(arguments0Value_, s0_.values_);
                        }
                        s0_ = s0_.next_;
                    }
                }
                if ((state_0 & 0b10) != 0 /* is-state_0 isExecutable(Object, InteropLibrary) */) {
                    return this.isExecutable1Boundary(state_0, arguments0Value_);
                }
            }
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return executeAndSpecialize(arguments0Value_);
        }

        @SuppressWarnings("static-method")
        @TruffleBoundary
        private Object isExecutable1Boundary(int state_0, Object arguments0Value_) {
            EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
            Node prev_ = encapsulating_.set(this);
            try {
                {
                    InteropLibrary isExecutable1_values__ = (INTEROP_LIBRARY_.getUncached(arguments0Value_));
                    return isExecutable(arguments0Value_, isExecutable1_values__);
                }
            } finally {
                encapsulating_.set(prev_);
            }
        }

        private boolean executeAndSpecialize(Object arguments0Value) {
            Lock lock = getLock();
            boolean hasLock = true;
            lock.lock();
            try {
                int state_0 = this.state_0_;
                int exclude = this.exclude_;
                if ((exclude) == 0 /* is-not-exclude isExecutable(Object, InteropLibrary) */) {
                    int count0_ = 0;
                    IsExecutable0Data s0_ = this.isExecutable0_cache;
                    if ((state_0 & 0b1) != 0 /* is-state_0 isExecutable(Object, InteropLibrary) */) {
                        while (s0_ != null) {
                            if ((s0_.values_.accepts(arguments0Value))) {
                                break;
                            }
                            s0_ = s0_.next_;
                            count0_++;
                        }
                    }
                    if (s0_ == null) {
                        // assert (s0_.values_.accepts(arguments0Value));
                        if (count0_ < (3)) {
                            s0_ = super.insert(new IsExecutable0Data(isExecutable0_cache));
                            s0_.values_ = s0_.insertAccessor((INTEROP_LIBRARY_.create(arguments0Value)));
                            MemoryFence.storeStore();
                            this.isExecutable0_cache = s0_;
                            this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 isExecutable(Object, InteropLibrary) */;
                        }
                    }
                    if (s0_ != null) {
                        lock.unlock();
                        hasLock = false;
                        return isExecutable(arguments0Value, s0_.values_);
                    }
                }
                {
                    InteropLibrary isExecutable1_values__ = null;
                    {
                        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                        Node prev_ = encapsulating_.set(this);
                        try {
                            isExecutable1_values__ = (INTEROP_LIBRARY_.getUncached(arguments0Value));
                            this.exclude_ = exclude = exclude | 0b1 /* add-exclude isExecutable(Object, InteropLibrary) */;
                            this.isExecutable0_cache = null;
                            state_0 = state_0 & 0xfffffffe /* remove-state_0 isExecutable(Object, InteropLibrary) */;
                            this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 isExecutable(Object, InteropLibrary) */;
                            lock.unlock();
                            hasLock = false;
                            return isExecutable(arguments0Value, isExecutable1_values__);
                        } finally {
                            encapsulating_.set(prev_);
                        }
                    }
                }
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
                if ((state_0 & (state_0 - 1)) == 0 /* is-single-state_0  */) {
                    IsExecutable0Data s0_ = this.isExecutable0_cache;
                    if ((s0_ == null || s0_.next_ == null)) {
                        return NodeCost.MONOMORPHIC;
                    }
                }
            }
            return NodeCost.POLYMORPHIC;
        }

        @GeneratedBy(SLIsNullBuiltin.class)
        private static final class IsExecutable0Data extends Node {

            @Child IsExecutable0Data next_;
            @Child InteropLibrary values_;

            IsExecutable0Data(IsExecutable0Data next_) {
                this.next_ = next_;
            }

            @Override
            public NodeCost getCost() {
                return NodeCost.NONE;
            }

            <T extends Node> T insertAccessor(T node) {
                return super.insert(node);
            }

        }
    }
}

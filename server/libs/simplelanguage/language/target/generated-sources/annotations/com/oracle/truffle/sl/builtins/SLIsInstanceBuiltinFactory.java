// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
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

@GeneratedBy(SLIsInstanceBuiltin.class)
@SuppressWarnings("unused")
public final class SLIsInstanceBuiltinFactory implements NodeFactory<SLIsInstanceBuiltin> {

    private static final SLIsInstanceBuiltinFactory INSTANCE = new SLIsInstanceBuiltinFactory();
    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    private SLIsInstanceBuiltinFactory() {
    }

    @Override
    public Class<SLIsInstanceBuiltin> getNodeClass() {
        return SLIsInstanceBuiltin.class;
    }

    @Override
    public List<Class<? extends Node>> getExecutionSignature() {
        return Arrays.asList(SLExpressionNode.class, SLExpressionNode.class);
    }

    @Override
    public List<List<Class<?>>> getNodeSignatures() {
        return Arrays.asList(Arrays.asList(SLExpressionNode[].class));
    }

    @Override
    public SLIsInstanceBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLIsInstanceBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLIsInstanceBuiltin create(SLExpressionNode[] arguments) {
        return new SLIsInstanceBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLIsInstanceBuiltin.class)
    public static final class SLIsInstanceBuiltinNodeGen extends SLIsInstanceBuiltin {

        @Child private SLExpressionNode arguments0_;
        @Child private SLExpressionNode arguments1_;
        @CompilationFinal private volatile int state_0_;
        @CompilationFinal private volatile int exclude_;
        @Child private Default0Data default0_cache;

        private SLIsInstanceBuiltinNodeGen(SLExpressionNode[] arguments) {
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
            this.arguments1_ = arguments != null && 1 < arguments.length ? arguments[1] : null;
        }

        @ExplodeLoop
        @Override
        protected Object execute(VirtualFrame frameValue) {
            int state_0 = this.state_0_;
            Object arguments0Value_ = this.arguments0_.executeGeneric(frameValue);
            Object arguments1Value_ = this.arguments1_.executeGeneric(frameValue);
            if (state_0 != 0 /* is-state_0 doDefault(Object, Object, InteropLibrary) || doDefault(Object, Object, InteropLibrary) */) {
                if ((state_0 & 0b1) != 0 /* is-state_0 doDefault(Object, Object, InteropLibrary) */) {
                    Default0Data s0_ = this.default0_cache;
                    while (s0_ != null) {
                        if ((s0_.metaLib_.accepts(arguments0Value_)) && (s0_.metaLib_.isMetaObject(arguments0Value_))) {
                            return doDefault(arguments0Value_, arguments1Value_, s0_.metaLib_);
                        }
                        s0_ = s0_.next_;
                    }
                }
                if ((state_0 & 0b10) != 0 /* is-state_0 doDefault(Object, Object, InteropLibrary) */) {
                    EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                    Node prev_ = encapsulating_.set(this);
                    try {
                        {
                            InteropLibrary default1_metaLib__ = (INTEROP_LIBRARY_.getUncached());
                            if ((default1_metaLib__.isMetaObject(arguments0Value_))) {
                                return this.default1Boundary(state_0, arguments0Value_, arguments1Value_);
                            }
                        }
                    } finally {
                        encapsulating_.set(prev_);
                    }
                }
            }
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return executeAndSpecialize(arguments0Value_, arguments1Value_);
        }

        @SuppressWarnings("static-method")
        @TruffleBoundary
        private Object default1Boundary(int state_0, Object arguments0Value_, Object arguments1Value_) {
            {
                InteropLibrary default1_metaLib__ = (INTEROP_LIBRARY_.getUncached());
                return doDefault(arguments0Value_, arguments1Value_, default1_metaLib__);
            }
        }

        private Object executeAndSpecialize(Object arguments0Value, Object arguments1Value) {
            Lock lock = getLock();
            boolean hasLock = true;
            lock.lock();
            try {
                int state_0 = this.state_0_;
                int exclude = this.exclude_;
                if ((exclude) == 0 /* is-not-exclude doDefault(Object, Object, InteropLibrary) */) {
                    int count0_ = 0;
                    Default0Data s0_ = this.default0_cache;
                    if ((state_0 & 0b1) != 0 /* is-state_0 doDefault(Object, Object, InteropLibrary) */) {
                        while (s0_ != null) {
                            if ((s0_.metaLib_.accepts(arguments0Value)) && (s0_.metaLib_.isMetaObject(arguments0Value))) {
                                break;
                            }
                            s0_ = s0_.next_;
                            count0_++;
                        }
                    }
                    if (s0_ == null) {
                        {
                            InteropLibrary metaLib__ = super.insert((INTEROP_LIBRARY_.create(arguments0Value)));
                            // assert (s0_.metaLib_.accepts(arguments0Value));
                            if ((metaLib__.isMetaObject(arguments0Value)) && count0_ < (3)) {
                                s0_ = super.insert(new Default0Data(default0_cache));
                                s0_.metaLib_ = s0_.insertAccessor(metaLib__);
                                MemoryFence.storeStore();
                                this.default0_cache = s0_;
                                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 doDefault(Object, Object, InteropLibrary) */;
                            }
                        }
                    }
                    if (s0_ != null) {
                        lock.unlock();
                        hasLock = false;
                        return doDefault(arguments0Value, arguments1Value, s0_.metaLib_);
                    }
                }
                {
                    InteropLibrary default1_metaLib__ = null;
                    {
                        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                        Node prev_ = encapsulating_.set(this);
                        try {
                            {
                                default1_metaLib__ = (INTEROP_LIBRARY_.getUncached());
                                if ((default1_metaLib__.isMetaObject(arguments0Value))) {
                                    this.exclude_ = exclude = exclude | 0b1 /* add-exclude doDefault(Object, Object, InteropLibrary) */;
                                    this.default0_cache = null;
                                    state_0 = state_0 & 0xfffffffe /* remove-state_0 doDefault(Object, Object, InteropLibrary) */;
                                    this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 doDefault(Object, Object, InteropLibrary) */;
                                    lock.unlock();
                                    hasLock = false;
                                    return doDefault(arguments0Value, arguments1Value, default1_metaLib__);
                                }
                            }
                        } finally {
                            encapsulating_.set(prev_);
                        }
                    }
                }
                throw new UnsupportedSpecializationException(this, new Node[] {this.arguments0_, this.arguments1_}, arguments0Value, arguments1Value);
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
                    Default0Data s0_ = this.default0_cache;
                    if ((s0_ == null || s0_.next_ == null)) {
                        return NodeCost.MONOMORPHIC;
                    }
                }
            }
            return NodeCost.POLYMORPHIC;
        }

        @GeneratedBy(SLIsInstanceBuiltin.class)
        private static final class Default0Data extends Node {

            @Child Default0Data next_;
            @Child InteropLibrary metaLib_;

            Default0Data(Default0Data next_) {
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

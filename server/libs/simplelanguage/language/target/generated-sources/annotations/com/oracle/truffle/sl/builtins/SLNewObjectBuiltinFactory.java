// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.memory.MemoryFence;
import com.oracle.truffle.api.nodes.EncapsulatingNodeReference;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLTypes;
import com.oracle.truffle.sl.runtime.SLNull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLNewObjectBuiltin.class)
@SuppressWarnings("unused")
public final class SLNewObjectBuiltinFactory implements NodeFactory<SLNewObjectBuiltin> {

    private static final SLNewObjectBuiltinFactory INSTANCE = new SLNewObjectBuiltinFactory();
    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    private SLNewObjectBuiltinFactory() {
    }

    @Override
    public Class<SLNewObjectBuiltin> getNodeClass() {
        return SLNewObjectBuiltin.class;
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
    public SLNewObjectBuiltin createNode(Object... arguments) {
        if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof SLExpressionNode[])) {
            return create((SLExpressionNode[]) arguments[0]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<SLNewObjectBuiltin> getInstance() {
        return INSTANCE;
    }

    public static SLNewObjectBuiltin create(SLExpressionNode[] arguments) {
        return new SLNewObjectBuiltinNodeGen(arguments);
    }

    @GeneratedBy(SLNewObjectBuiltin.class)
    public static final class SLNewObjectBuiltinNodeGen extends SLNewObjectBuiltin {

        @Child private SLExpressionNode arguments0_;
        @CompilationFinal private volatile int state_0_;
        @CompilationFinal private volatile int exclude_;
        @CompilationFinal private AllocationReporter newObject0_reporter_;
        @Child private NewObject1Data newObject1_cache;

        private SLNewObjectBuiltinNodeGen(SLExpressionNode[] arguments) {
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
        }

        @ExplodeLoop
        @Override
        protected Object execute(VirtualFrame frameValue) {
            int state_0 = this.state_0_;
            Object arguments0Value_ = this.arguments0_.executeGeneric(frameValue);
            if ((state_0 & 0b1) != 0 /* is-state_0 newObject(SLNull, AllocationReporter) */ && SLTypes.isSLNull(arguments0Value_)) {
                SLNull arguments0Value__ = SLTypes.asSLNull(arguments0Value_);
                return newObject(arguments0Value__, this.newObject0_reporter_);
            }
            if ((state_0 & 0b110) != 0 /* is-state_0 newObject(Object, InteropLibrary) || newObject(Object, InteropLibrary) */) {
                if ((state_0 & 0b10) != 0 /* is-state_0 newObject(Object, InteropLibrary) */) {
                    NewObject1Data s1_ = this.newObject1_cache;
                    while (s1_ != null) {
                        if ((s1_.values_.accepts(arguments0Value_)) && (!(s1_.values_.isNull(arguments0Value_)))) {
                            return newObject(arguments0Value_, s1_.values_);
                        }
                        s1_ = s1_.next_;
                    }
                }
                if ((state_0 & 0b100) != 0 /* is-state_0 newObject(Object, InteropLibrary) */) {
                    EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                    Node prev_ = encapsulating_.set(this);
                    try {
                        {
                            InteropLibrary newObject2_values__ = (INTEROP_LIBRARY_.getUncached());
                            if ((!(newObject2_values__.isNull(arguments0Value_)))) {
                                return this.newObject2Boundary(state_0, arguments0Value_);
                            }
                        }
                    } finally {
                        encapsulating_.set(prev_);
                    }
                }
            }
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return executeAndSpecialize(arguments0Value_);
        }

        @SuppressWarnings("static-method")
        @TruffleBoundary
        private Object newObject2Boundary(int state_0, Object arguments0Value_) {
            {
                InteropLibrary newObject2_values__ = (INTEROP_LIBRARY_.getUncached());
                return newObject(arguments0Value_, newObject2_values__);
            }
        }

        private Object executeAndSpecialize(Object arguments0Value) {
            Lock lock = getLock();
            boolean hasLock = true;
            lock.lock();
            try {
                int state_0 = this.state_0_;
                int exclude = this.exclude_;
                if (SLTypes.isSLNull(arguments0Value)) {
                    SLNull arguments0Value_ = SLTypes.asSLNull(arguments0Value);
                    this.newObject0_reporter_ = (lookup());
                    this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 newObject(SLNull, AllocationReporter) */;
                    lock.unlock();
                    hasLock = false;
                    return newObject(arguments0Value_, this.newObject0_reporter_);
                }
                if ((exclude) == 0 /* is-not-exclude newObject(Object, InteropLibrary) */) {
                    int count1_ = 0;
                    NewObject1Data s1_ = this.newObject1_cache;
                    if ((state_0 & 0b10) != 0 /* is-state_0 newObject(Object, InteropLibrary) */) {
                        while (s1_ != null) {
                            if ((s1_.values_.accepts(arguments0Value)) && (!(s1_.values_.isNull(arguments0Value)))) {
                                break;
                            }
                            s1_ = s1_.next_;
                            count1_++;
                        }
                    }
                    if (s1_ == null) {
                        {
                            InteropLibrary values__ = super.insert((INTEROP_LIBRARY_.create(arguments0Value)));
                            // assert (s1_.values_.accepts(arguments0Value));
                            if ((!(values__.isNull(arguments0Value))) && count1_ < (3)) {
                                s1_ = super.insert(new NewObject1Data(newObject1_cache));
                                s1_.values_ = s1_.insertAccessor(values__);
                                MemoryFence.storeStore();
                                this.newObject1_cache = s1_;
                                this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 newObject(Object, InteropLibrary) */;
                            }
                        }
                    }
                    if (s1_ != null) {
                        lock.unlock();
                        hasLock = false;
                        return newObject(arguments0Value, s1_.values_);
                    }
                }
                {
                    InteropLibrary newObject2_values__ = null;
                    {
                        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                        Node prev_ = encapsulating_.set(this);
                        try {
                            {
                                newObject2_values__ = (INTEROP_LIBRARY_.getUncached());
                                if ((!(newObject2_values__.isNull(arguments0Value)))) {
                                    this.exclude_ = exclude = exclude | 0b1 /* add-exclude newObject(Object, InteropLibrary) */;
                                    this.newObject1_cache = null;
                                    state_0 = state_0 & 0xfffffffd /* remove-state_0 newObject(Object, InteropLibrary) */;
                                    this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 newObject(Object, InteropLibrary) */;
                                    lock.unlock();
                                    hasLock = false;
                                    return newObject(arguments0Value, newObject2_values__);
                                }
                            }
                        } finally {
                            encapsulating_.set(prev_);
                        }
                    }
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
                if ((state_0 & (state_0 - 1)) == 0 /* is-single-state_0  */) {
                    NewObject1Data s1_ = this.newObject1_cache;
                    if ((s1_ == null || s1_.next_ == null)) {
                        return NodeCost.MONOMORPHIC;
                    }
                }
            }
            return NodeCost.POLYMORPHIC;
        }

        @GeneratedBy(SLNewObjectBuiltin.class)
        private static final class NewObject1Data extends Node {

            @Child NewObject1Data next_;
            @Child InteropLibrary values_;

            NewObject1Data(NewObject1Data next_) {
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

// CheckStyle: start generated
package com.oracle.truffle.sl.runtime;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.DynamicDispatchLibrary;
import com.oracle.truffle.api.library.LibraryExport;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.memory.MemoryFence;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.TriState;
import com.oracle.truffle.sl.runtime.SLFunction.Execute;
import com.oracle.truffle.sl.runtime.SLFunction.IsIdenticalOrUndefined;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLFunction.class)
@SuppressWarnings("unused")
final class SLFunctionGen {

    private static final LibraryFactory<DynamicDispatchLibrary> DYNAMIC_DISPATCH_LIBRARY_ = LibraryFactory.resolve(DynamicDispatchLibrary.class);

    static  {
        LibraryExport.register(SLFunction.class, new InteropLibraryExports());
    }

    private SLFunctionGen() {
    }

    @GeneratedBy(SLFunction.class)
    private static final class InteropLibraryExports extends LibraryExport<InteropLibrary> {

        private InteropLibraryExports() {
            super(InteropLibrary.class, SLFunction.class, false, false, 0);
        }

        @Override
        protected InteropLibrary createUncached(Object receiver) {
            assert receiver instanceof SLFunction;
            InteropLibrary uncached = new Uncached();
            return uncached;
        }

        @Override
        protected InteropLibrary createCached(Object receiver) {
            assert receiver instanceof SLFunction;
            return new Cached();
        }

        @GeneratedBy(SLFunction.class)
        private static final class Cached extends InteropLibrary {

            @CompilationFinal private volatile int state_0_;
            @CompilationFinal private volatile int exclude_;
            @Child private ExecuteDirectData execute_direct_cache;
            @Child private IndirectCallNode execute_indirect_callNode_;

            protected Cached() {
            }

            @Override
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLFunction) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof SLFunction;
            }

            @Override
            protected TriState isIdenticalOrUndefined(Object arg0Value_, Object arg1Value) {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                SLFunction arg0Value = ((SLFunction) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b11) != 0 /* is-state_0 doSLFunction(SLFunction, SLFunction) || doOther(SLFunction, Object) */) {
                    if ((state_0 & 0b1) != 0 /* is-state_0 doSLFunction(SLFunction, SLFunction) */ && arg1Value instanceof SLFunction) {
                        SLFunction arg1Value_ = (SLFunction) arg1Value;
                        return IsIdenticalOrUndefined.doSLFunction(arg0Value, arg1Value_);
                    }
                    if ((state_0 & 0b10) != 0 /* is-state_0 doOther(SLFunction, Object) */) {
                        if (isIdenticalOrUndefinedFallbackGuard_(state_0, arg0Value, arg1Value)) {
                            return IsIdenticalOrUndefined.doOther(arg0Value, arg1Value);
                        }
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return isIdenticalOrUndefinedAndSpecialize(arg0Value, arg1Value);
            }

            private TriState isIdenticalOrUndefinedAndSpecialize(SLFunction arg0Value, Object arg1Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    if (arg1Value instanceof SLFunction) {
                        SLFunction arg1Value_ = (SLFunction) arg1Value;
                        this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 doSLFunction(SLFunction, SLFunction) */;
                        lock.unlock();
                        hasLock = false;
                        return IsIdenticalOrUndefined.doSLFunction(arg0Value, arg1Value_);
                    }
                    this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 doOther(SLFunction, Object) */;
                    lock.unlock();
                    hasLock = false;
                    return IsIdenticalOrUndefined.doOther(arg0Value, arg1Value);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @Override
            public NodeCost getCost() {
                int state_0 = this.state_0_;
                if ((state_0 & 0b11) == 0) {
                    return NodeCost.UNINITIALIZED;
                } else {
                    if (((state_0 & 0b11) & ((state_0 & 0b11) - 1)) == 0 /* is-single-state_0  */) {
                        return NodeCost.MONOMORPHIC;
                    }
                }
                return NodeCost.POLYMORPHIC;
            }

            @ExplodeLoop
            @Override
            public Object execute(Object arg0Value_, Object... arg1Value) throws UnsupportedTypeException, ArityException, UnsupportedMessageException {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                SLFunction arg0Value = ((SLFunction) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b1100) != 0 /* is-state_0 doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) || doIndirect(SLFunction, Object[], IndirectCallNode) */) {
                    if ((state_0 & 0b100) != 0 /* is-state_0 doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) */) {
                        ExecuteDirectData s0_ = this.execute_direct_cache;
                        while (s0_ != null) {
                            if (!Assumption.isValidAssumption(s0_.assumption0_)) {
                                CompilerDirectives.transferToInterpreterAndInvalidate();
                                removeDirect_(s0_);
                                return executeAndSpecialize(arg0Value, arg1Value);
                            }
                            if ((arg0Value.getCallTarget() == s0_.cachedTarget_)) {
                                return Execute.doDirect(arg0Value, arg1Value, s0_.callTargetStable_, s0_.cachedTarget_, s0_.callNode_);
                            }
                            s0_ = s0_.next_;
                        }
                    }
                    if ((state_0 & 0b1000) != 0 /* is-state_0 doIndirect(SLFunction, Object[], IndirectCallNode) */) {
                        return Execute.doIndirect(arg0Value, arg1Value, this.execute_indirect_callNode_);
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return executeAndSpecialize(arg0Value, arg1Value);
            }

            private Object executeAndSpecialize(SLFunction arg0Value, Object[] arg1Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    int exclude = this.exclude_;
                    int oldState_0 = (state_0 & 0b1100);
                    int oldExclude = exclude;
                    int oldCacheCount = execute_countCaches();
                    try {
                        if ((exclude) == 0 /* is-not-exclude doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) */) {
                            int count0_ = 0;
                            ExecuteDirectData s0_ = this.execute_direct_cache;
                            if ((state_0 & 0b100) != 0 /* is-state_0 doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) */) {
                                while (s0_ != null) {
                                    if ((arg0Value.getCallTarget() == s0_.cachedTarget_) && (s0_.assumption0_ == null || Assumption.isValidAssumption(s0_.assumption0_))) {
                                        break;
                                    }
                                    s0_ = s0_.next_;
                                    count0_++;
                                }
                            }
                            if (s0_ == null) {
                                {
                                    RootCallTarget cachedTarget__ = (arg0Value.getCallTarget());
                                    if ((arg0Value.getCallTarget() == cachedTarget__)) {
                                        Assumption callTargetStable__ = (arg0Value.getCallTargetStable());
                                        Assumption assumption0 = (callTargetStable__);
                                        if (Assumption.isValidAssumption(assumption0)) {
                                            if (count0_ < (SLFunction.INLINE_CACHE_SIZE)) {
                                                s0_ = super.insert(new ExecuteDirectData(execute_direct_cache));
                                                s0_.callTargetStable_ = callTargetStable__;
                                                s0_.cachedTarget_ = cachedTarget__;
                                                s0_.callNode_ = s0_.insertAccessor((DirectCallNode.create(cachedTarget__)));
                                                s0_.assumption0_ = assumption0;
                                                MemoryFence.storeStore();
                                                this.execute_direct_cache = s0_;
                                                this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) */;
                                            }
                                        }
                                    }
                                }
                            }
                            if (s0_ != null) {
                                lock.unlock();
                                hasLock = false;
                                return Execute.doDirect(arg0Value, arg1Value, s0_.callTargetStable_, s0_.cachedTarget_, s0_.callNode_);
                            }
                        }
                        this.execute_indirect_callNode_ = super.insert((IndirectCallNode.create()));
                        this.exclude_ = exclude = exclude | 0b1 /* add-exclude doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) */;
                        this.execute_direct_cache = null;
                        state_0 = state_0 & 0xfffffffb /* remove-state_0 doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) */;
                        this.state_0_ = state_0 = state_0 | 0b1000 /* add-state_0 doIndirect(SLFunction, Object[], IndirectCallNode) */;
                        lock.unlock();
                        hasLock = false;
                        return Execute.doIndirect(arg0Value, arg1Value, this.execute_indirect_callNode_);
                    } finally {
                        if (oldState_0 != 0 || oldExclude != 0) {
                            execute_checkForPolymorphicSpecialize(oldState_0, oldExclude, oldCacheCount);
                        }
                    }
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            private void execute_checkForPolymorphicSpecialize(int oldState_0, int oldExclude, int oldCacheCount) {
                int newState_0 = (this.state_0_ & 0b1100);
                int newExclude = this.exclude_;
                if (((oldState_0 ^ newState_0) != 0) || (oldExclude ^ newExclude) != 0 || oldCacheCount < execute_countCaches()) {
                    this.reportPolymorphicSpecialize();
                }
            }

            private int execute_countCaches() {
                int cacheCount = 0;
                ExecuteDirectData s0_ = this.execute_direct_cache;
                while (s0_ != null) {
                    cacheCount++;
                    s0_= s0_.next_;
                }
                return cacheCount;
            }

            void removeDirect_(Object s0_) {
                Lock lock = getLock();
                lock.lock();
                try {
                    ExecuteDirectData prev = null;
                    ExecuteDirectData cur = this.execute_direct_cache;
                    while (cur != null) {
                        if (cur == s0_) {
                            if (prev == null) {
                                this.execute_direct_cache = cur.next_;
                                this.adoptChildren();
                            } else {
                                prev.next_ = cur.next_;
                                prev.adoptChildren();
                            }
                            break;
                        }
                        prev = cur;
                        cur = cur.next_;
                    }
                    if (this.execute_direct_cache == null) {
                        this.state_0_ = this.state_0_ & 0xfffffffb /* remove-state_0 doDirect(SLFunction, Object[], Assumption, RootCallTarget, DirectCallNode) */;
                    }
                } finally {
                    lock.unlock();
                }
            }

            @Override
            public boolean hasLanguage(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).hasLanguage();
            }

            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).getLanguage();
            }

            @Override
            public SourceSection getSourceLocation(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).getSourceLocation();
            }

            @Override
            public boolean hasSourceLocation(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).hasSourceLocation();
            }

            @Override
            public boolean isExecutable(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).isExecutable();
            }

            @Override
            public boolean hasMetaObject(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).hasMetaObject();
            }

            @Override
            public Object getMetaObject(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).getMetaObject();
            }

            @Override
            public int identityHashCode(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return SLFunction.identityHashCode((((SLFunction) receiver)));
            }

            @Override
            public Object toDisplayString(Object receiver, boolean allowSideEffects) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLFunction) receiver)).toDisplayString(allowSideEffects);
            }

            private static boolean isIdenticalOrUndefinedFallbackGuard_(int state_0, SLFunction arg0Value, Object arg1Value) {
                if (((state_0 & 0b1)) == 0 /* is-not-state_0 doSLFunction(SLFunction, SLFunction) */ && arg1Value instanceof SLFunction) {
                    return false;
                }
                return true;
            }

            @GeneratedBy(SLFunction.class)
            private static final class ExecuteDirectData extends Node {

                @Child ExecuteDirectData next_;
                @CompilationFinal Assumption callTargetStable_;
                @CompilationFinal RootCallTarget cachedTarget_;
                @Child DirectCallNode callNode_;
                @CompilationFinal Assumption assumption0_;

                ExecuteDirectData(ExecuteDirectData next_) {
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
        @GeneratedBy(SLFunction.class)
        private static final class Uncached extends InteropLibrary {

            protected Uncached() {
            }

            @Override
            @TruffleBoundary
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLFunction) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof SLFunction;
            }

            @Override
            public boolean isAdoptable() {
                return false;
            }

            @Override
            public NodeCost getCost() {
                return NodeCost.MEGAMORPHIC;
            }

            @TruffleBoundary
            @Override
            public TriState isIdenticalOrUndefined(Object arg0Value_, Object arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                SLFunction arg0Value = ((SLFunction) arg0Value_);
                if (arg1Value instanceof SLFunction) {
                    SLFunction arg1Value_ = (SLFunction) arg1Value;
                    return IsIdenticalOrUndefined.doSLFunction(arg0Value, arg1Value_);
                }
                return IsIdenticalOrUndefined.doOther(arg0Value, arg1Value);
            }

            @TruffleBoundary
            @Override
            public Object execute(Object arg0Value_, Object... arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                SLFunction arg0Value = ((SLFunction) arg0Value_);
                return Execute.doIndirect(arg0Value, arg1Value, (IndirectCallNode.getUncached()));
            }

            @TruffleBoundary
            @Override
            public boolean hasLanguage(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .hasLanguage();
            }

            @TruffleBoundary
            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .getLanguage();
            }

            @TruffleBoundary
            @Override
            public SourceSection getSourceLocation(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .getSourceLocation();
            }

            @TruffleBoundary
            @Override
            public boolean hasSourceLocation(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .hasSourceLocation();
            }

            @TruffleBoundary
            @Override
            public boolean isExecutable(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .isExecutable();
            }

            @TruffleBoundary
            @Override
            public boolean hasMetaObject(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .hasMetaObject();
            }

            @TruffleBoundary
            @Override
            public Object getMetaObject(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .getMetaObject();
            }

            @TruffleBoundary
            @Override
            public int identityHashCode(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return SLFunction.identityHashCode(((SLFunction) receiver) );
            }

            @TruffleBoundary
            @Override
            public Object toDisplayString(Object receiver, boolean allowSideEffects) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLFunction) receiver) .toDisplayString(allowSideEffects);
            }

        }
    }
}

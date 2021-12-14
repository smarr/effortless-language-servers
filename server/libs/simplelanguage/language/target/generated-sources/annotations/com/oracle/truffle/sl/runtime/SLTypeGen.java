// CheckStyle: start generated
package com.oracle.truffle.sl.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.DynamicDispatchLibrary;
import com.oracle.truffle.api.library.LibraryExport;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.memory.MemoryFence;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.runtime.SLType.IsMetaInstance;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLType.class)
final class SLTypeGen {

    private static final LibraryFactory<DynamicDispatchLibrary> DYNAMIC_DISPATCH_LIBRARY_ = LibraryFactory.resolve(DynamicDispatchLibrary.class);
    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    static  {
        LibraryExport.register(SLType.class, new InteropLibraryExports());
    }

    private SLTypeGen() {
    }

    @GeneratedBy(SLType.class)
    private static final class InteropLibraryExports extends LibraryExport<InteropLibrary> {

        private InteropLibraryExports() {
            super(InteropLibrary.class, SLType.class, false, false, 0);
        }

        @Override
        protected InteropLibrary createUncached(Object receiver) {
            assert receiver instanceof SLType;
            InteropLibrary uncached = new Uncached();
            return uncached;
        }

        @Override
        protected InteropLibrary createCached(Object receiver) {
            assert receiver instanceof SLType;
            return new Cached();
        }

        @GeneratedBy(SLType.class)
        private static final class Cached extends InteropLibrary {

            @CompilationFinal private volatile int state_0_;
            @CompilationFinal private volatile int exclude_;
            @Child private CachedData cached_cache;

            protected Cached() {
            }

            @Override
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLType) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof SLType;
            }

            @ExplodeLoop
            @Override
            public boolean isMetaInstance(Object arg0Value_, Object arg1Value) throws UnsupportedMessageException {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                SLType arg0Value = ((SLType) arg0Value_);
                int state_0 = this.state_0_;
                if (state_0 != 0 /* is-state_0 doCached(SLType, Object, SLType, InteropLibrary) || doGeneric(SLType, Object) */) {
                    if ((state_0 & 0b1) != 0 /* is-state_0 doCached(SLType, Object, SLType, InteropLibrary) */) {
                        CachedData s0_ = this.cached_cache;
                        while (s0_ != null) {
                            if ((s0_.valueLib_.accepts(arg1Value)) && (arg0Value == s0_.cachedType_)) {
                                return IsMetaInstance.doCached(arg0Value, arg1Value, s0_.cachedType_, s0_.valueLib_);
                            }
                            s0_ = s0_.next_;
                        }
                    }
                    if ((state_0 & 0b10) != 0 /* is-state_0 doGeneric(SLType, Object) */) {
                        return IsMetaInstance.doGeneric(arg0Value, arg1Value);
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return executeAndSpecialize(arg0Value, arg1Value);
            }

            private boolean executeAndSpecialize(SLType arg0Value, Object arg1Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    int exclude = this.exclude_;
                    if ((exclude) == 0 /* is-not-exclude doCached(SLType, Object, SLType, InteropLibrary) */) {
                        int count0_ = 0;
                        CachedData s0_ = this.cached_cache;
                        if ((state_0 & 0b1) != 0 /* is-state_0 doCached(SLType, Object, SLType, InteropLibrary) */) {
                            while (s0_ != null) {
                                if ((s0_.valueLib_.accepts(arg1Value)) && (arg0Value == s0_.cachedType_)) {
                                    break;
                                }
                                s0_ = s0_.next_;
                                count0_++;
                            }
                        }
                        if (s0_ == null) {
                            // assert (s0_.valueLib_.accepts(arg1Value));
                            // assert (arg0Value == s0_.cachedType_);
                            if (count0_ < (3)) {
                                s0_ = super.insert(new CachedData(cached_cache));
                                s0_.cachedType_ = (arg0Value);
                                s0_.valueLib_ = s0_.insertAccessor((INTEROP_LIBRARY_.create(arg1Value)));
                                MemoryFence.storeStore();
                                this.cached_cache = s0_;
                                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 doCached(SLType, Object, SLType, InteropLibrary) */;
                            }
                        }
                        if (s0_ != null) {
                            lock.unlock();
                            hasLock = false;
                            return IsMetaInstance.doCached(arg0Value, arg1Value, s0_.cachedType_, s0_.valueLib_);
                        }
                    }
                    this.exclude_ = exclude = exclude | 0b1 /* add-exclude doCached(SLType, Object, SLType, InteropLibrary) */;
                    this.cached_cache = null;
                    state_0 = state_0 & 0xfffffffe /* remove-state_0 doCached(SLType, Object, SLType, InteropLibrary) */;
                    this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 doGeneric(SLType, Object) */;
                    lock.unlock();
                    hasLock = false;
                    return IsMetaInstance.doGeneric(arg0Value, arg1Value);
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
                        CachedData s0_ = this.cached_cache;
                        if ((s0_ == null || s0_.next_ == null)) {
                            return NodeCost.MONOMORPHIC;
                        }
                    }
                }
                return NodeCost.POLYMORPHIC;
            }

            @Override
            public boolean hasLanguage(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLType) receiver)).hasLanguage();
            }

            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLType) receiver)).getLanguage();
            }

            @Override
            public boolean isMetaObject(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLType) receiver)).isMetaObject();
            }

            @Override
            public Object getMetaQualifiedName(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLType) receiver)).getName();
            }

            @Override
            public Object getMetaSimpleName(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLType) receiver)).getName();
            }

            @Override
            public Object toDisplayString(Object receiver, boolean allowSideEffects) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((SLType) receiver)).toDisplayString(allowSideEffects);
            }

            @GeneratedBy(SLType.class)
            private static final class CachedData extends Node {

                @Child CachedData next_;
                @CompilationFinal SLType cachedType_;
                @Child InteropLibrary valueLib_;

                CachedData(CachedData next_) {
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
        @GeneratedBy(SLType.class)
        private static final class Uncached extends InteropLibrary {

            protected Uncached() {
            }

            @Override
            @TruffleBoundary
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLType) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof SLType;
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
            public boolean isMetaInstance(Object arg0Value_, Object arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                SLType arg0Value = ((SLType) arg0Value_);
                return IsMetaInstance.doGeneric(arg0Value, arg1Value);
            }

            @TruffleBoundary
            @Override
            public boolean hasLanguage(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLType) receiver) .hasLanguage();
            }

            @TruffleBoundary
            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLType) receiver) .getLanguage();
            }

            @TruffleBoundary
            @Override
            public boolean isMetaObject(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLType) receiver) .isMetaObject();
            }

            @TruffleBoundary
            @Override
            public Object getMetaQualifiedName(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLType) receiver) .getName();
            }

            @TruffleBoundary
            @Override
            public Object getMetaSimpleName(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLType) receiver) .getName();
            }

            @TruffleBoundary
            @Override
            public Object toDisplayString(Object receiver, boolean allowSideEffects) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLType) receiver) .toDisplayString(allowSideEffects);
            }

        }
    }
}

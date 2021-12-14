// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.util;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.memory.MemoryFence;
import com.oracle.truffle.api.nodes.EncapsulatingNodeReference;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.nodes.SLTypesGen;
import com.oracle.truffle.sl.runtime.SLBigNumber;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLToMemberNode.class)
@SuppressWarnings("unused")
public final class SLToMemberNodeGen extends SLToMemberNode {

    private static final Uncached UNCACHED = new Uncached();
    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    @CompilationFinal private volatile int state_0_;
    @CompilationFinal private volatile int exclude_;
    @Child private FromInterop0Data fromInterop0_cache;

    private SLToMemberNodeGen() {
    }

    @ExplodeLoop
    @Override
    public String execute(Object arg0Value) throws UnknownIdentifierException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b1) != 0 /* is-state_0 fromString(String) */ && arg0Value instanceof String) {
            String arg0Value_ = (String) arg0Value;
            return SLToMemberNode.fromString(arg0Value_);
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 fromBoolean(boolean) */ && arg0Value instanceof Boolean) {
            boolean arg0Value_ = (boolean) arg0Value;
            return SLToMemberNode.fromBoolean(arg0Value_);
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 fromLong(long) */ && arg0Value instanceof Long) {
            long arg0Value_ = (long) arg0Value;
            return SLToMemberNode.fromLong(arg0Value_);
        }
        if ((state_0 & 0b1000) != 0 /* is-state_0 fromBigNumber(SLBigNumber) */ && SLTypesGen.isImplicitSLBigNumber((state_0 & 0b11000000) >>> 6 /* extract-implicit-state_0 0:SLBigNumber */, arg0Value)) {
            SLBigNumber arg0Value_ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b11000000) >>> 6 /* extract-implicit-state_0 0:SLBigNumber */, arg0Value);
            return SLToMemberNode.fromBigNumber(arg0Value_);
        }
        if ((state_0 & 0b110000) != 0 /* is-state_0 fromInterop(Object, InteropLibrary) || fromInterop(Object, InteropLibrary) */) {
            if ((state_0 & 0b10000) != 0 /* is-state_0 fromInterop(Object, InteropLibrary) */) {
                FromInterop0Data s4_ = this.fromInterop0_cache;
                while (s4_ != null) {
                    if ((s4_.interop_.accepts(arg0Value))) {
                        return SLToMemberNode.fromInterop(arg0Value, s4_.interop_);
                    }
                    s4_ = s4_.next_;
                }
            }
            if ((state_0 & 0b100000) != 0 /* is-state_0 fromInterop(Object, InteropLibrary) */) {
                return this.fromInterop1Boundary(state_0, arg0Value);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(arg0Value);
    }

    @SuppressWarnings("static-method")
    @TruffleBoundary
    private String fromInterop1Boundary(int state_0, Object arg0Value) throws UnknownIdentifierException {
        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
        Node prev_ = encapsulating_.set(this);
        try {
            {
                InteropLibrary fromInterop1_interop__ = (INTEROP_LIBRARY_.getUncached(arg0Value));
                return SLToMemberNode.fromInterop(arg0Value, fromInterop1_interop__);
            }
        } finally {
            encapsulating_.set(prev_);
        }
    }

    private String executeAndSpecialize(Object arg0Value) throws UnknownIdentifierException {
        Lock lock = getLock();
        boolean hasLock = true;
        lock.lock();
        try {
            int state_0 = this.state_0_;
            int exclude = this.exclude_;
            if (arg0Value instanceof String) {
                String arg0Value_ = (String) arg0Value;
                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 fromString(String) */;
                lock.unlock();
                hasLock = false;
                return SLToMemberNode.fromString(arg0Value_);
            }
            if (arg0Value instanceof Boolean) {
                boolean arg0Value_ = (boolean) arg0Value;
                this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 fromBoolean(boolean) */;
                lock.unlock();
                hasLock = false;
                return SLToMemberNode.fromBoolean(arg0Value_);
            }
            if (arg0Value instanceof Long) {
                long arg0Value_ = (long) arg0Value;
                this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 fromLong(long) */;
                lock.unlock();
                hasLock = false;
                return SLToMemberNode.fromLong(arg0Value_);
            }
            {
                int sLBigNumberCast0;
                if ((sLBigNumberCast0 = SLTypesGen.specializeImplicitSLBigNumber(arg0Value)) != 0) {
                    SLBigNumber arg0Value_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast0, arg0Value);
                    state_0 = (state_0 | (sLBigNumberCast0 << 6) /* set-implicit-state_0 0:SLBigNumber */);
                    this.state_0_ = state_0 = state_0 | 0b1000 /* add-state_0 fromBigNumber(SLBigNumber) */;
                    lock.unlock();
                    hasLock = false;
                    return SLToMemberNode.fromBigNumber(arg0Value_);
                }
            }
            if ((exclude) == 0 /* is-not-exclude fromInterop(Object, InteropLibrary) */) {
                int count4_ = 0;
                FromInterop0Data s4_ = this.fromInterop0_cache;
                if ((state_0 & 0b10000) != 0 /* is-state_0 fromInterop(Object, InteropLibrary) */) {
                    while (s4_ != null) {
                        if ((s4_.interop_.accepts(arg0Value))) {
                            break;
                        }
                        s4_ = s4_.next_;
                        count4_++;
                    }
                }
                if (s4_ == null) {
                    // assert (s4_.interop_.accepts(arg0Value));
                    if (count4_ < (SLToMemberNode.LIMIT)) {
                        s4_ = super.insert(new FromInterop0Data(fromInterop0_cache));
                        s4_.interop_ = s4_.insertAccessor((INTEROP_LIBRARY_.create(arg0Value)));
                        MemoryFence.storeStore();
                        this.fromInterop0_cache = s4_;
                        this.state_0_ = state_0 = state_0 | 0b10000 /* add-state_0 fromInterop(Object, InteropLibrary) */;
                    }
                }
                if (s4_ != null) {
                    lock.unlock();
                    hasLock = false;
                    return SLToMemberNode.fromInterop(arg0Value, s4_.interop_);
                }
            }
            {
                InteropLibrary fromInterop1_interop__ = null;
                {
                    EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                    Node prev_ = encapsulating_.set(this);
                    try {
                        fromInterop1_interop__ = (INTEROP_LIBRARY_.getUncached(arg0Value));
                        this.exclude_ = exclude = exclude | 0b1 /* add-exclude fromInterop(Object, InteropLibrary) */;
                        this.fromInterop0_cache = null;
                        state_0 = state_0 & 0xffffffef /* remove-state_0 fromInterop(Object, InteropLibrary) */;
                        this.state_0_ = state_0 = state_0 | 0b100000 /* add-state_0 fromInterop(Object, InteropLibrary) */;
                        lock.unlock();
                        hasLock = false;
                        return SLToMemberNode.fromInterop(arg0Value, fromInterop1_interop__);
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
        if ((state_0 & 0b111111) == 0) {
            return NodeCost.UNINITIALIZED;
        } else {
            if (((state_0 & 0b111111) & ((state_0 & 0b111111) - 1)) == 0 /* is-single-state_0  */) {
                FromInterop0Data s4_ = this.fromInterop0_cache;
                if ((s4_ == null || s4_.next_ == null)) {
                    return NodeCost.MONOMORPHIC;
                }
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    public static SLToMemberNode create() {
        return new SLToMemberNodeGen();
    }

    public static SLToMemberNode getUncached() {
        return SLToMemberNodeGen.UNCACHED;
    }

    @GeneratedBy(SLToMemberNode.class)
    private static final class FromInterop0Data extends Node {

        @Child FromInterop0Data next_;
        @Child InteropLibrary interop_;

        FromInterop0Data(FromInterop0Data next_) {
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
    @GeneratedBy(SLToMemberNode.class)
    private static final class Uncached extends SLToMemberNode {

        @TruffleBoundary
        @Override
        public String execute(Object arg0Value) throws UnknownIdentifierException {
            if (arg0Value instanceof String) {
                String arg0Value_ = (String) arg0Value;
                return SLToMemberNode.fromString(arg0Value_);
            }
            if (arg0Value instanceof Boolean) {
                boolean arg0Value_ = (boolean) arg0Value;
                return SLToMemberNode.fromBoolean(arg0Value_);
            }
            if (arg0Value instanceof Long) {
                long arg0Value_ = (long) arg0Value;
                return SLToMemberNode.fromLong(arg0Value_);
            }
            if (SLTypesGen.isImplicitSLBigNumber(arg0Value)) {
                SLBigNumber arg0Value_ = SLTypesGen.asImplicitSLBigNumber(arg0Value);
                return SLToMemberNode.fromBigNumber(arg0Value_);
            }
            return SLToMemberNode.fromInterop(arg0Value, (INTEROP_LIBRARY_.getUncached(arg0Value)));
        }

        @Override
        public NodeCost getCost() {
            return NodeCost.MEGAMORPHIC;
        }

        @Override
        public boolean isAdoptable() {
            return false;
        }

    }
}

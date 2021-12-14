// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.util;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.memory.MemoryFence;
import com.oracle.truffle.api.nodes.EncapsulatingNodeReference;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLTypes;
import com.oracle.truffle.sl.nodes.SLTypesGen;
import com.oracle.truffle.sl.runtime.SLBigNumber;
import com.oracle.truffle.sl.runtime.SLFunction;
import com.oracle.truffle.sl.runtime.SLNull;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLUnboxNode.class)
@SuppressWarnings("unused")
public final class SLUnboxNodeGen extends SLUnboxNode {

    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    @Child private SLExpressionNode child0_;
    @CompilationFinal private volatile int state_0_;
    @CompilationFinal private volatile int exclude_;
    @Child private FromForeign0Data fromForeign0_cache;

    private SLUnboxNodeGen(SLExpressionNode child0) {
        this.child0_ = child0;
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b11111101) == 0 /* only-active fromBoolean(boolean) */ && ((state_0 & 0b11111111) != 0  /* is-not fromString(String) && fromBoolean(boolean) && fromLong(long) && fromBigNumber(SLBigNumber) && fromFunction(SLFunction) && fromFunction(SLNull) && fromForeign(Object, InteropLibrary) && fromForeign(Object, InteropLibrary) */)) {
            return executeGeneric_boolean0(state_0, frameValue);
        } else if ((state_0 & 0b11111011) == 0 /* only-active fromLong(long) */ && ((state_0 & 0b11111111) != 0  /* is-not fromString(String) && fromBoolean(boolean) && fromLong(long) && fromBigNumber(SLBigNumber) && fromFunction(SLFunction) && fromFunction(SLNull) && fromForeign(Object, InteropLibrary) && fromForeign(Object, InteropLibrary) */)) {
            return executeGeneric_long1(state_0, frameValue);
        } else {
            return executeGeneric_generic2(state_0, frameValue);
        }
    }

    private Object executeGeneric_boolean0(int state_0, VirtualFrame frameValue) {
        boolean child0Value_;
        try {
            child0Value_ = this.child0_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(ex.getResult());
        }
        assert (state_0 & 0b10) != 0 /* is-state_0 fromBoolean(boolean) */;
        return SLUnboxNode.fromBoolean(child0Value_);
    }

    private Object executeGeneric_long1(int state_0, VirtualFrame frameValue) {
        long child0Value_;
        try {
            child0Value_ = this.child0_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(ex.getResult());
        }
        assert (state_0 & 0b100) != 0 /* is-state_0 fromLong(long) */;
        return SLUnboxNode.fromLong(child0Value_);
    }

    @SuppressWarnings("static-method")
    @TruffleBoundary
    private Object fromForeign1Boundary(int state_0, Object child0Value_) {
        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
        Node prev_ = encapsulating_.set(this);
        try {
            {
                InteropLibrary fromForeign1_interop__ = (INTEROP_LIBRARY_.getUncached(child0Value_));
                return SLUnboxNode.fromForeign(child0Value_, fromForeign1_interop__);
            }
        } finally {
            encapsulating_.set(prev_);
        }
    }

    @ExplodeLoop
    private Object executeGeneric_generic2(int state_0, VirtualFrame frameValue) {
        Object child0Value_ = this.child0_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 fromString(String) */ && child0Value_ instanceof String) {
            String child0Value__ = (String) child0Value_;
            return SLUnboxNode.fromString(child0Value__);
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 fromBoolean(boolean) */ && child0Value_ instanceof Boolean) {
            boolean child0Value__ = (boolean) child0Value_;
            return SLUnboxNode.fromBoolean(child0Value__);
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 fromLong(long) */ && child0Value_ instanceof Long) {
            long child0Value__ = (long) child0Value_;
            return SLUnboxNode.fromLong(child0Value__);
        }
        if ((state_0 & 0b1000) != 0 /* is-state_0 fromBigNumber(SLBigNumber) */ && SLTypesGen.isImplicitSLBigNumber((state_0 & 0b1100000000) >>> 8 /* extract-implicit-state_0 0:SLBigNumber */, child0Value_)) {
            SLBigNumber child0Value__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b1100000000) >>> 8 /* extract-implicit-state_0 0:SLBigNumber */, child0Value_);
            return SLUnboxNode.fromBigNumber(child0Value__);
        }
        if ((state_0 & 0b10000) != 0 /* is-state_0 fromFunction(SLFunction) */ && child0Value_ instanceof SLFunction) {
            SLFunction child0Value__ = (SLFunction) child0Value_;
            return SLUnboxNode.fromFunction(child0Value__);
        }
        if ((state_0 & 0b100000) != 0 /* is-state_0 fromFunction(SLNull) */ && SLTypes.isSLNull(child0Value_)) {
            SLNull child0Value__ = SLTypes.asSLNull(child0Value_);
            return SLUnboxNode.fromFunction(child0Value__);
        }
        if ((state_0 & 0b11000000) != 0 /* is-state_0 fromForeign(Object, InteropLibrary) || fromForeign(Object, InteropLibrary) */) {
            if ((state_0 & 0b1000000) != 0 /* is-state_0 fromForeign(Object, InteropLibrary) */) {
                FromForeign0Data s6_ = this.fromForeign0_cache;
                while (s6_ != null) {
                    if ((s6_.interop_.accepts(child0Value_))) {
                        return SLUnboxNode.fromForeign(child0Value_, s6_.interop_);
                    }
                    s6_ = s6_.next_;
                }
            }
            if ((state_0 & 0b10000000) != 0 /* is-state_0 fromForeign(Object, InteropLibrary) */) {
                return this.fromForeign1Boundary(state_0, child0Value_);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(child0Value_);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b11000000) != 0 /* is-state_0 fromForeign(Object, InteropLibrary) || fromForeign(Object, InteropLibrary) */) {
            return SLTypesGen.expectBoolean(executeGeneric(frameValue));
        }
        boolean child0Value_;
        try {
            child0Value_ = this.child0_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return SLTypesGen.expectBoolean(executeAndSpecialize(ex.getResult()));
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 fromBoolean(boolean) */) {
            return SLUnboxNode.fromBoolean(child0Value_);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectBoolean(executeAndSpecialize(child0Value_));
    }

    @Override
    public long executeLong(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b11000000) != 0 /* is-state_0 fromForeign(Object, InteropLibrary) || fromForeign(Object, InteropLibrary) */) {
            return SLTypesGen.expectLong(executeGeneric(frameValue));
        }
        long child0Value_;
        try {
            child0Value_ = this.child0_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return SLTypesGen.expectLong(executeAndSpecialize(ex.getResult()));
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 fromLong(long) */) {
            return SLUnboxNode.fromLong(child0Value_);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectLong(executeAndSpecialize(child0Value_));
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        try {
            if ((state_0 & 0b11111011) == 0 /* only-active fromLong(long) */ && ((state_0 & 0b11111111) != 0  /* is-not fromString(String) && fromBoolean(boolean) && fromLong(long) && fromBigNumber(SLBigNumber) && fromFunction(SLFunction) && fromFunction(SLNull) && fromForeign(Object, InteropLibrary) && fromForeign(Object, InteropLibrary) */)) {
                executeLong(frameValue);
                return;
            } else if ((state_0 & 0b11111101) == 0 /* only-active fromBoolean(boolean) */ && ((state_0 & 0b11111111) != 0  /* is-not fromString(String) && fromBoolean(boolean) && fromLong(long) && fromBigNumber(SLBigNumber) && fromFunction(SLFunction) && fromFunction(SLNull) && fromForeign(Object, InteropLibrary) && fromForeign(Object, InteropLibrary) */)) {
                executeBoolean(frameValue);
                return;
            }
            executeGeneric(frameValue);
            return;
        } catch (UnexpectedResultException ex) {
            return;
        }
    }

    private Object executeAndSpecialize(Object child0Value) {
        Lock lock = getLock();
        boolean hasLock = true;
        lock.lock();
        try {
            int state_0 = this.state_0_;
            int exclude = this.exclude_;
            if (child0Value instanceof String) {
                String child0Value_ = (String) child0Value;
                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 fromString(String) */;
                lock.unlock();
                hasLock = false;
                return SLUnboxNode.fromString(child0Value_);
            }
            if (child0Value instanceof Boolean) {
                boolean child0Value_ = (boolean) child0Value;
                this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 fromBoolean(boolean) */;
                lock.unlock();
                hasLock = false;
                return SLUnboxNode.fromBoolean(child0Value_);
            }
            if (child0Value instanceof Long) {
                long child0Value_ = (long) child0Value;
                this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 fromLong(long) */;
                lock.unlock();
                hasLock = false;
                return SLUnboxNode.fromLong(child0Value_);
            }
            {
                int sLBigNumberCast0;
                if ((sLBigNumberCast0 = SLTypesGen.specializeImplicitSLBigNumber(child0Value)) != 0) {
                    SLBigNumber child0Value_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast0, child0Value);
                    state_0 = (state_0 | (sLBigNumberCast0 << 8) /* set-implicit-state_0 0:SLBigNumber */);
                    this.state_0_ = state_0 = state_0 | 0b1000 /* add-state_0 fromBigNumber(SLBigNumber) */;
                    lock.unlock();
                    hasLock = false;
                    return SLUnboxNode.fromBigNumber(child0Value_);
                }
            }
            if (child0Value instanceof SLFunction) {
                SLFunction child0Value_ = (SLFunction) child0Value;
                this.state_0_ = state_0 = state_0 | 0b10000 /* add-state_0 fromFunction(SLFunction) */;
                lock.unlock();
                hasLock = false;
                return SLUnboxNode.fromFunction(child0Value_);
            }
            if (SLTypes.isSLNull(child0Value)) {
                SLNull child0Value_ = SLTypes.asSLNull(child0Value);
                this.state_0_ = state_0 = state_0 | 0b100000 /* add-state_0 fromFunction(SLNull) */;
                lock.unlock();
                hasLock = false;
                return SLUnboxNode.fromFunction(child0Value_);
            }
            if ((exclude) == 0 /* is-not-exclude fromForeign(Object, InteropLibrary) */) {
                int count6_ = 0;
                FromForeign0Data s6_ = this.fromForeign0_cache;
                if ((state_0 & 0b1000000) != 0 /* is-state_0 fromForeign(Object, InteropLibrary) */) {
                    while (s6_ != null) {
                        if ((s6_.interop_.accepts(child0Value))) {
                            break;
                        }
                        s6_ = s6_.next_;
                        count6_++;
                    }
                }
                if (s6_ == null) {
                    // assert (s6_.interop_.accepts(child0Value));
                    if (count6_ < (SLUnboxNode.LIMIT)) {
                        s6_ = super.insert(new FromForeign0Data(fromForeign0_cache));
                        s6_.interop_ = s6_.insertAccessor((INTEROP_LIBRARY_.create(child0Value)));
                        MemoryFence.storeStore();
                        this.fromForeign0_cache = s6_;
                        this.state_0_ = state_0 = state_0 | 0b1000000 /* add-state_0 fromForeign(Object, InteropLibrary) */;
                    }
                }
                if (s6_ != null) {
                    lock.unlock();
                    hasLock = false;
                    return SLUnboxNode.fromForeign(child0Value, s6_.interop_);
                }
            }
            {
                InteropLibrary fromForeign1_interop__ = null;
                {
                    EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                    Node prev_ = encapsulating_.set(this);
                    try {
                        fromForeign1_interop__ = (INTEROP_LIBRARY_.getUncached(child0Value));
                        this.exclude_ = exclude = exclude | 0b1 /* add-exclude fromForeign(Object, InteropLibrary) */;
                        this.fromForeign0_cache = null;
                        state_0 = state_0 & 0xffffffbf /* remove-state_0 fromForeign(Object, InteropLibrary) */;
                        this.state_0_ = state_0 = state_0 | 0b10000000 /* add-state_0 fromForeign(Object, InteropLibrary) */;
                        lock.unlock();
                        hasLock = false;
                        return SLUnboxNode.fromForeign(child0Value, fromForeign1_interop__);
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
        if ((state_0 & 0b11111111) == 0) {
            return NodeCost.UNINITIALIZED;
        } else {
            if (((state_0 & 0b11111111) & ((state_0 & 0b11111111) - 1)) == 0 /* is-single-state_0  */) {
                FromForeign0Data s6_ = this.fromForeign0_cache;
                if ((s6_ == null || s6_.next_ == null)) {
                    return NodeCost.MONOMORPHIC;
                }
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    public static SLUnboxNode create(SLExpressionNode child0) {
        return new SLUnboxNodeGen(child0);
    }

    @GeneratedBy(SLUnboxNode.class)
    private static final class FromForeign0Data extends Node {

        @Child FromForeign0Data next_;
        @Child InteropLibrary interop_;

        FromForeign0Data(FromForeign0Data next_) {
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

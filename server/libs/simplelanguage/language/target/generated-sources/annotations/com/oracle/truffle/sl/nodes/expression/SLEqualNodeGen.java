// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.expression;

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

@GeneratedBy(SLEqualNode.class)
@SuppressWarnings("unused")
public final class SLEqualNodeGen extends SLEqualNode {

    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    @Child private SLExpressionNode leftNode_;
    @Child private SLExpressionNode rightNode_;
    @CompilationFinal private volatile int state_0_;
    @CompilationFinal private volatile int exclude_;
    @Child private Generic0Data generic0_cache;

    private SLEqualNodeGen(SLExpressionNode leftNode, SLExpressionNode rightNode) {
        this.leftNode_ = leftNode;
        this.rightNode_ = rightNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b11111110) == 0 /* only-active doLong(long, long) */ && ((state_0 & 0b11111111) != 0  /* is-not doLong(long, long) && doBigNumber(SLBigNumber, SLBigNumber) && doBoolean(boolean, boolean) && doString(String, String) && doNull(SLNull, SLNull) && doFunction(SLFunction, Object) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) */)) {
            return executeGeneric_long_long0(state_0, frameValue);
        } else if ((state_0 & 0b11111011) == 0 /* only-active doBoolean(boolean, boolean) */ && ((state_0 & 0b11111111) != 0  /* is-not doLong(long, long) && doBigNumber(SLBigNumber, SLBigNumber) && doBoolean(boolean, boolean) && doString(String, String) && doNull(SLNull, SLNull) && doFunction(SLFunction, Object) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) */)) {
            return executeGeneric_boolean_boolean1(state_0, frameValue);
        } else {
            return executeGeneric_generic2(state_0, frameValue);
        }
    }

    private Object executeGeneric_long_long0(int state_0, VirtualFrame frameValue) {
        long leftNodeValue_;
        try {
            leftNodeValue_ = this.leftNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            Object rightNodeValue = this.rightNode_.executeGeneric(frameValue);
            return executeAndSpecialize(ex.getResult(), rightNodeValue);
        }
        long rightNodeValue_;
        try {
            rightNodeValue_ = this.rightNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(leftNodeValue_, ex.getResult());
        }
        assert (state_0 & 0b1) != 0 /* is-state_0 doLong(long, long) */;
        return doLong(leftNodeValue_, rightNodeValue_);
    }

    private Object executeGeneric_boolean_boolean1(int state_0, VirtualFrame frameValue) {
        boolean leftNodeValue_;
        try {
            leftNodeValue_ = this.leftNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            Object rightNodeValue = this.rightNode_.executeGeneric(frameValue);
            return executeAndSpecialize(ex.getResult(), rightNodeValue);
        }
        boolean rightNodeValue_;
        try {
            rightNodeValue_ = this.rightNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(leftNodeValue_, ex.getResult());
        }
        assert (state_0 & 0b100) != 0 /* is-state_0 doBoolean(boolean, boolean) */;
        return doBoolean(leftNodeValue_, rightNodeValue_);
    }

    @SuppressWarnings("static-method")
    @TruffleBoundary
    private Object generic1Boundary(int state_0, Object leftNodeValue_, Object rightNodeValue_) {
        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
        Node prev_ = encapsulating_.set(this);
        try {
            {
                InteropLibrary generic1_leftInterop__ = (INTEROP_LIBRARY_.getUncached(leftNodeValue_));
                InteropLibrary generic1_rightInterop__ = (INTEROP_LIBRARY_.getUncached(rightNodeValue_));
                return doGeneric(leftNodeValue_, rightNodeValue_, generic1_leftInterop__, generic1_rightInterop__);
            }
        } finally {
            encapsulating_.set(prev_);
        }
    }

    @ExplodeLoop
    private Object executeGeneric_generic2(int state_0, VirtualFrame frameValue) {
        Object leftNodeValue_ = this.leftNode_.executeGeneric(frameValue);
        Object rightNodeValue_ = this.rightNode_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 doLong(long, long) */ && leftNodeValue_ instanceof Long) {
            long leftNodeValue__ = (long) leftNodeValue_;
            if (rightNodeValue_ instanceof Long) {
                long rightNodeValue__ = (long) rightNodeValue_;
                return doLong(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 doBigNumber(SLBigNumber, SLBigNumber) */ && SLTypesGen.isImplicitSLBigNumber((state_0 & 0b1100000000) >>> 8 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_)) {
            SLBigNumber leftNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b1100000000) >>> 8 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_);
            if (SLTypesGen.isImplicitSLBigNumber((state_0 & 0b110000000000) >>> 10 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_)) {
                SLBigNumber rightNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b110000000000) >>> 10 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_);
                return doBigNumber(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 doBoolean(boolean, boolean) */ && leftNodeValue_ instanceof Boolean) {
            boolean leftNodeValue__ = (boolean) leftNodeValue_;
            if (rightNodeValue_ instanceof Boolean) {
                boolean rightNodeValue__ = (boolean) rightNodeValue_;
                return doBoolean(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b1000) != 0 /* is-state_0 doString(String, String) */ && leftNodeValue_ instanceof String) {
            String leftNodeValue__ = (String) leftNodeValue_;
            if (rightNodeValue_ instanceof String) {
                String rightNodeValue__ = (String) rightNodeValue_;
                return doString(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b10000) != 0 /* is-state_0 doNull(SLNull, SLNull) */ && SLTypes.isSLNull(leftNodeValue_)) {
            SLNull leftNodeValue__ = SLTypes.asSLNull(leftNodeValue_);
            if (SLTypes.isSLNull(rightNodeValue_)) {
                SLNull rightNodeValue__ = SLTypes.asSLNull(rightNodeValue_);
                return doNull(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b11100000) != 0 /* is-state_0 doFunction(SLFunction, Object) || doGeneric(Object, Object, InteropLibrary, InteropLibrary) || doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
            if ((state_0 & 0b100000) != 0 /* is-state_0 doFunction(SLFunction, Object) */ && leftNodeValue_ instanceof SLFunction) {
                SLFunction leftNodeValue__ = (SLFunction) leftNodeValue_;
                return doFunction(leftNodeValue__, rightNodeValue_);
            }
            if ((state_0 & 0b11000000) != 0 /* is-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) || doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                if ((state_0 & 0b1000000) != 0 /* is-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                    Generic0Data s6_ = this.generic0_cache;
                    while (s6_ != null) {
                        if ((s6_.leftInterop_.accepts(leftNodeValue_)) && (s6_.rightInterop_.accepts(rightNodeValue_))) {
                            return doGeneric(leftNodeValue_, rightNodeValue_, s6_.leftInterop_, s6_.rightInterop_);
                        }
                        s6_ = s6_.next_;
                    }
                }
                if ((state_0 & 0b10000000) != 0 /* is-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                    return this.generic1Boundary(state_0, leftNodeValue_, rightNodeValue_);
                }
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(leftNodeValue_, rightNodeValue_);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b11111110) == 0 /* only-active doLong(long, long) */ && ((state_0 & 0b11111111) != 0  /* is-not doLong(long, long) && doBigNumber(SLBigNumber, SLBigNumber) && doBoolean(boolean, boolean) && doString(String, String) && doNull(SLNull, SLNull) && doFunction(SLFunction, Object) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) */)) {
            return executeBoolean_long_long3(state_0, frameValue);
        } else if ((state_0 & 0b11111011) == 0 /* only-active doBoolean(boolean, boolean) */ && ((state_0 & 0b11111111) != 0  /* is-not doLong(long, long) && doBigNumber(SLBigNumber, SLBigNumber) && doBoolean(boolean, boolean) && doString(String, String) && doNull(SLNull, SLNull) && doFunction(SLFunction, Object) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) && doGeneric(Object, Object, InteropLibrary, InteropLibrary) */)) {
            return executeBoolean_boolean_boolean4(state_0, frameValue);
        } else {
            return executeBoolean_generic5(state_0, frameValue);
        }
    }

    private boolean executeBoolean_long_long3(int state_0, VirtualFrame frameValue) {
        long leftNodeValue_;
        try {
            leftNodeValue_ = this.leftNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            Object rightNodeValue = this.rightNode_.executeGeneric(frameValue);
            return executeAndSpecialize(ex.getResult(), rightNodeValue);
        }
        long rightNodeValue_;
        try {
            rightNodeValue_ = this.rightNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(leftNodeValue_, ex.getResult());
        }
        assert (state_0 & 0b1) != 0 /* is-state_0 doLong(long, long) */;
        return doLong(leftNodeValue_, rightNodeValue_);
    }

    private boolean executeBoolean_boolean_boolean4(int state_0, VirtualFrame frameValue) {
        boolean leftNodeValue_;
        try {
            leftNodeValue_ = this.leftNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            Object rightNodeValue = this.rightNode_.executeGeneric(frameValue);
            return executeAndSpecialize(ex.getResult(), rightNodeValue);
        }
        boolean rightNodeValue_;
        try {
            rightNodeValue_ = this.rightNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(leftNodeValue_, ex.getResult());
        }
        assert (state_0 & 0b100) != 0 /* is-state_0 doBoolean(boolean, boolean) */;
        return doBoolean(leftNodeValue_, rightNodeValue_);
    }

    @SuppressWarnings("static-method")
    @TruffleBoundary
    private boolean generic1Boundary0(int state_0, Object leftNodeValue_, Object rightNodeValue_) {
        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
        Node prev_ = encapsulating_.set(this);
        try {
            {
                InteropLibrary generic1_leftInterop__ = (INTEROP_LIBRARY_.getUncached(leftNodeValue_));
                InteropLibrary generic1_rightInterop__ = (INTEROP_LIBRARY_.getUncached(rightNodeValue_));
                return doGeneric(leftNodeValue_, rightNodeValue_, generic1_leftInterop__, generic1_rightInterop__);
            }
        } finally {
            encapsulating_.set(prev_);
        }
    }

    @ExplodeLoop
    private boolean executeBoolean_generic5(int state_0, VirtualFrame frameValue) {
        Object leftNodeValue_ = this.leftNode_.executeGeneric(frameValue);
        Object rightNodeValue_ = this.rightNode_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 doLong(long, long) */ && leftNodeValue_ instanceof Long) {
            long leftNodeValue__ = (long) leftNodeValue_;
            if (rightNodeValue_ instanceof Long) {
                long rightNodeValue__ = (long) rightNodeValue_;
                return doLong(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 doBigNumber(SLBigNumber, SLBigNumber) */ && SLTypesGen.isImplicitSLBigNumber((state_0 & 0b1100000000) >>> 8 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_)) {
            SLBigNumber leftNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b1100000000) >>> 8 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_);
            if (SLTypesGen.isImplicitSLBigNumber((state_0 & 0b110000000000) >>> 10 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_)) {
                SLBigNumber rightNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b110000000000) >>> 10 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_);
                return doBigNumber(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 doBoolean(boolean, boolean) */ && leftNodeValue_ instanceof Boolean) {
            boolean leftNodeValue__ = (boolean) leftNodeValue_;
            if (rightNodeValue_ instanceof Boolean) {
                boolean rightNodeValue__ = (boolean) rightNodeValue_;
                return doBoolean(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b1000) != 0 /* is-state_0 doString(String, String) */ && leftNodeValue_ instanceof String) {
            String leftNodeValue__ = (String) leftNodeValue_;
            if (rightNodeValue_ instanceof String) {
                String rightNodeValue__ = (String) rightNodeValue_;
                return doString(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b10000) != 0 /* is-state_0 doNull(SLNull, SLNull) */ && SLTypes.isSLNull(leftNodeValue_)) {
            SLNull leftNodeValue__ = SLTypes.asSLNull(leftNodeValue_);
            if (SLTypes.isSLNull(rightNodeValue_)) {
                SLNull rightNodeValue__ = SLTypes.asSLNull(rightNodeValue_);
                return doNull(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b11100000) != 0 /* is-state_0 doFunction(SLFunction, Object) || doGeneric(Object, Object, InteropLibrary, InteropLibrary) || doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
            if ((state_0 & 0b100000) != 0 /* is-state_0 doFunction(SLFunction, Object) */ && leftNodeValue_ instanceof SLFunction) {
                SLFunction leftNodeValue__ = (SLFunction) leftNodeValue_;
                return doFunction(leftNodeValue__, rightNodeValue_);
            }
            if ((state_0 & 0b11000000) != 0 /* is-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) || doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                if ((state_0 & 0b1000000) != 0 /* is-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                    Generic0Data s6_ = this.generic0_cache;
                    while (s6_ != null) {
                        if ((s6_.leftInterop_.accepts(leftNodeValue_)) && (s6_.rightInterop_.accepts(rightNodeValue_))) {
                            return doGeneric(leftNodeValue_, rightNodeValue_, s6_.leftInterop_, s6_.rightInterop_);
                        }
                        s6_ = s6_.next_;
                    }
                }
                if ((state_0 & 0b10000000) != 0 /* is-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                    return this.generic1Boundary0(state_0, leftNodeValue_, rightNodeValue_);
                }
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(leftNodeValue_, rightNodeValue_);
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        executeBoolean(frameValue);
        return;
    }

    private boolean executeAndSpecialize(Object leftNodeValue, Object rightNodeValue) {
        Lock lock = getLock();
        boolean hasLock = true;
        lock.lock();
        try {
            int state_0 = this.state_0_;
            int exclude = this.exclude_;
            if (leftNodeValue instanceof Long) {
                long leftNodeValue_ = (long) leftNodeValue;
                if (rightNodeValue instanceof Long) {
                    long rightNodeValue_ = (long) rightNodeValue;
                    this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 doLong(long, long) */;
                    lock.unlock();
                    hasLock = false;
                    return doLong(leftNodeValue_, rightNodeValue_);
                }
            }
            {
                int sLBigNumberCast0;
                if ((sLBigNumberCast0 = SLTypesGen.specializeImplicitSLBigNumber(leftNodeValue)) != 0) {
                    SLBigNumber leftNodeValue_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast0, leftNodeValue);
                    int sLBigNumberCast1;
                    if ((sLBigNumberCast1 = SLTypesGen.specializeImplicitSLBigNumber(rightNodeValue)) != 0) {
                        SLBigNumber rightNodeValue_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast1, rightNodeValue);
                        state_0 = (state_0 | (sLBigNumberCast0 << 8) /* set-implicit-state_0 0:SLBigNumber */);
                        state_0 = (state_0 | (sLBigNumberCast1 << 10) /* set-implicit-state_0 1:SLBigNumber */);
                        this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 doBigNumber(SLBigNumber, SLBigNumber) */;
                        lock.unlock();
                        hasLock = false;
                        return doBigNumber(leftNodeValue_, rightNodeValue_);
                    }
                }
            }
            if (leftNodeValue instanceof Boolean) {
                boolean leftNodeValue_ = (boolean) leftNodeValue;
                if (rightNodeValue instanceof Boolean) {
                    boolean rightNodeValue_ = (boolean) rightNodeValue;
                    this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 doBoolean(boolean, boolean) */;
                    lock.unlock();
                    hasLock = false;
                    return doBoolean(leftNodeValue_, rightNodeValue_);
                }
            }
            if (leftNodeValue instanceof String) {
                String leftNodeValue_ = (String) leftNodeValue;
                if (rightNodeValue instanceof String) {
                    String rightNodeValue_ = (String) rightNodeValue;
                    this.state_0_ = state_0 = state_0 | 0b1000 /* add-state_0 doString(String, String) */;
                    lock.unlock();
                    hasLock = false;
                    return doString(leftNodeValue_, rightNodeValue_);
                }
            }
            if (SLTypes.isSLNull(leftNodeValue)) {
                SLNull leftNodeValue_ = SLTypes.asSLNull(leftNodeValue);
                if (SLTypes.isSLNull(rightNodeValue)) {
                    SLNull rightNodeValue_ = SLTypes.asSLNull(rightNodeValue);
                    this.state_0_ = state_0 = state_0 | 0b10000 /* add-state_0 doNull(SLNull, SLNull) */;
                    lock.unlock();
                    hasLock = false;
                    return doNull(leftNodeValue_, rightNodeValue_);
                }
            }
            if (leftNodeValue instanceof SLFunction) {
                SLFunction leftNodeValue_ = (SLFunction) leftNodeValue;
                this.state_0_ = state_0 = state_0 | 0b100000 /* add-state_0 doFunction(SLFunction, Object) */;
                lock.unlock();
                hasLock = false;
                return doFunction(leftNodeValue_, rightNodeValue);
            }
            if ((exclude) == 0 /* is-not-exclude doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                int count6_ = 0;
                Generic0Data s6_ = this.generic0_cache;
                if ((state_0 & 0b1000000) != 0 /* is-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */) {
                    while (s6_ != null) {
                        if ((s6_.leftInterop_.accepts(leftNodeValue)) && (s6_.rightInterop_.accepts(rightNodeValue))) {
                            break;
                        }
                        s6_ = s6_.next_;
                        count6_++;
                    }
                }
                if (s6_ == null) {
                    // assert (s6_.leftInterop_.accepts(leftNodeValue));
                    // assert (s6_.rightInterop_.accepts(rightNodeValue));
                    if (count6_ < (4)) {
                        s6_ = super.insert(new Generic0Data(generic0_cache));
                        s6_.leftInterop_ = s6_.insertAccessor((INTEROP_LIBRARY_.create(leftNodeValue)));
                        s6_.rightInterop_ = s6_.insertAccessor((INTEROP_LIBRARY_.create(rightNodeValue)));
                        MemoryFence.storeStore();
                        this.generic0_cache = s6_;
                        this.state_0_ = state_0 = state_0 | 0b1000000 /* add-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */;
                    }
                }
                if (s6_ != null) {
                    lock.unlock();
                    hasLock = false;
                    return doGeneric(leftNodeValue, rightNodeValue, s6_.leftInterop_, s6_.rightInterop_);
                }
            }
            {
                InteropLibrary generic1_rightInterop__ = null;
                InteropLibrary generic1_leftInterop__ = null;
                {
                    EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                    Node prev_ = encapsulating_.set(this);
                    try {
                        generic1_leftInterop__ = (INTEROP_LIBRARY_.getUncached(leftNodeValue));
                        generic1_rightInterop__ = (INTEROP_LIBRARY_.getUncached(rightNodeValue));
                        this.exclude_ = exclude = exclude | 0b1 /* add-exclude doGeneric(Object, Object, InteropLibrary, InteropLibrary) */;
                        this.generic0_cache = null;
                        state_0 = state_0 & 0xffffffbf /* remove-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */;
                        this.state_0_ = state_0 = state_0 | 0b10000000 /* add-state_0 doGeneric(Object, Object, InteropLibrary, InteropLibrary) */;
                        lock.unlock();
                        hasLock = false;
                        return doGeneric(leftNodeValue, rightNodeValue, generic1_leftInterop__, generic1_rightInterop__);
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
                Generic0Data s6_ = this.generic0_cache;
                if ((s6_ == null || s6_.next_ == null)) {
                    return NodeCost.MONOMORPHIC;
                }
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    public static SLEqualNode create(SLExpressionNode leftNode, SLExpressionNode rightNode) {
        return new SLEqualNodeGen(leftNode, rightNode);
    }

    @GeneratedBy(SLEqualNode.class)
    private static final class Generic0Data extends Node {

        @Child Generic0Data next_;
        @Child InteropLibrary leftInterop_;
        @Child InteropLibrary rightInterop_;

        Generic0Data(Generic0Data next_) {
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

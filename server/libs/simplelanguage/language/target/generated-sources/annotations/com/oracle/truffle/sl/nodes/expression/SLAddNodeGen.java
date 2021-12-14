// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.expression;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLTypesGen;
import com.oracle.truffle.sl.runtime.SLBigNumber;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLAddNode.class)
public final class SLAddNodeGen extends SLAddNode {

    @Child private SLExpressionNode leftNode_;
    @Child private SLExpressionNode rightNode_;
    @CompilationFinal private int state_0_;
    @CompilationFinal private int exclude_;

    private SLAddNodeGen(SLExpressionNode leftNode, SLExpressionNode rightNode) {
        this.leftNode_ = leftNode;
        this.rightNode_ = rightNode;
    }

    private boolean fallbackGuard_(int state_0, Object leftNodeValue, Object rightNodeValue) {
        if (SLTypesGen.isImplicitSLBigNumber(leftNodeValue) && SLTypesGen.isImplicitSLBigNumber(rightNodeValue)) {
            return false;
        }
        if (((state_0 & 0b100)) == 0 /* is-not-state_0 add(Object, Object) */ && (isString(leftNodeValue, rightNodeValue))) {
            return false;
        }
        return true;
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b1110) == 0 /* only-active add(long, long) */ && ((state_0 & 0b1111) != 0  /* is-not add(long, long) && add(SLBigNumber, SLBigNumber) && add(Object, Object) && typeError(Object, Object) */)) {
            return executeGeneric_long_long0(state_0, frameValue);
        } else {
            return executeGeneric_generic1(state_0, frameValue);
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
        assert (state_0 & 0b1) != 0 /* is-state_0 add(long, long) */;
        try {
            return add(leftNodeValue_, rightNodeValue_);
        } catch (ArithmeticException ex) {
            // implicit transferToInterpreterAndInvalidate()
            Lock lock = getLock();
            lock.lock();
            try {
                this.exclude_ = this.exclude_ | 0b1 /* add-exclude add(long, long) */;
                this.state_0_ = this.state_0_ & 0xfffffffe /* remove-state_0 add(long, long) */;
            } finally {
                lock.unlock();
            }
            return executeAndSpecialize(leftNodeValue_, rightNodeValue_);
        }
    }

    private Object executeGeneric_generic1(int state_0, VirtualFrame frameValue) {
        Object leftNodeValue_ = this.leftNode_.executeGeneric(frameValue);
        Object rightNodeValue_ = this.rightNode_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 add(long, long) */ && leftNodeValue_ instanceof Long) {
            long leftNodeValue__ = (long) leftNodeValue_;
            if (rightNodeValue_ instanceof Long) {
                long rightNodeValue__ = (long) rightNodeValue_;
                try {
                    return add(leftNodeValue__, rightNodeValue__);
                } catch (ArithmeticException ex) {
                    // implicit transferToInterpreterAndInvalidate()
                    Lock lock = getLock();
                    lock.lock();
                    try {
                        this.exclude_ = this.exclude_ | 0b1 /* add-exclude add(long, long) */;
                        this.state_0_ = this.state_0_ & 0xfffffffe /* remove-state_0 add(long, long) */;
                    } finally {
                        lock.unlock();
                    }
                    return executeAndSpecialize(leftNodeValue__, rightNodeValue__);
                }
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 add(SLBigNumber, SLBigNumber) */ && SLTypesGen.isImplicitSLBigNumber((state_0 & 0b110000) >>> 4 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_)) {
            SLBigNumber leftNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b110000) >>> 4 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_);
            if (SLTypesGen.isImplicitSLBigNumber((state_0 & 0b11000000) >>> 6 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_)) {
                SLBigNumber rightNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b11000000) >>> 6 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_);
                return add(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b1100) != 0 /* is-state_0 add(Object, Object) || typeError(Object, Object) */) {
            if ((state_0 & 0b100) != 0 /* is-state_0 add(Object, Object) */) {
                if ((isString(leftNodeValue_, rightNodeValue_))) {
                    return add(leftNodeValue_, rightNodeValue_);
                }
            }
            if ((state_0 & 0b1000) != 0 /* is-state_0 typeError(Object, Object) */) {
                if (fallbackGuard_(state_0, leftNodeValue_, rightNodeValue_)) {
                    return typeError(leftNodeValue_, rightNodeValue_);
                }
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(leftNodeValue_, rightNodeValue_);
    }

    @Override
    public long executeLong(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b1000) != 0 /* is-state_0 typeError(Object, Object) */) {
            return SLTypesGen.expectLong(executeGeneric(frameValue));
        }
        long leftNodeValue_;
        try {
            leftNodeValue_ = this.leftNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            Object rightNodeValue = this.rightNode_.executeGeneric(frameValue);
            return SLTypesGen.expectLong(executeAndSpecialize(ex.getResult(), rightNodeValue));
        }
        long rightNodeValue_;
        try {
            rightNodeValue_ = this.rightNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return SLTypesGen.expectLong(executeAndSpecialize(leftNodeValue_, ex.getResult()));
        }
        if ((state_0 & 0b1) != 0 /* is-state_0 add(long, long) */) {
            try {
                return add(leftNodeValue_, rightNodeValue_);
            } catch (ArithmeticException ex) {
                // implicit transferToInterpreterAndInvalidate()
                Lock lock = getLock();
                lock.lock();
                try {
                    this.exclude_ = this.exclude_ | 0b1 /* add-exclude add(long, long) */;
                    this.state_0_ = this.state_0_ & 0xfffffffe /* remove-state_0 add(long, long) */;
                } finally {
                    lock.unlock();
                }
                return SLTypesGen.expectLong(executeAndSpecialize(leftNodeValue_, rightNodeValue_));
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectLong(executeAndSpecialize(leftNodeValue_, rightNodeValue_));
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        try {
            if ((state_0 & 0b1110) == 0 /* only-active add(long, long) */ && ((state_0 & 0b1111) != 0  /* is-not add(long, long) && add(SLBigNumber, SLBigNumber) && add(Object, Object) && typeError(Object, Object) */)) {
                executeLong(frameValue);
                return;
            }
            executeGeneric(frameValue);
            return;
        } catch (UnexpectedResultException ex) {
            return;
        }
    }

    private Object executeAndSpecialize(Object leftNodeValue, Object rightNodeValue) {
        Lock lock = getLock();
        boolean hasLock = true;
        lock.lock();
        try {
            int state_0 = this.state_0_;
            int exclude = this.exclude_;
            if ((exclude) == 0 /* is-not-exclude add(long, long) */ && leftNodeValue instanceof Long) {
                long leftNodeValue_ = (long) leftNodeValue;
                if (rightNodeValue instanceof Long) {
                    long rightNodeValue_ = (long) rightNodeValue;
                    this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 add(long, long) */;
                    try {
                        lock.unlock();
                        hasLock = false;
                        return add(leftNodeValue_, rightNodeValue_);
                    } catch (ArithmeticException ex) {
                        // implicit transferToInterpreterAndInvalidate()
                        lock.lock();
                        try {
                            this.exclude_ = this.exclude_ | 0b1 /* add-exclude add(long, long) */;
                            this.state_0_ = this.state_0_ & 0xfffffffe /* remove-state_0 add(long, long) */;
                        } finally {
                            lock.unlock();
                        }
                        return executeAndSpecialize(leftNodeValue_, rightNodeValue_);
                    }
                }
            }
            {
                int sLBigNumberCast0;
                if ((sLBigNumberCast0 = SLTypesGen.specializeImplicitSLBigNumber(leftNodeValue)) != 0) {
                    SLBigNumber leftNodeValue_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast0, leftNodeValue);
                    int sLBigNumberCast1;
                    if ((sLBigNumberCast1 = SLTypesGen.specializeImplicitSLBigNumber(rightNodeValue)) != 0) {
                        SLBigNumber rightNodeValue_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast1, rightNodeValue);
                        state_0 = (state_0 | (sLBigNumberCast0 << 4) /* set-implicit-state_0 0:SLBigNumber */);
                        state_0 = (state_0 | (sLBigNumberCast1 << 6) /* set-implicit-state_0 1:SLBigNumber */);
                        this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 add(SLBigNumber, SLBigNumber) */;
                        lock.unlock();
                        hasLock = false;
                        return add(leftNodeValue_, rightNodeValue_);
                    }
                }
            }
            if ((isString(leftNodeValue, rightNodeValue))) {
                this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 add(Object, Object) */;
                lock.unlock();
                hasLock = false;
                return add(leftNodeValue, rightNodeValue);
            }
            this.state_0_ = state_0 = state_0 | 0b1000 /* add-state_0 typeError(Object, Object) */;
            lock.unlock();
            hasLock = false;
            return typeError(leftNodeValue, rightNodeValue);
        } finally {
            if (hasLock) {
                lock.unlock();
            }
        }
    }

    @Override
    public NodeCost getCost() {
        int state_0 = this.state_0_;
        if ((state_0 & 0b1111) == 0) {
            return NodeCost.UNINITIALIZED;
        } else {
            if (((state_0 & 0b1111) & ((state_0 & 0b1111) - 1)) == 0 /* is-single-state_0  */) {
                return NodeCost.MONOMORPHIC;
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    public static SLAddNode create(SLExpressionNode leftNode, SLExpressionNode rightNode) {
        return new SLAddNodeGen(leftNode, rightNode);
    }

}

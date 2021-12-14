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

@GeneratedBy(SLLessOrEqualNode.class)
public final class SLLessOrEqualNodeGen extends SLLessOrEqualNode {

    @Child private SLExpressionNode leftNode_;
    @Child private SLExpressionNode rightNode_;
    @CompilationFinal private int state_0_;

    private SLLessOrEqualNodeGen(SLExpressionNode leftNode, SLExpressionNode rightNode) {
        this.leftNode_ = leftNode;
        this.rightNode_ = rightNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b110) == 0 /* only-active lessOrEqual(long, long) */ && ((state_0 & 0b111) != 0  /* is-not lessOrEqual(long, long) && lessOrEqual(SLBigNumber, SLBigNumber) && typeError(Object, Object) */)) {
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
        assert (state_0 & 0b1) != 0 /* is-state_0 lessOrEqual(long, long) */;
        return lessOrEqual(leftNodeValue_, rightNodeValue_);
    }

    private Object executeGeneric_generic1(int state_0, VirtualFrame frameValue) {
        Object leftNodeValue_ = this.leftNode_.executeGeneric(frameValue);
        Object rightNodeValue_ = this.rightNode_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 lessOrEqual(long, long) */ && leftNodeValue_ instanceof Long) {
            long leftNodeValue__ = (long) leftNodeValue_;
            if (rightNodeValue_ instanceof Long) {
                long rightNodeValue__ = (long) rightNodeValue_;
                return lessOrEqual(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 lessOrEqual(SLBigNumber, SLBigNumber) */ && SLTypesGen.isImplicitSLBigNumber((state_0 & 0b11000) >>> 3 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_)) {
            SLBigNumber leftNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b11000) >>> 3 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_);
            if (SLTypesGen.isImplicitSLBigNumber((state_0 & 0b1100000) >>> 5 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_)) {
                SLBigNumber rightNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b1100000) >>> 5 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_);
                return lessOrEqual(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 typeError(Object, Object) */) {
            if (fallbackGuard_(leftNodeValue_, rightNodeValue_)) {
                return typeError(leftNodeValue_, rightNodeValue_);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(leftNodeValue_, rightNodeValue_);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b100) != 0 /* is-state_0 typeError(Object, Object) */) {
            return SLTypesGen.expectBoolean(executeGeneric(frameValue));
        }
        if ((state_0 & 0b10) == 0 /* only-active lessOrEqual(long, long) */ && ((state_0 & 0b11) != 0  /* is-not lessOrEqual(long, long) && lessOrEqual(SLBigNumber, SLBigNumber) */)) {
            return executeBoolean_long_long2(state_0, frameValue);
        } else {
            return executeBoolean_generic3(state_0, frameValue);
        }
    }

    private boolean executeBoolean_long_long2(int state_0, VirtualFrame frameValue) throws UnexpectedResultException {
        long leftNodeValue_;
        try {
            leftNodeValue_ = this.leftNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            Object rightNodeValue = this.rightNode_.executeGeneric(frameValue);
            return SLTypesGen.expectBoolean(executeAndSpecialize(ex.getResult(), rightNodeValue));
        }
        long rightNodeValue_;
        try {
            rightNodeValue_ = this.rightNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return SLTypesGen.expectBoolean(executeAndSpecialize(leftNodeValue_, ex.getResult()));
        }
        assert (state_0 & 0b1) != 0 /* is-state_0 lessOrEqual(long, long) */;
        return lessOrEqual(leftNodeValue_, rightNodeValue_);
    }

    private boolean executeBoolean_generic3(int state_0, VirtualFrame frameValue) throws UnexpectedResultException {
        Object leftNodeValue_ = this.leftNode_.executeGeneric(frameValue);
        Object rightNodeValue_ = this.rightNode_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 lessOrEqual(long, long) */ && leftNodeValue_ instanceof Long) {
            long leftNodeValue__ = (long) leftNodeValue_;
            if (rightNodeValue_ instanceof Long) {
                long rightNodeValue__ = (long) rightNodeValue_;
                return lessOrEqual(leftNodeValue__, rightNodeValue__);
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 lessOrEqual(SLBigNumber, SLBigNumber) */ && SLTypesGen.isImplicitSLBigNumber((state_0 & 0b11000) >>> 3 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_)) {
            SLBigNumber leftNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b11000) >>> 3 /* extract-implicit-state_0 0:SLBigNumber */, leftNodeValue_);
            if (SLTypesGen.isImplicitSLBigNumber((state_0 & 0b1100000) >>> 5 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_)) {
                SLBigNumber rightNodeValue__ = SLTypesGen.asImplicitSLBigNumber((state_0 & 0b1100000) >>> 5 /* extract-implicit-state_0 1:SLBigNumber */, rightNodeValue_);
                return lessOrEqual(leftNodeValue__, rightNodeValue__);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectBoolean(executeAndSpecialize(leftNodeValue_, rightNodeValue_));
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        try {
            if ((state_0 & 0b100) == 0 /* only-active lessOrEqual(long, long) && lessOrEqual(SLBigNumber, SLBigNumber) */ && ((state_0 & 0b111) != 0  /* is-not lessOrEqual(long, long) && lessOrEqual(SLBigNumber, SLBigNumber) && typeError(Object, Object) */)) {
                executeBoolean(frameValue);
                return;
            }
            executeGeneric(frameValue);
            return;
        } catch (UnexpectedResultException ex) {
            return;
        }
    }

    private Object executeAndSpecialize(Object leftNodeValue, Object rightNodeValue) {
        int state_0 = this.state_0_;
        if (leftNodeValue instanceof Long) {
            long leftNodeValue_ = (long) leftNodeValue;
            if (rightNodeValue instanceof Long) {
                long rightNodeValue_ = (long) rightNodeValue;
                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 lessOrEqual(long, long) */;
                return lessOrEqual(leftNodeValue_, rightNodeValue_);
            }
        }
        {
            int sLBigNumberCast0;
            if ((sLBigNumberCast0 = SLTypesGen.specializeImplicitSLBigNumber(leftNodeValue)) != 0) {
                SLBigNumber leftNodeValue_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast0, leftNodeValue);
                int sLBigNumberCast1;
                if ((sLBigNumberCast1 = SLTypesGen.specializeImplicitSLBigNumber(rightNodeValue)) != 0) {
                    SLBigNumber rightNodeValue_ = SLTypesGen.asImplicitSLBigNumber(sLBigNumberCast1, rightNodeValue);
                    state_0 = (state_0 | (sLBigNumberCast0 << 3) /* set-implicit-state_0 0:SLBigNumber */);
                    state_0 = (state_0 | (sLBigNumberCast1 << 5) /* set-implicit-state_0 1:SLBigNumber */);
                    this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 lessOrEqual(SLBigNumber, SLBigNumber) */;
                    return lessOrEqual(leftNodeValue_, rightNodeValue_);
                }
            }
        }
        this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 typeError(Object, Object) */;
        return typeError(leftNodeValue, rightNodeValue);
    }

    @Override
    public NodeCost getCost() {
        int state_0 = this.state_0_;
        if ((state_0 & 0b111) == 0) {
            return NodeCost.UNINITIALIZED;
        } else {
            if (((state_0 & 0b111) & ((state_0 & 0b111) - 1)) == 0 /* is-single-state_0  */) {
                return NodeCost.MONOMORPHIC;
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    private static boolean fallbackGuard_(Object leftNodeValue, Object rightNodeValue) {
        if (SLTypesGen.isImplicitSLBigNumber(leftNodeValue) && SLTypesGen.isImplicitSLBigNumber(rightNodeValue)) {
            return false;
        }
        return true;
    }

    public static SLLessOrEqualNode create(SLExpressionNode leftNode, SLExpressionNode rightNode) {
        return new SLLessOrEqualNodeGen(leftNode, rightNode);
    }

}

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

@GeneratedBy(SLLogicalNotNode.class)
public final class SLLogicalNotNodeGen extends SLLogicalNotNode {

    @Child private SLExpressionNode valueNode_;
    @CompilationFinal private int state_0_;

    private SLLogicalNotNodeGen(SLExpressionNode valueNode) {
        this.valueNode_ = valueNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b10) == 0 /* only-active doBoolean(boolean) */ && (state_0 != 0  /* is-not doBoolean(boolean) && typeError(Object) */)) {
            return executeGeneric_boolean0(state_0, frameValue);
        } else {
            return executeGeneric_generic1(state_0, frameValue);
        }
    }

    private Object executeGeneric_boolean0(int state_0, VirtualFrame frameValue) {
        boolean valueNodeValue_;
        try {
            valueNodeValue_ = this.valueNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(ex.getResult());
        }
        assert (state_0 & 0b1) != 0 /* is-state_0 doBoolean(boolean) */;
        return doBoolean(valueNodeValue_);
    }

    private Object executeGeneric_generic1(int state_0, VirtualFrame frameValue) {
        Object valueNodeValue_ = this.valueNode_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 doBoolean(boolean) */ && valueNodeValue_ instanceof Boolean) {
            boolean valueNodeValue__ = (boolean) valueNodeValue_;
            return doBoolean(valueNodeValue__);
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 typeError(Object) */) {
            if (fallbackGuard_(state_0, valueNodeValue_)) {
                return typeError(valueNodeValue_);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(valueNodeValue_);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b10) != 0 /* is-state_0 typeError(Object) */) {
            return SLTypesGen.expectBoolean(executeGeneric(frameValue));
        }
        boolean valueNodeValue_;
        try {
            valueNodeValue_ = this.valueNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return SLTypesGen.expectBoolean(executeAndSpecialize(ex.getResult()));
        }
        if ((state_0 & 0b1) != 0 /* is-state_0 doBoolean(boolean) */) {
            return doBoolean(valueNodeValue_);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectBoolean(executeAndSpecialize(valueNodeValue_));
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        try {
            if ((state_0 & 0b10) == 0 /* only-active doBoolean(boolean) */ && (state_0 != 0  /* is-not doBoolean(boolean) && typeError(Object) */)) {
                executeBoolean(frameValue);
                return;
            }
            executeGeneric(frameValue);
            return;
        } catch (UnexpectedResultException ex) {
            return;
        }
    }

    private Object executeAndSpecialize(Object valueNodeValue) {
        int state_0 = this.state_0_;
        if (valueNodeValue instanceof Boolean) {
            boolean valueNodeValue_ = (boolean) valueNodeValue;
            this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 doBoolean(boolean) */;
            return doBoolean(valueNodeValue_);
        }
        this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 typeError(Object) */;
        return typeError(valueNodeValue);
    }

    @Override
    public NodeCost getCost() {
        int state_0 = this.state_0_;
        if (state_0 == 0) {
            return NodeCost.UNINITIALIZED;
        } else {
            if ((state_0 & (state_0 - 1)) == 0 /* is-single-state_0  */) {
                return NodeCost.MONOMORPHIC;
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    private static boolean fallbackGuard_(int state_0, Object valueNodeValue) {
        if (((state_0 & 0b1)) == 0 /* is-not-state_0 doBoolean(boolean) */ && valueNodeValue instanceof Boolean) {
            return false;
        }
        return true;
    }

    public static SLLogicalNotNode create(SLExpressionNode valueNode) {
        return new SLLogicalNotNodeGen(valueNode);
    }

}

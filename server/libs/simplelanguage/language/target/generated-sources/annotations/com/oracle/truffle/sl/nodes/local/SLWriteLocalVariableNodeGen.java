// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.local;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.SLTypesGen;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLWriteLocalVariableNode.class)
public final class SLWriteLocalVariableNodeGen extends SLWriteLocalVariableNode {

    private final FrameSlot slot;
    private final SLExpressionNode nameNode;
    private final boolean declaration;
    @Child private SLExpressionNode valueNode_;
    @CompilationFinal private int state_0_;
    @CompilationFinal private int exclude_;

    private SLWriteLocalVariableNodeGen(SLExpressionNode valueNode, FrameSlot slot, SLExpressionNode nameNode, boolean declaration) {
        this.slot = slot;
        this.nameNode = nameNode;
        this.declaration = declaration;
        this.valueNode_ = valueNode;
    }

    @Override
    protected FrameSlot getSlot() {
        return this.slot;
    }

    @Override
    protected SLExpressionNode getNameNode() {
        return this.nameNode;
    }

    @Override
    public boolean isDeclaration() {
        return this.declaration;
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b110) == 0 /* only-active writeLong(VirtualFrame, long) */ && (state_0 != 0  /* is-not writeLong(VirtualFrame, long) && writeBoolean(VirtualFrame, boolean) && write(VirtualFrame, Object) */)) {
            return executeGeneric_long0(state_0, frameValue);
        } else if ((state_0 & 0b101) == 0 /* only-active writeBoolean(VirtualFrame, boolean) */ && (state_0 != 0  /* is-not writeLong(VirtualFrame, long) && writeBoolean(VirtualFrame, boolean) && write(VirtualFrame, Object) */)) {
            return executeGeneric_boolean1(state_0, frameValue);
        } else {
            return executeGeneric_generic2(state_0, frameValue);
        }
    }

    private Object executeGeneric_long0(int state_0, VirtualFrame frameValue) {
        long valueNodeValue_;
        try {
            valueNodeValue_ = this.valueNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(frameValue, ex.getResult());
        }
        assert (state_0 & 0b1) != 0 /* is-state_0 writeLong(VirtualFrame, long) */;
        if ((isLongOrIllegal(frameValue))) {
            return writeLong(frameValue, valueNodeValue_);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(frameValue, valueNodeValue_);
    }

    private Object executeGeneric_boolean1(int state_0, VirtualFrame frameValue) {
        boolean valueNodeValue_;
        try {
            valueNodeValue_ = this.valueNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return executeAndSpecialize(frameValue, ex.getResult());
        }
        assert (state_0 & 0b10) != 0 /* is-state_0 writeBoolean(VirtualFrame, boolean) */;
        if ((isBooleanOrIllegal(frameValue))) {
            return writeBoolean(frameValue, valueNodeValue_);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(frameValue, valueNodeValue_);
    }

    private Object executeGeneric_generic2(int state_0, VirtualFrame frameValue) {
        Object valueNodeValue_ = this.valueNode_.executeGeneric(frameValue);
        if ((state_0 & 0b1) != 0 /* is-state_0 writeLong(VirtualFrame, long) */ && valueNodeValue_ instanceof Long) {
            long valueNodeValue__ = (long) valueNodeValue_;
            if ((isLongOrIllegal(frameValue))) {
                return writeLong(frameValue, valueNodeValue__);
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 writeBoolean(VirtualFrame, boolean) */ && valueNodeValue_ instanceof Boolean) {
            boolean valueNodeValue__ = (boolean) valueNodeValue_;
            if ((isBooleanOrIllegal(frameValue))) {
                return writeBoolean(frameValue, valueNodeValue__);
            }
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 write(VirtualFrame, Object) */) {
            return write(frameValue, valueNodeValue_);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(frameValue, valueNodeValue_);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b100) != 0 /* is-state_0 write(VirtualFrame, Object) */) {
            return SLTypesGen.expectBoolean(executeGeneric(frameValue));
        }
        boolean valueNodeValue_;
        try {
            valueNodeValue_ = this.valueNode_.executeBoolean(frameValue);
        } catch (UnexpectedResultException ex) {
            return SLTypesGen.expectBoolean(executeAndSpecialize(frameValue, ex.getResult()));
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 writeBoolean(VirtualFrame, boolean) */) {
            if ((isBooleanOrIllegal(frameValue))) {
                return writeBoolean(frameValue, valueNodeValue_);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectBoolean(executeAndSpecialize(frameValue, valueNodeValue_));
    }

    @Override
    public long executeLong(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b100) != 0 /* is-state_0 write(VirtualFrame, Object) */) {
            return SLTypesGen.expectLong(executeGeneric(frameValue));
        }
        long valueNodeValue_;
        try {
            valueNodeValue_ = this.valueNode_.executeLong(frameValue);
        } catch (UnexpectedResultException ex) {
            return SLTypesGen.expectLong(executeAndSpecialize(frameValue, ex.getResult()));
        }
        if ((state_0 & 0b1) != 0 /* is-state_0 writeLong(VirtualFrame, long) */) {
            if ((isLongOrIllegal(frameValue))) {
                return writeLong(frameValue, valueNodeValue_);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectLong(executeAndSpecialize(frameValue, valueNodeValue_));
    }

    @Override
    public void executeWrite(VirtualFrame frameValue, Object valueNodeValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b1) != 0 /* is-state_0 writeLong(VirtualFrame, long) */ && valueNodeValue instanceof Long) {
            long valueNodeValue_ = (long) valueNodeValue;
            if ((isLongOrIllegal(frameValue))) {
                writeLong(frameValue, valueNodeValue_);
                return;
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 writeBoolean(VirtualFrame, boolean) */ && valueNodeValue instanceof Boolean) {
            boolean valueNodeValue_ = (boolean) valueNodeValue;
            if ((isBooleanOrIllegal(frameValue))) {
                writeBoolean(frameValue, valueNodeValue_);
                return;
            }
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 write(VirtualFrame, Object) */) {
            write(frameValue, valueNodeValue);
            return;
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        executeAndSpecialize(frameValue, valueNodeValue);
        return;
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        try {
            if ((state_0 & 0b110) == 0 /* only-active writeLong(VirtualFrame, long) */ && (state_0 != 0  /* is-not writeLong(VirtualFrame, long) && writeBoolean(VirtualFrame, boolean) && write(VirtualFrame, Object) */)) {
                executeLong(frameValue);
                return;
            } else if ((state_0 & 0b101) == 0 /* only-active writeBoolean(VirtualFrame, boolean) */ && (state_0 != 0  /* is-not writeLong(VirtualFrame, long) && writeBoolean(VirtualFrame, boolean) && write(VirtualFrame, Object) */)) {
                executeBoolean(frameValue);
                return;
            }
            executeGeneric(frameValue);
            return;
        } catch (UnexpectedResultException ex) {
            return;
        }
    }

    private Object executeAndSpecialize(VirtualFrame frameValue, Object valueNodeValue) {
        Lock lock = getLock();
        boolean hasLock = true;
        lock.lock();
        try {
            int state_0 = this.state_0_;
            int exclude = this.exclude_;
            if (((exclude & 0b1)) == 0 /* is-not-exclude writeLong(VirtualFrame, long) */ && valueNodeValue instanceof Long) {
                long valueNodeValue_ = (long) valueNodeValue;
                if ((isLongOrIllegal(frameValue))) {
                    this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 writeLong(VirtualFrame, long) */;
                    lock.unlock();
                    hasLock = false;
                    return writeLong(frameValue, valueNodeValue_);
                }
            }
            if (((exclude & 0b10)) == 0 /* is-not-exclude writeBoolean(VirtualFrame, boolean) */ && valueNodeValue instanceof Boolean) {
                boolean valueNodeValue_ = (boolean) valueNodeValue;
                if ((isBooleanOrIllegal(frameValue))) {
                    this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 writeBoolean(VirtualFrame, boolean) */;
                    lock.unlock();
                    hasLock = false;
                    return writeBoolean(frameValue, valueNodeValue_);
                }
            }
            this.exclude_ = exclude = exclude | 0b11 /* add-exclude writeLong(VirtualFrame, long), writeBoolean(VirtualFrame, boolean) */;
            state_0 = state_0 & 0xfffffffc /* remove-state_0 writeLong(VirtualFrame, long), writeBoolean(VirtualFrame, boolean) */;
            this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 write(VirtualFrame, Object) */;
            lock.unlock();
            hasLock = false;
            return write(frameValue, valueNodeValue);
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
                return NodeCost.MONOMORPHIC;
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    public static SLWriteLocalVariableNode create(SLExpressionNode valueNode, FrameSlot slot, SLExpressionNode nameNode, boolean declaration) {
        return new SLWriteLocalVariableNodeGen(valueNode, slot, nameNode, declaration);
    }

}

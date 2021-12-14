// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.local;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.sl.nodes.SLTypesGen;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLReadLocalVariableNode.class)
public final class SLReadLocalVariableNodeGen extends SLReadLocalVariableNode {

    private final FrameSlot slot;
    @CompilationFinal private int state_0_;
    @CompilationFinal private int exclude_;

    private SLReadLocalVariableNodeGen(FrameSlot slot) {
        this.slot = slot;
    }

    @Override
    protected FrameSlot getSlot() {
        return this.slot;
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        if ((state_0 & 0b1) != 0 /* is-state_0 readLong(VirtualFrame) */) {
            if ((frameValue.isLong(getSlot()))) {
                return readLong(frameValue);
            }
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 readBoolean(VirtualFrame) */) {
            if ((frameValue.isBoolean(getSlot()))) {
                return readBoolean(frameValue);
            }
        }
        if ((state_0 & 0b100) != 0 /* is-state_0 readObject(VirtualFrame) */) {
            return readObject(frameValue);
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(frameValue);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b100) != 0 /* is-state_0 readObject(VirtualFrame) */) {
            return SLTypesGen.expectBoolean(executeGeneric(frameValue));
        }
        if ((state_0 & 0b10) != 0 /* is-state_0 readBoolean(VirtualFrame) */) {
            if ((frameValue.isBoolean(getSlot()))) {
                return readBoolean(frameValue);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectBoolean(executeAndSpecialize(frameValue));
    }

    @Override
    public long executeLong(VirtualFrame frameValue) throws UnexpectedResultException {
        int state_0 = this.state_0_;
        if ((state_0 & 0b100) != 0 /* is-state_0 readObject(VirtualFrame) */) {
            return SLTypesGen.expectLong(executeGeneric(frameValue));
        }
        if ((state_0 & 0b1) != 0 /* is-state_0 readLong(VirtualFrame) */) {
            if ((frameValue.isLong(getSlot()))) {
                return readLong(frameValue);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return SLTypesGen.expectLong(executeAndSpecialize(frameValue));
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        try {
            if ((state_0 & 0b110) == 0 /* only-active readLong(VirtualFrame) */ && (state_0 != 0  /* is-not readLong(VirtualFrame) && readBoolean(VirtualFrame) && readObject(VirtualFrame) */)) {
                executeLong(frameValue);
                return;
            } else if ((state_0 & 0b101) == 0 /* only-active readBoolean(VirtualFrame) */ && (state_0 != 0  /* is-not readLong(VirtualFrame) && readBoolean(VirtualFrame) && readObject(VirtualFrame) */)) {
                executeBoolean(frameValue);
                return;
            }
            executeGeneric(frameValue);
            return;
        } catch (UnexpectedResultException ex) {
            return;
        }
    }

    private Object executeAndSpecialize(VirtualFrame frameValue) {
        Lock lock = getLock();
        boolean hasLock = true;
        lock.lock();
        try {
            int state_0 = this.state_0_;
            int exclude = this.exclude_;
            if (((exclude & 0b1)) == 0 /* is-not-exclude readLong(VirtualFrame) */) {
                if ((frameValue.isLong(getSlot()))) {
                    this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 readLong(VirtualFrame) */;
                    lock.unlock();
                    hasLock = false;
                    return readLong(frameValue);
                }
            }
            if (((exclude & 0b10)) == 0 /* is-not-exclude readBoolean(VirtualFrame) */) {
                if ((frameValue.isBoolean(getSlot()))) {
                    this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 readBoolean(VirtualFrame) */;
                    lock.unlock();
                    hasLock = false;
                    return readBoolean(frameValue);
                }
            }
            this.exclude_ = exclude = exclude | 0b11 /* add-exclude readLong(VirtualFrame), readBoolean(VirtualFrame) */;
            state_0 = state_0 & 0xfffffffc /* remove-state_0 readLong(VirtualFrame), readBoolean(VirtualFrame) */;
            this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 readObject(VirtualFrame) */;
            lock.unlock();
            hasLock = false;
            return readObject(frameValue);
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

    public static SLReadLocalVariableNode create(FrameSlot slot) {
        return new SLReadLocalVariableNodeGen(slot);
    }

}

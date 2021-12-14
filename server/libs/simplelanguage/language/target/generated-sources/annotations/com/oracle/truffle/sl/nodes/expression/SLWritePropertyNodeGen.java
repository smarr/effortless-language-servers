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
import com.oracle.truffle.sl.nodes.SLExpressionNode;
import com.oracle.truffle.sl.nodes.util.SLToMemberNode;
import com.oracle.truffle.sl.nodes.util.SLToMemberNodeGen;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLWritePropertyNode.class)
@SuppressWarnings("unused")
public final class SLWritePropertyNodeGen extends SLWritePropertyNode {

    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);

    @Child private SLExpressionNode receiverNode_;
    @Child private SLExpressionNode nameNode_;
    @Child private SLExpressionNode valueNode_;
    @CompilationFinal private volatile int state_0_;
    @CompilationFinal private volatile int exclude_;
    @Child private WriteArray0Data writeArray0_cache;
    @Child private WriteObject0Data writeObject0_cache;
    @Child private SLToMemberNode writeObject1_asMember_;

    private SLWritePropertyNodeGen(SLExpressionNode receiverNode, SLExpressionNode nameNode, SLExpressionNode valueNode) {
        this.receiverNode_ = receiverNode;
        this.nameNode_ = nameNode;
        this.valueNode_ = valueNode;
    }

    @ExplodeLoop
    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        Object receiverNodeValue_ = this.receiverNode_.executeGeneric(frameValue);
        Object nameNodeValue_ = this.nameNode_.executeGeneric(frameValue);
        Object valueNodeValue_ = this.valueNode_.executeGeneric(frameValue);
        if (state_0 != 0 /* is-state_0 writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) || writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) || writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) || writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */) {
            if ((state_0 & 0b1) != 0 /* is-state_0 writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */) {
                WriteArray0Data s0_ = this.writeArray0_cache;
                while (s0_ != null) {
                    if ((s0_.arrays_.accepts(receiverNodeValue_)) && (s0_.numbers_.accepts(nameNodeValue_)) && (s0_.arrays_.hasArrayElements(receiverNodeValue_))) {
                        return writeArray(receiverNodeValue_, nameNodeValue_, valueNodeValue_, s0_.arrays_, s0_.numbers_);
                    }
                    s0_ = s0_.next_;
                }
            }
            if ((state_0 & 0b10) != 0 /* is-state_0 writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */) {
                EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                Node prev_ = encapsulating_.set(this);
                try {
                    {
                        InteropLibrary writeArray1_arrays__ = (INTEROP_LIBRARY_.getUncached());
                        if ((writeArray1_arrays__.hasArrayElements(receiverNodeValue_))) {
                            return this.writeArray1Boundary(state_0, receiverNodeValue_, nameNodeValue_, valueNodeValue_);
                        }
                    }
                } finally {
                    encapsulating_.set(prev_);
                }
            }
            if ((state_0 & 0b100) != 0 /* is-state_0 writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */) {
                WriteObject0Data s2_ = this.writeObject0_cache;
                while (s2_ != null) {
                    if ((s2_.objectLibrary_.accepts(receiverNodeValue_))) {
                        return writeObject(receiverNodeValue_, nameNodeValue_, valueNodeValue_, s2_.objectLibrary_, s2_.asMember_);
                    }
                    s2_ = s2_.next_;
                }
            }
            if ((state_0 & 0b1000) != 0 /* is-state_0 writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */) {
                return this.writeObject1Boundary(state_0, receiverNodeValue_, nameNodeValue_, valueNodeValue_);
            }
        }
        CompilerDirectives.transferToInterpreterAndInvalidate();
        return executeAndSpecialize(receiverNodeValue_, nameNodeValue_, valueNodeValue_);
    }

    @SuppressWarnings("static-method")
    @TruffleBoundary
    private Object writeArray1Boundary(int state_0, Object receiverNodeValue_, Object nameNodeValue_, Object valueNodeValue_) {
        {
            InteropLibrary writeArray1_arrays__ = (INTEROP_LIBRARY_.getUncached());
            InteropLibrary writeArray1_numbers__ = (INTEROP_LIBRARY_.getUncached());
            return writeArray(receiverNodeValue_, nameNodeValue_, valueNodeValue_, writeArray1_arrays__, writeArray1_numbers__);
        }
    }

    @SuppressWarnings("static-method")
    @TruffleBoundary
    private Object writeObject1Boundary(int state_0, Object receiverNodeValue_, Object nameNodeValue_, Object valueNodeValue_) {
        EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
        Node prev_ = encapsulating_.set(this);
        try {
            {
                InteropLibrary writeObject1_objectLibrary__ = (INTEROP_LIBRARY_.getUncached(receiverNodeValue_));
                return writeObject(receiverNodeValue_, nameNodeValue_, valueNodeValue_, writeObject1_objectLibrary__, this.writeObject1_asMember_);
            }
        } finally {
            encapsulating_.set(prev_);
        }
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        executeGeneric(frameValue);
        return;
    }

    private Object executeAndSpecialize(Object receiverNodeValue, Object nameNodeValue, Object valueNodeValue) {
        Lock lock = getLock();
        boolean hasLock = true;
        lock.lock();
        try {
            int state_0 = this.state_0_;
            int exclude = this.exclude_;
            if (((exclude & 0b1)) == 0 /* is-not-exclude writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */) {
                int count0_ = 0;
                WriteArray0Data s0_ = this.writeArray0_cache;
                if ((state_0 & 0b1) != 0 /* is-state_0 writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */) {
                    while (s0_ != null) {
                        if ((s0_.arrays_.accepts(receiverNodeValue)) && (s0_.numbers_.accepts(nameNodeValue)) && (s0_.arrays_.hasArrayElements(receiverNodeValue))) {
                            break;
                        }
                        s0_ = s0_.next_;
                        count0_++;
                    }
                }
                if (s0_ == null) {
                    {
                        InteropLibrary arrays__ = super.insert((INTEROP_LIBRARY_.create(receiverNodeValue)));
                        // assert (s0_.arrays_.accepts(receiverNodeValue));
                        // assert (s0_.numbers_.accepts(nameNodeValue));
                        if ((arrays__.hasArrayElements(receiverNodeValue)) && count0_ < (SLWritePropertyNode.LIBRARY_LIMIT)) {
                            s0_ = super.insert(new WriteArray0Data(writeArray0_cache));
                            s0_.arrays_ = s0_.insertAccessor(arrays__);
                            s0_.numbers_ = s0_.insertAccessor((INTEROP_LIBRARY_.create(nameNodeValue)));
                            MemoryFence.storeStore();
                            this.writeArray0_cache = s0_;
                            this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */;
                        }
                    }
                }
                if (s0_ != null) {
                    lock.unlock();
                    hasLock = false;
                    return writeArray(receiverNodeValue, nameNodeValue, valueNodeValue, s0_.arrays_, s0_.numbers_);
                }
            }
            {
                InteropLibrary writeArray1_numbers__ = null;
                InteropLibrary writeArray1_arrays__ = null;
                {
                    EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                    Node prev_ = encapsulating_.set(this);
                    try {
                        {
                            writeArray1_arrays__ = (INTEROP_LIBRARY_.getUncached());
                            if ((writeArray1_arrays__.hasArrayElements(receiverNodeValue))) {
                                writeArray1_numbers__ = (INTEROP_LIBRARY_.getUncached());
                                this.exclude_ = exclude = exclude | 0b1 /* add-exclude writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */;
                                this.writeArray0_cache = null;
                                state_0 = state_0 & 0xfffffffe /* remove-state_0 writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */;
                                this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 writeArray(Object, Object, Object, InteropLibrary, InteropLibrary) */;
                                lock.unlock();
                                hasLock = false;
                                return writeArray(receiverNodeValue, nameNodeValue, valueNodeValue, writeArray1_arrays__, writeArray1_numbers__);
                            }
                        }
                    } finally {
                        encapsulating_.set(prev_);
                    }
                }
            }
            if (((exclude & 0b10)) == 0 /* is-not-exclude writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */) {
                int count2_ = 0;
                WriteObject0Data s2_ = this.writeObject0_cache;
                if ((state_0 & 0b100) != 0 /* is-state_0 writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */) {
                    while (s2_ != null) {
                        if ((s2_.objectLibrary_.accepts(receiverNodeValue))) {
                            break;
                        }
                        s2_ = s2_.next_;
                        count2_++;
                    }
                }
                if (s2_ == null) {
                    // assert (s2_.objectLibrary_.accepts(receiverNodeValue));
                    if (count2_ < (SLWritePropertyNode.LIBRARY_LIMIT)) {
                        s2_ = super.insert(new WriteObject0Data(writeObject0_cache));
                        s2_.objectLibrary_ = s2_.insertAccessor((INTEROP_LIBRARY_.create(receiverNodeValue)));
                        s2_.asMember_ = s2_.insertAccessor((SLToMemberNodeGen.create()));
                        MemoryFence.storeStore();
                        this.writeObject0_cache = s2_;
                        this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */;
                    }
                }
                if (s2_ != null) {
                    lock.unlock();
                    hasLock = false;
                    return writeObject(receiverNodeValue, nameNodeValue, valueNodeValue, s2_.objectLibrary_, s2_.asMember_);
                }
            }
            {
                InteropLibrary writeObject1_objectLibrary__ = null;
                {
                    EncapsulatingNodeReference encapsulating_ = EncapsulatingNodeReference.getCurrent();
                    Node prev_ = encapsulating_.set(this);
                    try {
                        writeObject1_objectLibrary__ = (INTEROP_LIBRARY_.getUncached(receiverNodeValue));
                        this.writeObject1_asMember_ = super.insert((SLToMemberNodeGen.create()));
                        this.exclude_ = exclude = exclude | 0b10 /* add-exclude writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */;
                        this.writeObject0_cache = null;
                        state_0 = state_0 & 0xfffffffb /* remove-state_0 writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */;
                        this.state_0_ = state_0 = state_0 | 0b1000 /* add-state_0 writeObject(Object, Object, Object, InteropLibrary, SLToMemberNode) */;
                        lock.unlock();
                        hasLock = false;
                        return writeObject(receiverNodeValue, nameNodeValue, valueNodeValue, writeObject1_objectLibrary__, this.writeObject1_asMember_);
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
        if (state_0 == 0) {
            return NodeCost.UNINITIALIZED;
        } else {
            if ((state_0 & (state_0 - 1)) == 0 /* is-single-state_0  */) {
                WriteArray0Data s0_ = this.writeArray0_cache;
                WriteObject0Data s2_ = this.writeObject0_cache;
                if ((s0_ == null || s0_.next_ == null) && (s2_ == null || s2_.next_ == null)) {
                    return NodeCost.MONOMORPHIC;
                }
            }
        }
        return NodeCost.POLYMORPHIC;
    }

    public static SLWritePropertyNode create(SLExpressionNode receiverNode, SLExpressionNode nameNode, SLExpressionNode valueNode) {
        return new SLWritePropertyNodeGen(receiverNode, nameNode, valueNode);
    }

    @GeneratedBy(SLWritePropertyNode.class)
    private static final class WriteArray0Data extends Node {

        @Child WriteArray0Data next_;
        @Child InteropLibrary arrays_;
        @Child InteropLibrary numbers_;

        WriteArray0Data(WriteArray0Data next_) {
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
    @GeneratedBy(SLWritePropertyNode.class)
    private static final class WriteObject0Data extends Node {

        @Child WriteObject0Data next_;
        @Child InteropLibrary objectLibrary_;
        @Child SLToMemberNode asMember_;

        WriteObject0Data(WriteObject0Data next_) {
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

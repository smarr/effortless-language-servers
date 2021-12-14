// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.local;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.DynamicDispatchLibrary;
import com.oracle.truffle.api.library.LibraryExport;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.memory.MemoryFence;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.sl.nodes.controlflow.SLBlockNode;
import com.oracle.truffle.sl.nodes.local.SLScopedNode.VariablesObject;
import com.oracle.truffle.sl.nodes.local.SLScopedNode.VariablesObject.ExistsMember;
import com.oracle.truffle.sl.nodes.local.SLScopedNode.VariablesObject.ModifiableMember;
import com.oracle.truffle.sl.nodes.local.SLScopedNode.VariablesObject.ReadMember;
import com.oracle.truffle.sl.nodes.local.SLScopedNode.VariablesObject.WriteMember;
import java.util.concurrent.locks.Lock;

@GeneratedBy(VariablesObject.class)
final class VariablesObjectGen {

    private static final LibraryFactory<DynamicDispatchLibrary> DYNAMIC_DISPATCH_LIBRARY_ = LibraryFactory.resolve(DynamicDispatchLibrary.class);

    static  {
        LibraryExport.register(VariablesObject.class, new InteropLibraryExports());
    }

    private VariablesObjectGen() {
    }

    @GeneratedBy(VariablesObject.class)
    private static final class InteropLibraryExports extends LibraryExport<InteropLibrary> {

        private InteropLibraryExports() {
            super(InteropLibrary.class, VariablesObject.class, false, false, 0);
        }

        @Override
        protected InteropLibrary createUncached(Object receiver) {
            assert receiver instanceof VariablesObject;
            InteropLibrary uncached = new Uncached();
            return uncached;
        }

        @Override
        protected InteropLibrary createCached(Object receiver) {
            assert receiver instanceof VariablesObject;
            return new Cached(receiver);
        }

        @GeneratedBy(VariablesObject.class)
        private static final class Cached extends InteropLibrary {

            @CompilationFinal private volatile int state_0_;
            @CompilationFinal private volatile int exclude_;
            @CompilationFinal private SLBlockNode block;
            @CompilationFinal private Node parentBlock;
            @CompilationFinal private ExistsMemberCachedData existsMember_cached_cache;
            @CompilationFinal private ModifiableMemberCachedData modifiableMember_cached_cache;
            @CompilationFinal private ReadMemberCachedData readMember_cached_cache;
            @Child private WriteMemberCachedData writeMember_cached_cache;
            @CompilationFinal private SLScopedNode acceptsNode__accepts_cachedNode_;
            @CompilationFinal private boolean acceptsNode__accepts_cachedNodeEnter_;
            @Child private GetMembersNode_GetMembersData getMembersNode__getMembers_cache;

            protected Cached(Object receiver) {
                VariablesObject castReceiver = ((VariablesObject) receiver) ;
                this.acceptsNode__accepts_cachedNode_ = (castReceiver.node);
                this.acceptsNode__accepts_cachedNodeEnter_ = (castReceiver.nodeEnter);
            }

            @Override
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof VariablesObject) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof VariablesObject && accepts_(receiver);
            }

            @ExplodeLoop
            @Override
            public boolean isMemberReadable(Object arg0Value_, String arg1Value) {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b11) != 0 /* is-state_0 doCached(VariablesObject, String, String, boolean) || doGeneric(VariablesObject, String) */) {
                    if ((state_0 & 0b1) != 0 /* is-state_0 doCached(VariablesObject, String, String, boolean) */) {
                        ExistsMemberCachedData s0_ = this.existsMember_cached_cache;
                        while (s0_ != null) {
                            if ((s0_.cachedMember_.equals(arg1Value))) {
                                return ExistsMember.doCached(arg0Value, arg1Value, s0_.cachedMember_, s0_.cachedResult_);
                            }
                            s0_ = s0_.next_;
                        }
                    }
                    if ((state_0 & 0b10) != 0 /* is-state_0 doGeneric(VariablesObject, String) */) {
                        return ExistsMember.doGeneric(arg0Value, arg1Value);
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return existsMemberAndSpecialize(arg0Value, arg1Value);
            }

            private boolean existsMemberAndSpecialize(VariablesObject arg0Value, String arg1Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    int exclude = this.exclude_;
                    if (((exclude & 0b1)) == 0 /* is-not-exclude doCached(VariablesObject, String, String, boolean) */) {
                        int count0_ = 0;
                        ExistsMemberCachedData s0_ = this.existsMember_cached_cache;
                        if ((state_0 & 0b1) != 0 /* is-state_0 doCached(VariablesObject, String, String, boolean) */) {
                            while (s0_ != null) {
                                if ((s0_.cachedMember_.equals(arg1Value))) {
                                    break;
                                }
                                s0_ = s0_.next_;
                                count0_++;
                            }
                        }
                        if (s0_ == null) {
                            // assert (s0_.cachedMember_.equals(arg1Value));
                            if (count0_ < (VariablesObject.LIMIT)) {
                                s0_ = new ExistsMemberCachedData(existsMember_cached_cache);
                                s0_.cachedMember_ = (arg1Value);
                                s0_.cachedResult_ = (ExistsMember.doGeneric(arg0Value, arg1Value));
                                MemoryFence.storeStore();
                                this.existsMember_cached_cache = s0_;
                                this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 doCached(VariablesObject, String, String, boolean) */;
                            }
                        }
                        if (s0_ != null) {
                            lock.unlock();
                            hasLock = false;
                            return ExistsMember.doCached(arg0Value, arg1Value, s0_.cachedMember_, s0_.cachedResult_);
                        }
                    }
                    this.exclude_ = exclude = exclude | 0b1 /* add-exclude doCached(VariablesObject, String, String, boolean) */;
                    this.existsMember_cached_cache = null;
                    state_0 = state_0 & 0xfffffffe /* remove-state_0 doCached(VariablesObject, String, String, boolean) */;
                    this.state_0_ = state_0 = state_0 | 0b10 /* add-state_0 doGeneric(VariablesObject, String) */;
                    lock.unlock();
                    hasLock = false;
                    return ExistsMember.doGeneric(arg0Value, arg1Value);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @Override
            public NodeCost getCost() {
                int state_0 = this.state_0_;
                if ((state_0 & 0b11) == 0) {
                    return NodeCost.UNINITIALIZED;
                } else {
                    if (((state_0 & 0b11) & ((state_0 & 0b11) - 1)) == 0 /* is-single-state_0  */) {
                        ExistsMemberCachedData s0_ = this.existsMember_cached_cache;
                        if ((s0_ == null || s0_.next_ == null)) {
                            return NodeCost.MONOMORPHIC;
                        }
                    }
                }
                return NodeCost.POLYMORPHIC;
            }

            @ExplodeLoop
            @Override
            public boolean isMemberModifiable(Object arg0Value_, String arg1Value) {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b1100) != 0 /* is-state_0 doCached(VariablesObject, String, String, boolean) || doGeneric(VariablesObject, String) */) {
                    if ((state_0 & 0b100) != 0 /* is-state_0 doCached(VariablesObject, String, String, boolean) */) {
                        ModifiableMemberCachedData s0_ = this.modifiableMember_cached_cache;
                        while (s0_ != null) {
                            if ((s0_.cachedMember_.equals(arg1Value))) {
                                return ModifiableMember.doCached(arg0Value, arg1Value, s0_.cachedMember_, s0_.cachedResult_);
                            }
                            s0_ = s0_.next_;
                        }
                    }
                    if ((state_0 & 0b1000) != 0 /* is-state_0 doGeneric(VariablesObject, String) */) {
                        return ModifiableMember.doGeneric(arg0Value, arg1Value);
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return modifiableMemberAndSpecialize(arg0Value, arg1Value);
            }

            private boolean modifiableMemberAndSpecialize(VariablesObject arg0Value, String arg1Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    int exclude = this.exclude_;
                    if (((exclude & 0b10)) == 0 /* is-not-exclude doCached(VariablesObject, String, String, boolean) */) {
                        int count0_ = 0;
                        ModifiableMemberCachedData s0_ = this.modifiableMember_cached_cache;
                        if ((state_0 & 0b100) != 0 /* is-state_0 doCached(VariablesObject, String, String, boolean) */) {
                            while (s0_ != null) {
                                if ((s0_.cachedMember_.equals(arg1Value))) {
                                    break;
                                }
                                s0_ = s0_.next_;
                                count0_++;
                            }
                        }
                        if (s0_ == null) {
                            // assert (s0_.cachedMember_.equals(arg1Value));
                            if (count0_ < (VariablesObject.LIMIT)) {
                                s0_ = new ModifiableMemberCachedData(modifiableMember_cached_cache);
                                s0_.cachedMember_ = (arg1Value);
                                s0_.cachedResult_ = (arg0Value.hasWriteNode(arg1Value));
                                MemoryFence.storeStore();
                                this.modifiableMember_cached_cache = s0_;
                                this.state_0_ = state_0 = state_0 | 0b100 /* add-state_0 doCached(VariablesObject, String, String, boolean) */;
                            }
                        }
                        if (s0_ != null) {
                            lock.unlock();
                            hasLock = false;
                            return ModifiableMember.doCached(arg0Value, arg1Value, s0_.cachedMember_, s0_.cachedResult_);
                        }
                    }
                    this.exclude_ = exclude = exclude | 0b10 /* add-exclude doCached(VariablesObject, String, String, boolean) */;
                    this.modifiableMember_cached_cache = null;
                    state_0 = state_0 & 0xfffffffb /* remove-state_0 doCached(VariablesObject, String, String, boolean) */;
                    this.state_0_ = state_0 = state_0 | 0b1000 /* add-state_0 doGeneric(VariablesObject, String) */;
                    lock.unlock();
                    hasLock = false;
                    return ModifiableMember.doGeneric(arg0Value, arg1Value);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @ExplodeLoop
            @Override
            public Object readMember(Object arg0Value_, String arg1Value) throws UnsupportedMessageException, UnknownIdentifierException {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b110000) != 0 /* is-state_0 doCached(VariablesObject, String, String, FrameSlot) || doGeneric(VariablesObject, String) */) {
                    if ((state_0 & 0b10000) != 0 /* is-state_0 doCached(VariablesObject, String, String, FrameSlot) */) {
                        ReadMemberCachedData s0_ = this.readMember_cached_cache;
                        while (s0_ != null) {
                            if ((s0_.cachedMember_.equals(arg1Value))) {
                                return ReadMember.doCached(arg0Value, arg1Value, s0_.cachedMember_, s0_.slot_);
                            }
                            s0_ = s0_.next_;
                        }
                    }
                    if ((state_0 & 0b100000) != 0 /* is-state_0 doGeneric(VariablesObject, String) */) {
                        return ReadMember.doGeneric(arg0Value, arg1Value);
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return readMemberAndSpecialize(arg0Value, arg1Value);
            }

            private Object readMemberAndSpecialize(VariablesObject arg0Value, String arg1Value) throws UnknownIdentifierException {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    int exclude = this.exclude_;
                    if (((exclude & 0b100)) == 0 /* is-not-exclude doCached(VariablesObject, String, String, FrameSlot) */) {
                        int count0_ = 0;
                        ReadMemberCachedData s0_ = this.readMember_cached_cache;
                        if ((state_0 & 0b10000) != 0 /* is-state_0 doCached(VariablesObject, String, String, FrameSlot) */) {
                            while (s0_ != null) {
                                if ((s0_.cachedMember_.equals(arg1Value))) {
                                    break;
                                }
                                s0_ = s0_.next_;
                                count0_++;
                            }
                        }
                        if (s0_ == null) {
                            // assert (s0_.cachedMember_.equals(arg1Value));
                            if (count0_ < (VariablesObject.LIMIT)) {
                                s0_ = new ReadMemberCachedData(readMember_cached_cache);
                                s0_.cachedMember_ = (arg1Value);
                                s0_.slot_ = (arg0Value.findSlot(arg1Value));
                                MemoryFence.storeStore();
                                this.readMember_cached_cache = s0_;
                                this.state_0_ = state_0 = state_0 | 0b10000 /* add-state_0 doCached(VariablesObject, String, String, FrameSlot) */;
                            }
                        }
                        if (s0_ != null) {
                            lock.unlock();
                            hasLock = false;
                            return ReadMember.doCached(arg0Value, arg1Value, s0_.cachedMember_, s0_.slot_);
                        }
                    }
                    this.exclude_ = exclude = exclude | 0b100 /* add-exclude doCached(VariablesObject, String, String, FrameSlot) */;
                    this.readMember_cached_cache = null;
                    state_0 = state_0 & 0xffffffef /* remove-state_0 doCached(VariablesObject, String, String, FrameSlot) */;
                    this.state_0_ = state_0 = state_0 | 0b100000 /* add-state_0 doGeneric(VariablesObject, String) */;
                    lock.unlock();
                    hasLock = false;
                    return ReadMember.doGeneric(arg0Value, arg1Value);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @ExplodeLoop
            @Override
            public void writeMember(Object arg0Value_, String arg1Value, Object arg2Value) throws UnsupportedMessageException, UnknownIdentifierException, UnsupportedTypeException {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b11000000) != 0 /* is-state_0 doCached(VariablesObject, String, Object, String, SLWriteLocalVariableNode) || doGeneric(VariablesObject, String, Object) */) {
                    if ((state_0 & 0b1000000) != 0 /* is-state_0 doCached(VariablesObject, String, Object, String, SLWriteLocalVariableNode) */) {
                        WriteMemberCachedData s0_ = this.writeMember_cached_cache;
                        while (s0_ != null) {
                            if ((s0_.cachedMember_.equals(arg1Value))) {
                                WriteMember.doCached(arg0Value, arg1Value, arg2Value, s0_.cachedMember_, s0_.writeNode_);
                                return;
                            }
                            s0_ = s0_.next_;
                        }
                    }
                    if ((state_0 & 0b10000000) != 0 /* is-state_0 doGeneric(VariablesObject, String, Object) */) {
                        WriteMember.doGeneric(arg0Value, arg1Value, arg2Value);
                        return;
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                writeMemberAndSpecialize(arg0Value, arg1Value, arg2Value);
                return;
            }

            private void writeMemberAndSpecialize(VariablesObject arg0Value, String arg1Value, Object arg2Value) throws UnknownIdentifierException, UnsupportedMessageException {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    int exclude = this.exclude_;
                    if (((exclude & 0b1000)) == 0 /* is-not-exclude doCached(VariablesObject, String, Object, String, SLWriteLocalVariableNode) */) {
                        int count0_ = 0;
                        WriteMemberCachedData s0_ = this.writeMember_cached_cache;
                        if ((state_0 & 0b1000000) != 0 /* is-state_0 doCached(VariablesObject, String, Object, String, SLWriteLocalVariableNode) */) {
                            while (s0_ != null) {
                                if ((s0_.cachedMember_.equals(arg1Value))) {
                                    break;
                                }
                                s0_ = s0_.next_;
                                count0_++;
                            }
                        }
                        if (s0_ == null) {
                            // assert (s0_.cachedMember_.equals(arg1Value));
                            if (count0_ < (VariablesObject.LIMIT)) {
                                s0_ = super.insert(new WriteMemberCachedData(writeMember_cached_cache));
                                s0_.cachedMember_ = (arg1Value);
                                s0_.writeNode_ = (arg0Value.findWriteNode(arg1Value));
                                MemoryFence.storeStore();
                                this.writeMember_cached_cache = s0_;
                                this.state_0_ = state_0 = state_0 | 0b1000000 /* add-state_0 doCached(VariablesObject, String, Object, String, SLWriteLocalVariableNode) */;
                            }
                        }
                        if (s0_ != null) {
                            lock.unlock();
                            hasLock = false;
                            WriteMember.doCached(arg0Value, arg1Value, arg2Value, s0_.cachedMember_, s0_.writeNode_);
                            return;
                        }
                    }
                    this.exclude_ = exclude = exclude | 0b1000 /* add-exclude doCached(VariablesObject, String, Object, String, SLWriteLocalVariableNode) */;
                    this.writeMember_cached_cache = null;
                    state_0 = state_0 & 0xffffffbf /* remove-state_0 doCached(VariablesObject, String, Object, String, SLWriteLocalVariableNode) */;
                    this.state_0_ = state_0 = state_0 | 0b10000000 /* add-state_0 doGeneric(VariablesObject, String, Object) */;
                    lock.unlock();
                    hasLock = false;
                    WriteMember.doGeneric(arg0Value, arg1Value, arg2Value);
                    return;
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            private boolean accepts_(Object arg0Value_) {
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return arg0Value.accepts(this.acceptsNode__accepts_cachedNode_, this.acceptsNode__accepts_cachedNodeEnter_);
            }

            @Override
            public boolean isScope(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((VariablesObject) receiver)).isScope();
            }

            @Override
            public boolean hasLanguage(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((VariablesObject) receiver)).hasLanguage();
            }

            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((VariablesObject) receiver)).getLanguage();
            }

            @Override
            public Object toDisplayString(Object arg0Value_, boolean arg1Value) {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b100000000) != 0 /* is-state_0 toDisplayString(VariablesObject, boolean, SLBlockNode, Node) */) {
                    return arg0Value.toDisplayString(arg1Value, this.block, this.parentBlock);
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return toDisplayStringNode_AndSpecialize(arg0Value, arg1Value);
            }

            private Object toDisplayStringNode_AndSpecialize(VariablesObject arg0Value, boolean arg1Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    this.block = this.block == null ? ((arg0Value.block)) : this.block;
                    this.parentBlock = this.parentBlock == null ? ((arg0Value.block.findBlock())) : this.parentBlock;
                    this.state_0_ = state_0 = state_0 | 0b100000000 /* add-state_0 toDisplayString(VariablesObject, boolean, SLBlockNode, Node) */;
                    lock.unlock();
                    hasLock = false;
                    return arg0Value.toDisplayString(arg1Value, this.block, this.parentBlock);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @Override
            public boolean hasScopeParent(Object arg0Value_) {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b1000000000) != 0 /* is-state_0 hasScopeParent(VariablesObject, SLBlockNode, Node) */) {
                    return arg0Value.hasScopeParent(this.block, this.parentBlock);
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return hasScopeParentNode_AndSpecialize(arg0Value);
            }

            private boolean hasScopeParentNode_AndSpecialize(VariablesObject arg0Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    this.block = this.block == null ? ((arg0Value.block)) : this.block;
                    this.parentBlock = this.parentBlock == null ? ((arg0Value.block.findBlock())) : this.parentBlock;
                    this.state_0_ = state_0 = state_0 | 0b1000000000 /* add-state_0 hasScopeParent(VariablesObject, SLBlockNode, Node) */;
                    lock.unlock();
                    hasLock = false;
                    return arg0Value.hasScopeParent(this.block, this.parentBlock);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @Override
            public Object getScopeParent(Object arg0Value_) throws UnsupportedMessageException {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b10000000000) != 0 /* is-state_0 getScopeParent(VariablesObject, SLBlockNode, Node) */) {
                    return arg0Value.getScopeParent(this.block, this.parentBlock);
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return getScopeParentNode_AndSpecialize(arg0Value);
            }

            private Object getScopeParentNode_AndSpecialize(VariablesObject arg0Value) throws UnsupportedMessageException {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    this.block = this.block == null ? ((arg0Value.block)) : this.block;
                    this.parentBlock = this.parentBlock == null ? ((arg0Value.block.findBlock())) : this.parentBlock;
                    this.state_0_ = state_0 = state_0 | 0b10000000000 /* add-state_0 getScopeParent(VariablesObject, SLBlockNode, Node) */;
                    lock.unlock();
                    hasLock = false;
                    return arg0Value.getScopeParent(this.block, this.parentBlock);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @Override
            public boolean hasSourceLocation(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((VariablesObject) receiver)).hasSourceLocation();
            }

            @Override
            public SourceSection getSourceLocation(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((VariablesObject) receiver)).getSourceLocation();
            }

            @Override
            public boolean hasMembers(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((VariablesObject) receiver)).hasMembers();
            }

            @Override
            public boolean isMemberInsertable(Object receiver, String member) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                return (((VariablesObject) receiver)).isMemberInsertable(member);
            }

            @Override
            public Object getMembers(Object arg0Value_, boolean arg1Value) throws UnsupportedMessageException {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert assertAdopted();
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                int state_0 = this.state_0_;
                if ((state_0 & 0b100000000000) != 0 /* is-state_0 getMembers(VariablesObject, boolean, SLWriteLocalVariableNode[], int, int) */) {
                    GetMembersNode_GetMembersData s0_ = this.getMembersNode__getMembers_cache;
                    if (s0_ != null) {
                        return arg0Value.getMembers(arg1Value, s0_.writeNodes_, s0_.visibleVariablesIndex_, s0_.parentBlockIndex_);
                    }
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return getMembersNode_AndSpecialize(arg0Value, arg1Value);
            }

            private Object getMembersNode_AndSpecialize(VariablesObject arg0Value, boolean arg1Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    GetMembersNode_GetMembersData s0_ = super.insert(new GetMembersNode_GetMembersData());
                    s0_.writeNodes_ = (arg0Value.block.getDeclaredLocalVariables());
                    s0_.visibleVariablesIndex_ = (arg0Value.getVisibleVariablesIndex());
                    s0_.parentBlockIndex_ = (arg0Value.block.getParentBlockIndex());
                    MemoryFence.storeStore();
                    this.getMembersNode__getMembers_cache = s0_;
                    this.state_0_ = state_0 = state_0 | 0b100000000000 /* add-state_0 getMembers(VariablesObject, boolean, SLWriteLocalVariableNode[], int, int) */;
                    lock.unlock();
                    hasLock = false;
                    return arg0Value.getMembers(arg1Value, s0_.writeNodes_, s0_.visibleVariablesIndex_, s0_.parentBlockIndex_);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @GeneratedBy(VariablesObject.class)
            private static final class ExistsMemberCachedData {

                @CompilationFinal ExistsMemberCachedData next_;
                @CompilationFinal String cachedMember_;
                @CompilationFinal boolean cachedResult_;

                ExistsMemberCachedData(ExistsMemberCachedData next_) {
                    this.next_ = next_;
                }

            }
            @GeneratedBy(VariablesObject.class)
            private static final class ModifiableMemberCachedData {

                @CompilationFinal ModifiableMemberCachedData next_;
                @CompilationFinal String cachedMember_;
                @CompilationFinal boolean cachedResult_;

                ModifiableMemberCachedData(ModifiableMemberCachedData next_) {
                    this.next_ = next_;
                }

            }
            @GeneratedBy(VariablesObject.class)
            private static final class ReadMemberCachedData {

                @CompilationFinal ReadMemberCachedData next_;
                @CompilationFinal String cachedMember_;
                @CompilationFinal FrameSlot slot_;

                ReadMemberCachedData(ReadMemberCachedData next_) {
                    this.next_ = next_;
                }

            }
            @GeneratedBy(VariablesObject.class)
            private static final class WriteMemberCachedData extends Node {

                @Child WriteMemberCachedData next_;
                @CompilationFinal String cachedMember_;
                @CompilationFinal SLWriteLocalVariableNode writeNode_;

                WriteMemberCachedData(WriteMemberCachedData next_) {
                    this.next_ = next_;
                }

                @Override
                public NodeCost getCost() {
                    return NodeCost.NONE;
                }

            }
            @GeneratedBy(VariablesObject.class)
            private static final class GetMembersNode_GetMembersData extends Node {

                @CompilationFinal(dimensions = 1) SLWriteLocalVariableNode[] writeNodes_;
                @CompilationFinal int visibleVariablesIndex_;
                @CompilationFinal int parentBlockIndex_;

                GetMembersNode_GetMembersData() {
                }

                @Override
                public NodeCost getCost() {
                    return NodeCost.NONE;
                }

            }
        }
        @GeneratedBy(VariablesObject.class)
        private static final class Uncached extends InteropLibrary {

            protected Uncached() {
            }

            @Override
            @TruffleBoundary
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof VariablesObject) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof VariablesObject && accepts_(receiver);
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
            public boolean isMemberReadable(Object arg0Value_, String arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return ExistsMember.doGeneric(arg0Value, arg1Value);
            }

            @TruffleBoundary
            @Override
            public boolean isMemberModifiable(Object arg0Value_, String arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return ModifiableMember.doGeneric(arg0Value, arg1Value);
            }

            @TruffleBoundary
            @Override
            public Object readMember(Object arg0Value_, String arg1Value) throws UnknownIdentifierException {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return ReadMember.doGeneric(arg0Value, arg1Value);
            }

            @TruffleBoundary
            @Override
            public void writeMember(Object arg0Value_, String arg1Value, Object arg2Value) throws UnknownIdentifierException, UnsupportedMessageException {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                WriteMember.doGeneric(arg0Value, arg1Value, arg2Value);
                return;
            }

            @TruffleBoundary
            @Override
            public boolean isScope(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((VariablesObject) receiver) .isScope();
            }

            @TruffleBoundary
            @Override
            public boolean hasLanguage(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((VariablesObject) receiver) .hasLanguage();
            }

            @TruffleBoundary
            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((VariablesObject) receiver) .getLanguage();
            }

            @TruffleBoundary
            @Override
            public Object toDisplayString(Object arg0Value_, boolean arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return arg0Value.toDisplayString(arg1Value, (arg0Value.block), (arg0Value.block.findBlock()));
            }

            @TruffleBoundary
            @Override
            public boolean hasScopeParent(Object arg0Value_) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return arg0Value.hasScopeParent((arg0Value.block), (arg0Value.block.findBlock()));
            }

            @TruffleBoundary
            @Override
            public Object getScopeParent(Object arg0Value_) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return arg0Value.getScopeParent((arg0Value.block), (arg0Value.block.findBlock()));
            }

            @TruffleBoundary
            @Override
            public boolean hasSourceLocation(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((VariablesObject) receiver) .hasSourceLocation();
            }

            @TruffleBoundary
            @Override
            public SourceSection getSourceLocation(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((VariablesObject) receiver) .getSourceLocation();
            }

            @TruffleBoundary
            @Override
            public boolean hasMembers(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((VariablesObject) receiver) .hasMembers();
            }

            @TruffleBoundary
            @Override
            public boolean isMemberInsertable(Object receiver, String member) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((VariablesObject) receiver) .isMemberInsertable(member);
            }

            @TruffleBoundary
            @Override
            public Object getMembers(Object arg0Value_, boolean arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return arg0Value.getMembers(arg1Value, (arg0Value.block.getDeclaredLocalVariables()), (arg0Value.getVisibleVariablesIndex()), (arg0Value.block.getParentBlockIndex()));
            }

            @TruffleBoundary
            private static boolean accepts_(Object arg0Value_) {
                VariablesObject arg0Value = ((VariablesObject) arg0Value_);
                return arg0Value.accepts((arg0Value.node), (arg0Value.nodeEnter));
            }

        }
    }
}

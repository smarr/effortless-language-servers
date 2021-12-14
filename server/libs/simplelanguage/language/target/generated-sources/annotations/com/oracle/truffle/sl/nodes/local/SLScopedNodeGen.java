// CheckStyle: start generated
package com.oracle.truffle.sl.nodes.local;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.interop.NodeLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.DynamicDispatchLibrary;
import com.oracle.truffle.api.library.LibraryExport;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import java.util.concurrent.locks.Lock;

@GeneratedBy(SLScopedNode.class)
public final class SLScopedNodeGen {

    private static final LibraryFactory<DynamicDispatchLibrary> DYNAMIC_DISPATCH_LIBRARY_ = LibraryFactory.resolve(DynamicDispatchLibrary.class);

    static  {
        LibraryExport.register(SLScopedNode.class, new NodeLibraryExports());
    }

    private SLScopedNodeGen() {
    }

    @GeneratedBy(SLScopedNode.class)
    public static class NodeLibraryExports extends LibraryExport<NodeLibrary> {

        private NodeLibraryExports() {
            super(NodeLibrary.class, SLScopedNode.class, false, false, 0);
        }

        @Override
        protected NodeLibrary createUncached(Object receiver) {
            assert receiver instanceof SLScopedNode;
            NodeLibrary uncached = new Uncached(receiver);
            return uncached;
        }

        @Override
        protected NodeLibrary createCached(Object receiver) {
            assert receiver instanceof SLScopedNode;
            return new Cached(receiver);
        }

        @GeneratedBy(SLScopedNode.class)
        public static class Cached extends NodeLibrary {

            private final Class<? extends SLScopedNode> receiverClass_;
            @CompilationFinal private volatile int state_0_;
            @CompilationFinal private SLScopedNode node;
            @CompilationFinal private Node getScopeNode__getScope_blockNode_;

            protected Cached(Object receiver) {
                SLScopedNode castReceiver = ((SLScopedNode) receiver) ;
                this.node = (castReceiver);
                this.receiverClass_ = castReceiver.getClass();
            }

            @Override
            public boolean accepts(Object receiver) {
                assert receiver.getClass() != this.receiverClass_ || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return CompilerDirectives.isExact(receiver, this.receiverClass_) && accepts_(receiver);
            }

            private boolean accepts_(Object arg0Value_) {
                SLScopedNode arg0Value = CompilerDirectives.castExact(arg0Value_, receiverClass_);
                return arg0Value.accepts(this.node);
            }

            @Override
            public NodeCost getCost() {
                return NodeCost.MONOMORPHIC;
            }

            @Override
            public boolean hasScope(Object receiver, Frame frame) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert getRootNode() != null : "Invalid libray usage. Cached library must be adopted by a RootNode before it is executed.";
                return (CompilerDirectives.castExact(receiver, receiverClass_)).hasScope(frame);
            }

            @Override
            public Object getScope(Object arg0Value_, Frame arg1Value, boolean arg2Value) throws UnsupportedMessageException {
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                assert getRootNode() != null : "Invalid libray usage. Cached library must be adopted by a RootNode before it is executed.";
                SLScopedNode arg0Value = CompilerDirectives.castExact(arg0Value_, receiverClass_);
                int state_0 = this.state_0_;
                if (state_0 != 0 /* is-state_0 getScope(SLScopedNode, Frame, boolean, SLScopedNode, Node) */) {
                    return arg0Value.getScope(arg1Value, arg2Value, this.node, this.getScopeNode__getScope_blockNode_);
                }
                CompilerDirectives.transferToInterpreterAndInvalidate();
                return getScopeNode_AndSpecialize(arg0Value, arg1Value, arg2Value);
            }

            private Object getScopeNode_AndSpecialize(SLScopedNode arg0Value, Frame arg1Value, boolean arg2Value) {
                Lock lock = getLock();
                boolean hasLock = true;
                lock.lock();
                try {
                    int state_0 = this.state_0_;
                    this.getScopeNode__getScope_blockNode_ = (arg0Value.findBlock());
                    this.state_0_ = state_0 = state_0 | 0b1 /* add-state_0 getScope(SLScopedNode, Frame, boolean, SLScopedNode, Node) */;
                    lock.unlock();
                    hasLock = false;
                    return arg0Value.getScope(arg1Value, arg2Value, this.node, this.getScopeNode__getScope_blockNode_);
                } finally {
                    if (hasLock) {
                        lock.unlock();
                    }
                }
            }

            @Override
            public boolean hasRootInstance(Object receiver, Frame frame) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert getRootNode() != null : "Invalid libray usage. Cached library must be adopted by a RootNode before it is executed.";
                return (CompilerDirectives.castExact(receiver, receiverClass_)).hasRootInstance(frame);
            }

            @Override
            public Object getRootInstance(Object receiver, Frame frame) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                assert getRootNode() != null : "Invalid libray usage. Cached library must be adopted by a RootNode before it is executed.";
                return (CompilerDirectives.castExact(receiver, receiverClass_)).getRootInstance(frame);
            }

        }
        @GeneratedBy(SLScopedNode.class)
        public static class Uncached extends NodeLibrary {

            private final Class<? extends SLScopedNode> receiverClass_;

            protected Uncached(Object receiver) {
                this.receiverClass_ = ((SLScopedNode) receiver).getClass();
            }

            @Override
            @TruffleBoundary
            public boolean accepts(Object receiver) {
                assert receiver.getClass() != this.receiverClass_ || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return CompilerDirectives.isExact(receiver, this.receiverClass_) && accepts_(receiver);
            }

            @Override
            public final boolean isAdoptable() {
                return false;
            }

            @Override
            public final NodeCost getCost() {
                return NodeCost.MEGAMORPHIC;
            }

            @TruffleBoundary
            @Override
            public boolean hasScope(Object receiver, Frame frame) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLScopedNode) receiver) .hasScope(frame);
            }

            @TruffleBoundary
            @Override
            public Object getScope(Object arg0Value_, Frame arg1Value, boolean arg2Value) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                SLScopedNode arg0Value = ((SLScopedNode) arg0Value_);
                return arg0Value.getScope(arg1Value, arg2Value, (arg0Value), (arg0Value.findBlock()));
            }

            @TruffleBoundary
            @Override
            public boolean hasRootInstance(Object receiver, Frame frame) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLScopedNode) receiver) .hasRootInstance(frame);
            }

            @TruffleBoundary
            @Override
            public Object getRootInstance(Object receiver, Frame frame) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLScopedNode) receiver) .getRootInstance(frame);
            }

            @TruffleBoundary
            private static boolean accepts_(Object arg0Value_) {
                SLScopedNode arg0Value = ((SLScopedNode) arg0Value_);
                return arg0Value.accepts((arg0Value));
            }

        }
    }
}

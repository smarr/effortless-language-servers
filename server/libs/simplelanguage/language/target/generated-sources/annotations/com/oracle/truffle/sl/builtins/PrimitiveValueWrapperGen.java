// CheckStyle: start generated
package com.oracle.truffle.sl.builtins;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.library.DynamicDispatchLibrary;
import com.oracle.truffle.api.library.LibraryExport;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.library.Message;
import com.oracle.truffle.api.library.ReflectionLibrary;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.sl.builtins.SLWrapPrimitiveBuiltin.PrimitiveValueWrapper;

@GeneratedBy(PrimitiveValueWrapper.class)
final class PrimitiveValueWrapperGen {

    private static final LibraryFactory<DynamicDispatchLibrary> DYNAMIC_DISPATCH_LIBRARY_ = LibraryFactory.resolve(DynamicDispatchLibrary.class);
    private static final LibraryFactory<ReflectionLibrary> REFLECTION_LIBRARY_ = LibraryFactory.resolve(ReflectionLibrary.class);

    static  {
        LibraryExport.register(PrimitiveValueWrapper.class, new ReflectionLibraryExports());
    }

    private PrimitiveValueWrapperGen() {
    }

    @GeneratedBy(PrimitiveValueWrapper.class)
    private static final class ReflectionLibraryExports extends LibraryExport<ReflectionLibrary> {

        private static final Uncached UNCACHED = new Uncached();

        private ReflectionLibraryExports() {
            super(ReflectionLibrary.class, PrimitiveValueWrapper.class, false, false, 0);
        }

        @Override
        protected ReflectionLibrary createUncached(Object receiver) {
            assert receiver instanceof PrimitiveValueWrapper;
            ReflectionLibrary uncached = ReflectionLibraryExports.UNCACHED;
            return uncached;
        }

        @Override
        protected ReflectionLibrary createCached(Object receiver) {
            assert receiver instanceof PrimitiveValueWrapper;
            return new Cached(receiver);
        }

        @GeneratedBy(PrimitiveValueWrapper.class)
        private static final class Cached extends ReflectionLibrary {

            @Child private ReflectionLibrary receiverDelegateReflectionLibrary_;

            protected Cached(Object receiver) {
                PrimitiveValueWrapper castReceiver = ((PrimitiveValueWrapper) receiver) ;
                this.receiverDelegateReflectionLibrary_ = super.insert(REFLECTION_LIBRARY_.create((castReceiver.delegate)));
            }

            @Override
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof PrimitiveValueWrapper) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                if (!(receiver instanceof PrimitiveValueWrapper)) {
                    return false;
                } else if (!this.receiverDelegateReflectionLibrary_.accepts((((PrimitiveValueWrapper) receiver).delegate))) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public Object send(Object arg0Value_, Message arg1Value, Object... arg2Value) throws Exception {
                assert arg0Value_ instanceof PrimitiveValueWrapper : "Invalid library usage. Library does not accept given receiver.";
                PrimitiveValueWrapper arg0Value = ((PrimitiveValueWrapper) arg0Value_);
                {
                    ReflectionLibrary reflection__ = this.receiverDelegateReflectionLibrary_;
                    return arg0Value.send(arg1Value, arg2Value, reflection__);
                }
            }

            @Override
            public NodeCost getCost() {
                return NodeCost.MONOMORPHIC;
            }

        }
        @GeneratedBy(PrimitiveValueWrapper.class)
        private static final class Uncached extends ReflectionLibrary {

            protected Uncached() {
            }

            @Override
            @TruffleBoundary
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof PrimitiveValueWrapper) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof PrimitiveValueWrapper;
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
            public Object send(Object arg0Value_, Message arg1Value, Object... arg2Value) throws Exception {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                PrimitiveValueWrapper arg0Value = ((PrimitiveValueWrapper) arg0Value_);
                return arg0Value.send(arg1Value, arg2Value, REFLECTION_LIBRARY_.getUncached((arg0Value.delegate)));
            }

        }
    }
}

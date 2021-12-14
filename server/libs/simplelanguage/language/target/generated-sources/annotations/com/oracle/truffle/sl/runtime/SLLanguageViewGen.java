// CheckStyle: start generated
package com.oracle.truffle.sl.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.DynamicDispatchLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryExport;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.utilities.FinalBitSet;

@GeneratedBy(SLLanguageView.class)
final class SLLanguageViewGen {

    private static final LibraryFactory<InteropLibrary> INTEROP_LIBRARY_ = LibraryFactory.resolve(InteropLibrary.class);
    private static final LibraryFactory<DynamicDispatchLibrary> DYNAMIC_DISPATCH_LIBRARY_ = LibraryFactory.resolve(DynamicDispatchLibrary.class);

    static  {
        LibraryExport.register(SLLanguageView.class, new InteropLibraryExports());
    }

    private SLLanguageViewGen() {
    }

    @GeneratedBy(SLLanguageView.class)
    private static final class InteropLibraryExports extends LibraryExport<InteropLibrary> {

        static final FinalBitSet ENABLED_MESSAGES = createMessageBitSet(INTEROP_LIBRARY_, "hasLanguage", "getLanguage", "hasMetaObject", "getMetaObject", "toDisplayString");
        private static final Uncached UNCACHED = new Uncached();

        private InteropLibraryExports() {
            super(InteropLibrary.class, SLLanguageView.class, false, false, 0);
        }

        @Override
        protected InteropLibrary createUncached(Object receiver) {
            assert receiver instanceof SLLanguageView;
            InteropLibrary uncached = createDelegate(INTEROP_LIBRARY_, InteropLibraryExports.UNCACHED);
            return uncached;
        }

        @Override
        protected InteropLibrary createCached(Object receiver) {
            assert receiver instanceof SLLanguageView;
            return createDelegate(INTEROP_LIBRARY_, new Cached(receiver));
        }

        @GeneratedBy(SLLanguageView.class)
        private static final class Cached extends InteropLibrary implements DelegateExport {

            @Child private InteropLibrary receiverDelegateInteropLibrary_;

            protected Cached(Object receiver) {
                SLLanguageView castReceiver = ((SLLanguageView) receiver) ;
                this.receiverDelegateInteropLibrary_ = super.insert(INTEROP_LIBRARY_.create((castReceiver.delegate)));
            }

            @Override
            public FinalBitSet getDelegateExportMessages() {
                return ENABLED_MESSAGES;
            }

            @Override
            public Object readDelegateExport(Object receiver_) {
                return ((SLLanguageView) receiver_).delegate;
            }

            @Override
            public Library getDelegateExportLibrary(Object delegate) {
                return this.receiverDelegateInteropLibrary_;
            }

            @Override
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLLanguageView) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                if (!(receiver instanceof SLLanguageView)) {
                    return false;
                } else if (!this.receiverDelegateInteropLibrary_.accepts((((SLLanguageView) receiver).delegate))) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public boolean hasLanguage(Object receiver) {
                assert receiver instanceof SLLanguageView : "Invalid library usage. Library does not accept given receiver.";
                return (((SLLanguageView) receiver)).hasLanguage();
            }

            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                assert receiver instanceof SLLanguageView : "Invalid library usage. Library does not accept given receiver.";
                return (((SLLanguageView) receiver)).getLanguage();
            }

            @Override
            public boolean hasMetaObject(Object arg0Value_) {
                assert arg0Value_ instanceof SLLanguageView : "Invalid library usage. Library does not accept given receiver.";
                SLLanguageView arg0Value = ((SLLanguageView) arg0Value_);
                {
                    InteropLibrary hasMetaObjectNode__hasMetaObject_interop__ = this.receiverDelegateInteropLibrary_;
                    return arg0Value.hasMetaObject(hasMetaObjectNode__hasMetaObject_interop__);
                }
            }

            @Override
            public NodeCost getCost() {
                return NodeCost.MONOMORPHIC;
            }

            @Override
            public Object getMetaObject(Object arg0Value_) throws UnsupportedMessageException {
                assert arg0Value_ instanceof SLLanguageView : "Invalid library usage. Library does not accept given receiver.";
                SLLanguageView arg0Value = ((SLLanguageView) arg0Value_);
                {
                    InteropLibrary getMetaObjectNode__getMetaObject_interop__ = this.receiverDelegateInteropLibrary_;
                    return arg0Value.getMetaObject(getMetaObjectNode__getMetaObject_interop__);
                }
            }

            @Override
            public Object toDisplayString(Object arg0Value_, boolean arg1Value) {
                assert arg0Value_ instanceof SLLanguageView : "Invalid library usage. Library does not accept given receiver.";
                SLLanguageView arg0Value = ((SLLanguageView) arg0Value_);
                {
                    InteropLibrary toDisplayStringNode__toDisplayString_interop__ = this.receiverDelegateInteropLibrary_;
                    return arg0Value.toDisplayString(arg1Value, toDisplayStringNode__toDisplayString_interop__);
                }
            }

        }
        @GeneratedBy(SLLanguageView.class)
        private static final class Uncached extends InteropLibrary implements DelegateExport {

            protected Uncached() {
            }

            @Override
            public FinalBitSet getDelegateExportMessages() {
                return ENABLED_MESSAGES;
            }

            @Override
            public Object readDelegateExport(Object receiver_) {
                return (((SLLanguageView) receiver_)).delegate;
            }

            @Override
            public Library getDelegateExportLibrary(Object delegate_) {
                return INTEROP_LIBRARY_.getUncached(delegate_);
            }

            @Override
            @TruffleBoundary
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLLanguageView) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof SLLanguageView;
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
            public boolean hasLanguage(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLLanguageView) receiver) .hasLanguage();
            }

            @TruffleBoundary
            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLLanguageView) receiver) .getLanguage();
            }

            @TruffleBoundary
            @Override
            public boolean hasMetaObject(Object arg0Value_) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                SLLanguageView arg0Value = ((SLLanguageView) arg0Value_);
                return arg0Value.hasMetaObject(INTEROP_LIBRARY_.getUncached((arg0Value.delegate)));
            }

            @TruffleBoundary
            @Override
            public Object getMetaObject(Object arg0Value_) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                SLLanguageView arg0Value = ((SLLanguageView) arg0Value_);
                return arg0Value.getMetaObject(INTEROP_LIBRARY_.getUncached((arg0Value.delegate)));
            }

            @TruffleBoundary
            @Override
            public Object toDisplayString(Object arg0Value_, boolean arg1Value) {
                // declared: true
                assert this.accepts(arg0Value_) : "Invalid library usage. Library does not accept given receiver.";
                SLLanguageView arg0Value = ((SLLanguageView) arg0Value_);
                return arg0Value.toDisplayString(arg1Value, INTEROP_LIBRARY_.getUncached((arg0Value.delegate)));
            }

        }
    }
}

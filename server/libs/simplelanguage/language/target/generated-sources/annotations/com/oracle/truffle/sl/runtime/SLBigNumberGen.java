// CheckStyle: start generated
package com.oracle.truffle.sl.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.DynamicDispatchLibrary;
import com.oracle.truffle.api.library.LibraryExport;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.nodes.NodeCost;

@GeneratedBy(SLBigNumber.class)
final class SLBigNumberGen {

    private static final LibraryFactory<DynamicDispatchLibrary> DYNAMIC_DISPATCH_LIBRARY_ = LibraryFactory.resolve(DynamicDispatchLibrary.class);

    static  {
        LibraryExport.register(SLBigNumber.class, new InteropLibraryExports());
    }

    private SLBigNumberGen() {
    }

    @GeneratedBy(SLBigNumber.class)
    private static final class InteropLibraryExports extends LibraryExport<InteropLibrary> {

        private static final Uncached UNCACHED = new Uncached();
        private static final Cached CACHE = new Cached();

        private InteropLibraryExports() {
            super(InteropLibrary.class, SLBigNumber.class, false, false, 0);
        }

        @Override
        protected InteropLibrary createUncached(Object receiver) {
            assert receiver instanceof SLBigNumber;
            InteropLibrary uncached = InteropLibraryExports.UNCACHED;
            return uncached;
        }

        @Override
        protected InteropLibrary createCached(Object receiver) {
            assert receiver instanceof SLBigNumber;
            return InteropLibraryExports.CACHE;
        }

        @GeneratedBy(SLBigNumber.class)
        private static final class Cached extends InteropLibrary {

            protected Cached() {
            }

            @Override
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLBigNumber) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof SLBigNumber;
            }

            @Override
            public boolean isAdoptable() {
                return false;
            }

            @Override
            public boolean isNumber(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).isNumber();
            }

            @Override
            public boolean fitsInByte(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).fitsInByte();
            }

            @Override
            public boolean fitsInShort(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).fitsInShort();
            }

            @Override
            public boolean fitsInFloat(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).fitsInFloat();
            }

            @Override
            public boolean fitsInLong(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).fitsInLong();
            }

            @Override
            public boolean fitsInInt(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).fitsInInt();
            }

            @Override
            public boolean fitsInDouble(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).fitsInDouble();
            }

            @Override
            public double asDouble(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).asDouble();
            }

            @Override
            public long asLong(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).asLong();
            }

            @Override
            public byte asByte(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).asByte();
            }

            @Override
            public int asInt(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).asInt();
            }

            @Override
            public float asFloat(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).asFloat();
            }

            @Override
            public short asShort(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).asShort();
            }

            @Override
            public boolean hasLanguage(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).hasLanguage();
            }

            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).getLanguage();
            }

            @Override
            public boolean hasMetaObject(Object receiver) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).hasMetaObject();
            }

            @Override
            public Object getMetaObject(Object receiver) throws UnsupportedMessageException {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).getMetaObject();
            }

            @Override
            public Object toDisplayString(Object receiver, boolean allowSideEffects) {
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return (((SLBigNumber) receiver)).toDisplayString(allowSideEffects);
            }

        }
        @GeneratedBy(SLBigNumber.class)
        private static final class Uncached extends InteropLibrary {

            protected Uncached() {
            }

            @Override
            @TruffleBoundary
            public boolean accepts(Object receiver) {
                assert !(receiver instanceof SLBigNumber) || DYNAMIC_DISPATCH_LIBRARY_.getUncached().dispatch(receiver) == null : "Invalid library export. Exported receiver with dynamic dispatch found but not expected.";
                return receiver instanceof SLBigNumber;
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
            public boolean isNumber(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .isNumber();
            }

            @TruffleBoundary
            @Override
            public boolean fitsInByte(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .fitsInByte();
            }

            @TruffleBoundary
            @Override
            public boolean fitsInShort(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .fitsInShort();
            }

            @TruffleBoundary
            @Override
            public boolean fitsInFloat(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .fitsInFloat();
            }

            @TruffleBoundary
            @Override
            public boolean fitsInLong(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .fitsInLong();
            }

            @TruffleBoundary
            @Override
            public boolean fitsInInt(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .fitsInInt();
            }

            @TruffleBoundary
            @Override
            public boolean fitsInDouble(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .fitsInDouble();
            }

            @TruffleBoundary
            @Override
            public double asDouble(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .asDouble();
            }

            @TruffleBoundary
            @Override
            public long asLong(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .asLong();
            }

            @TruffleBoundary
            @Override
            public byte asByte(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .asByte();
            }

            @TruffleBoundary
            @Override
            public int asInt(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .asInt();
            }

            @TruffleBoundary
            @Override
            public float asFloat(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .asFloat();
            }

            @TruffleBoundary
            @Override
            public short asShort(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .asShort();
            }

            @TruffleBoundary
            @Override
            public boolean hasLanguage(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .hasLanguage();
            }

            @TruffleBoundary
            @Override
            public Class<? extends TruffleLanguage<?>> getLanguage(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .getLanguage();
            }

            @TruffleBoundary
            @Override
            public boolean hasMetaObject(Object receiver) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .hasMetaObject();
            }

            @TruffleBoundary
            @Override
            public Object getMetaObject(Object receiver) throws UnsupportedMessageException {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .getMetaObject();
            }

            @TruffleBoundary
            @Override
            public Object toDisplayString(Object receiver, boolean allowSideEffects) {
                // declared: true
                assert this.accepts(receiver) : "Invalid library usage. Library does not accept given receiver.";
                return ((SLBigNumber) receiver) .toDisplayString(allowSideEffects);
            }

        }
    }
}

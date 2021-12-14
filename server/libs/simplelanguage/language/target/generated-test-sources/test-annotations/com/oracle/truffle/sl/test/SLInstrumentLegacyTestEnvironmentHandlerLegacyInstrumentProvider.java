// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Provider;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;
import com.oracle.truffle.sl.test.SLInstrumentLegacyTest.EnvironmentHandlerLegacyInstrument;
import java.util.Arrays;
import java.util.Collection;

@GeneratedBy(EnvironmentHandlerLegacyInstrument.class)
@Registration(id = "testEnvironmentHandlerLegacyInstrument")
public final class SLInstrumentLegacyTestEnvironmentHandlerLegacyInstrumentProvider implements Provider {

    @Override
    public String getInstrumentClassName() {
        return "com.oracle.truffle.sl.test.SLInstrumentLegacyTest$EnvironmentHandlerLegacyInstrument";
    }

    @Override
    public TruffleInstrument create() {
        return new EnvironmentHandlerLegacyInstrument();
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Arrays.asList("com.oracle.truffle.sl.test.SLInstrumentLegacyTest$Environment");
    }

}

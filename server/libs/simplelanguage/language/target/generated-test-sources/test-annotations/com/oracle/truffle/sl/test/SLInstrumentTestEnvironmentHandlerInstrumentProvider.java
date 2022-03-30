// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Provider;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;
import com.oracle.truffle.sl.test.SLInstrumentTest.EnvironmentHandlerInstrument;
import java.util.Arrays;
import java.util.Collection;

@GeneratedBy(EnvironmentHandlerInstrument.class)
@Registration(id = "testEnvironmentHandlerInstrument")
public final class SLInstrumentTestEnvironmentHandlerInstrumentProvider implements Provider {

    @Override
    public TruffleInstrument create() {
        return new EnvironmentHandlerInstrument();
    }

    @Override
    public String getInstrumentClassName() {
        return "com.oracle.truffle.sl.test.SLInstrumentTest$EnvironmentHandlerInstrument";
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Arrays.asList("com.oracle.truffle.sl.test.SLInstrumentTest$Environment");
    }

}

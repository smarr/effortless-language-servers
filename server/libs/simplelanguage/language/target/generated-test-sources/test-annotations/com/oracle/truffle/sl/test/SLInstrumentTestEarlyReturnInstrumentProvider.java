// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Provider;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;
import com.oracle.truffle.sl.test.SLInstrumentTest.EarlyReturnInstrument;
import java.util.Arrays;
import java.util.Collection;

@GeneratedBy(EarlyReturnInstrument.class)
@Registration(id = "testEarlyReturn")
public final class SLInstrumentTestEarlyReturnInstrumentProvider implements Provider {

    @Override
    public TruffleInstrument create() {
        return new EarlyReturnInstrument();
    }

    @Override
    public String getInstrumentClassName() {
        return "com.oracle.truffle.sl.test.SLInstrumentTest$EarlyReturnInstrument";
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Arrays.asList("com.oracle.truffle.sl.test.SLInstrumentTest$EarlyReturnInstrument");
    }

}

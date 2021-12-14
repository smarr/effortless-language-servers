// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Provider;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;
import com.oracle.truffle.sl.test.SLInstrumentTest.IncreaseArgOnErrorInstrument;
import java.util.Arrays;
import java.util.Collection;

@GeneratedBy(IncreaseArgOnErrorInstrument.class)
@Registration(id = "testIncreaseArgumentOnError")
public final class SLInstrumentTestIncreaseArgOnErrorInstrumentProvider implements Provider {

    @Override
    public String getInstrumentClassName() {
        return "com.oracle.truffle.sl.test.SLInstrumentTest$IncreaseArgOnErrorInstrument";
    }

    @Override
    public TruffleInstrument create() {
        return new IncreaseArgOnErrorInstrument();
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Arrays.asList("com.oracle.truffle.sl.test.SLInstrumentTest$IncreaseArgOnErrorInstrument");
    }

}

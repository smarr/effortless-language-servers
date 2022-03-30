// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Provider;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;
import com.oracle.truffle.sl.test.SLInstrumentLegacyTest.NewReplacedInstrumentLegacy;
import java.util.Arrays;
import java.util.Collection;

@GeneratedBy(NewReplacedInstrumentLegacy.class)
@Registration(id = "testNewNodeReplacedLegacy")
public final class SLInstrumentLegacyTestNewReplacedInstrumentLegacyProvider implements Provider {

    @Override
    public TruffleInstrument create() {
        return new NewReplacedInstrumentLegacy();
    }

    @Override
    public String getInstrumentClassName() {
        return "com.oracle.truffle.sl.test.SLInstrumentLegacyTest$NewReplacedInstrumentLegacy";
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Arrays.asList("com.oracle.truffle.sl.test.SLInstrumentLegacyTest$NewReplacedInstrumentLegacy");
    }

}

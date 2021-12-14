// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Provider;
import com.oracle.truffle.api.instrumentation.TruffleInstrument.Registration;
import com.oracle.truffle.sl.test.SLSharedCodeSeparatedEnvTest.CaptureOutput;
import java.util.Arrays;
import java.util.Collection;

@GeneratedBy(CaptureOutput.class)
@Registration(id = "captureOutput")
public final class SLSharedCodeSeparatedEnvTestCaptureOutputProvider implements Provider {

    @Override
    public String getInstrumentClassName() {
        return "com.oracle.truffle.sl.test.SLSharedCodeSeparatedEnvTest$CaptureOutput";
    }

    @Override
    public TruffleInstrument create() {
        return new CaptureOutput();
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Arrays.asList("java.io.ByteArrayOutputStream");
    }

}

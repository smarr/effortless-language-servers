// CheckStyle: start generated
package com.oracle.truffle.sl;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleFile.FileTypeDetector;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.TruffleLanguage.Provider;
import com.oracle.truffle.api.TruffleLanguage.Registration;
import com.oracle.truffle.api.debug.DebuggerTags.AlwaysHalt;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags.CallTag;
import com.oracle.truffle.api.instrumentation.StandardTags.ExpressionTag;
import com.oracle.truffle.api.instrumentation.StandardTags.ReadVariableTag;
import com.oracle.truffle.api.instrumentation.StandardTags.RootBodyTag;
import com.oracle.truffle.api.instrumentation.StandardTags.RootTag;
import com.oracle.truffle.api.instrumentation.StandardTags.StatementTag;
import com.oracle.truffle.api.instrumentation.StandardTags.WriteVariableTag;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@GeneratedBy(SLLanguage.class)
@Registration(characterMimeTypes = {"application/x-sl"}, contextPolicy = ContextPolicy.SHARED, defaultMimeType = "application/x-sl", id = "sl", name = "SL")
@ProvidedTags({CallTag.class, StatementTag.class, RootTag.class, RootBodyTag.class, ExpressionTag.class, AlwaysHalt.class, ReadVariableTag.class, WriteVariableTag.class})
public final class SLLanguageProvider implements Provider {

    @Override
    public String getLanguageClassName() {
        return "com.oracle.truffle.sl.SLLanguage";
    }

    @Override
    public TruffleLanguage<?> create() {
        return new SLLanguage();
    }

    @Override
    public List<FileTypeDetector> createFileTypeDetectors() {
        return Arrays.asList(new SLFileDetector());
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Collections.emptySet();
    }

}

// CheckStyle: start generated
package com.oracle.truffle.sl.test;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleFile.FileTypeDetector;
import com.oracle.truffle.api.TruffleLanguage.Provider;
import com.oracle.truffle.api.TruffleLanguage.Registration;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.sl.test.SLParseInContextTest.EvalLang;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@GeneratedBy(EvalLang.class)
@Registration(id = "x-test-eval", name = "EvalLang", version = "1.0")
public final class SLParseInContextTestEvalLangProvider implements Provider {

    @Override
    public String getLanguageClassName() {
        return "com.oracle.truffle.sl.test.SLParseInContextTest$EvalLang";
    }

    @Override
    public TruffleLanguage<?> create() {
        return new EvalLang();
    }

    @Override
    public List<FileTypeDetector> createFileTypeDetectors() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Collections.emptySet();
    }

}

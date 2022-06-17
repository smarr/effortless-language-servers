package som.langserv.lens;

import java.util.List;

import org.eclipse.lsp4j.CodeLens;

import som.langserv.structure.DocumentStructures;


public interface FileLens {
  List<CodeLens> getCodeLenses(DocumentStructures structures);
}

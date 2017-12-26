import { resolve as resolvePath } from "path";
import { LanguageClient, StreamInfo, Disposable, ErrorHandler, ErrorAction, CloseAction } from "vscode-languageclient";
import { startLanguageServer, CLIENT_OPTION, connectToLanguageServer } from "../src/extension";
import * as vscode from "vscode";
import { Message } from "vscode-languageserver-protocol";
import { readFileSync } from "fs";
import { expect } from "chai";

let serverDisposable: Disposable;

function createLSPServer() : Promise<StreamInfo> {
  return new Promise((resolve, reject) => {
    serverDisposable = startLanguageServer(resolvePath, resolve, reject);
    // serverDisposable = new vscode.Disposable(() => {});
    // connectToLanguageServer(resolve, reject);
  });
}

class TestLanguageClient extends LanguageClient {
  private oneShotDiagnosticsHandler;

  constructor(name: string, serverOptions, clientOptions, forceDebug?: boolean) {
    super(name, serverOptions, clientOptions, forceDebug);

    const hd = (<any> this).handleDiagnostics;
    (<any> this).handleDiagnostics = (params) => {
      hd.call(this, params);
      this.handleDiag(params);
    }

    this.oneShotDiagnosticsHandler = [];
  }

  public addDiagnosticsHandler(handler) {
    this.oneShotDiagnosticsHandler.push(handler);
  }

  protected handleDiag(params) {
    const handler = this.oneShotDiagnosticsHandler.pop();
    if (handler === undefined) {
      console.error("Unexpected diagnostics", params);
    } else {
      handler(params);
    }
  }
}

describe("Basic Tests", () => {
  let clientDisposable : Disposable;
  let client: TestLanguageClient;

  after(done => {
    clientDisposable.dispose();
    serverDisposable.dispose();
    done();
  });

  const p = resolvePath("out/test/ns");
  const uri = vscode.Uri.file(p);
  const wsF = vscode.workspace.getWorkspaceFolder(uri);

  it("Start Client", () => {
    const errorHandler: ErrorHandler = {
      error: (error: Error, message: Message, count: number) => {
        console.error(error, message, count);
        return ErrorAction.Continue;
      },
      closed: () => {
        return CloseAction.DoNotRestart;
      }
    };
    const options = Object.assign({}, CLIENT_OPTION, {errorHandler: errorHandler});
    client = new TestLanguageClient('SOMns Language Server', createLSPServer, options);
    clientDisposable = client.start();

    return client.onReady();
  });

  it("Load Hello World and expect diagnostics", done => {
    const content = readFileSync(p + '/Hello.ns').toString();
    client.sendNotification("textDocument/didChange", {
      textDocument: {
        uri: uri.toString() + '/Hello.ns',
        version: 1,
      },
      contentChanges: [{
        text: content,
      }]
    });

    client.addDiagnosticsHandler(function (params) {
      try {
        expect(params.uri).to.equal(uri.toString() + '/Hello.ns');
        expect(params.diagnostics).to.have.lengthOf(7);
        done();
      } catch (e) {
        done(e);
      }
    });
  });
});

import { resolve as resolvePath } from "path";
import { LanguageClient, StreamInfo, Disposable, ErrorHandler, ErrorAction, CloseAction } from "vscode-languageclient/node";
import { startLanguageServer, CLIENT_OPTION, deactivate } from "../src/extension";
import * as vscode from "vscode";
import { Message } from "vscode-languageserver-protocol";
import { readFileSync } from "fs";
import { expect } from "chai";

function resolvePathAbsolute(path: string): string {
  const result = resolvePath(__dirname + '/../../' + path);
  return result;
}

function createLSPServer(): Promise<StreamInfo> {
  return new Promise((resolve, reject) => {
    startLanguageServer(resolvePathAbsolute, resolve, reject);
  });
}

class TestLanguageClient extends LanguageClient {
  private oneShotDiagnosticsHandler;

  constructor(name: string, serverOptions, clientOptions, forceDebug?: boolean) {
    super(name, serverOptions, clientOptions, forceDebug);

    const hd = (<any>this).handleDiagnostics;
    (<any>this).handleDiagnostics = (params) => {
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
  let clientDisposable: Disposable;
  let client: TestLanguageClient;

  after(done => {
    clientDisposable.dispose();
    deactivate();
    done();
  });

  const examplesPath = resolvePath("out/test/examples");
  const examplesUri = vscode.Uri.file(examplesPath);
  const wsF = vscode.workspace.getWorkspaceFolder(examplesUri);

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
    const options = Object.assign({}, CLIENT_OPTION, { errorHandler: errorHandler });
    client = new TestLanguageClient('SOMns Language Server', createLSPServer, options);
    clientDisposable = client.start();

    return client.onReady();
  });

  it("Load SOMns Hello World and expect diagnostics", done => {
    const content = readFileSync(examplesPath + '/Hello.ns').toString();
    client.sendNotification("textDocument/didChange", {
      textDocument: {
        uri: examplesUri.toString() + '/Hello.ns',
        version: 1,
      },
      contentChanges: [{
        text: content,
      }]
    });

    client.addDiagnosticsHandler(function (params) {
      try {
        expect(params.uri).to.equal(examplesUri.toString() + '/Hello.ns');
        expect(params.diagnostics).to.have.lengthOf(9);
        done();
      } catch (e) {
        done(e);
      }
    });
  });

  it("Load SOM Hello World and expect diagnostics", done => {
    const content = readFileSync(examplesPath + '/Hello.som').toString();
    client.sendNotification("textDocument/didChange", {
      textDocument: {
        uri: examplesUri.toString() + '/Hello.som',
        version: 1,
      },
      contentChanges: [{
        text: content,
      }]
    });

    client.addDiagnosticsHandler(function (params) {
      try {
        expect(params.uri).to.equal(examplesUri.toString() + '/Hello.som');
        expect(params.diagnostics).to.have.lengthOf(2);
        done();
      } catch (e) {
        done(e);
      }
    });
  });

  it("Load SOM Hello World and expect parse errors", done => {
    const content = readFileSync(examplesPath + '/HelloWithError.som').toString();
    client.sendNotification("textDocument/didChange", {
      textDocument: {
        uri: examplesUri.toString() + '/HelloWithError.som',
        version: 1,
      },
      contentChanges: [{
        text: content,
      }]
    });

    client.addDiagnosticsHandler(function (params) {
      try {
        console.log(params);
        expect(params.uri).to.equal(examplesUri.toString() + '/HelloWithError.som');
        expect(params.diagnostics).to.have.lengthOf(5);
        const error = params.diagnostics[0];
        expect(error.source).to.equal('Parser');
        expect(error.message).to.contain('Unexpected symbol');
        done();
      } catch (e) {
        done(e);
      }
    });
  });
});

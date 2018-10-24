'use strict';

import { spawn } from 'child_process';
import { Socket } from 'net';

import { workspace, Disposable, ExtensionContext, commands, window } from 'vscode';
import { LanguageClient, LanguageClientOptions, SettingMonitor, ServerOptions, TransportKind, StreamInfo } from 'vscode-languageclient';

const LSPort = 8123;  // TODO: make configurable
const EnableExtensionDebugging : boolean = <boolean> workspace.getConfiguration('somns').get('debugMode');

export const CLIENT_OPTION: LanguageClientOptions = {
	documentSelector: ['SOMns']
}

type PathConverter = (path: string) => string;

function getServerOptions(asAbsolutePath: PathConverter, enableDebug:
	  boolean, enableTcp: boolean): ServerOptions {
	const javaCmd = '/usr/bin/java';

  const javaClassPath = [
		asAbsolutePath('out/server/som.jar'),
		asAbsolutePath('out/server/black-diamonds.jar'),
		asAbsolutePath('out/server/graal-sdk.jar'),
		asAbsolutePath('out/server/word-api.jar'),
		asAbsolutePath('out/server/truffle-api.jar'),
		asAbsolutePath('out/server/svm-core.jar'),
		asAbsolutePath('out/server/truffle-profiler.jar'),
		asAbsolutePath('out/server/somns-deps.jar'),

		asAbsolutePath('out/server/guava-19.0.jar'),
		asAbsolutePath('out/server/org.eclipse.xtend.lib-2.10.0.jar'),
		asAbsolutePath('out/server/org.eclipse.xtext.xbase.lib-2.10.0.jar'),
		asAbsolutePath('out/server/som-language-server.jar')];

	const somLib = '-Dsom.langserv.core-lib=' + asAbsolutePath('out/server/core-lib')

	let javaArgs = [
		'-cp', javaClassPath.join(':'),
		somLib,
		'som.langserv.ServerLauncher'];

	if (enableDebug) {
		javaArgs = ['-ea', '-esa',
								'-Xdebug',
								'-Xrunjdwp:transport=dt_socket,quiet=y,server=y,suspend=n,address=8000'
							 ].concat(javaArgs);
	}

	if (enableTcp) {
		javaArgs = ['-Dsom.langserv.transport=tcp'].concat(javaArgs);
	}

	return {
		run:   { command: javaCmd, args: javaArgs },
		debug: { command: javaCmd, args: javaArgs }
	}
}

function startLanguageServerAndConnectTCP(context: ExtensionContext,
													   resolve: (value?: StreamInfo | PromiseLike<StreamInfo>) => void,
														 reject: (reason?: any) => void): Disposable {
	const serverOptions: any = getServerOptions(context.asAbsolutePath, true, true);
	const lsProc = spawn(serverOptions.run.command, serverOptions.run.args);
	let sawServerStarted = false;
	lsProc.stdout.on('data', data => {
		if (!sawServerStarted && data.toString().includes('Server started and waiting')) {
			sawServerStarted = true;
			connectToLanguageServer(resolve, reject);
		}
	});
	lsProc.stderr.on('data', data => {
		window.showErrorMessage('LS stderr: ' + data.toString());
	});
	lsProc.on('exit', code => {
		reject('SOMns language server stopped. Exit code: ' + code);
	});

	return new Disposable(() => { lsProc.kill(); });
}

export function startLanguageServer(asAbsolutePath: PathConverter,
													   resolve: (value?: StreamInfo | PromiseLike<StreamInfo>) => void,
														 reject: (reason?: any) => void) {
	const serverOptions: any = getServerOptions(asAbsolutePath, EnableExtensionDebugging, false);
	const lsProc = spawn(serverOptions.run.command, serverOptions.run.args);

	resolve({
		reader: lsProc.stdout,
		writer: lsProc.stdin
	});

	return new Disposable(() => { lsProc.kill(); });
}

export function connectToLanguageServer(resolve: (value?: StreamInfo | PromiseLike<StreamInfo>) => void,
														     reject: (reason?: any) => void) {
	const clientSocket = new Socket();
	clientSocket.once('error', (e) => {
		window.showErrorMessage("Failed to connect to language server. Socket error: " + JSON.stringify(e));
		reject(e);
	});
	clientSocket.connect(LSPort, '127.0.0.1', () => {
		resolve({
			reader: clientSocket,
			writer: clientSocket
		});
	});
}

export function activate(context: ExtensionContext) {
	function createLSPServer() : Promise<StreamInfo> {
		return new Promise((resolve, reject) => {
			if (EnableExtensionDebugging) {
				window.showInformationMessage("SOMns Debug Mode: Trying to connect to Language Server on port " + LSPort);
				connectToLanguageServer(resolve, reject);
			} else {
				const disposable = startLanguageServer(context.asAbsolutePath, resolve, reject);

				// when not needed anymore, make sure the language server is shutdown
				// TODO: perhaps do this with some proper command sent to the server?
				context.subscriptions.push(disposable);
			}
		});
	}



	// Create the language client and start the client.
	let disposable = new LanguageClient('SOMns Language Server', createLSPServer, CLIENT_OPTION).start();

	// Push the disposable to the context's subscriptions so that the
	// client can be deactivated on extension deactivation
	context.subscriptions.push(disposable);

	// see: https://github.com/Microsoft/vscode-extension-samples/blob/37a9a2f413686d1a8f029accdbf969a568f07344/previewhtml-sample/src/extension.ts
	// context.subscriptions.push(disposable, registr) ??
}

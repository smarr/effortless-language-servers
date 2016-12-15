'use strict';

import { spawn } from 'child_process';
import { Socket } from 'net';

import { workspace, Disposable, ExtensionContext, commands, window } from 'vscode';
import { LanguageClient, LanguageClientOptions, SettingMonitor, ServerOptions, TransportKind, StreamInfo } from 'vscode-languageclient';

const LSPort = 8123;  // TODO: make configurable
const EnableExtensionDebugging : boolean = <boolean> workspace.getConfiguration('somns').get('debugMode');

function getServerOptions(context: ExtensionContext): ServerOptions {
	const javaCmd = '/usr/bin/java';
	const bootCP = ['-Xbootclasspath/a',
		context.asAbsolutePath('out/server/som.jar'),
		context.asAbsolutePath('out/server/truffle-api.jar'),
		context.asAbsolutePath('out/server/truffle-debug.jar'),
		context.asAbsolutePath('out/server/somns-deps.jar')]

  const javaClasspass = [
		context.asAbsolutePath('out/server/guava-19.0.jar'),
		context.asAbsolutePath('out/server/org.eclipse.xtend.lib-2.10.0.jar'),
		context.asAbsolutePath('out/server/org.eclipse.xtext.xbase.lib-2.10.0.jar'),
		context.asAbsolutePath('out/server/som-language-server.jar')]
	const somLib = '-Dsom.langserv.core-lib=' + context.asAbsolutePath('out/server/core-lib')

	let javaArgs = [
		bootCP.join(':'),
		'-cp', javaClasspass.join(':'),
		somLib,
		'som.langserv.ServerLauncher'];

	if (EnableExtensionDebugging) {
		javaArgs = ['-ea', '-esa',
								'-Xdebug',
								'-Xrunjdwp:transport=dt_socket,quiet=y,server=y,suspend=n,address=8000'
							 ].concat(javaArgs);
	}

	return {
		run:   { command: javaCmd, args: javaArgs },
		debug: { command: javaCmd, args: javaArgs }
	}
}

function startLanguageServer(context: ExtensionContext,
													   resolve: (value?: StreamInfo | PromiseLike<StreamInfo>) => void,
														 reject: (reason?: any) => void) {
	const serverOptions: any = getServerOptions(context);
	const lsProc = spawn(serverOptions.run.command, serverOptions.run.args);
	let sawServerStarted = false;
	lsProc.stdout.on('data', data => {
		if (!sawServerStarted && data.toString().includes('Server started and waiting')) {
			sawServerStarted = true;
			connectToLanguageServer(resolve, reject);
		}
	});
	lsProc.on('exit', code => {
		reject('SOMns language server stopped. Exit code: ' + code);
	});

	// when not needed anymore, make sure the language server is shutdown
	// TODO: perhaps do this with some proper command sent to the server?
	context.subscriptions.push(new Disposable(() => { lsProc.kill(); }));
}

function connectToLanguageServer(resolve: (value?: StreamInfo | PromiseLike<StreamInfo>) => void,
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
				startLanguageServer(context, resolve, reject);
			}
		});
	}

	let clientOptions: LanguageClientOptions = {
		documentSelector: ['SOM']
	}

	// Create the language client and start the client.
	let disposable = new LanguageClient('SOMns Language Server', createLSPServer, clientOptions).start();

	// Push the disposable to the context's subscriptions so that the
	// client can be deactivated on extension deactivation
	context.subscriptions.push(disposable);
}

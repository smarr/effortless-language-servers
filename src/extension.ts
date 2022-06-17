'use strict';

import { ChildProcess, spawn } from 'child_process';
import { Socket } from 'net';

import { workspace, ExtensionContext, window } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo } from 'vscode-languageclient/node';

const LSPort = 8123;  // TODO: make configurable
const EnableExtensionDebugging : boolean = <boolean> workspace.getConfiguration('somns').get('debugMode');

export const CLIENT_OPTION: LanguageClientOptions = {
	documentSelector: ['SOMns', 'SOM','simple']
}

type PathConverter = (path: string) => string;

let client: LanguageClient = null;
let serverProcess: ChildProcess = null;

function getServerOptions(asAbsolutePath: PathConverter, enableDebug:
	  boolean, enableTcp: boolean): ServerOptions {
	const javaCmd = 'java';

  const javaClassPath = [
		asAbsolutePath('out/server/som.jar'),
		asAbsolutePath('out/server/somns.jar'),
		asAbsolutePath('out/server/black-diamonds.jar'),
		asAbsolutePath('out/server/graal-sdk.jar'),
		asAbsolutePath('out/server/word-api.jar'),
		asAbsolutePath('out/server/truffle-api.jar'),
		asAbsolutePath('out/server/truffle-debug.jar'),
		asAbsolutePath('out/server/svm-core.jar'),
		asAbsolutePath('out/server/truffle-profiler.jar'),
		asAbsolutePath('out/server/somns-deps.jar'),

		asAbsolutePath('out/server/affinity.jar'),
		asAbsolutePath('out/server/slf4j-api.jar'),
		asAbsolutePath('out/server/slf4j-simple.jar'),
		asAbsolutePath('out/server/jna-platform.jar'),
		asAbsolutePath('out/server/jna.jar'),

		asAbsolutePath('out/server/gson-2.8.6.jar'),
		asAbsolutePath('out/server/guava-19.0.jar'),
		asAbsolutePath('out/server/org.eclipse.xtend.lib.macro-2.24.0.jar'),
		asAbsolutePath('out/server/org.eclipse.xtend.lib-2.24.0.jar'),
		asAbsolutePath('out/server/org.eclipse.xtext.xbase.lib-2.24.0.jar'),
		asAbsolutePath('out/server/antlr4-runtime-4.9.2.jar'),
		asAbsolutePath('out/server/som-language-server.jar')];

	const somnsLib = '-Dsom.langserv.somns-core-lib=' + asAbsolutePath('out/server/somns-core-lib')
	const somLib = '-Dsom.langserv.som-core-lib=' + asAbsolutePath('out/server/som-core-lib')

	let javaArgs = [
		'-cp', javaClassPath.join(':'),
		somLib, somnsLib,
		'-Dpolyglot.engine.WarnInterpreterOnly=false', // to disable warnings on stdout/stderr
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
														 reject: (reason?: any) => void): void {
	const serverOptions: any = getServerOptions(context.asAbsolutePath, true, true);
	const lsProc = spawn(serverOptions.run.command, serverOptions.run.args, {shell: true});
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

	serverProcess = lsProc;
}

export function startLanguageServer(asAbsolutePath: PathConverter,
													   resolve: (value?: StreamInfo | PromiseLike<StreamInfo>) => void,
														 reject: (reason?: any) => void): void {
	const serverOptions: any = getServerOptions(asAbsolutePath, EnableExtensionDebugging, false);

	const cmd = `${serverOptions.run.command} ${serverOptions.run.args.join(" ")}`;
	console.log(`[SOM-EXT] spawn '${cmd}'`);
	const lsProc = spawn(serverOptions.run.command, serverOptions.run.args, {shell: true});

	let stderr = '';

	lsProc.stderr.on('data', data => {
		stderr += data.toString();
	});

	lsProc.on('exit', code => {
		if (code !== 0) {
			console.log(`[SOM-EXT] Server processes exited with code: ${code}
	-------
	stderr: ${stderr}
	-------`);
		}
	});

	resolve({
		reader: lsProc.stdout,
		writer: lsProc.stdin
	});

	serverProcess = lsProc;
}

export function connectToLanguageServer(resolve: (value?: StreamInfo | PromiseLike<StreamInfo>) => void,
														     reject: (reason?: any) => void) {
	console.log("[SOM-EXT] Create socket to connect to SOM Language Server");
	const clientSocket = new Socket();
	clientSocket.once('error', (e) => {
		console.log(`[SOM-EXT] Failed to connect to language server. Socket error: ${JSON.stringify(e)}`);
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
				window.showInformationMessage("SOMns Starting Language Server");
				startLanguageServer(context.asAbsolutePath, resolve, reject);
			}
		});
	}



	// Create the language client and start the client.
	client = new LanguageClient('SOMns Language Server', createLSPServer, CLIENT_OPTION);
	client.start();
}

export function deactivate(): Thenable<void> | undefined {
	if (serverProcess !== null) {
		serverProcess.kill();
	}

	if (!client) {
		return undefined;
	}
	return client.stop();
}

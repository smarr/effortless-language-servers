'use strict';

import { ChildProcess, spawn } from 'child_process';
import { Socket } from 'net';

import { workspace, window, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo } from 'vscode-languageclient/node';
import { getCommandLine, isJavaAvailableAndCompatible } from './command-line';
import { activateDebuggerFeatures } from './debugger-config';

const LSPort = 8123;  // TODO: make configurable

const configuration = workspace.getConfiguration('els');
const EnableExtensionDebugging : boolean = <boolean> configuration.get('debugMode');

export const CLIENT_OPTION: LanguageClientOptions = {
	documentSelector: ['SOMns', 'SOM','simple']
}

type PathConverter = (path: string) => string;

let client: LanguageClient = null;
let serverProcess: ChildProcess = null;

function getServerOptions(asAbsolutePath: PathConverter, enableDebug:
	  boolean, enableTcp: boolean): ServerOptions {
	const cmdLine = getCommandLine(configuration.get('javaHome'), asAbsolutePath, enableDebug, enableTcp);

	return {
		run:   cmdLine,
		debug: cmdLine
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

export async function activate(context: ExtensionContext) {
	function createLSPServer() : Promise<StreamInfo> {
		return new Promise((resolve, reject) => {
			if (EnableExtensionDebugging) {
				window.showInformationMessage("SOMns Debug Mode: Trying to connect to Language Server on port " + LSPort);
				connectToLanguageServer(resolve, reject);
			} else {
				if (!isJavaAvailableAndCompatible(configuration.get('javaHome'))) {
					window.showErrorMessage('Java 17 or new was not found. Please configure it in the settings under the `els.javaHome` key.');
				}
				// window.showInformationMessage("SOMns Starting Language Server");
				startLanguageServer(context.asAbsolutePath, resolve, reject);
			}
		});
	}

	// Create the language client and start the client.
	client = new LanguageClient('SOMns Language Server', createLSPServer, CLIENT_OPTION);
	await client.start();

	activateDebuggerFeatures(context);
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

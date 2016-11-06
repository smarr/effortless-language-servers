/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
'use strict';

import { execFile } from 'child_process';
import { Socket } from 'net';

import { workspace, Disposable, ExtensionContext, commands, window } from 'vscode';
import { LanguageClient, LanguageClientOptions, SettingMonitor, ServerOptions, TransportKind, StreamInfo } from 'vscode-languageclient';

const port = 8123;  // TODO: make configurable

function getServerOptions(context: ExtensionContext): ServerOptions {
	const javaCmd = '/usr/bin/java';
	const bootCP = ['-Xbootclasspath/a',
		context.asAbsolutePath('out/server/som.jar'),
		context.asAbsolutePath('out/server/truffle-api.jar'),
		context.asAbsolutePath('out/server/truffle-debug.jar'),
		context.asAbsolutePath('out/server/somns-deps.jar')]

  const javaClasspass = [
		context.asAbsolutePath('out/server/gson-2.7.jar'),
		context.asAbsolutePath('out/server/guava-19.0.jar'),
		context.asAbsolutePath('out/server/org.eclipse.xtend.lib-2.10.0.jar'),
		context.asAbsolutePath('out/server/org.eclipse.xtext.xbase.lib-2.10.0.jar'),
		context.asAbsolutePath('out/server/som-language-server.jar')]
	const somLib = '-Dsom.langserv.core-lib=' + context.asAbsolutePath('out/server/core-lib')
	
	const javaArgs = [
		'-ea', '-esa', 
		'-Xdebug', '-Xrunjdwp:transport=dt_socket,quiet=y,server=y,suspend=n,address=8000',
		bootCP.join(':'),
		'-cp', javaClasspass.join(':'),
		somLib,
		'som.langserv.ServerLauncher'];

	return {
		run:   { command: javaCmd, args: javaArgs },
		debug: { command: javaCmd, args: javaArgs }
	}
}

export function activate(context: ExtensionContext) {
	let startServer: boolean = true;
	function createLSPServer() : Promise<StreamInfo> {
		return new Promise((resolve, reject) => {
			const clientSocket = new Socket();
			clientSocket.once('error', (e) => {
				if (startServer) {
					startServer = false;
					window.showInformationMessage("Starting SOMns language server on port "
				  	+ port + ". The server is started with debugging enabled. Potential security issue.");
					const serverOptions: any = getServerOptions(context);
					execFile(serverOptions.run.command, serverOptions.run.args);

					// Try to connect after waiting for 1.5 seconds.
					setTimeout(() => { resolve(createLSPServer()); }, 1500);
				} else {
					window.showErrorMessage("Connection to SOM server on port " + port +
					" failed. Please try after starting the server. Error: " + JSON.stringify(e));
					reject(e);
				}
			});
			clientSocket.connect(port, null, () => {
				resolve({
					reader: clientSocket,
					writer: clientSocket
				});
			});
		});
	}

	const serverOptions = createLSPServer;

	let clientOptions: LanguageClientOptions = {
		documentSelector: ['SOM']
	}
	
	// Create the language client and start the client.
	let disposable = new LanguageClient('SOMns Language Server', serverOptions, clientOptions).start();
	
	// Push the disposable to the context's subscriptions so that the 
	// client can be deactivated on extension deactivation
	context.subscriptions.push(disposable);
}

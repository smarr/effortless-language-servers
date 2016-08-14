/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
'use strict';

import { Socket } from 'net';

import { workspace, Disposable, ExtensionContext, commands, window } from 'vscode';
import { LanguageClient, LanguageClientOptions, SettingMonitor, ServerOptions, TransportKind, StreamInfo } from 'vscode-languageclient';

const port = 8123;  // TODO: make configurable

function getServerOptionsForStdIoVersion(context: ExtensionContext) {
	const javaClasspass = [context.asAbsolutePath('out/server/gson-2.7.jar'),
						 context.asAbsolutePath('out/server/guava-19.0.jar'),
						 context.asAbsolutePath('out/server/org.eclipse.xtend.lib-2.10.0.jar'),
						 context.asAbsolutePath('out/server/org.eclipse.xtext.xbase.lib-2.10.0.jar'),
						 context.asAbsolutePath('out/server/som-language-server.jar'),
						 context.asAbsolutePath('out/server/som.jar'),
						 context.asAbsolutePath('out/server/truffle-api.jar'),
						 context.asAbsolutePath('out/server/truffle-debug.jar')]
	const javaCmd = 'java';
	const javaArgs = ['-cp', javaClasspass.join(':'), 'som.langserv.ServerLauncher'];
	const args = ['-Dls.port=', port].concat(javaArgs);

	const serverOptions: ServerOptions = {
		run:   { command: javaCmd, args: javaArgs },
		debug: { command: '/Users/smarr/Projects/SOM/SOMns-vscode/server/run.sh' }
	}
}

export function activate(context: ExtensionContext) {

	function createLSPServer() : Promise<StreamInfo> {
		return new Promise((resolve, reject) => {
			const clientSocket = new Socket();
			clientSocket.once('error', (e) => {
				window.showErrorMessage("Connection to SOM server on port " + port +
					" failed. Please try after starting the server. Error: " + JSON.stringify(e));
				reject(e);
			});
			clientSocket.connect(port, null, () => {
				resolve({
					reader: clientSocket,
					writer: clientSocket
				});
			});

			// TODO: for production use, start the server
			// Start the child java process
			// import { execFile } from 'child_process';
			// ChildProcess.execFile(javaExecutablePath, args, options);
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

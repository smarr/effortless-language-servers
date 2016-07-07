/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
'use strict';

import * as path from 'path';

import { workspace, Disposable, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, SettingMonitor, ServerOptions, TransportKind } from 'vscode-languageclient';

export function activate(context: ExtensionContext) {
	let javaClasspass = [context.asAbsolutePath('out/server/gson-2.7.jar'),
						 context.asAbsolutePath('out/server/guava-19.0.jar'),
						 context.asAbsolutePath('out/server/org.eclipse.xtend.lib-2.10.0.jar'),
						 context.asAbsolutePath('out/server/org.eclipse.xtext.xbase.lib-2.10.0.jar'),
						 context.asAbsolutePath('out/server/som-language-server.jar'),
						 context.asAbsolutePath('out/server/som.jar'),
						 context.asAbsolutePath('out/server/truffle-api.jar'),
						 context.asAbsolutePath('out/server/truffle-debug.jar')]

	let serverOptions: ServerOptions = {
		command: 'java',
		args: ['-cp', javaClasspass.join(':'), 'lsp.ServerLauncher']
	}

	let clientOptions: LanguageClientOptions = {
		documentSelector: ['SOM']
	}
	
	// Create the language client and start the client.
	let disposable = new LanguageClient('SOMns Language Server', serverOptions, clientOptions).start();
	
	// Push the disposable to the context's subscriptions so that the 
	// client can be deactivated on extension deactivation
	context.subscriptions.push(disposable);
}

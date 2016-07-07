/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
'use strict';

import * as path from 'path';

import { workspace, Disposable, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, SettingMonitor, ServerOptions, TransportKind } from 'vscode-languageclient';

export function activate(context: ExtensionContext) {

	let serverOptions: ServerOptions = {
		command: '/Users/smarr/Projects/SOM/lsp-test/truffle-lang-server/run.sh'
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

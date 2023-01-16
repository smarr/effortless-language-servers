import {
  CancellationToken,
  commands,
  debug,
  DebugAdapterDescriptor,
  DebugAdapterDescriptorFactory,
  DebugAdapterInlineImplementation,
  DebugConfiguration,
  DebugConfigurationProvider,
  DebugSession,
  ExtensionContext,
  ProviderResult,
  Uri,
  window,
  workspace,
  WorkspaceFolder
} from "vscode";
import { SomDebugSession } from "./debugger";

const configuration = workspace.getConfiguration('els');

/**
 * This SOMnsConfigurationProvider is a dynamic provider that can change the debug configuration parameters
 */
class SOMnsConfigurationProvider implements DebugConfigurationProvider {

	/** Resolve the debug configuration to debug currently selected file */
	resolveDebugConfiguration(folder: WorkspaceFolder | undefined, config: DebugConfiguration, token?: CancellationToken): ProviderResult<DebugConfiguration> {
		// retrieve the active file, if it is a SOMns file then substitute the program variable with the file path
		const editor = window.activeTextEditor;
		if (editor && editor.document.languageId === 'SOMns') {
      if (!config.runtime) {
        const somNsExecutable = configuration.get('somnsExecutable');
        if (!somNsExecutable) {
          window.showErrorMessage('SOMns was not found. Please configure it in the settings under the `els.somnsExecutable` key.');
          return null;
        } else {
          config.runtime = somNsExecutable;
        }
      }
			if (!config.program) {
				config.program = '${file}';
				config.stopOnEntry = true;
			}
		}

		return config;
	}
}

class InlineDebugAdapterFactory implements DebugAdapterDescriptorFactory {

	createDebugAdapterDescriptor(_session: DebugSession): ProviderResult<DebugAdapterDescriptor> {
		return new DebugAdapterInlineImplementation(new SomDebugSession());
	}
}

export function activateDebuggerFeatures(context: ExtensionContext) {
  const configProvider = debug.registerDebugConfigurationProvider('SOMns', new SOMnsConfigurationProvider);

  // Registering the configuration provider for starting opened file
	const runEditor = commands.registerCommand('extension.effortless-language-servers.runEditorContents', (resource: Uri) => {
		let targetResource = resource;
			if (!targetResource && window.activeTextEditor) {
				targetResource = window.activeTextEditor.document.uri;
			}
			if (targetResource) {
				debug.startDebugging(undefined, {
            type: 'SOMns',
            name: 'Run File',
            request: 'launch',
            program: targetResource.fsPath
          },
					{ noDebug: true }
				);
			}
	});

	const debugEditor = commands.registerCommand('extension.effortless-language-servers.debugEditorContents', (resource: Uri) => {
		let targetResource = resource;
		if (!targetResource && window.activeTextEditor) {
			targetResource = window.activeTextEditor.document.uri;
		}
		if (targetResource) {
			debug.startDebugging(undefined, {
				type: 'SOMns',
				name: 'Debug File',
				request: 'launch',
				program: targetResource.fsPath,
				stopOnEntry: true
			});
		}
	});

  const adapterFactory = new InlineDebugAdapterFactory();
	const debugAdapterFactory = debug.registerDebugAdapterDescriptorFactory('SOMns', adapterFactory);

  context.subscriptions.push(configProvider, runEditor, debugEditor, debugAdapterFactory);
}

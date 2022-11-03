export function getCommandLine(asAbsolutePath: (string) => string,
    enableDebug: boolean, enableTcp: boolean): {
      command: string,
      args: string[]
} {
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

  return {command: javaCmd, args: javaArgs};
}

export function getShellScript(enableDebug: boolean, enableTcp: boolean) {
  const cmd = getCommandLine((p) => p, enableDebug, enableTcp);
  return `#!/bin/bash
SCRIPT_DIR=$(dirname $0)
pushd $\{SCRIPT_DIR\}/../../ > /dev/null
exec ${cmd.command} '${cmd.args.join("' '")}'`
}

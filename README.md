# Effortless Language Servers

The goal of the *Effortless Language Servers* project is to
enable the creation of language servers for dynamic languages
based on a share language-agnostic infrastructure.

Building a language server with standard features should not take
more than a few hundred lines of code.

Currently, we support the following features:

 - semantic highlighting (Som, Newspeak, Simple Language)
 - file and workspace symbols
 - goto definition
 - code completion
 - references and highlights
 - signature help
 - hover information
 - parse errors
 - basic linting
 - CodeLens for running unit tests

## Supported Languages and IDEs

We are building on top of the [Language Server Protocol](https://github.com/Microsoft/language-server-protocol),
but provide currently only a VS code extension.

The following languages are supported:

 - [SOM], the Simple Object Machine, a language for research and teaching
 - [SOMns], a Newspeak for concurrency research language
 - [SimpleLanguage], a language to document Oracle's Truffle framework

Additionally, we also provide support for the [Debug Adapter Protocol](https://microsoft.github.io/debug-adapter-protocol/) for SOMns programs.

#### Screenshot of SOMns Semantic Highlighting

![Screencast of SOMns Semantic Highlighting](https://raw.githubusercontent.com/HumphreyHCB/SOMns-vscode/master/resources/SomHighlighting.PNG)


#### Screenshot of SOMns Syntax Highlighting


![Screenshot of SOMns syntax highlighting](https://som-st.github.io/images/vscode-somns-syntax-highlighting.png)

#### Screencast of debugging a SOMns Program

![Screencast of SOMns debug session](https://som-st.github.io/images/vscode-somns-debugger.gif)


# Development Setup

To work on the extension, you can build it with the following commands:

```bash
npm install .
npm run compile
```

When working on the extension, it is better to not install it via the
Marketplace, but link the code repo directly into the VS Code extension
directory. Please replace `$pathToCheckout` in the following example with the actual
path to your checkout:

```bash
cd ~/.vscode/extensions
ln -s /$pathToCheckout/effortless-language-servers effortless-language-servers
```

To debug the extension, open the main folder in VS code and select
"Launch Extension" in the debug menu.

Because of current restrictions in VS Code, the same project cannot be opened
twice, and it is not possible to debug the debugger and the language server at
the same time. As a work around, one can use a separate folder with symlinks:

```
.
..
.vscode -> ../SOMns-vscode/.vscode
out -> ../SOMns-vscode/out
package.json -> ../SOMns-vscode/package.json
src -> ../SOMns-vscode/src
syntaxes -> ../SOMns-vscode/syntaxes
tsconfig.json -> ../SOMns-vscode/tsconfig.json
typings -> ../SOMns-vscode/typings
typings.json -> ../SOMns-vscode/typings.json
```

## Debugging the Language Server in Java

When working on the Language Server, which provides the IDE services, it is best
to start it for instance from Eclipse.

To instruct VS code to use an already running instance of the language server,
add the following to your VS Code User Settings:

```JavaScript
"els.debugMode" : true
```

The server can also be started from the command line:

```bash
cd /$pathToCheckout/SOMns-vscode/server
./run.sh # executes the server, the console will show all communication
```

## Debugging the Debugger Adapter

To debug the Debugger Adapter, load the code in VS code, and select "Run Debugger as server" in the debugger menu.

In the configuration of the VS Code instance with the SOM code, add a
configuration that executes a SOM program, and add the `debugServer` port
as part of the configuration. This needs to be inside the config, i.e., for
instance next to the `"program"` setting.

```
  "debugServer": 4711,
```


[SOMns]: https://github.com/smarr/SOMns
[SOM]: https://som-st.github.io
[SimpleLanguage]: https://github.com/graalvm/simplelanguage

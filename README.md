# SOMns

This is a VS code extension for the [SOMns][SOMns] research language.
The extension provides support for:

 - syntax highlighting
 - parse errors
 - code navigation
 - and debugging of SOMns programs

#### Screenshot of SOMns Syntax Highlighting


![Screenshot of SOMns syntax highlighting](http://stefan-marr.de/downloads/vscode-somns-syntax-highlighting.png)

#### Screencast of debugging a SOMns Program

![Screencast of SOMns debug session](http://stefan-marr.de/downloads/vscode-somns-debugger.gif)


# Development Setup

To work on the extension, you can build it with the following commands:

```bash
npm install .
npm run compile
```

When working on the extension, it is better to not install it via the
Marketplace, but link the code repo directly into the VS Code extension
directory. Please replace `$pathToCheckout` in the following with the actual
path to your checkout:

```bash
cd ~/.vscode/extensions
ln -s /$pathToCheckout/SOMns-vscode SOMns-vscode
```

This extension is based on the [Language Server
Protocol](https://github.com/Microsoft/language-server-protocol) to connect to
a SOMns language server, which provides the IDE capabilities.

Once the setup is completed with the instructions above, you can work on it. To
for instance debug it, open the main folder in VS code, for instance with
`code .`. Now, you can run the extension from the debug menu.

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
"somns.debugMode" : true
```

The server can also be started from the command line:

```bash
cd /$pathToCheckout/SOMns-vscode/server
./run.sh # executes the server, the console will show all communication
```

# SOMns Language Server and VS Code Client

This project implements a VS Code extension that uses the [Language Server
Protocol](https://github.com/Microsoft/language-server-protocol) to connect to
a SOMns language server providing IDE capabilities.

This is somewhat liberally derived from the [VS Code
example](https://github.com/Microsoft/vscode-languageserver-node-example).

# Build, Develop, Use

To build the VS Code extension, do the following:

```bash
cd server
ant deploy  # builds and deploys the server. ant is needed, and python, etc.
cd ../client
npm install .
node ./node_modules/vscode/bin/compile # to build the client, might give an error
```

## Develop

To debug the extension, open the client in VS code, for instance with `code .`.
Now, you can run the extension from the debug menu.

Because of current restrictions in VS Code, the same project cannot be opened
twice, and it is not possible to debug the debugger and the language server at
the same time. As a work around, one can use a separate folder with symlinks:

```
.
..
.vscode -> ../client/.vscode
out -> ../client/out
package.json -> ../client/package.json
src -> ../client/src
syntaxes -> ../client/syntaxes
tsconfig.json -> ../client/tsconfig.json
typings -> ../client/typings
typings.json -> ../client/typings.json
```


## Use, and Start Language Server

For using the plugin, it can be linked into the VS Code extension directory:

```bash
cd ~/.vscode/extensions
ln -s /$pathToCheckout/SOMns-vscode/client SOMns-vscode
```

Currently, the language server is not ready for general use, so, it is meant to
be debugged easily. Therefore, it is started separately:

```bash
cd /$pathToCheckout/SOMns-vscode/server
./run.sh # executes the server, the console will show all communication
```

Now, one can open a `.som` file in VS Code, and should get parse errors, bits of
code completion, and structural navigation.

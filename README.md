# SOMns Language Server and VS Code Client

This project implements a VS Code extension that uses the [Language Server
Protocol](https://github.com/Microsoft/language-server-protocol) to connect to
a SOMns language server providing IDE capabilities.

This is somewhat liberally derived from the [VS Code
example](https://github.com/Microsoft/vscode-languageserver-node-example).

# Usage

To use/develop the VS Code extension, do the following:

```bash
cd server
ant deploy  # builds and deploys the server. ant is needed, and python, etc.
cd ../client
npm install .
code .  # requires the VS Code shell command
```

Now, you can run the extension from the debug menu.

# Change Log

## [Not Started]

 -

## [0.8.0] - Effortless Language Servers

 - completely reengineer how the language servers work based on our approach to [Effortless Language Servers](https://stefan-marr.de/2022/10/effortless-language-servers/)
 - support Newspeak, SOM, and SimpleLanguage with the key LSP features
 - update TruffleSOM and SOMns to the latest versions
 - add support for semantic highlighting

## [0.7.0] - Unpublished

 - update TruffleSOM and SOMns to the latest version as of August 2021
 - update LSP4J to latest version to support latest language server protocol
 - updated JavaScript dependencies

## [0.6.0] - 2017-12-26

 - added CodeLens for Minitests to run them on a click in the editor window
 - added basic linting for SOMns code
 - fix stepping issue in debugger
 - improve code completion, also include VM primitives
 - enable partial transmission of arrays in debugger
 - enable completion for more kinds of lexical positions

## [0.5.3] - 2017-11-26

 - fix classpath issue
 - fix race condition
 - fix lang server for use on single files

## [0.5.2] - 2017-11-25

 - load all files in workspace on startup
 - enable go-to-definition for more language constructs
 - add support for workspace symbols (cmd+shift+t)
 - updated lsp4j to 0.4.0-SNAPSHOT
 - updated SOMns to latest 0.5-dev

## [0.5.1] - 2017-08-11

 - fix extension of kernel and platform files

## [0.5.0] - 2017-08-11

 - update to SOMns 0.5 to support new syntax in the parser
   - syntax highlighting still needs to be added
 - add support for braces of array literals and auto-closing
 - change file extension to .ns

## [0.4.1] - 2017-07-10

 - fix broken plugin version 0.4, the version uploaded to the marketplace was
   not able to run the debugger
 - update LSP implementation for Java, moved to org.eclipse.lsp4j code base
   (same project as before, but now an official Eclipse project)

## [0.4.0] - 2017-07-08

 - support adaptations in SOMns' Kompos debugger protocol
 - make LSP server more robust
 - make sure LSP server can be used multiple times by using stdin/stdout normally
 - update to latest VS Code

## [0.2.0] - 2017-01-05

 - updated SOMns
   - added support for multithreading
   - simplified AST inlining
   - info on local variables and arguments
   - improved comment parsing (support for adjacent comments)
   - improved String display of values in debugger
 - fix issue that prevented breakpoints from being disabled

## [0.1.1] - 2016-12-16

 - fix document selector so that language server is actually used
 - report assignments to method arguments as errors
 - improve highlighting for class and method definitions
 - highlight comments next to slot definitions

## [0.1.0] - 2016-12-15

 - added basic language server with support for navigation and parsing errors
 - added basic syntax highlighting for SOMns files
 - added user setting `somns.debugMode` to indicate whether the extension is to
   be debugged
 - improve first debug experience
 - publish extension on the [VS Code marketplace][SOMns-vscode]

[Unreleased]:   https://github.com/smarr/SOMns-vscode/compare/v0.6.0...HEAD
[0.6.0]:        https://github.com/smarr/SOMns-vscode/compare/v0.5.3...v0.6.0
[0.5.3]:        https://github.com/smarr/SOMns-vscode/compare/v0.5.2...v0.5.3
[0.5.2]:        https://github.com/smarr/SOMns-vscode/compare/v0.5.1...v0.5.2
[0.5.1]:        https://github.com/smarr/SOMns-vscode/compare/v0.5.0...v0.5.1
[0.5.0]:        https://github.com/smarr/SOMns-vscode/compare/v0.4.1...v0.5.0
[0.4.1]:        https://github.com/smarr/SOMns-vscode/compare/v0.4.0...v0.4.1
[0.4.0]:        https://github.com/smarr/SOMns-vscode/compare/v0.2.0...v0.4.0
[0.2.0]:        https://github.com/smarr/SOMns-vscode/compare/v0.1.1...v0.2.0
[0.1.1]:        https://github.com/smarr/SOMns-vscode/compare/v0.1.0...v0.1.1
[0.1.0]:        https://github.com/smarr/SOMns-vscode/compare/8f7ae145280f3c0c2a5a264f6d6b3315589765c3...v0.1.0
[SOMns-vscode]: https://marketplace.visualstudio.com/items?itemName=MetaConcProject.SOMns

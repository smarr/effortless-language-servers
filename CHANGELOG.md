# Change Log

## [Unreleased]
 
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

[Unreleased]:   https://github.com/smarr/SOMns-vscode/compare/v0.1.1...HEAD
[0.1.1]:        https://github.com/smarr/SOMns-vscode/compare/v0.1.0...v0.1.1
[0.1.0]:        https://github.com/smarr/SOMns-vscode/compare/8f7ae145280f3c0c2a5a264f6d6b3315589765c3...v0.1.0
[SOMns-vscode]: https://marketplace.visualstudio.com/items?itemName=MetaConcProject.SOMns

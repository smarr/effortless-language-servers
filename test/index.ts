const testRunner = require('vscode/lib/testrunner');

testRunner.configure({
	ui: 'bdd',
	useColors: true,
	timeout: 50000
});

module.exports = testRunner;

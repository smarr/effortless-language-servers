import * as path from 'path';

async function main() {
	try {
		const extensionDevelopmentPath = path.resolve(__dirname, '..');
		const extensionTestsPath = path.resolve(__dirname, './index');

		await runTests({extensionDevelopmentPath, extensionTestsPath});
	} catch (err) {
		console.error(err);
		console.error('Failed to run tests');
		process.exit(0);
	}
}

main();

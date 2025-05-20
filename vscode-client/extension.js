const path = require('path');
const { workspace } = require('vscode');
const { LanguageClient, TransportKind } = require('vscode-languageclient/node');
const { execSync } = require('child_process');

let client;

const getJavaHome = () => {
	const javaHome = process.env.JAVA_HOME;
	if (javaHome) {
		return javaHome;
	}

	try {
		// all i have is a mac at the moment
		return execSync('/usr/libexec/java_home').toString().trim();
	} catch (error) {
		throw new Error('Java home not set. Please set JAVA_HOME environment variable.');
	}
};

function activate(context) {
	const javaHome = getJavaHome();
	const javaBinary = path.join(javaHome, 'bin', 'java');
	const jarPath = path.join(__dirname, 'server', 'dgdlpc-language-server-1.0.0.jar');

	const serverOptions = {
		command: javaBinary,
		args: ['-jar', jarPath],
		transport: TransportKind.stdio,
		options: {
			env: { ...process.env, DEBUG: '*' }
		}
	};

	const clientOptions = {
		documentSelector: [{ scheme: 'file', language: 'lpc' }],
		synchronize: {
			fileEvents: workspace.createFileSystemWatcher('**/*.c')
		},
		markdown: { isTrusted: true },
		initializationOptions: { diagnosticsMode: 'push' }
	};

	client = new LanguageClient(
		'lpcLanguageServer',
		'LPC Language Server',
		serverOptions,
		clientOptions
	);

	try {
		client.start();
	} catch (error) {
		console.error('Failed to start client:', error);
		throw error;
	}

	context.subscriptions.push(client);
}

function deactivate() {
	if (!client) {
		return;
	}
	return client.stop();
}

module.exports = { activate, deactivate };

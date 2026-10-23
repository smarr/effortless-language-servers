import { getJavaMajorVersion } from "./command-line";

export const MIN_JAVA_VERSION = 25;

export interface FoundJava {
	/** Undefined for the `java` on the PATH. */
	javaHome: string | undefined;
	/** Where we found it, for instance `JAVA_HOME`. */
	source: string;
	major: number;
}

export interface JavaSearchSettings {
	/** Our own `els.javaHome` setting. */
	elsJavaHome?: unknown;
	/** The Java extension's `java.jdt.ls.java.home` setting. */
	javaExtensionHome?: unknown;
}

/** The dependencies of the search, to be replaced in tests. */
export interface JavaSearchEnvironment {
	env: { [key: string]: string | undefined };
	/** The major version of the Java, or undefined if it isn't usable. */
	getMajorVersion: (javaHome: string | undefined) => number | undefined;
}

const defaultEnvironment: JavaSearchEnvironment = {
	env: process.env,
	getMajorVersion: getJavaMajorVersion
};

/**
 * Search for a compatible Java in the following order, and use the first one
 * that is compatible:
 *  1. the `els.javaHome` setting
 *  2. the Java extension's `java.jdt.ls.java.home` setting
 *  3. the `JDK_HOME` and `JAVA_HOME` environment variables
 *  4. the `java` on the PATH
 *
 * @param warn called for an explicitly configured Java that isn't compatible
 */
export function findJava(settings: JavaSearchSettings,
		warn: (message: string) => void = () => {},
		environment: JavaSearchEnvironment = defaultEnvironment): FoundJava | undefined {
	const candidates: { javaHome: string | undefined, source: string, explicit: boolean }[] = [];
	if (typeof settings.elsJavaHome === "string" && settings.elsJavaHome) {
		candidates.push({ javaHome: settings.elsJavaHome, source: "els.javaHome", explicit: true });
	}
	if (typeof settings.javaExtensionHome === "string" && settings.javaExtensionHome) {
		candidates.push({ javaHome: settings.javaExtensionHome, source: "java.jdt.ls.java.home", explicit: false });
	}
	for (const variable of ["JDK_HOME", "JAVA_HOME"]) {
		const value = environment.env[variable];
		if (value) {
			candidates.push({ javaHome: value, source: variable, explicit: false });
		}
	}
	candidates.push({ javaHome: undefined, source: "PATH", explicit: false });

	for (const c of candidates) {
		const major = environment.getMajorVersion(c.javaHome);
		if (major !== undefined && major >= MIN_JAVA_VERSION) {
			return { javaHome: c.javaHome, source: c.source, major };
		}
		if (c.explicit) {
			warn(`The Java configured in ${c.source} (${c.javaHome}) could not be run, or is older than Java ${MIN_JAVA_VERSION}. Searching for another one.`);
		}
	}
	return undefined;
}

import { getMajorVersionFromJavaVersionString } from "../src/command-line";
import { findJava, JavaSearchEnvironment } from "../src/java-discovery";
import { expect } from "chai";

describe("Java Detection Tests", () => {

  const javaVersionOutput = [
    {
      major: "17",
      string: `openjdk 17.0.3 2022-04-19
              OpenJDK Runtime Environment Temurin-17.0.3+7 (build 17.0.3+7)
              OpenJDK 64-Bit Server VM Temurin-17.0.3+7 (build 17.0.3+7, mixed mode, sharing)`
    },
    {
      major: "1",
      string: `java version "1.8.0_311"
              Java(TM) SE Runtime Environment (build 1.8.0_311-b11)
              Java HotSpot(TM) 64-Bit Server VM (build 25.311-b11, mixed mode)`
    },
    {
      major: "17",
      string: `java version "17.0.2" 2022-01-18 LTS
              Java(TM) SE Runtime Environment GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05)
              Java HotSpot(TM) 64-Bit Server VM GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05, mixed mode, sharing)`
    },
    {
      major: "11",
      string: `java version "11.0.13" 2021-10-19 LTS
              Java(TM) SE Runtime Environment 18.9 (build 11.0.13+10-LTS-370)
              Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.13+10-LTS-370, mixed mode)`
    },
    {
      major: "11",
      string: `openjdk version "11.0.15" 2022-04-19
              OpenJDK Runtime Environment Temurin-11.0.15+10 (build 11.0.15+10)
              OpenJDK 64-Bit Server VM Temurin-11.0.15+10 (build 11.0.15+10, mixed mode)`
    },
    {
      major: "17",
      string: `openjdk version "17.0.3" 2022-04-19
              OpenJDK Runtime Environment Temurin-17.0.3+7 (build 17.0.3+7)
              OpenJDK 64-Bit Server VM Temurin-17.0.3+7 (build 17.0.3+7, mixed mode, sharing)`
    },
    {
      major: "17",
      string: `java version "17.0.2" 2022-01-18 LTS
              Java(TM) SE Runtime Environment GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05)
              Java HotSpot(TM) 64-Bit Server VM GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05, mixed mode, sharing)`
    },
    {
      major: "17",
      string: `java 17.0.2 2022-01-18 LTS
              Java(TM) SE Runtime Environment GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05)
              Java HotSpot(TM) 64-Bit Server VM GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05, mixed mode, sharing)`
    }
  ];

  describe("Java version detection", () => {
    it("should detected the expected major version from the version string", () => {
      for (const v of javaVersionOutput) {
        const major = getMajorVersionFromJavaVersionString(v.string);
        expect(major).to.equal(v.major);
      }
    });
  });

  describe("Java search", () => {
    /** Javas by home directory, the one on the PATH is "PATH". */
    function environment(javas: { [home: string]: number },
        env: { [key: string]: string } = {}): JavaSearchEnvironment {
      return {
        env,
        getMajorVersion: home => javas[home ?? "PATH"]
      };
    }

    it("should use els.javaHome first", () => {
      const java = findJava({ elsJavaHome: "/jdk26", javaExtensionHome: "/jdk25" },
        undefined, environment({ "/jdk26": 26, "/jdk25": 25, "PATH": 25 }, { JAVA_HOME: "/jdk25" }));
      expect(java).to.deep.equal({ javaHome: "/jdk26", source: "els.javaHome", major: 26 });
    });

    it("should warn about an incompatible els.javaHome, and continue", () => {
      const warnings: string[] = [];
      const java = findJava({ elsJavaHome: "/jdk17" }, w => warnings.push(w),
        environment({ "/jdk17": 17, "/jdk25": 25 }, { JAVA_HOME: "/jdk25" }));
      expect(java).to.deep.equal({ javaHome: "/jdk25", source: "JAVA_HOME", major: 25 });
      expect(warnings).to.have.lengthOf(1);
      expect(warnings[0]).to.contain("/jdk17");
    });

    it("should use the Java extension's setting before the environment", () => {
      const java = findJava({ javaExtensionHome: "/jdk25" }, undefined,
        environment({ "/jdk25": 25, "/jdk26": 26 }, { JAVA_HOME: "/jdk26" }));
      expect(java?.source).to.equal("java.jdt.ls.java.home");
    });

    it("should use JDK_HOME before JAVA_HOME, and skip old ones", () => {
      let java = findJava({}, undefined,
        environment({ "/a": 25, "/b": 26 }, { JDK_HOME: "/a", JAVA_HOME: "/b" }));
      expect(java?.source).to.equal("JDK_HOME");

      java = findJava({}, undefined,
        environment({ "/a": 21, "/b": 26 }, { JDK_HOME: "/a", JAVA_HOME: "/b" }));
      expect(java?.source).to.equal("JAVA_HOME");
    });

    it("should use the java on the PATH", () => {
      const java = findJava({}, undefined, environment({ "PATH": 25 }));
      expect(java).to.deep.equal({ javaHome: undefined, source: "PATH", major: 25 });
    });

    it("should report when there is no compatible Java", () => {
      expect(findJava({ elsJavaHome: 42 }, undefined,
        environment({ "/jdk21": 21, "PATH": 17 }, { JAVA_HOME: "/jdk21" }))).to.be.undefined;
      expect(findJava({}, undefined, environment({}))).to.be.undefined;
    });
  });
});

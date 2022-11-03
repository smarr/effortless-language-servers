import { getMajorVersionFromJavaVersionString } from "../src/command-line";
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
});

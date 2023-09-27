# Developing using vscode

## For vscode, you will need the following extensions:

- java extension pack
- language support for java(TM) by Red Hat
- Maven for Java
- Java Test Runner
- Project Manager for Java
- Debugger for Java

## Add the following to projects to your workspace, base folder is called avi-msgconverter:

Create the folder `avi-msgconverter`, that is the base folder. In folder `avi-msgconverter`, clone the following projects:

- git@gitlab.com:opengeoweb/avi-msgconverter/fmi-avi-messageconverter.git
- git@gitlab.com:opengeoweb/avi-msgconverter/fmi-avi-messageconverter-tac.git

So your folder structure looks like:

avi-msgconverter

- fmi-avi-messageconverter
- fmi-avi-messageconverter-tac

## Set the right JRE (1.8) in vscode:

It is very important that you set the right runtime in vscode, otherwise java versions get mixed up. It should be set to 1.8, and it should generate class files with version 52.

To set the right JRE in vscode: bring up the Command Palette `(Ctrl+Shift+P)` and use the command <b>Java: Configure Java Runtime</b>. Follow the instructions (download and install OpenJDK 8). The file has to be unpacked, preferably into the main folder avi-msgconverter.

Your folder structure should look like:

```
├── ~/code/gitlab/opengeoweb/avi-msgconverter
    ├── fmi-avi-messageconverter
    ├── fmi-avi-messageconverter-tac
    ├── jdk8u275-b01
```

From the avi-msgconverter you can do:

```
export JAVA_HOME=`pwd`/jdk8u275-b01
export PATH=${JAVA_HOME}/bin:${PATH}
```

Check with `mvn --version ` to see if indeed java 1.8 is listed.

- Stop vscode, and start vscode with this correct JAVA_HOME environmnet.
- Open the fmi-avi-messageconverter
- Click on maven, and press refresh.
- Then bring up the Command Palette `(Ctrl+Shift+P)`, select `Java: Configure Java Runtime` and set the Java Runtime to 1.8 under the `Maven/Gradle Projects` section
- In vscode, save the workspace and close vscode again. Next is to install dependencies using mvn, without the maven plugin from vscode interfering.

## Install dependencies using the profile for snapshots

```
cd fmi-avi-messageconverter
rm -rf ~/.m2/
rm -rf target
mvn clean install -U -P fmidev -s ./fmidev-settings.xml
```

## Start vscode and add the two repos to the workspace

Start vscode in avi-msgconverter and add both subfolders to your workspace.
Thats it! You can now start developing and run the tests from vscode.

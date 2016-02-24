[CoExpNetViz web site](http://bioinformatics.psb.ugent.be/webtools/coexpr/index.php)

## User manual
[User manual (pdf)](http://bioinformatics.psb.ugent.be/webtools/coexpr/files/plugin_manual_1_9.6.15.pdf)

### Installation
Put CoExpNetViz-*.jar in ~/CytoscapeConfiguration/3/apps/installed

## Developer manual
[Cytoscape app (~plugins) development](http://wiki.cytoscape.org/Cytoscape_3/AppDeveloper)

An introduction to Cytoscape app development: http://opentutorials.cgl.ucsf.edu/index.php/Tutorial:Create_a_Bundle_App_Using_IDE

Cytoscape cookbook: http://wiki.cytoscape.org/Cytoscape_3/AppDeveloper/Cytoscape_3_App_Cookbook

Versioning scheme: semantic versioning, i.e. major.minor.maintenance[-SNAPSHOT], e.g. 1.0.0-SNAPSHOT. [Explanation of SNAPSHOT](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN401), [Major, minor, maintenance usage](https://python-packaging-user-guide.readthedocs.org/en/latest/distributing/#semantic-versioning-preferred).

The latest Cytoscape (v3.2 as of writing) is compatible with Java 7 (i.e. version 1.7), so we develop for Java 7. We require at least Java 7 update 6 (which is from 2012-06-12, i.e. ancient). 

Cytoscape uses Swing, so we use a GUI library compatible with Swing such as Pivot.

### Compiling and setup

We use Maven.

JavaFX isn't in the maven repositories (https://community.oracle.com/thread/2309062?tstart=0),
though it is included in the JRE starting from Java 7 Update 6. So, we 'only' support Java 7 update 6 or newer.
 
We do need to depend on it so that `mvn eclipse:eclipse` includes it in the compilation classpath, 
to enable autocomplete features. In order to do so, install JavaFX into your local Maven repository
by running the following:
 
    jar_location=$(find `readlink -e $JAVA_HOME` -name jfxrt.jar)
    version=`grep version $JAVA_HOME/jre/lib/javafx.properties | sed 's/.*=//'`
    mvn install:install-file -Dfile=$jar_location -DgroupId=com.oracle.javafx -DartifactId=javafx -Dversion=$version -Dpackaging=jar
	
We support JavaFX 2.0 and above.

- For any `mvn` command, be sure to be in the directory with `pom.xml`.
- To get an eclipse project, run: `mvn eclipse:clean; mvn eclipse:eclipse`
- To get a jar, run: `mvn package`. The jar is in the target dir.

[CoExpNetViz web site](http://bioinformatics.psb.ugent.be/webtools/coexpr/index.php)

## User manual
[User manual (pdf)](http://bioinformatics.psb.ugent.be/webtools/coexpr/files/plugin_manual_1_9.6.15.pdf)

### Installation
Put CoExpNetViz-*.jar in ~/CytoscapeConfiguration/3/apps/installed

## Developer manual
[Cytoscape app (~plugins) development](http://wiki.cytoscape.org/Cytoscape_3/AppDeveloper)

An introduction to Cytoscape app development: http://opentutorials.cgl.ucsf.edu/index.php/Tutorial:Create_a_Bundle_App_Using_IDE

Versioning scheme: semantic versioning, i.e. major.minor.maintenance[-SNAPSHOT], e.g. 1.0.0-SNAPSHOT. [Explanation of SNAPSHOT](https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN401), [Major, minor, maintenance usage](https://python-packaging-user-guide.readthedocs.org/en/latest/distributing/#semantic-versioning-preferred).

The latest Cytoscape (v3.2 as of writing) is compatible with Java 7 (i.e. version 1.7), so we develop for Java 7.

Cytoscape uses Swing, so we use a GUI library compatible with Swing such as Pivot.

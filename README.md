CoExpNetViz Cytoscape app, see its [Cytoscape app page] for an explanation of
what it is and how to use it.

### Links
- [Cytoscape app page]
- [GitHub]

### Development docs
Docs for developing the app itself, users may ignore this.

Links:

- [Cytoscape dev wiki]
- [Cytoscape cookbook]
- [Cytoscape javadoc]
- [Cytoscape source code on GitHub][Cytoscape source] in case the docs weren't clear.

#### Setting up a dev env
Mostly boils down to following the [Cytoscape app ladder]. Here's a short
version for Debian:

- `apt install openjdk-...-jdk`, peek at the cytoscape install step to find out
  which Java version Cytoscape requires.
- `apt install maven`
- [Install Cytoscape](https://cytoscape.org/download.html). I clicked
  'Other platforms' on linux and downloaded a tgz; the sh on the other hand
  might set up PATH and desktop entries for you, but perhaps harder to
  uninstall later.
- [Install Eclipse IDE for Java Developers] by clicking Download Packages near
  Eclipse IDE and selecting it from the list. Or install Intellij; see the
  [Cytoscape dev wiki].
- Clone this repo
- Create the eclipse project files by running mvn inside the dir with
  `pom.xml`: `mvn eclipse:clean; mvn eclipse:eclipse`. When changing pom.xml,
  you may need to rerun `mvn eclipse:eclipse`, e.g. when changing dependencies.
- In Eclipse menu, choose File > Import, select General > Existing projects
  into workspace. As root dir pick the parent dir of the cloned repo. Select
  CoExpNetViz and finish.

To build the jar, run `mvn package`. The jar is in the target dir. To install
it, add a symlink in `~/CytoscapeConfiguration/3/apps/installed` to the created
jar.

Debugging:

- [Cytoscape reloads the app] when it's rebuilt without needing to restart
  Cytoscape, even when symlinked like above. If for some reason that doesn't
  work, you could try [debugging from Eclipse], which also might not require
  rebuilding the app in its entirety.
- Cytoscape logs what it's doing, e.g. whether it reloaded the app, in
  `~/CytoscapeConfiguration/3/framework-cytoscape.log`.
- [How to log]. WARNING and above log messages end up in Cytoscape's
  stdout/err. Not sure where INFO and DEBUG go to, they do not appear in the
  log file.


#### Releasing a new version
- Bump the version in pom.xml, be sure to remove `-SNAPSHOT`. SNAPSHOT is
  commonly understood by tools to be a dev version and they may refresh
  accordingly when they notice a new snapshot is pushed.
- Build the jar
- Upload it to the [Cytoscape app page]
- Bump to the next patch version and append `-SNAPSHOT` again.


[Cytoscape app page]: https://apps.cytoscape.org/apps/coexpnetviz
[GitHub]: https://github.com/CoExpNetViz/CoExpNetViz
[Cytoscape app ladder]: https://github.com/cytoscape/cytoscape/wiki/Cytoscape-App-Ladder
[Cytoscape cookbook]: http://wikiold.cytoscape.org/Cytoscape_3/AppDeveloper/Cytoscape_3_App_Cookbook
[Cytoscape javadoc]: http://code.cytoscape.org/javadoc/current_release/overview-summary.html
[Cytoscape dev wiki]: https://github.com/cytoscape/cytoscape/wiki
[Cytoscape source]: https://github.com/cytoscape/cytoscape-impl
[Cytoscape reloads the app]: https://github.com/cytoscape/cytoscape/wiki/Java-Hotswap
[Install Eclipse]: http://www.eclipse.org/downloads/
[Debugging from eclipse]: https://github.com/cytoscape/cytoscape/wiki/Launch-and-Debug-from-Eclipse
[How to log]: http://wikiold.cytoscape.org/Cytoscape_3/AppDeveloper/Cytoscape_3_App_Cookbook#Logging

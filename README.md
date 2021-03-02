CoExpNetViz Cytoscape app, see its [Cytoscape app page] for an explanation of
what it is and how to use it.

### Links
- [Cytoscape app page]
- [GitHub]

### Development docs
Some tips for developing the app.

Links:

- [Cytoscape dev wiki]
- [Cytoscape cookbook]
- [Cytoscape javadoc]
- [Cytoscape source code on GitHub][Cytoscape source] in case the docs weren't clear.

Setting up your dev env mostly boils down to following the [Cytoscape app
ladder]. Here's a short version for Debian:

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
- In Eclipse open the Import dialog: Existing maven project, as root dir pick
  the parent dir of the cloned repo. Select CoExpNetViz' pom, add to a new
  working set 'CoExpNetViz' and finish.

Debugging/testing: [Cytoscape reloads the app] when it's rebuilt without
needing to restart Cytoscape. If for some reason that doesn't work, you could
try [debugging from Eclipse], which also might not require rebuilding the app
in its entirety.

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

CoExpNetViz creates a network which visualises the co-expression between a
group of bait genes and all other genes of one or more species; optionally,
grouping nodes by gene families.

### Links
- [Cytoscape app page]
- [GitHub]


### Installation
CoExpNetViz requires the coexpnetviz conda package. The app will install it for you
in a conda environment named `coexpnetviz{backend_major_version}`. For this to work
you need to install conda, either [Miniconda][install miniconda] or the larger
[Anaconda][install anaconda]. (When updating to a new app version which uses a
newer backend, you may delete the conda environment of the older backend to
save some space).

Other than that just install the app from the [Cytoscape app page] by clicking
Upgrade. If you do not see a button, try [the cytoscape docs][add cytoscape
app] instead.


### Tutorial
You can create a co-expression network by filling in a dialog or by executing a
command in [scripts or the Cytoscape command line][cytoscape automation]. The
latter is convenient to avoid retyping inputs. For help about the command,
execute `help coexpnetviz` and `help coexpnetviz create_network`. Remember to
quote command arguments containing `,` or `;` with `"`, for example:

```
coexpnetviz create_network baitsSource=File baitsFile=/home/user/example/baits_two_species.txt expressionMatrices="/home/user/example/arabidopsis_dataset.txt;/home/user/coexpnetviz/example/tomato_dataset.txt" networkName=my_network geneFamiliesFile=/home/user/example/plaza_mono_and_dicot_v3_fams.yml
```

TODO gene fams won't be part of the example tgz but instead the tutorial will
show how to download plaza gene fams and convert them to the desired format.
For now please ignore the gene families arg to follow the tutorial.

Files for the above command can be found in
[coexpnetviz_example.tgz][example files]. If your current Cytoscape session has
never been saved before, you'll also need to provide an `outputDir` in the above
command as it defaults to a subdirectory of your session location. The output
directory stores coexpnetviz.log and intermediate results.

The tutorial will continue with the dialog instead. To open the dialog, select
`Apps > Create co-expression network` from the Cytoscape menu. You can hover
over each input field for a tooltip. CoExpNetViz needs a subset of genes to use
as baits, other genes will be compared against the baits to check for
co-expression. Please fill in the following:

- Network name: `my_network`
- Baits source: `File`
- Baits file: `/home/user/example/baits_one_species.txt`
- Expression matrices:
  `/home/user/example/arabidopsis_dataset.txt;/home/user/coexpnetviz/example/tomato_dataset.txt`.
  Do not add quotes in the dialog; that's only necessary in the command line.
- Gene families file: `/home/user/example/plaza_mono_and_dicot_v3_fams.yml`

The percentiles can be tweaked to move the cutoff points for genes to be
considered co-expressed. E.g. increasing the upper percentile from 95 to 97
will increase the minimum correlation for us to consider 2 genes co-expressed;
i.e. you will see less green edges (edges will be explained below). Lower the
upper percentile and you'll get more green edges. The lower percentile will
affect anti-correlated / red edges, e.g. lowering it from 5 to 3 will reduce
the amount of red edges.

Submit the dialog. CoExpNetViz will install its backend with conda for you (and
keeps it up to date) and creates the network.


#### Output
The created network has 3 types of nodes:

- bait: represents a single bait gene. They are white and diamond shaped.
- family: represents the genes of a family which (anti-)correlate with a bait.
- gene: represents a gene which is neither a bait, nor appears in a family, but
  does (anti-)correlate with a bait.

Genes which do not (anti-)correlate with any bait are not represented by any
node. All other genes are represented by exactly one node in the network.

Nodes are partitioned into partitions, denoted by their color by the baits they
(anti-)correlate to. Bait nodes are the white partition. The CoExpNetViz layout
groups nodes by their partition. Partitions containing just a single node are
grouped together (but each node will have a different colour and partition id).
You can always restore this layout (in case you tried a different one) by
selecting `Layout > CoExpNetViz` in the Cytoscape menu.

Nodes have the following attributes:

- id: Unique node id.
- label: Labels are unique (but often don't make valid identifiers, e.g. they
  may contain spaces).
- colour: Node display colour.
- type: `bait`, `family` or `gene`.
- family: The family the genes represented by this node are part of, if any.
- genes: Genes represented by the node. For family nodes these are all genes of
  the family which sufficiently (anti-)correlate with a bait.
- partition_id: Id of the partition the node is part of. Bait nodes form a
  partition. Other nodes are partitioned by the subset of baits they correlate
  with. I.e. nodes of the same partition correlate to the same baits.

Edges between a bait node and another node denotes the bait is sufficiently
(anti-)correlated to the gene expression of the gene(s) represented by the
other node. A green edge denotes correlation, a red edge denotes
anti-correlation. The darker the color, the stronger the correlation. Edges
have a `max_correlation` attribute, which is the strongest Pearson correlation
of the bait and each gene represented by the non-bait node (i.e. excluding
correlation values that were cut off by the tresholds).

Dotted edges between baits denote homology, i.e. they are of the same family.

There are no self (`x-x`), synonymous (`x-y` and `y-x`) or duplicate
(`x-y` and `x-y`) edges.

The following files are saved in a directory `{network_name}_{datetime}` next
to your Cytoscape session file (the `.cys` file):

- `{expression_matrix}.correlation_matrix.txt`: Correlations between genes and
  baits of the expression matrix before any cutoffs have been applied.
- `{expression_matrix}.sample_matrix.txt`: Correlations between a sample of
  genes of the expression matrix. This sample is used to derive the matrix'
  percentiles.
- `{expression_matrix}.sample_histogram.png`: Frequency of sample matrix
  values. The red vertical lines are the percentiles. Correlations ranging
  between the red lines are cut off.
- `{expression_matrix}.sample_cdf.png`: Cumulative distribution function
  (discrete) of the sample matrix values. The red horizontal lines are the
  percentiles used, divided by 100. The points where the red lines first
  intersect with the function, roughly (depending on how jumpy the function is
  in that part) mark the cutoff thresholds (imagine a vertical line at the
  intersection and read the correlation value on the X-axis).
- `percentile_values.txt`: Lower and upper percentile cutoffs used for each
  expression matrix.
- `significant_correlations.txt`: Correlations between genes and baits after
  cutoffs have been applied.
- `coexpnetviz.log`: Further details of the run: coexpnetviz version, warnings, ...


### Input formats

#### Bait files
Examples:

```
PGSC0003DMP400054926;Solyc02g014730;Solyc04g011600;AT5G41040;AT5G23190;AT3G11430
```

```
PGSC0003DMP400054926
Solyc02g014730
Solyc04g011600
AT5G41040
AT5G23190
AT3G11430
```

or even (you probably shouldn't rely on this too much):

```
PGSC0003DMP400054926
PGSC0003DMP400018704
Solyc03g097500
Solyc02g014730, Solyc04g011600			AT5G41040 AT5G23190	AT3G11430
```

#### Expression matrix file
A matrix file is a CSV file that starts with a header line of column names. The
first column are the gene names; its column name is ignored and may be empty.
The other columns are the expression values. Each column represents a
condition/experiment. So, each row is a gene and its expression levels under
various conditions.

The separator and whether values are quoted is autodetected (best-effort). Line
endings and encoding are also autodetected. Tabs are a popular separator (TSV
file), but it risks hard to spot problems such as accidentally mixing tabs and
spaces, accidentally including a double tab in some places or adding trailing
tabs/whitespace. If you have trouble with your TSV file, try using `,` as
separator instead (i.e. a CSV file) and check for the above problems.

For example:

```
gene,Heinz_bud,Heinz_flower,Heinz_leaf
Solyc02g085950,12710.51,10259.24,122316.7
Solyc10g075130,22209.16,46884,.78
Solyc04g071610,4880.03,2966.38,310.43
```

In the example, Solyc04g071610 has an expression level of 310.43 in the leaf.

#### Gene families file
A YAML file which maps family names to the gene names it contains. The
easiest and most robust way of generating YAML is to use a [YAML library][YAML]
(see Projects) for your favourite programming language.

Gene names should match the names used in your expression matrices. Genes
should not appear in multiple families, however it is fine if a gene does not
appear in any family.

Examples:

```
'family1': ['gene1', 'gene2']
'family2': ['gene3', 'gene4', 'gene5']
```

```
family1:
  - gene1
  - gene2

family2:
  - gene3
  - gene4
  - gene5
```


### Algorithm
First, the algorithm obtains, for each gene expression matrix, all
co-expression values (i.e. correlations) between the baits (present in the
matrix) and other genes (of the matrix).

Next, for each gene expression matrix, a sample of rows is taken and
co-expression is measured between all genes in the sample. The lower and upper
percentile of the distribution of co-expression values are taken. Genes are
considered co-expressed if their co-expression value is either less (or equal)
to the lower percentile or greater (or equal) to the upper percentile of the
corresponding expression matrix.

Having obtained the co-expression values between all baits and other genes, the
nodes and edges of the resulting network are built. There are 3 types of nodes:
bait, gene and family nodes. Respectively they represent a bait gene, a
non-bait gene and a gene family. Each gene corresponds to exactly one node.
When gene families are provided, non-bait genes are grouped into family nodes
and non-bait genes not appearing in any family become gene nodes. Gene and
family nodes which aren't co-expressed (correlated or anti-correlated) with any
bait are dropped from the network.

Next, bait-bait and bait-gene correlation edges are added between bait/gene
nodes whose genes are co-expressed, as well as between bait and family nodes
where at least one of the family's genes is co-expressed with the corresponding
bait. There are no edges between gene nodes.

Then, gene and family nodes are grouped into partitions in such a way that all
nodes in a partition are co-expressed with the same set of baits.

Finally, nodes and edges are annotated with attributes such as which genes of a
family node are actually co-expressed.

### Development docs
Docs for developing the app itself, users can stop reading beyond this point.

Links:

- [Cytoscape dev wiki]
- [Cytoscape cookbook]
- [Cytoscape javadoc]
- [Cytoscape source code on GitHub][Cytoscape source] in case the docs weren't clear.

#### Setting up a dev env
Mostly boils down to following the [Cytoscape app ladder]. Here's a short
version for Debian:

- `apt install openjdk-...-jdk`, peek at the Cytoscape install step to find out
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


[Add cytoscape app]: https://manual.cytoscape.org/en/stable/App_Manager.html
[Cytoscape app page]: https://apps.cytoscape.org/apps/coexpnetviz
[Cytoscape app ladder]: https://github.com/cytoscape/cytoscape/wiki/Cytoscape-App-Ladder
[Cytoscape automation]: https://manual.cytoscape.org/en/stable/Command_Tool.html
[Cytoscape cookbook]: http://wikiold.cytoscape.org/Cytoscape_3/AppDeveloper/Cytoscape_3_App_Cookbook
[Cytoscape javadoc]: http://code.cytoscape.org/javadoc/current_release/overview-summary.html
[Cytoscape dev wiki]: https://github.com/cytoscape/cytoscape/wiki
[Cytoscape source]: https://github.com/cytoscape/cytoscape-impl
[Cytoscape reloads the app]: https://github.com/cytoscape/cytoscape/wiki/Java-Hotswap
[Debugging from eclipse]: https://github.com/cytoscape/cytoscape/wiki/Launch-and-Debug-from-Eclipse
[GitHub]: https://github.com/CoExpNetViz/CoExpNetViz
[Example files]: http://bioinformatics.psb.ugent.be/webtools/coexpr/index.php?__controller=ui&__action=get_example_files
[How to log]: http://wikiold.cytoscape.org/Cytoscape_3/AppDeveloper/Cytoscape_3_App_Cookbook#Logging
[Install anaconda]: https://docs.anaconda.com/anaconda/install/index.html
[Install Eclipse]: http://www.eclipse.org/downloads/
[Install miniconda]: https://docs.conda.io/en/latest/miniconda.html
[Open an issue]: https://github.com/CoExpNetViz/CoExpNetViz/issues
[YAML]: https://yaml.org/

package be.samey.cynetw;

import org.cytoscape.work.Tunable;

public class CevGroupAttributesLayoutContext {
    /*
     Layout parameters:
     - spacingx: Horizontal spacing (on the x-axis) between two partitions in a row.
     - spacingy: Vertical spacing (on the y-axis) between the largest partitions of two rows.
     - maxwidth: Maximum width of a row
     - minrad:   Minimum radius of a partition.
     - radmult:  The scale of the radius of the partition. Increasing this value
     will increase the size of the partition proportionally.
    //TODO: improve descriptions
     */

    @Tunable(description = "Which column to use to group baits")
    public String speciesAttribute = "Species";
    @Tunable(description = "Horizontal spacing between two unconnected nodes")
    public double nspacingx = 80.0;
    @Tunable(description = "Vertical spacing between two unconnected nodes")
    public double nspacingy = 40.0;
    @Tunable(description = "Maximum width for group of unconnected nodes")
    public double maxwidths = 800.0;
    @Tunable(description = "Horizontal spacing between two partitions in a row")
    public double spacingx = 400.0;
    @Tunable(description = "Vertical spacing between the largest partitions of two rows")
    public double spacingy = 400.0;
    @Tunable(description = "Vertical spacing between two connected components")
    public double ccspacingy = 800.0;
    @Tunable(description = "Maximum width of a row")
    public double maxwidth = 10000.0;
    @Tunable(description = "Minimum width of a partition")
    public double minrad = 100.0;
    @Tunable(description = "Scale of the radius of the partition")
    public double radmult = 50.0;
    @Tunable(description = "How far the baits are from the connected component center")
    public double enlargef = 2.0;
    @Tunable(description = "How large the radius is for baits in the same group")
    public double benlargef = 5.0;
}

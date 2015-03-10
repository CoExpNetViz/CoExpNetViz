package be.samey.cynetw;

import be.samey.model.CoreStatus;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class CevGroupAttributesLayout extends AbstractLayoutAlgorithm {

    /**
     * Creates a new GroupAttributesLayout object.
     * @param undo
     */
    public CevGroupAttributesLayout(UndoSupport undo) {
        super(CoreStatus.COMP_LAYOUT_NAME, CoreStatus.HUMAN_LAYOUT_NAME, undo);
    }

    public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String attrName) {
        return new TaskIterator(new CevGroupAttributesLayoutTask(toString(), networkView, nodesToLayOut, (CevGroupAttributesLayoutContext) context, attrName, null, undoSupport));
    }

    public TaskIterator createTaskIterator(CyNetworkView networkView, Object context, Set<View<CyNode>> nodesToLayOut, String attrName, String baitName) {
        return new TaskIterator(new CevGroupAttributesLayoutTask(toString(), networkView, nodesToLayOut, (CevGroupAttributesLayoutContext) context, attrName, baitName, undoSupport));
    }

    @Override
    public Set<Class<?>> getSupportedNodeAttributeTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();

        ret.add(Integer.class);
        ret.add(Double.class);
        ret.add(String.class);
        ret.add(Boolean.class);

        return ret;
    }

    @Override
    public CevGroupAttributesLayoutContext createLayoutContext() {
        return new CevGroupAttributesLayoutContext();
    }

    @Override
    public boolean getSupportsSelectedOnly() {
        return true;
    }

}

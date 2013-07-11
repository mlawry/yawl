package org.yawlfoundation.yawl.editor.ui.properties.editor;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import org.yawlfoundation.yawl.editor.ui.actions.element.FlowPriorityDialog;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLFlowRelation;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.properties.NetTaskPair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 12/07/12
 */
public class SplitPredicatePropertyEditor extends DialogPropertyEditor {

    private NetTaskPair netTaskPair;


    public SplitPredicatePropertyEditor() {
        super(new DefaultCellRenderer());
    }

    public Object getValue() {
        return netTaskPair;
    }

    public void setValue(Object value) {
        netTaskPair = (NetTaskPair) value;
        String simpleText = netTaskPair != null ? netTaskPair.getSimpleText() : "None";
        ((DefaultCellRenderer) label).setValue(simpleText);
    }


    protected void showDialog() {
        FlowPriorityDialog dialog = new FlowPriorityDialog();
        dialog.setTask(netTaskPair.getTask(), netTaskPair.getGraph());
        dialog.setVisible(true);
        NetTaskPair oldPair = netTaskPair;
        netTaskPair = new NetTaskPair(oldPair.getTask(), oldPair.getGraph());
        netTaskPair.setSimpleText(getText(oldPair.getTask()));
        firePropertyChange(oldPair, netTaskPair);
    }


    private String getText(YAWLTask task) {
        if (task == null || task.getOutgoingFlowCount() == 0) return "None";
        List<String> predicates = new ArrayList<String>();
        for (YAWLFlowRelation flow : task.getOutgoingFlows()) {
            String predicate = flow.getPredicate();
            if (predicate != null) predicates.add(predicate);
        }
        return predicates.isEmpty() ? "None" : predicates.toString();
    }

}

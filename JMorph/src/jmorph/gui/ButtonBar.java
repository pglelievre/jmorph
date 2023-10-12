package jmorph.gui;

import gui.MenuTaskButton;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import jmorph.JMorph;
import jmorph.menutasks.ChangeSampleMenuTask;
import tasks.MenuTask;

/** A panel for putting buttons on for providing common tasks.
 * @author Peter Lelievre
 */
public final class ButtonBar extends JPanel {
    private static final long serialVersionUID = 1L;

    // ------------------ Properties ------------------

    private JMorph controller;

    // Button objects:
    private JButton bAllSamples;
    private ArrayList<MenuTaskButton> taskButtons = new ArrayList<>();

    // ------------------ Constructor ------------------

    /** Creates the panel.
     * @param con The JMorph controller object.
     */
    public ButtonBar(JMorph con) {

        super();
        controller = con;

        // Make and add all the buttons:
        setLayout(new FlowLayout());
        MyActionListener listener = new MyActionListener();
        makeMenuTaskButton(new ChangeSampleMenuTask(controller,-2),"^",listener);
        makeMenuTaskButton(new ChangeSampleMenuTask(controller,-1),"<",listener);
        makeMenuTaskButton(new ChangeSampleMenuTask(controller, 1),">",listener);
        makeMenuTaskButton(new ChangeSampleMenuTask(controller, 2),"v",listener);
        bAllSamples = new JButton("ALL");
        bAllSamples.setToolTipText("Perform all measurements on all samples sequentially");
        bAllSamples.addActionListener(listener);
        add(bAllSamples);
        
    }
    
    private MenuTaskButton makeMenuTaskButton(MenuTask task, String alt, ActionListener listener) {
        MenuTaskButton button;
        button = new MenuTaskButton(task,alt);
        button.setToolTipText(task.text());
        button.addActionListener(listener);
        add(button);
        taskButtons.add(button);
        return button;
    }

    /** Action listener for the buttons. */
    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
            // Deal with the MenuTaskButtons:
            if (src instanceof MenuTaskButton) {
                MenuTaskButton button = (MenuTaskButton) src; // cast
                button.execute();
                return;
            }
            // Deal with the other buttons:
            if (src == bAllSamples) { controller.measureAllSamples(); }
        }
    }

    /** Enables or disables some menu items. */
    public void checkItemsEnabled() {
        boolean measuring = controller.isMeasuring();
        // Deal with the TaskButtons:
        for (int i=0 ; i<taskButtons.size() ; i++) {
            if (measuring) {
                taskButtons.get(i).setEnabled(false);
            } else {
                taskButtons.get(i).checkEnabled();
            }
        }
        // Deal with the other buttons:
        if (measuring) {
            bAllSamples.setEnabled(false);
        } else {
            bAllSamples.setEnabled( controller.hasSamples() && controller.hasMeasurements() && controller.currentSampleImageExists() );
        }
    }

}

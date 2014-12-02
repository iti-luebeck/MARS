package mars.auvtree.nodes;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import mars.auvtree.TreeUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 * This class is the presentation for actuators, accumulators and sensors of an
 * auv.
 *
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class ParamNode extends AbstractNode implements PropertyChangeListener {

    /**
     * Object which is representated by the node
     */
    private Object obj;

    /**
     * Hashmap with paramaeters of object.
     */
    private HashMap params;

    /**
     * Hashmap with noise paramaeters of object.
     */
    private HashMap noise;

    /**
     *
     */
    private ArrayList slavesNames;

    /**
     * Name of the image file on the harddisk.
     */
    private String icon;

    /**
     * Displayname of the node.
     */
    private final String nodeName;

    /**
     * This constructor is used to generate three nodes for the subcategories of
     * the auv attachements. The subcategories are accumulators, actuators and
     * sensors. For each of the an icon is displayed in the tree.
     *
     * @param key Used to determine the category of this node
     * @param auvParams HashMap of sensors, accumulators or actuators
     */
    public ParamNode(Integer key, HashMap auvParams) {
        // set ChildFactory for creating child nodes
        super(Children.create(new PhysicalExchangerChildNodeFactory(auvParams), true));

        // set node name and icon depending on the given type
        switch (key) {
            case ParamChildNodeFactory.ACCUMULATORS:
                nodeName = "Accumulators";
                icon = "battery_charge.png";
                break;
            case ParamChildNodeFactory.ACTUATORS:
                nodeName = "Actuators";
                icon = "hand.png";
                break;
            case ParamChildNodeFactory.SENSORS:
                nodeName = "Sensors";
                icon = "eye.png";
                break;
            case ParamChildNodeFactory.PARAMETER:
                nodeName = "Parameter";
                icon = "gear_in.png";
                break;
            default:
                nodeName = "";
        }
        setDisplayName(nodeName);
    }

    /**
     * This is constructor is called to create a node for an attachement.
     *
     * @param obj This can be an accumulator, actuator or a sensor
     * @param nodeName
     */
    public ParamNode(Object obj, String nodeName) {
        // initially this node is asumed to be a leaf
        super(Children.LEAF, Lookups.singleton(obj));
        this.nodeName = nodeName;
        this.obj = obj;

        // create subchilds
        // don't show them currently, because one has to use the property window
        //when you want to activate it you need addtional code:
        //https://blogs.oracle.com/geertjan/entry/no_expansion_key_when_no
        //http://netbeans.dzone.com/nb-dynamic-icons-for-explorer-trees
        /*if (params != null && !params.isEmpty()) {
         setChildren(Children.create(new ParamChildNodeFactory(params), true));
         }*/
        setDisplayName(nodeName);
    }

    /**
     * This method returns the image icon.
     *
     * @param type
     * @return Icon which will be displayed.
     */
    @Override
    public Image getIcon(int type) {
        return TreeUtil.getImage(icon);
    }

    /**
     * Loads image which is displayed next to a opened node.
     *
     * @param type
     * @return Returns image which is loaded with getImage()
     * @see also TreeUtil.getImage()
     */
    @Override
    public Image getOpenedIcon(int type) {
        return TreeUtil.getImage(icon);
    }

    /**
     * Returns the string which is displayed in the tree. Node name is used
     * here.
     *
     * @return Returns node name.
     */
    @Override
    public String getDisplayName() {
        return nodeName;
    }

    /**
     * This listerner is called on property changes. It updates the Property
     * Sheet to display adjusted values.
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.fireDisplayNameChange(null, getDisplayName());
        this.fireIconChange();
        //if ("Position".equals(evt.getPropertyName()) || "Rotation".equals(evt.getPropertyName())) {
        setSheet(getSheet());
        //}
    }
}

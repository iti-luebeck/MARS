package mars.auvtree.nodes;

import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.actuators.Actuator;
import mars.auv.AUV;
import mars.auvtree.TreeUtil;
import mars.sensors.Sensor;
import org.openide.actions.PasteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;
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
     * 
     */
    PhysicalExchangerChildNodeFactory physicalExchangerChildNodeFactory;
            
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
        super(Children.LEAF);
        physicalExchangerChildNodeFactory = new PhysicalExchangerChildNodeFactory(auvParams);
        setChildren( Children.create(physicalExchangerChildNodeFactory, true));
        
        this.params = auvParams;
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

    @Override
    protected SystemAction[] createActions() {
        return new SystemAction[] {
            SystemAction.get (PasteAction.class)
        };
    }

    @Override
    protected void createPasteTypes(Transferable t, List ls) {
        final Transferable tt = t;
        if(t.isDataFlavorSupported(PhysicalExchangerFlavor.CUSTOMER_FLAVOR)){
            try {
                if((nodeName.equals("Sensors") && (t.getTransferData(PhysicalExchangerFlavor.CUSTOMER_FLAVOR) instanceof Sensor)) || nodeName.equals("Actuators") && (t.getTransferData(PhysicalExchangerFlavor.CUSTOMER_FLAVOR) instanceof Actuator)){
                    final Node[] ns = NodeTransfer.nodes (t, NodeTransfer.COPY);
                    if (ns != null) {
                      ls.add (new PasteType () {
                        public Transferable paste () throws IOException {
                            try {
                                PhysicalExchanger pe = (PhysicalExchanger)tt.getTransferData(PhysicalExchangerFlavor.CUSTOMER_FLAVOR);
                                PhysicalExchanger copy = pe.copy();
                                copy.setName(copy.getName() + System.nanoTime());
                                AUVNode parentNode = (AUVNode)getParentNode();
                                AUV auv = parentNode.getLookup().lookup(AUV.class);
                                auv.registerPhysicalExchanger(copy);
                                auv.initPhysicalExchangerFuture();
                                /*Node[] nue = new Node[1];
                                Node n = physicalExchangerChildNodeFactory.createNodeForKey(copy.getName());
                                nue[0] = n;
                                getChildren().add(nue);*/
                                physicalExchangerChildNodeFactory.refresh();
                            } catch (UnsupportedFlavorException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                          return null;
                        }
                      });
                    }
                }
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        // Also try superclass, but give it lower priority:
        super.createPasteTypes(t, ls);
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

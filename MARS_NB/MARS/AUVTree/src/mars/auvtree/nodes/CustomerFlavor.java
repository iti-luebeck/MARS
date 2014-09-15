/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auvtree.nodes;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class CustomerFlavor extends DataFlavor{
    public static final DataFlavor CUSTOMER_FLAVOR = new CustomerFlavor();
    
    /*public CustomerFlavor() {
        super(Customer.class, "Customer");
    }*/
}

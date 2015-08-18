package mars.communication;

import mars.auv.AUV;
import mars.ros.MARSNodeMain;

public class AUVConnectionFactory {

    public static AUVConnection createNewConnection(AUV auv, MARSNodeMain mars_node) {

        //TODOFAB mapping when more than ros is implemented!
        AUVConnection conn = new AUVConnectionRosImpl(auv);
        ((AUVConnectionRosImpl) conn).setRosNode(mars_node);
        return conn;
    }
}

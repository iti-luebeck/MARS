/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package mars;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

/**
 * This is a simple rosjava {@link Subscriber} {@link Node}. It assumes an
 * external roscore is already running.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Listener implements NodeMain {

  @Override
  public void onStart(Node node) {
    try {
      final Log log = node.getLog();
      node.newSubscriber("monsun/press", "iti_msgs/pressure",
          new MessageListener<org.ros.message.iti_msgs.pressure>() {
            @Override
            public void onNewMessage(org.ros.message.iti_msgs.pressure message) {
              log.info("I heard: \"" + message.data + "\"" + message.header.seq + ":" + message.header.stamp);
            }
          });
    } catch (Exception e) {
      if (node != null) {
        node.getLog().fatal(e);
      } else {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onShutdown(Node node) {
  }
}

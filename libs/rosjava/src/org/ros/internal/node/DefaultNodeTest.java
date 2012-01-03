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

package org.ros.internal.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ros.Assert.assertGraphNameEquals;

import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;

import org.junit.Before;
import org.junit.Test;
import org.ros.address.AdvertiseAddress;
import org.ros.address.BindAddress;
import org.ros.internal.node.client.SlaveClient;
import org.ros.internal.node.response.Response;
import org.ros.internal.node.server.MasterServer;
import org.ros.internal.transport.ProtocolDescription;
import org.ros.internal.transport.ProtocolNames;
import org.ros.message.MessageListener;
import org.ros.message.std_msgs.Int64;
import org.ros.namespace.GraphName;
import org.ros.namespace.NameResolver;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.CountDownPublisherListener;
import org.ros.node.topic.CountDownSubscriberListener;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author kwc@willowgarage.com (Ken Conley)
 */
public class DefaultNodeTest {

  private MasterServer masterServer;
  private NodeConfiguration privateNodeConfiguration;
  private NodeFactory nodeFactory;
  private URI masterUri;
  private ExecutorService executorService;

  void checkHostName(String hostName) {
    assertTrue(!hostName.equals("0.0.0.0"));
    assertTrue(!hostName.equals("0:0:0:0:0:0:0:0"));
  }

  @Before
  public void setUp() throws Exception {
    executorService = Executors.newCachedThreadPool();
    masterServer = new MasterServer(BindAddress.newPublic(), AdvertiseAddress.newPublic());
    masterServer.start();
    masterUri = masterServer.getUri();
    checkHostName(masterUri.getHost());
    privateNodeConfiguration = NodeConfiguration.newPrivate(masterUri);
    privateNodeConfiguration.setNodeName("node_name");
    privateNodeConfiguration.setExecutorService(executorService);
    nodeFactory = new DefaultNodeFactory();
  }

  public void shutdown() {
    executorService.shutdown();
  }

  @Test
  public void testFailIfStoppedWhileNotRunning() throws UnknownHostException {
    Node node = nodeFactory.newNode(privateNodeConfiguration);
    node.shutdown();
    try {
      node.shutdown();
      fail();
    } catch (RuntimeException e) {
      // Calling shutdown() while the node is not running must fail.
    }
  }

  @Test
  public void testCreatePublic() throws Exception {
    String host = InetAddress.getLocalHost().getCanonicalHostName();
    assertFalse(InetAddresses.isInetAddress(host));
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host, masterServer.getUri());
    nodeConfiguration.setNodeName("node");
    Node node = nodeFactory.newNode(nodeConfiguration);
    InetSocketAddress nodeAddress = ((DefaultNode) node).getAddress();
    assertTrue(nodeAddress.getPort() > 0);
    assertEquals(nodeAddress.getHostName(), host);
    node.shutdown();
  }

  @Test
  public void testCreatePublicWithIpv4() throws Exception {
    String host = "1.2.3.4";
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host, masterServer.getUri());
    nodeConfiguration.setNodeName("node");
    Node node = nodeFactory.newNode(nodeConfiguration);
    InetSocketAddress nodeAddress = ((DefaultNode) node).getAddress();
    assertTrue(nodeAddress.getPort() > 0);
    assertEquals(nodeAddress.getHostName(), host);
    node.shutdown();
  }

  @Test
  public void testCreatePublicWithIpv6() throws Exception {
    String host = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host, masterServer.getUri());
    nodeConfiguration.setNodeName("node");
    Node node = nodeFactory.newNode(nodeConfiguration);
    InetSocketAddress nodeAddress = ((DefaultNode) node).getAddress();
    assertTrue(nodeAddress.getPort() > 0);
    assertEquals(nodeAddress.getHostName(), host);
    node.shutdown();
  }

  @Test
  public void testCreatePrivate() {
    Node node = nodeFactory.newNode(privateNodeConfiguration);
    InetSocketAddress nodeAddress = ((DefaultNode) node).getAddress();
    assertTrue(nodeAddress.getPort() > 0);
    assertTrue(nodeAddress.getAddress().isLoopbackAddress());
    node.shutdown();
  }

  @Test
  public void testPubSubRegistration() throws InterruptedException {
    Node node = nodeFactory.newNode(privateNodeConfiguration);
    ((DefaultNode) node).getRegistrar().setRetryDelay(1, TimeUnit.MILLISECONDS);

    RosoutLogger rosoutLogger = (RosoutLogger) node.getLog();
    CountDownPublisherListener rosoutLoggerPublisherListener = new CountDownPublisherListener();
    rosoutLogger.getPublisher().addPublisherListener(rosoutLoggerPublisherListener);
    assertTrue(rosoutLoggerPublisherListener.awaitMasterRegistrationSuccess(1, TimeUnit.SECONDS));

    CountDownPublisherListener publisherListener = new CountDownPublisherListener();
    Publisher<org.ros.message.std_msgs.String> publisher =
        node.newPublisher("/foo", "std_msgs/String");
    publisher.addPublisherListener(publisherListener);
    assertTrue(publisherListener.awaitMasterRegistrationSuccess(1, TimeUnit.SECONDS));

    CountDownSubscriberListener subscriberListener = new CountDownSubscriberListener();
    Subscriber<org.ros.message.std_msgs.String> subscriber =
        node.newSubscriber("/foo", "std_msgs/String");
    subscriber.addSubscriberListener(subscriberListener);
    assertTrue(subscriberListener.awaitMasterRegistrationSuccess(1, TimeUnit.SECONDS));

    // There are now two registered publishers /rosout and /foo.
    assertEquals(2, masterServer.getRegisteredPublishers().size());
    assertEquals(1, masterServer.getRegisteredSubscribers().size());

    node.shutdown();

    assertTrue(publisherListener.awaitShutdown(1, TimeUnit.SECONDS));
    assertTrue(subscriberListener.awaitShutdown(1, TimeUnit.SECONDS));

    assertEquals(0, masterServer.getRegisteredPublishers().size());
    assertEquals(0, masterServer.getRegisteredSubscribers().size());
  }

  @Test
  public void testResolveName() {
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPrivate(masterUri);
    nodeConfiguration.setParentResolver(NameResolver.create("/ns1"));
    nodeConfiguration.setNodeName("test_resolver");
    Node node = nodeFactory.newNode(nodeConfiguration);

    assertGraphNameEquals("/foo", node.resolveName("/foo"));
    assertGraphNameEquals("/ns1/foo", node.resolveName("foo"));
    assertGraphNameEquals("/ns1/test_resolver/foo", node.resolveName("~foo"));

    Publisher<Int64> pub = node.newPublisher("pub", "std_msgs/Int64");
    assertGraphNameEquals("/ns1/pub", pub.getTopicName());
    pub = node.newPublisher("/pub", "std_msgs/Int64");
    assertGraphNameEquals("/pub", pub.getTopicName());
    pub = node.newPublisher("~pub", "std_msgs/Int64");
    assertGraphNameEquals("/ns1/test_resolver/pub", pub.getTopicName());

    MessageListener<Int64> callback = new MessageListener<Int64>() {
      @Override
      public void onNewMessage(Int64 message) {
      }
    };

    Subscriber<Int64> sub = node.newSubscriber("sub", "std_msgs/Int64");
    sub.addMessageListener(callback);
    assertGraphNameEquals("/ns1/sub", sub.getTopicName());
    sub = node.newSubscriber("/sub", "std_msgs/Int64");
    sub.addMessageListener(callback);
    assertGraphNameEquals("/sub", sub.getTopicName());
    sub = node.newSubscriber("~sub", "std_msgs/Int64");
    sub.addMessageListener(callback);
    assertGraphNameEquals("/ns1/test_resolver/sub", sub.getTopicName());
  }

  @Test
  public void testPublicAddresses() throws InterruptedException {
    MasterServer master = new MasterServer(BindAddress.newPublic(), AdvertiseAddress.newPublic());
    master.start();
    URI masterUri = master.getUri();
    checkHostName(masterUri.getHost());

    NodeConfiguration nodeConfiguration =
        NodeConfiguration.newPublic(masterUri.getHost(), masterUri);
    nodeConfiguration.setNodeName("test_addresses");
    Node node = nodeFactory.newNode(nodeConfiguration);

    URI nodeUri = node.getUri();
    assertTrue(nodeUri.getPort() > 0);
    checkHostName(nodeUri.getHost());

    CountDownPublisherListener publisherListener = new CountDownPublisherListener();
    Publisher<org.ros.message.std_msgs.Int64> publisher =
        node.newPublisher("test_addresses_pub", "std_msgs/Int64");
    publisher.addPublisherListener(publisherListener);
    assertTrue(publisherListener.awaitMasterRegistrationSuccess(1, TimeUnit.SECONDS));

    // Check the TCPROS server address via the XML-RPC API.
    SlaveClient slaveClient = new SlaveClient(new GraphName("test_addresses"), nodeUri);
    Response<ProtocolDescription> response =
        slaveClient.requestTopic(new GraphName("test_addresses_pub"),
            Lists.newArrayList(ProtocolNames.TCPROS));
    ProtocolDescription result = response.getResult();
    InetSocketAddress tcpRosAddress = result.getAdverstiseAddress().toInetSocketAddress();
    checkHostName(tcpRosAddress.getHostName());
  }
}

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

package org.ros.internal.message.new_style;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MessageSerializationTest {

  private MessageLoader loader;
  private DefaultedClassMap<Message> classes;
  private MessageFactory factory;

  @Before
  public void setUp() {
    loader = new MessageLoader();
    URL resource = this.getClass().getResource("/data/std_msgs");
    File searchPath = new File(resource.getPath());
    loader.addSearchPath(searchPath);
    loader.updateMessageDefinitions();
    classes = DefaultedClassMap.create(Message.class);
    factory = new MessageFactory(loader, classes);
  }

  @Test
  public void testInt32() {
    Message message = factory.createMessage("std_msgs/Int32");
    message.setInt32("data", 42);
    ByteBuffer buffer = message.serialize();
    assertEquals(message, factory.deserializeMessage("std_msgs/Int32", buffer));
  }

  @Test
  public void testString() {
    Message message = factory.createMessage("std_msgs/String");
    message.setString("data", "Hello, ROS!");
    ByteBuffer buffer = message.serialize();
    assertEquals(message, factory.deserializeMessage("std_msgs/String", buffer));
  }

  @Test
  public void testNestedMessage() {
    loader.addMessageDefinition("foo", "std_msgs/String data");
    Message fooMessage = factory.createMessage("foo");
    Message stringMessage = factory.createMessage("std_msgs/String");
    stringMessage.setString("data", "Hello, ROS!");
    fooMessage.setMessage("data", stringMessage);
    ByteBuffer buffer = fooMessage.serialize();
    assertEquals(fooMessage, factory.deserializeMessage("foo", buffer));
  }

  @Test
  public void testNestedMessageArray() {
    loader.addMessageDefinition("foo", "std_msgs/String[] data");
    Message fooMessage = factory.createMessage("foo");
    Message stringMessageA = factory.createMessage("std_msgs/String");
    stringMessageA.setString("data", "Hello, ROS!");
    Message stringMessageB = factory.createMessage("std_msgs/String");
    stringMessageB.setString("data", "Goodbye, ROS!");
    fooMessage.setMessageList("data", Lists.newArrayList(stringMessageA, stringMessageB));
    ByteBuffer buffer = fooMessage.serialize();
    assertEquals(fooMessage, factory.deserializeMessage("foo", buffer));
  }

  @Test
  public void testInt32Array() {
    loader.addMessageDefinition("foo", "int32[] data");
    Message message = factory.createMessage("foo");
    message.setInt32List("data", Lists.newArrayList(1, 2, 3, 4, 5));
    ByteBuffer buffer = message.serialize();
    assertEquals(message, factory.deserializeMessage("foo", buffer));
  }

}

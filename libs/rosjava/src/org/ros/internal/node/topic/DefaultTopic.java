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

package org.ros.internal.node.topic;

import org.ros.namespace.GraphName;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class DefaultTopic implements Topic {

  private final TopicDefinition topicDefinition;
  private final CountDownLatch registrationLatch;

  public DefaultTopic(TopicDefinition topicDefinition) {
    this.topicDefinition = topicDefinition;
    registrationLatch = new CountDownLatch(1);
  }

  public TopicDefinition getTopicDefinition() {
    return topicDefinition;
  }

  public List<String> getTopicDefinitionAsList() {
    return topicDefinition.toList();
  }
  
  @Override
  public GraphName getTopicName() {
    return topicDefinition.getName();
  }

  @Override
  public String getTopicMessageType() {
    return topicDefinition.getMessageType();
  }

  public Map<String, String> getTopicDefinitionHeader() {
    return topicDefinition.toHeader();
  }

  public void signalRegistrationDone() {
    registrationLatch.countDown();
  }

  @Override
  public boolean isRegistered() {
    return registrationLatch.getCount() == 0;
  }

  @Override
  public void awaitRegistration() throws InterruptedException {
    registrationLatch.await();
  }

  @Override
  public boolean awaitRegistration(long timeout, TimeUnit unit)
      throws InterruptedException {
    return registrationLatch.await(timeout, unit);
  }

}

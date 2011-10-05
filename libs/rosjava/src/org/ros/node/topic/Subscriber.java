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

package org.ros.node.topic;

import org.ros.internal.node.topic.Topic;

import org.ros.message.MessageListener;

/**
 * Subscribes to messages of a given type on a given ROS topic.
 * 
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author damonkohler@google.com (Damon Kohler)
 * 
 * @param <MessageType>
 *          the {@link Subscriber} may only subscribe to messages of this type
 */
public interface Subscriber<MessageType> extends Topic {

  /**
   * @param listener
   *          this {@link MessageListener} will be called for every new message
   *          received
   */
  void addMessageListener(MessageListener<MessageType> listener);

  /**
   * @param listener
   *          the {@link MessageListener} to remove
   */
  void removeMessageListener(MessageListener<MessageType> listener);

  /**
   * Cancels the subscription and unregisters the {@link Subscriber}.
   */
  void shutdown();

}

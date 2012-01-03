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

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.ros.internal.node.server.SlaveIdentifier;
import org.ros.namespace.GraphName;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class PublisherIdentifier {

  private final SlaveIdentifier slaveIdentifier;
  private final TopicIdentifier topicIdentifier;

  public static Collection<PublisherIdentifier> newCollectionFromUris(
      Collection<URI> publisherUris, TopicDefinition topicDefinition) {
    Set<PublisherIdentifier> publishers = Sets.newHashSet();
    for (URI uri : publisherUris) {
      SlaveIdentifier slaveIdentifier = new SlaveIdentifier(null, uri);
      publishers.add(new PublisherIdentifier(slaveIdentifier, topicDefinition.toIdentifier()));
    }
    return publishers;
  }

  public static PublisherIdentifier
      newFromStrings(String nodeName, String uri, String topicName) {
    return new PublisherIdentifier(SlaveIdentifier.newFromStrings(nodeName, uri),
        TopicIdentifier.createFromString(topicName));
  }

  public PublisherIdentifier(SlaveIdentifier slaveIdentifier, TopicIdentifier topicIdentifier) {
    Preconditions.checkNotNull(slaveIdentifier);
    Preconditions.checkNotNull(topicIdentifier);
    this.slaveIdentifier = slaveIdentifier;
    this.topicIdentifier = topicIdentifier;
  }

  public SlaveIdentifier getSlaveIdentifier() {
    return slaveIdentifier;
  }
  
  public GraphName getSlaveName() {
    return slaveIdentifier.getName();
  }

  public URI getSlaveUri() {
    return slaveIdentifier.getUri();
  }

  public TopicIdentifier getTopicIdentifier() {
    return topicIdentifier;
  }

  public GraphName getTopicName() {
    return topicIdentifier.getName();
  }

  @Override
  public String toString() {
    return "PublisherIdentifier<" + slaveIdentifier + ", " + topicIdentifier + ">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((slaveIdentifier == null) ? 0 : slaveIdentifier.hashCode());
    result = prime * result + ((topicIdentifier == null) ? 0 : topicIdentifier.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PublisherIdentifier other = (PublisherIdentifier) obj;
    if (slaveIdentifier == null) {
      if (other.slaveIdentifier != null)
        return false;
    } else if (!slaveIdentifier.equals(other.slaveIdentifier))
      return false;
    if (topicIdentifier == null) {
      if (other.topicIdentifier != null)
        return false;
    } else if (!topicIdentifier.equals(other.topicIdentifier))
      return false;
    return true;
  }
}

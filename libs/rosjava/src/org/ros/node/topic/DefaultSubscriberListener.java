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

/**
 * A {@link SubscriberListener} which provides empty defaults for all signals.
 * 
 * @author khughes@google.com (Keith M. Hughes)
 */
public class DefaultSubscriberListener implements SubscriberListener {

  @Override
  public void onMasterRegistrationSuccess(Subscriber<?> subscriber) {
  }

  @Override
  public void onMasterRegistrationFailure(Subscriber<?> subscriber) {
  }

  @Override
  public void onMasterUnregistrationSuccess(Subscriber<?> subscriber) {
  }

  @Override
  public void onMasterUnregistrationFailure(Subscriber<?> subscriber) {
  }

  @Override
  public void onNewPublisher(Subscriber<?> subscriber) {
  }

  @Override
  public void onShutdown(Subscriber<?> subscriber) {
  }
}

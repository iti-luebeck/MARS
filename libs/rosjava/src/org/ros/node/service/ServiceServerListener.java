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

package org.ros.node.service;

/**
 * A listener for events from a {@link ServiceServer}.
 * 
 * @author Keith M. Hughes
 */
public interface ServiceServerListener {

  /**
   * A {@link ServiceServer} has been registered.
   * 
   * @param server
   *          The server which has been registered.
   */
  void onServiceServerRegistration(ServiceServer<?, ?> server);

  /**
   * A {@link ServiceServer} has been shut down.
   * 
   * @param server
   *          The server which has been shut down.
   */
  void onServiceServerShutdown(ServiceServer<?, ?> server);

}

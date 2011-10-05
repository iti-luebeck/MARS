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

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Service {

  public interface Request extends Message {
  }

  public interface Response extends Message {
  }

  private final Request request;
  private final Response response;

  public Service(Request request, Response response) {
    this.request = request;
    this.response = response;
  }

  @SuppressWarnings("unchecked")
  public <MessageType extends Request> MessageType getRequest() {
    return (MessageType) request;
  }

  @SuppressWarnings("unchecked")
  public <MessageType extends Response> MessageType getResponse() {
    return (MessageType) response;
  }

}

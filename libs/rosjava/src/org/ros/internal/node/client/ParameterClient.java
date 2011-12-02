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

package org.ros.internal.node.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.ros.internal.node.response.BooleanResultFactory;
import org.ros.internal.node.response.IntegerResultFactory;
import org.ros.internal.node.response.ObjectResultFactory;
import org.ros.internal.node.response.Response;
import org.ros.internal.node.response.StringListResultFactory;
import org.ros.internal.node.response.StringResultFactory;
import org.ros.internal.node.response.VoidResultFactory;
import org.ros.internal.node.server.SlaveIdentifier;
import org.ros.internal.node.xmlrpc.ParameterServer;
import org.ros.namespace.GraphName;

import com.google.common.collect.Lists;

/**
 * Provide access to the XML-RPC API for a ROS {@link ParameterServer}.
 * 
 * @author kwc@willowgarage.com (Ken Conley)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class ParameterClient extends Client<org.ros.internal.node.xmlrpc.ParameterServer> {

  private final SlaveIdentifier slaveIdentifier;
  private final String nodeName;

  /**
   * Create a new {@link ParameterClient} connected to the specified
   * {@link ParameterServer} URI.
   * 
   * @param uri
   *          the {@link URI} of the {@link ParameterServer} to connect to
   * @throws MalformedURLException
   */
  public ParameterClient(SlaveIdentifier slaveIdentifier, URI uri) {
    super(uri, org.ros.internal.node.xmlrpc.ParameterServer.class);
    this.slaveIdentifier = slaveIdentifier;
    nodeName = slaveIdentifier.getName().toString();
  }

  public Response<Object> getParam(GraphName parameterName) {
    return Response.fromListCheckedFailure(node.getParam(nodeName, parameterName.toString()),
        new ObjectResultFactory());
  }

  public Response<Void> setParam(GraphName parameterName, Boolean parameterValue) {
    return Response.fromListChecked(
        node.setParam(nodeName, parameterName.toString(), parameterValue), new VoidResultFactory());
  }

  public Response<Void> setParam(GraphName parameterName, Integer parameterValue) {
    return Response.fromListChecked(
        node.setParam(nodeName, parameterName.toString(), parameterValue), new VoidResultFactory());
  }

  public Response<Void> setParam(GraphName parameterName, Double parameterValue) {
    return Response.fromListChecked(
        node.setParam(nodeName, parameterName.toString(), parameterValue), new VoidResultFactory());
  }

  public Response<Void> setParam(GraphName parameterName, String parameterValue) {
    return Response.fromListChecked(
        node.setParam(nodeName, parameterName.toString(), parameterValue), new VoidResultFactory());
  }

  public Response<Void> setParam(GraphName parameterName, List<?> parameterValue) {
    return Response.fromListChecked(
        node.setParam(nodeName, parameterName.toString(), parameterValue), new VoidResultFactory());
  }

  public Response<Void> setParam(GraphName parameterName, Map<?, ?> parameterValue) {
    return Response.fromListChecked(
        node.setParam(nodeName, parameterName.toString(), parameterValue), new VoidResultFactory());
  }

  public Response<GraphName> searchParam(GraphName parameterName) {
    Response<String> response =
        Response.fromListCheckedFailure(node.searchParam(nodeName, parameterName.toString()),
            new StringResultFactory());
    return new Response<GraphName>(response.getStatusCode(), response.getStatusMessage(),
        new GraphName(response.getResult()));
  }

  public Response<Object> subscribeParam(GraphName parameterName) {
    return Response.fromListChecked(node.subscribeParam(nodeName, slaveIdentifier.getUri()
        .toString(), parameterName.toString()), new ObjectResultFactory());
  }

  public Response<Integer> unsubscribeParam(GraphName parameterName) {
    return Response.fromListChecked(
        node.unsubscribeParam(nodeName, slaveIdentifier.getUri().toString(),
            parameterName.toString()), new IntegerResultFactory());
  }

  public Response<Boolean> hasParam(GraphName parameterName) {
    return Response.fromListChecked(node.hasParam(nodeName, parameterName.toString()),
        new BooleanResultFactory());
  }

  public Response<Void> deleteParam(GraphName parameterName) {
    return Response.fromListChecked(node.deleteParam(nodeName, parameterName.toString()),
        new VoidResultFactory());
  }

  public Response<List<GraphName>> getParamNames() {
    Response<List<String>> response =
        Response.fromListChecked(node.getParamNames(nodeName), new StringListResultFactory());
    List<GraphName> graphNames = Lists.newArrayList();
    for (String name : response.getResult()) {
      graphNames.add(new GraphName(name));
    }
    return new Response<List<GraphName>>(response.getStatusCode(), response.getStatusMessage(),
        graphNames);
  }

}

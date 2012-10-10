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

package org.ros.internal.message.field;

import com.google.common.base.Preconditions;

import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Arrays;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class DoubleArrayField extends Field {

  private final int size;

  private double[] value;

  public static DoubleArrayField newVariable(String name, int size) {
    return new DoubleArrayField(PrimitiveFieldType.FLOAT64, name, size);
  }

  private DoubleArrayField(FieldType type, String name, int size) {
    super(type, name, false);
    this.size = size;
    setValue(new double[Math.max(0, size)]);
  }

  @SuppressWarnings("unchecked")
  @Override
  public double[] getValue() {
    return value;
  }

  @Override
  public void setValue(Object value) {
    Preconditions.checkArgument(size < 0 || ((double[]) value).length == size);
    this.value = (double[]) value;
  }

  @Override
  public void serialize(ChannelBuffer buffer) {
    if (size < 0) {
      buffer.writeInt(value.length);
    }
    for (double v : value) {
      type.serialize(v, buffer);
    }
  }

  @Override
  public void deserialize(ChannelBuffer buffer) {
    int currentSize = size;
    if (currentSize < 0) {
      currentSize = buffer.readInt();
    }
    value = new double[currentSize];
    for (int i = 0; i < currentSize; i++) {
      value[i] = buffer.readDouble();
    }
  }

  @Override
  public String getMd5String() {
    return String.format("%s %s\n", type, name);
  }

  @Override
  public String getJavaTypeName() {
    return type.getJavaTypeName() + "[]";
  }

  @Override
  public String toString() {
    return "DoubleArrayField<" + type + ", " + name + ">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    DoubleArrayField other = (DoubleArrayField) obj;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!Arrays.equals(value, other.value))
      return false;
    return true;
  }
}

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

import com.google.common.base.Preconditions;

import java.nio.ByteBuffer;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
class ScalarField<ValueType> extends Field {

  private ValueType value;

  static <T> ScalarField<T> createConstant(String name, FieldType type, T value) {
    return new ScalarField<T>(name, type, value, true);
  }

  static <T> ScalarField<T> createValue(String name, FieldType type) {
    // TODO(damonkohler): All values should have a default.
    return new ScalarField<T>(name, type, null, false);
  }

  private ScalarField(String name, FieldType type, ValueType value, boolean isConstant) {
    super(name, type, isConstant);
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ValueType getValue() {
    return value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue(Object value) {
    Preconditions.checkState(!isConstant);
    this.value = (ValueType) value;
  }

  @Override
  public void serialize(ByteBuffer buffer) {
    type.serialize(value, buffer);
  }

  @Override
  public void deserialize(ByteBuffer buffer) {
    value = type.<ValueType>deserialize(buffer);
  }

  @Override
  public int getSerializedSize() {
    Preconditions.checkNotNull(value);
      if (type instanceof MessageFieldType) {
        return ((Message) value).getSerializedSize();
      } else if (type == PrimitiveFieldType.STRING) {
        // We only support ASCII strings and reserve 4 bytes for the length.
        return ((String) value).length() + 4;
      } else {
        return type.getSerializedSize();
      }
  }

  @Override
  public String toString() {
    return "ScalarField<" + name + ", " + type + ">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isConstant ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ScalarField<?> other = (ScalarField<?>) obj;
    if (isConstant != other.isConstant) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (type == null) {
      if (other.type != null) return false;
    } else if (!type.equals(other.type)) return false;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }

}

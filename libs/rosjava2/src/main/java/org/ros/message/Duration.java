/*
 * Software License Agreement (BSD License)
 *
 *  Copyright (c) 2008, Willow Garage, Inc.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of Willow Garage, Inc. nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */

package org.ros.message;

/**
 * ROS Duration representation. Time and Duration are primitive types in ROS.
 * ROS represents each as two 32-bit integers: seconds and nanoseconds since
 * epoch.
 * 
 * http://www.ros.org/wiki/msg
 * 
 * @author Jason Wolfe
 * @author kwc@willowgarage.com (Ken Conley)
 * 
 */
public class Duration implements Comparable<Duration> {

  public static final Duration MAX_VALUE = new Duration(Integer.MAX_VALUE, 999999999);

  public int secs;
  public int nsecs;

  public Duration() {
  }

  public Duration(int secs, int nsecs) {
    this.secs = secs;
    this.nsecs = nsecs;
    normalize();
  }

  public Duration(double secs) {
    this.secs = (int) secs;
    this.nsecs = (int) ((secs - this.secs) * 1000000000);
    normalize();
  }

  public Duration(Duration t) {
    this.secs = t.secs;
    this.nsecs = t.nsecs;
  }

  public Duration add(Duration d) {
    return new Duration(secs + d.secs, nsecs + d.nsecs);
  }

  public Duration subtract(Duration d) {
    return new Duration(secs - d.secs, nsecs - d.nsecs);
  }

  public static Duration fromMillis(long durationInMillis) {
    int secs = (int) (durationInMillis / 1000);
    int nsecs = (int) (durationInMillis % 1000) * 1000000;
    return new Duration(secs, nsecs);
  }

  public static Duration fromNano(long durationInNs) {
    int secs = (int) (durationInNs / 1000000000);
    int nsecs = (int) (durationInNs % 1000000000);
    return new Duration(secs, nsecs);
  }

  public void normalize() {
    while (nsecs < 0) {
      nsecs += 1000000000;
      secs -= 1;
    }
    while (nsecs >= 1000000000) {
      nsecs -= 1000000000;
      secs += 1;
    }
  }

  public long totalNsecs() {
    return ((long) secs) * 1000000000 + nsecs;
  }

  public boolean isZero() {
    return totalNsecs() == 0;
  }

  public boolean isPositive() {
    return totalNsecs() > 0;
  }

  public boolean isNegative() {
    return totalNsecs() < 0;
  }

  @Override
  public String toString() {
    return secs + ":" + nsecs;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + nsecs;
    result = prime * result + secs;
    return result;
  }

  @Override
  /**
   * Check for equality between Time objects.  
   * equals() does not normalize Time representations, so fields must match exactly.
   */
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Duration other = (Duration) obj;
    if (nsecs != other.nsecs)
      return false;
    if (secs != other.secs)
      return false;
    return true;
  }

  @Override
  public int compareTo(Duration d) {
    if ((secs > d.secs) || ((secs == d.secs) && nsecs > d.nsecs)) {
      return 1;
    }
    if ((secs == d.secs) && (nsecs == d.nsecs)) {
      return 0;
    }
    return -1;
  }

}

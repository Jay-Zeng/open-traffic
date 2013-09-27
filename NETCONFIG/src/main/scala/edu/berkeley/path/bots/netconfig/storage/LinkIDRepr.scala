/**
 * Copyright 2012. The Regents of the University of California (Regents).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package edu.berkeley.path.bots.netconfig.storage
import scala.util.Sorting
import scala.math.Ordering.LongOrdering

/**
 * A generic representation of the identifier of a link.
 *
 * It is assumed that any link of any road network can be uniquely identified
 * by a pair of a long integer and of an integer.
 * 
 * @param primary the primary identifier of the link
 * @param secondary the secondary identifier of the link. Set to 0 if you do not need this feature.
 *
 * An ordering is defined on the linkID representations. import edu.berkeley.path.bots.netconfig.storage._
 */
case class LinkIDRepr(
  var primary: Long,
  var secondary: Int) {
}

object LinkIDReprOrdering extends Ordering[LinkIDRepr] {

  def compare(a: LinkIDRepr, b: LinkIDRepr) = {
    val n = math.signum(a.primary - b.primary).toInt
    if (n == 0) {
      a.secondary - b.secondary
    } else n
  }
}
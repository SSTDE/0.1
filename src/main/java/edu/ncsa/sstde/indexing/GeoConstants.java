/*
 * Copyright 2011 by TalkingTrends (Amsterdam, The Netherlands)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensahara.com/licenses/apache-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.ncsa.sstde.indexing;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public interface GeoConstants {
    URI XMLSCHEMA_SPATIAL_TEXT = new URIImpl("http://rdf.opensahara.com/type/geo/wkt");
    URI XMLSCHEMA_SPATIAL_TEXTGZ = new URIImpl("http://rdf.opensahara.com/type/geo/wkt.gz");
    URI XMLSCHEMA_SPATIAL_BIN = new URIImpl("http://rdf.opensahara.com/type/geo/wkb");
    URI XMLSCHEMA_SPATIAL_BINGZ = new URIImpl("http://rdf.opensahara.com/type/geo/wkb.gz");
}

/*
 * Copyright 2012 by TalkingTrends (Amsterdam, The Netherlands)
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
package edu.ncsa.uiuc.rdfrepo.testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.sail.SailException;

import edu.ncsa.sstde.util.PGConnectionManager;

public class IndexBuilder {

    public static void main(String[] args) throws RepositoryException, MalformedQueryException, QueryEvaluationException, SailException, ClassNotFoundException, SQLException,
        ParseException {
        //        SailRepositoryConnection connection = (new RDBLoader()).getPgSQLRepo().getConnection();
        String sparql =
            "PREFIX time: <http://www.w3.org/2006/time#>  PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
                +
                "select * where {?observation ssn:observationResultTime ?time. ?time time:inXSDDateTime ?timevalue. ?loc <http://www.opengis.net/rdf#hasWKT> ?coord. ?sensor dul:hasLocation ?loc. ?observation ssn:observedBy ?sensor.}";
        //        String sparql = "PREFIX search: <http://rdf.opensahara.com/search#>" +
        //            "PREFIX  geo: <http://rdf.opensahara.com/type/geo/>" +
        //            "SELECT ?geometry ?wkt WHERE {?geometry <http://www.opengis.net/rdf#hasWKT> ?wkt.}";
        //        String dir = "repo2";
        //        Sail sail = new NativeStore(new File(dir));
        //        sail.initialize();
        //        SailRepositoryConnection connection = (new SailRepository(sail)).getConnection();
        
        Repository repository = new HTTPRepository("http://iacat-dev.ncsa.illinois.edu:8080/openrdf-sesame", "IACAT2");
        IndexBuilder builder = new IndexBuilder();
        builder.buildIndex(repository.getConnection(), sparql, PGConnectionManager.getConnection("iacat-dev.ncsa.illinois.edu", "5432", "iacat", "iacat", "*iacat*"), "idxst3",
            null);

    }

    private void buildIndex(RepositoryConnection connection, String sparql, Connection dbConn, String tablename, HashMap<String, String> columnNameMapping)
        throws MalformedQueryException,
        RepositoryException, QueryEvaluationException, SQLException {
        TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
        TupleQueryResult queryResult = query.evaluate();
        List<String> names = queryResult.getBindingNames();
        System.out.println(names);

        //        String sql = "insert into idxst2 (observation, time, timevalue, loc, coord, sensor) values (?,?,?,?,?,?)";
        //        String sql = "insert into idxst3 (geometry, wkt) values (?,?)";
        Statement statement = dbConn.createStatement();
        int i = 0;
        while (queryResult.hasNext()) {
            BindingSet bindingSet = queryResult.next();
            SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'hh:mm:ss");
            //            String sql =
            //                "insert into idxst3 (geometry, wkt) values ('" + bindingSet.getValue("geometry").stringValue() + "', ST_GeomFromText('"
            //                    + bindingSet.getValue("wkt").stringValue() + "'))";
            String sql = "insert into idxst2 (observation, time, timevalue, loc, coord, sensor) values ('" + bindingSet.getValue("observation").stringValue() + "','"
                + bindingSet.getValue("time").stringValue() + "','" + bindingSet.getValue("timevalue").stringValue() + "','" + bindingSet.getValue("loc").stringValue()
                + "'," + "ST_GeomFromText('" + bindingSet.getValue("coord").stringValue() + "')" + ",'" + bindingSet.getValue("sensor").stringValue() + "')";
            
            
            //            System.out.println(sql);
            //            statement.setObject(1, bindingSet.getValue("observation").stringValue());
            //            statement.setObject(2, bindingSet.getValue("time").stringValue());
            //            statement.setDate(3, new Date(format.parse(bindingSet.getValue("timevalue").stringValue()).getTime()));
            //            statement.setObject(4, bindingSet.getValue("loc").stringValue());
            //            statement.setObject(5, "ST_GeomFromText('" + bindingSet.getValue("coord").stringValue() + "')");
            //            statement.setObject(6, bindingSet.getValue("sensor").stringValue());
            statement.executeUpdate(sql);
            System.out.println(i++);
            //            i++;
            //            if (i > 1000) {
            //                statement.executeUpdate();
            //                System.out.println("updated");
            //                i = 0;
            //            }

        }
    }

    //    private void setValue(PreparedStatement statement, int index, Value value, List<String> geometryTypes) throws NumberFormatException, SQLException {
    //        if (value instanceof Literal) {
    //            String typeString = ((Literal)value).getDatatype().stringValue();
    //            if (typeString.equals(DataTypeURI.INTEGER)) {
    //                statement.setInt(index, Integer.valueOf(((Literal)value).getLabel()));
    //            } else if (typeString.equals(DataTypeURI.FLOAT) || typeString.equals(DataTypeURI.DOUBLE)) {
    //                statement.setDouble(index, Double.valueOf(((Literal)value).getLabel()));
    //            } else if (typeString.equals(DataTypeURI.DATETIME)) {
    //
    //            } else if (geometryTypes.contains(typeString)) {
    //
    //            }
    //        } else if (value instanceof BNode) {
    //
    //        } else if (value instanceof URI) {
    //
    //        }
    //    }
}

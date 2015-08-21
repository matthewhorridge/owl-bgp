/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of OWL-BGP.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP. If not, see <http://www.gnu.org/licenses/>.
 */

package  org.semanticweb.sparql.hermit;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;

public class AlgebraTranslation {
	public static final String LB = System.getProperty("line.separator") ; 

	public static void main(String[] args) throws Exception {
	    String sparqlQueryString[]=new String[9];
	    sparqlQueryString[0]="PREFIX ex: <http://example.org/test#> "+LB
	       +"SELECT ?book WHERE "+LB
	       + "{ { { ?book ex:price ?price . FILTER (?price < 15) } OPTIONAL { ?book ex:title ?title . } } "+LB
           + "  {  { ?book ex:author ex:Shakespeare . } UNION "+LB
           + "     { ?book ex:author ex:Marlowe . } "+LB
           + "  } "
	       + "} ";
	   sparqlQueryString[1]="PREFIX ex: <http://example.org/test#> "+LB
           +"SELECT ?book WHERE "+LB
           + "{ ?book ex:price ?price . "+LB
           + "  OPTIONAL { ?book ex:title ?title . } "+LB
           + "  FILTER (?price < 15) "+LB
           + "  { ?book ex:author ex:Shakespeare . } UNION "+LB
           + "  { ?book ex:author ex:Marlowe . } "+LB
           + "} ";
       sparqlQueryString[2]="PREFIX ex: <http://example.org/test#> "+LB
           +"SELECT ?book WHERE "+LB
           + "{ { ?book ex:price ?price . "+LB
           + "    FILTER (?price < 15) } "+LB
           + "  OPTIONAL { ?book ex:title ?title . } "+LB
           + "  { ?book ex:author ex:Shakespeare . } UNION "+LB
           + "  { ?book ex:author ex:Marlowe . } "+LB
           + "} ";
        sparqlQueryString[3]="PREFIX ex: <http://example.org/test#> "+LB
           +"SELECT ?book WHERE "+LB
           + "{ { ?book ex:price ?price . } UNION "+LB
           + "  {  ?book ex:author ex:Shakespeare . } UNION "+LB
           + "  { ?book ex:author ex:Marlowe . } "+LB
           + "} ";
        sparqlQueryString[4]="PREFIX ex: <http://example.org/test#> "+LB
            +"SELECT ?book WHERE "+LB
            + "{ { { ?book ex:price ?price . } UNION "+LB
            + "    {  ?book ex:author ex:Shakespeare . } } UNION "+LB
            + "  { ?book ex:author ex:Marlowe . } "+LB
            + "} ";
        sparqlQueryString[5]="PREFIX ex: <http://example.org/test#> "+LB
            +"SELECT ?book WHERE "+LB
            + "{ ?book ex:price ?price . "+LB
            + "  FILTER (?book > 15) "+LB
            + "  FILTER (?nummSales > 100) "+LB
            + "  ?book ex:sales ?numSales . "+LB
            + "} ";
	    sparqlQueryString[6]="PREFIX ex: <http://example.org/test#> "+LB
           +"SELECT ?book WHERE "+LB
           + " {  { ?book ex:price ?price . } "+LB
           + "    { ?book ex:author ex:Shakespeare . } UNION "+LB
           + "    { ?book ex:author ex:Marlowe . } "+LB
           + " } ";
        sparqlQueryString[7]="PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                +"SELECT ?s ?p ?o ?z WHERE "+LB
                + " {  ?s ?p ?o . "+LB
                + "    ?p a owl:DatatypeProperty . "+LB
                + "    { BIND(?o+1 AS ?z) } UNION "+LB
                + "    { BIND(?o+2 AS ?z) } "+LB
                + " } ";
        sparqlQueryString[8]="PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                +"SELECT * WHERE "+LB
                + " {  ?s ?p ?o . "+LB
                + "    BIND(?o+10 AS ?z) "+LB
                + " } ";
        
	    for (String queryString : sparqlQueryString) {
    	    // Parse
            Query query=QueryFactory.create(queryString);
            System.out.println(query);
            System.out.println("--------------------");
            // Generate algebra
            Op op=Algebra.compile(query);
            System.out.println(op);
            System.out.println("--------------------");
            op=Algebra.optimize(op);
            System.out.println(op);
            System.out.println("--------------------");
            // Plan
//            Plan plan=QueryExecutionFactory.createPlan(query, ModelFactory.createDefaultModel().getGraph());
//    	    System.out.println(plan);
    	    System.out.println("====================");
	    }
	}
}
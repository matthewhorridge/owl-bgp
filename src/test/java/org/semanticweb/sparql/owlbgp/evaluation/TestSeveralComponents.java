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

package org.semanticweb.sparql.owlbgp.evaluation;

import org.semanticweb.sparql.OWLReasonerSPARQLEngine;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

public class TestSeveralComponents extends AbstractQueryTest {
    public static final String LB = System.getProperty("line.separator") ;
    
    public TestSeveralComponents() {
        this(null);
    }
    public TestSeveralComponents(String name) {
        super(name);
    }
    
    public void testTwoComponents() throws Exception {
        String axioms="Declaration( Class( :A ) )"+
                "Declaration( Class( :B ) )"+
                "Declaration( NamedIndividual( :a1 ) )"+LB+
                "Declaration( NamedIndividual( :a2 ) )"+LB+
                "Declaration( NamedIndividual( :b1 ) )"+LB+
                "Declaration( NamedIndividual( :b2 ) )"+LB+
                "ClassAssertion( :A :a1 )"+LB+
                "ClassAssertion( :A :a2 )"+LB+
                "ClassAssertion( :B :b1 )"+LB+
                "ClassAssertion( :B :b2 )";
        loadDataSetWithAxioms(axioms);
        
        String s="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                + "PREFIX ex: <http://example.org/>" +LB
                + "PREFIX : <http://example.org/>" +LB
                + "SELECT ?x ?y WHERE {"+LB
                + "  ?x rdf:type :A . "+LB
                + "  ?y rdf:type :B . "+LB
                + "}";
        
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==4);
    }

}
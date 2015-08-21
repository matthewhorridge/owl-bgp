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

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.sparql.OWLReasonerSPARQLEngine;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

public class QueryTests extends AbstractQueryTest {
    public static final String LB = System.getProperty("line.separator") ;
    
    public QueryTests() {
        this(null);
    }
    public QueryTests(String name) {
        super(name);
    }
    
    protected String getAxioms() {
        String axioms="Declaration( Class( :GraduateAssistant ) )"+
                "Declaration( Class( :Student ) )"+
                "Declaration( Class( :Employee ) )"+
                "Declaration( Class( :ConferencePaper ) )"+
                "Declaration( Class( :Conference ) )"+
                "Declaration( Class( :Workshop ) )"+
                "Declaration( ObjectProperty( :publishedAt ) )"+LB+
                "Declaration( ObjectProperty( :hasPublication ) )"+LB+
                "Declaration( DataProperty( :name ) )"+LB+
                "Declaration( NamedIndividual( :John ) )"+LB+
                "Declaration( NamedIndividual( :Anite ) )"+LB+
                "Declaration( NamedIndividual( :George ) )"+LB+
                "Declaration( NamedIndividual( :paper1 ) )"+LB+
                "Declaration( NamedIndividual( :person1 ) )"+LB+
                "ClassAssertion( :GraduateAssistant :John )"+LB+
                "ClassAssertion( :ConferencePaper :paper1 )"+LB+
                "SubClassOf( :GraduateAssistant :Student )"+LB+
                "SubClassOf( :GraduateAssistant :Employee )"+LB+
                "ClassAssertion( :Student :Anite )"+LB+
                "ClassAssertion( :Employee :George )"+LB+
                "ObjectPropertyAssertion( :hasPublication :John :paper1 )"+LB+
                "ObjectPropertyAssertion( :hasPublication :person1 :paper1 )"+LB+
                "DataPropertyAssertion( :name :John \"Johnnie\" )"+LB+
                "SubClassOf( :ConferencePaper ObjectSomeValuesFrom(:publishedAt :Conference) )"+LB+
                "DisjointClasses( :Conference :Workshop )";
        return axioms;
    }
    
    protected String getAxiomsPunning() {
        String axioms="Declaration( Class( :Pneumonia ) )"+
                "Declaration( Class( :Flu ) )"+
                "Declaration( Class( :Book ) )"+
                "Declaration( ObjectProperty( :isAbout ) )"+LB+
                "Declaration( ObjectProperty( :has ) )"+LB+
                "Declaration( DataProperty( :prevalence ) )"+LB+
                "Declaration( NamedIndividual( :John ) )"+LB+
                "Declaration( NamedIndividual( :Anite ) )"+LB+
                "Declaration( NamedIndividual( :George ) )"+LB+
                "Declaration( NamedIndividual( :Pneumonia ) )"+LB+
                "Declaration( NamedIndividual( :Flu ) )"+LB+
                "Declaration( NamedIndividual( :Arthritis ) )"+LB+
                "Declaration( NamedIndividual( :BookAboutPneumonia ) )"+LB+
                "ClassAssertion( ObjectSomeValuesFrom( :has :Pneumonia ) :John )"+LB+
                "ClassAssertion( ObjectSomeValuesFrom( :has :Flu ) :George )"+LB+
                "ObjectPropertyAssertion( :isAbout :BookAboutPneumonia :Pneumonia )"+LB+
                "ClassAssertion( :Book :BookAboutPneumonia )"+LB+
                "DataPropertyAssertion( :prevalence :Pneumonia \"0.0001\"^^xsd:decimal )"+LB+
                "DataPropertyAssertion( :prevalence :Arthritis \"0.0005\"^^xsd:decimal )"+LB+
                "DataPropertyAssertion( :prevalence :Flu \"0.000001\"^^xsd:decimal )";
        return axioms;
    }
    
    public void testPunning() throws Exception {
        loadDataSetWithAxioms(getAxiomsPunning());
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :     <http://example.org/>" +LB
                + "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?pun ?prev WHERE {"+LB
                + "  ?pun :prevalence ?prev . "+LB
                + "  FILTER (?prev >= 0.0001) "+LB
                + "  ?pun rdf:type owl:Class . "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==2);
    } 
    
    public void testSubquery() throws Exception {
        String axioms="Declaration(NamedIndividual( :alice ))"+LB+
                "Declaration(NamedIndividual( :bob ))"+LB+
                "Declaration(NamedIndividual( :carol ))"+LB+
                "Declaration(ObjectProperty( :knows ))"+LB+
                "Declaration(DataProperty( :name ))"+LB+
                "ObjectPropertyAssertion( :knows :alice :bob )"+LB+
                "ObjectPropertyAssertion( :knows :alice :carol )"+LB+
                "DataPropertyAssertion( :name :alice \"Alice\" )"+LB+
                "DataPropertyAssertion( :name :alice \"Alice Foo\" )"+LB+
                "DataPropertyAssertion( :name :alice \"A. Foo\" )"+LB+
                "DataPropertyAssertion( :name :bob \"Bob\" )"+LB+
                "DataPropertyAssertion( :name :bob \"Bob Bar\" )"+LB+
                "DataPropertyAssertion( :name :bob \"B. Bar\" )"+LB+
                "DataPropertyAssertion( :name :carol \"Carol\" )"+LB+
                "DataPropertyAssertion( :name :carol \"Carol Baz\" )"+LB+
                "DataPropertyAssertion( :name :carol \"C. Baz\" )";
        loadDataSetWithAxioms(axioms);
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?y ?minName WHERE {"+LB
                + "  :alice :knows ?y . "+LB
                + "  { "+LB
                + "    SELECT ?y (MIN(?name) AS ?minName)"+LB
                + "    WHERE { "+LB
                + "    ?y :name ?name . "+LB
                + "    } GROUP BY ?y "+LB
                + "  } "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
//        System.out.println(ResultSetFormatter.asText(result));
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==2);
    }         
    public void testMinus() throws Exception {
        String axioms="Declaration(NamedIndividual( :alice ))"+LB+
                "Declaration(NamedIndividual( :bob ))"+LB+
                "Declaration(NamedIndividual( :tom ))"+LB+
                "Declaration(Class( :Person ))"+LB+
                "Declaration(ObjectProperty( :knows ))"+LB+
                "ClassAssertion( :Person :alice )"+LB+
                "ObjectPropertyAssertion( :knows :alice  :tom )"+LB+ 
                "ClassAssertion( :Person :bob )";
        loadDataSetWithAxioms(axioms);
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?person WHERE {"+LB
                + "  ?person rdf:type :Person . "+LB
                + "  MINUS { ?person :knows :tom } "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==1);
    } 
    public void testMinusLiterals() throws Exception {
        String axioms="Declaration(NamedIndividual( :alice ))"+LB+
                "Declaration(NamedIndividual( :bob ))"+LB+
                "Declaration(NamedIndividual( :tom ))"+LB+
                "Declaration(DataProperty( :name ))"+LB+
                "Declaration(DataProperty( :nick ))"+LB+
                "DataPropertyAssertion( :name :tom \"Tom\" )"+LB+
                "DataPropertyAssertion( :name :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :nick :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :name :bob \"Bob\" )"+LB+ 
                "DataPropertyAssertion( :nick :bob \"Bobby\" )";
        loadDataSetWithAxioms(axioms);
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?name WHERE {"+LB
                + "  ?person :name ?name . "+LB
                + "  MINUS { ?person :nick ?name } "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==2);
    } 
    
    public void testMinusBlanks() throws Exception {
        String axioms="Declaration(NamedIndividual( :alice ))"+LB+
                "Declaration(NamedIndividual( :tom ))"+LB+
                "Declaration(DataProperty( :name ))"+LB+
                "Declaration(DataProperty( :nick ))"+LB+
                "DataPropertyAssertion( :name :tom \"Tom\" )"+LB+
                "DataPropertyAssertion( :name :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :nick :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :name _:bob \"Bob\" )"+LB+ 
                "DataPropertyAssertion( :nick _:bob \"Bobby\" )";
        loadDataSetWithAxioms(axioms);
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?name WHERE {"+LB
                + "  ?person :name ?name . "+LB
                + "  MINUS { ?person :nick ?name } "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==2);
    } 
    
    public void testNotExists() throws Exception {
        String axioms="Declaration(NamedIndividual( :alice ))"+LB+
                "Declaration(NamedIndividual( :bob ))"+LB+
                "Declaration(NamedIndividual( :tom ))"+LB+
                "Declaration(Class( :Person ))"+LB+
                "Declaration(ObjectProperty( :knows ))"+LB+
                "ClassAssertion( :Person :alice )"+LB+
                "ObjectPropertyAssertion( :knows :alice  :tom )"+LB+ 
                "ClassAssertion( :Person :bob )";
        loadDataSetWithAxioms(axioms);
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?person WHERE {"+LB
                + "  ?person rdf:type :Person . "+LB
                + "  FILTER NOT EXISTS { ?person :knows :tom } "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==1);
    } 
    
    public void testNotExistsLiterals() throws Exception {
        String axioms="Declaration(NamedIndividual( :alice ))"+LB+
                "Declaration(NamedIndividual( :bob ))"+LB+
                "Declaration(NamedIndividual( :tom ))"+LB+
                "Declaration(DataProperty( :name ))"+LB+
                "Declaration(DataProperty( :nick ))"+LB+
                "DataPropertyAssertion( :name :tom \"Tom\" )"+LB+
                "DataPropertyAssertion( :name :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :nick :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :name :bob \"Bob\" )"+LB+ 
                "DataPropertyAssertion( :nick :bob \"Bobby\" )";
        loadDataSetWithAxioms(axioms);
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?name WHERE {"+LB
                + "  ?person :name ?name . "+LB
                + "  FILTER NOT EXISTS { ?person :nick ?name } "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==2);
    } 
    
    public void testAddToCarDriver() throws Exception {
        org.semanticweb.owlapi.model.IRI physicalIRI=org.semanticweb.owlapi.model.IRI.create(getClass().getResource("/ontologies/CarDriverTest.owl").toURI());
        OWLOntology o=m_ontologyManager.loadOntologyFromOntologyDocument(physicalIRI);
        loadDataSetFromOntology(o);
        String s= "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                + "PREFIX test: <http://www.whatif-project.org/ontology/authoringOntology/CarDriverTest.owl#>" +LB
                + "SELECT ?indi ?role ?filler ?class"+LB
                + "WHERE {"+LB
                + "  ?role rdf:type owl:ObjectProperty . " + LB
                + "  [owl:onProperty ?role; rdf:type owl:Restriction; owl:someValuesFrom ?filler ] rdfs:subClassOf ?class . "+LB
                //+ "  ?indi ?role ?obj . "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
//        ResultSetFormatter.out(System.out, result, query);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==118);
    }
    
    public void testNotExistsBlanks() throws Exception {
        String axioms="Declaration(NamedIndividual( :alice ))"+LB+
                "Declaration(NamedIndividual( :tom ))"+LB+
                "Declaration(DataProperty( :name ))"+LB+
                "Declaration(DataProperty( :nick ))"+LB+
                "DataPropertyAssertion( :name :tom \"Tom\" )"+LB+
                "DataPropertyAssertion( :name :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :nick :alice  \"Alice\" )"+LB+
                "DataPropertyAssertion( :name _:bob \"Bob\" )"+LB+ 
                "DataPropertyAssertion( :nick _:bob \"Bobby\" )";
        loadDataSetWithAxioms(axioms);
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?name WHERE {"+LB
                + "  ?person :name ?name . "+LB
                + "  FILTER NOT EXISTS { ?person :nick ?name } "+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==2);
    } 
    
//    public void testParsingFromJeff() throws Exception {
//        loadDataSetWithAxioms(getAxioms());
//        String s= "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
//                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
//                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
//                + "PREFIX test: <http://www.whatif-project.org/ontology/authoringOntology/CarDriverTest.owl#> "+LB
//                + "SELECT ?obj ?filler "+LB
//                + "WHERE { "+LB
//                //+ "    ?indi rdf:type owl:NamedIndividual . "+LB
//                //+ "    ?class rdf:type owl:Class . "+LB
//                + "    ?role rdf:type owl:ObjectProperty . "+LB
//                //+ "    ?filler rdf:type owl:Class . "+LB
//                //+ "    ?obj rdf:type owl:NamedIndividual . "+LB
//                + "    test:r rdf:type owl:ObjectProperty . "+LB
//                + "    ?indi rdf:type ["+LB
//                + "        owl:onProperty test:r;"+LB
//                + "        rdf:type owl:Restriction;"+LB
//                + "        owl:someValuesFrom ?class ] . "+LB
//                + "    [ owl:onProperty ?role;"+LB
//                + "      rdf:type owl:Restriction;"+LB
//                + "      owl:someValuesFrom ?filler ]"+LB
//                + "    rdfs:subClassOf ?class . "+LB
//                + "    ?indi ?role ?obj . "+LB
//                + "}";
//        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
//        Query query=QueryFactory.create(s);
//        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
//        int noResults=0;
//        while (result.hasNext()) {
//            result.next();
//            noResults++;
//        }
//        assertTrue(noResults==2);
//    }
            
    public void testOPParsing() throws Exception {
        loadDataSetWithAxioms(getAxioms());
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?x WHERE {"+LB
                + "  ?x ex:hasPublication _:b0 . "+LB
                + "  _:b0 rdf:type [ "+LB
                + "    owl:onProperty ex:publishedAt ; "+LB
                + "    rdf:type owl:Restriction ; "+LB
                + "    owl:someValuesFrom [ "+LB
                + "      rdf:type owl:Class ; "+LB
                + "      owl:complementOf ex:Workshop ] "+LB
                + "   ] "+LB
                + "}";
        
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==2);
    }
    public void testInstanceOfComplexClass() throws Exception {
        loadDataSetWithAxioms(getAxioms());
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?x ?y WHERE {"+LB
                + "  ?x rdf:type _:b0 . "+LB
                //+ "  _:b0 rdf:type owl:Class . "+LB
                + "  _:b0 owl:intersectionOf _:b1 . "+LB
                + "  _:b1 rdf:first ex:Student . "+LB
                + "  _:b1 rdf:rest _:b2 . "+LB
                + "  _:b2 rdf:first ex:Employee . "+LB
                + "  _:b2 rdf:rest rdf:nil . "+LB
                + "  ?x ex:name ?y "+LB
                + "}";
        
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==1);
    }
    public void testSubClassQuery() throws Exception {
        loadDataSetWithAxioms(getAxioms());
        String s= "PREFIX  ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "SELECT ?c WHERE {"+LB
                + "  ?c rdfs:subClassOf ex:Student "+LB
                + "}";
        
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==3);
    }
    
    public void testDisjointClassesQuery() throws Exception {
        loadDataSetWithAxioms(getAxioms());
        String s= "PREFIX  ex:   <http://example.org/> "+LB
                + "PREFIX :      <http://example.org/>" +LB
                + "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?c WHERE {"+LB
                + "?c owl:disjointWith ex:Conference." +LB
                + "}";
        
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            //System.out.println(result.next());
            noResults++;
        }
        
        assertTrue(noResults==2);
    }
    
    public void testOPSuccessors() throws Exception {
        loadDataSetWithAxioms(getAxioms());
        String s= "PREFIX ex:   <http://example.org/> "+LB
                + "PREFIX :     <http://example.org/>" +LB
                + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "+LB
                + "SELECT ?p ?v WHERE { "+LB
                + "  ?p rdf:type owl:ObjectProperty . ex:John ?p ?v "+LB
                + "}";
        
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,m_dataSet);
        int noResults=0;
        while (result.hasNext()) {
            result.next();
            noResults++;
        }
        assertTrue(noResults==1);
    }
}
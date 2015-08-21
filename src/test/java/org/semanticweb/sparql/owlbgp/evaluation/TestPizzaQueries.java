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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.OWLBGPHermiT;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.sparql.OWLReasonerSPARQLEngine;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class TestPizzaQueries extends TestCase {
    public static final String LB = System.getProperty("line.separator") ;
    
    protected final OWLOntologyManager manager;
    protected final OWLDataFactory factory;
    protected OWLOntology queriedOntology;
    protected OWLOntologyDataSet dataset;
    
    public TestPizzaQueries() {
        this(null);
    }
    public TestPizzaQueries(String name) {
        super(name);
        manager = OWLManager.createOWLOntologyManager();
        factory=manager.getOWLDataFactory();
    }
    
    @Override
    protected void setUp() throws Exception {
        org.semanticweb.owlapi.model.IRI physicalIRI=org.semanticweb.owlapi.model.IRI.create(getClass().getResource("/ontologies/pizza.owl").toURI());
    	queriedOntology=manager.loadOntologyFromOntologyDocument(physicalIRI);
        dataset=new OWLOntologyDataSet(queriedOntology, new HashMap<String, OWLOntology>());
        OWLOntologyGraph graph=dataset.getDefaultGraph();
        graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY);
    }

    public void testPizzaBNode() throws Exception {
    	String s="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
                + "PREFIX : <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
                + "SELECT ?x WHERE {"+LB
                + "?x :hasTopping _:bnode"+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,dataset);
        assertTrue(!result.hasNext());
    }
    public void testPizzaComplexClassVar() throws Exception {
        String s="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
                + "PREFIX : <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
                + "SELECT ?x ?o WHERE {"+LB
                + "?o rdf:type owl:Class . "+LB
                + "?x rdfs:subClassOf [rdf:type owl:Restriction; owl:onProperty :hasTopping; owl:someValuesFrom ?o]"+LB
                + "}";
        OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine();
        Query query=QueryFactory.create(s);
        ResultSet result=sparqlEngine.execQuery(query,dataset);
        Configuration c=new Configuration();
        c.ignoreUnsupportedDatatypes=true;
        OWLReasoner hermit=new OWLBGPHermiT(c, dataset.getDefaultGraph().getOntology());
        OWLDataFactory factory=manager.getOWLDataFactory();
        OWLClass subClass;
        OWLClass qualification;
        OWLClassExpression superClass;
        OWLSubClassOfAxiom subClassOf;
        OWLObjectProperty hasTopping=factory.getOWLObjectProperty(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping"));
        int noExpectedResults=0;
        Set<OWLClass> allClasses=queriedOntology.getClassesInSignature(true);
        allClasses.add(factory.getOWLThing());
        allClasses.add(factory.getOWLNothing());
        Map<OWLClass, Set<OWLClass>> expectedSolutions=new HashMap<OWLClass, Set<OWLClass>>();
        for (OWLClass sub : allClasses) {
            for (OWLClass filler : allClasses) {
                superClass=factory.getOWLObjectSomeValuesFrom(hasTopping, filler);
                subClassOf=factory.getOWLSubClassOfAxiom(sub, superClass);
                if (hermit.isEntailed(subClassOf)) {
                    noExpectedResults++;
                    Set<OWLClass> values=expectedSolutions.get(sub);
                    if (values==null)
                        values=new HashSet<OWLClass>();
                    values.add(filler);
                    expectedSolutions.put(sub, values);
                }
            }
        }
        //System.out.println(expectedResults);
        // We get 635 expected results
        int noResults=0;
        while (result.hasNext()) {
            QuerySolution solution=result.next();
            noResults++;
            subClass=factory.getOWLClass(IRI.create(solution.get("?x").asResource().getURI()));
            qualification=factory.getOWLClass(IRI.create(solution.get("?o").asResource().getURI()));
//            System.out.println(subClass+" -> "+qualification);
            superClass=factory.getOWLObjectSomeValuesFrom(hasTopping, qualification);
            subClassOf=factory.getOWLSubClassOfAxiom(subClass, superClass);
            boolean isEntailed=hermit.isEntailed(subClassOf);
            assertTrue(isEntailed);
            Set<OWLClass> values=expectedSolutions.get(subClass);
            assertTrue(values!=null);
            boolean isRemoved=values.remove(qualification);
            assertTrue(isRemoved); 
            if (values.isEmpty())
                expectedSolutions.remove(subClass);
        }
        /*System.out.println("----missed-----");
        for (OWLClass missedSubClass : expectedSolutions.keySet())
            for (OWLClass missedQualification : expectedSolutions.get(missedSubClass))
                System.out.println(missedSubClass+" -> "+missedQualification);
        System.out.println("NoResults: "+noResults);*/
        assertTrue(noResults==noExpectedResults); 
    }
}
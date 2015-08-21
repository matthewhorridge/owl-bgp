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

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.sparql.OWLReasonerSPARQLEngine;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.MinimalPrintingMonitor;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class TestSamsung {
	public static final String LB = System.getProperty("line.separator") ; 

	public static void main(String[] args) throws Exception {
	    long t=System.currentTimeMillis();
	    OWLOntologyDataSet dataset=getSamsungDataSet();
	    System.out.println("OWLOntology: "+(System.currentTimeMillis()-t));
	    OWLOntologyGraph graph=dataset.getDefaultGraph();
	    t=System.currentTimeMillis();
	    graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS);
	    System.out.println("Precompute: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine(new MinimalPrintingMonitor());
	    System.out.println();
	    getSamsungQ1(sparqlEngine, dataset);
	    System.out.println();
	    getSamsungQ1(sparqlEngine, dataset);
	    System.out.println();
	    getSamsungQ2(sparqlEngine, dataset);
	    System.out.println();
	    getSamsungQ3(sparqlEngine, dataset);
	    System.out.println();
	    getSamsungQ4(sparqlEngine, dataset);
	    System.out.println();
	    getSamsungQ5(sparqlEngine, dataset);
	}
    public static OWLOntologyDataSet getSamsungDataSet() throws OWLOntologyCreationException {
        OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
        OWLOntologyIRIMapper mapper=new AutoIRIMapper(new File("/Users/bglimm/Downloads/Samsung/"), false);
        manager.addIRIMapper(mapper);
        OWLOntology ont=manager.loadOntologyFromOntologyDocument(IRI.create("file:/Users/bglimm/Downloads/Samsung/ContextReasoning_full.owl"));
        return new OWLOntologyDataSet(ont, null);
    }
    public static String getSamsungPrefix() {
        String prefix="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
             + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
             + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
             + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
             + "PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +LB
             + "PREFIX g: <http://www.co-ode.org/ontologies/galen#> " +LB
             + "PREFIX ContextReasoning: <http://sait.samsung.com/MS/IMP/2010/5/19/ContextReasoning.owl#> " +LB 
             + "PREFIX common: <http://sait.samsung.com/MS/IMP/2010/5/18/environments.owl#>  " +LB
             + "PREFIX location: <http://sait.samsung.com/MS/IMP/2010/5/13/Location.owl#>  " +LB
             + "PREFIX datetime: <http://sait.samsung.com/MS/IMP/2010/5/17/DateTime.owl#>  " +LB
             + "PREFIX smartphone: <http://sait.samsung.com/MS/IMP/2010/5/17/SmartPhone.owl#> " +LB;
         return prefix;
    } 
    public static void getSamsungQ1(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        String queryString=getSamsungPrefix()
            + "SELECT ?class WHERE {"+LB
            + "   ?ctx rdf:type owl:NamedIndividual."+LB
            + "   ?class rdf:type owl:Class."+LB
            + "   ?ctx rdf:type ContextReasoning:Context."+LB 
            + "   ?class rdfs:subClassOf ContextReasoning:Context."+LB  
            + "   ?ctx rdf:type ?class."+LB 
            + "} "+LB;
//        System.out.println("Query: "+queryString);
        long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query creation: "+(System.currentTimeMillis()-t) + "ms");
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        System.out.println("Result computation: "+(System.currentTimeMillis()-t) + "ms");
    }
    public static void getSamsungQ2(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        String queryString=getSamsungPrefix()
            + "SELECT ?class WHERE {"+LB
            + "   ?ctx rdf:type owl:NamedIndividual."+LB
            + "   ?class rdf:type owl:Class."+LB
            + "   ?ctx rdf:type ContextReasoning:WhenContext."+LB 
            + "   ?class rdfs:subClassOf ContextReasoning:Context."+LB  
            + "   ?ctx rdf:type ?class."+LB 
            + "} "+LB;
//        System.out.println("Query: "+queryString);
        long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
//        System.out.println("Query creation: "+(System.currentTimeMillis()-t) + "ms");
//        t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        System.out.println("Result computation: "+(System.currentTimeMillis()-t) + "ms");
    } 
    public static void getSamsungQ3(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        String queryString=getSamsungPrefix()
            + "SELECT ?class WHERE {"+LB
            + "   ?ctx rdf:type owl:NamedIndividual."+LB
            + "   ?class rdf:type owl:Class."+LB
            + "   ?ctx rdf:type ContextReasoning:WhereContext."+LB 
            + "   ?class rdfs:subClassOf ContextReasoning:Context."+LB  
            + "   ?ctx rdf:type ?class."+LB 
            + "} "+LB;
//        System.out.println("Query: "+queryString);
        long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
//        System.out.println("Query: "+(System.currentTimeMillis()-t));
//        t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        System.out.println("Result computation: "+(System.currentTimeMillis()-t) + "ms");
    } 
    public static void getSamsungQ4(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        String queryString=getSamsungPrefix()
            + "SELECT ?class WHERE {"+LB
            + "   ?ctx rdf:type owl:NamedIndividual."+LB
            + "   ?class rdf:type owl:Class."+LB
            + "   ?ctx rdf:type ContextReasoning:WithWhomContext."+LB 
            + "   ?class rdfs:subClassOf ContextReasoning:Context."+LB  
            + "   ?ctx rdf:type ?class."+LB 
            + "} "+LB;
//        System.out.println("Query: "+queryString);
        long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
//        System.out.println("Query creation: "+(System.currentTimeMillis()-t) + "ms");
//        t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        System.out.println("Result computation: "+(System.currentTimeMillis()-t) + "ms");
    } 
    public static void getSamsungQ5(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        String queryString=getSamsungPrefix()
            + "SELECT ?wp WHERE {"+LB
            + "   ?wp rdf:type owl:NamedIndividual."+LB
            + "   ?wp rdf:type smartphone:WithPerson."+LB 
            + "} "+LB;
//        System.out.println("Query: "+queryString);
        long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
//        System.out.println("Query creation: "+(System.currentTimeMillis()-t) + "ms");
//        t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        System.out.println("Result computation: "+(System.currentTimeMillis()-t) + "ms");
    } 
}
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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.sparql.OWLReasonerSPARQLEngine;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.MinimalPrintingMonitor;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class TestSPARQLDL {
	public static final String LB = System.getProperty("line.separator") ; 

	public static void main(String[] args) throws Exception {
	    long t=System.currentTimeMillis();
	    OWLOntologyDataSet dataset=getSPARQLDLDataSet();
	    System.out.println("OWLOntology loaded in: "+(System.currentTimeMillis()-t));
	    OWLOntologyGraph graph=dataset.getDefaultGraph();
	    t=System.currentTimeMillis();	    
//	    graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY);
	    graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY/*, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS*/);
	    System.out.println("Precomputation lasted: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine(new MinimalPrintingMonitor());
//	    getLUBMQTest(sparqlEngine, dataset);
//	    getLUBMQ0(sparqlEngine, dataset);
	    t=System.currentTimeMillis();
	    
	    
	    
	    getLUBMQ1(sparqlEngine, dataset);
        /*getLUBMQ2(sparqlEngine, dataset);
        getLUBMQ3(sparqlEngine, dataset);
        getLUBMQ4(sparqlEngine, dataset);
        getLUBMQ5(sparqlEngine, dataset);
        getLUBMQ6(sparqlEngine, dataset);
        getLUBMQ7(sparqlEngine, dataset);
        getLUBMQ8(sparqlEngine, dataset);
        getLUBMQ9(sparqlEngine, dataset);
        getLUBMQ10(sparqlEngine, dataset);*/
        
        System.out.println("The execution of the 10 queries finished in "+(System.currentTimeMillis()-t) +"  msec");
	}
	public static OWLOntologyDataSet getSPARQLDLDataSet() throws OWLOntologyCreationException {
	    OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
	    OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies/univ-bench.owl"));
	    File dir = new File("evaluation/ontologies/LUBM-1");
        String[] children = dir.list();
        for (int i=0;i<children.length;i++){
            File file=new File("evaluation/ontologies/LUBM-1/"+children[i]); 
            if (file.isFile()) {
        	  OWLOntology tmp=manager.loadOntologyFromOntologyDocument(file);
	          manager.addAxioms(ont, tmp.getAxioms());
            }
        }
	    
	   
	    /*OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/univ-bench-dl.owl"));
	    OWLOntology tmp=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBM_owl-dl/1-ub-dl-univ0"+".owl"));
	    manager.addAxioms(ont, tmp.getAxioms());
	    for (int i=0;i<3;i++) {
	    	tmp=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBM_owl-dl/1-ub-dl-univ0-dept"+i+".owl"));
            manager.addAxioms(ont, tmp.getAxioms());
	    }*/
	    //OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBMw3Dep.owl"));
        /*try {
        	 File file = new File("evaluation/ontologies/LUBMwDep.owl");
        	 manager.saveOntology(ont, IRI.create(file.toURI()));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        return new OWLOntologyDataSet(ont, null);
    }
	public static String getLUBMPrefix() {
	   String prefix="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
	        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
	        + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
	        + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
	        + "PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +LB
	        //+ "PREFIX ub: <http://uob.iodt.ibm.com/univ-bench-dl.owl#> " +LB 
	        + "PREFIX g: <http://www.co-ode.org/ontologies/galen#>"
	        + "SELECT * WHERE { " +LB;
	    return prefix;
	}
	
	public static void getLUBMQ1(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q1");
       String queryString=getLUBMPrefix()
        + "?x rdf:type ub:GraduateStudent. " +LB
        + "?x ?y ?z. " +LB
        + "?y rdf:type owl:ObjectProperty." +LB
        + "?w rdf:type ub:Course." +LB
        + " } "+LB;
        //long t=System.currentTimeMillis();
       Query query=QueryFactory.create(queryString);
       //System.out.println("Query: "+(System.currentTimeMillis()-t));
       //t=System.currentTimeMillis();
       sparqlEngine.execQuery(query,dataset);
       //System.out.println("Result: "+(System.currentTimeMillis()-t));
	}
	public static void getLUBMQ2(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q2");
	    String queryString=getLUBMPrefix()
        + "?x rdf:type ub:Student." +LB
        + "?x rdf:type ?C." +LB
        + "?C rdfs:subClassOf ub:Employee."+LB
        + "?x ub:undergraduateDegreeFrom ?y." +LB
        //+ "?x ub:hasUndergraduateDegreeFrom ?y." +LB
            + "} "+LB;
	    //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
	}
	public static void getLUBMQ3(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q3");
        String queryString=getLUBMPrefix()
        	+"?x rdf:type ub:Person." +LB
            +"?x ?y <http://www.Department0.University0.edu>." +LB
            +"?y rdf:type owl:ObjectProperty." +LB
            +"?y rdfs:subPropertyOf ub:memberOf." +LB
            //+"?y rdfs:subPropertyOf ub:isMemberOf." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getLUBMQ4(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q4");
        String queryString=getLUBMPrefix()
            +"<http://www.Department0.University0.edu/GraduateStudent5> ?y ?w." +LB
            +"?w rdf:type ?z." +LB
            +"?z rdfs:subClassOf ub:Course." +LB
            + "?y rdf:type owl:ObjectProperty." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getLUBMQ5(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q5");
        String queryString=getLUBMPrefix()
        	+"?x rdf:type ub:GraduateStudent." +LB
            +"?x ?y ?w." +LB
        	+"?y rdf:type owl:ObjectProperty." +LB
            +"?w rdf:type ?z." +LB
        	+"?z rdfs:subClassOf ub:Course." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getLUBMQ6(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q6");
        String queryString=getLUBMPrefix()
        	+"?x rdf:type ub:GraduateStudent." +LB
            +"?x ?y ?w." +LB
            +"?y rdf:type owl:ObjectProperty." +LB
            +"?w rdf:type ?z." +LB
            +"?z owl:disjointWith ub:GraduateCourse." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getLUBMQ7(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q7");
        String queryString=getLUBMPrefix()
            +"?c rdfs:subClassOf owl:Thing." +LB
            +"?x rdf:type ?c." +LB
            +"?x ub:takesCourse ?a." +LB
            +"?x ub:teachingAssistantOf ?a." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getLUBMQ8(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q8");
        String queryString=getLUBMPrefix()
        	+"?x ub:advisor ?y." +LB
            +"?x rdf:type ?a." +LB
            +"?a rdfs:subClassOf ub:Person." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getLUBMQ9(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q9");
        String queryString=getLUBMPrefix()
        	+"?a rdfs:subClassOf ub:Person." +LB
            +"?x rdf:type ?a." +LB
        	+"?x ub:teachingAssistantOf ?y." +LB
        	+"?y rdf:type ub:Course." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getLUBMQ10(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q10");
        String queryString=getLUBMPrefix()
        	+"?x rdf:type ub:GraduateStudent." +LB
            +"?x ub:memberOf ?w." +LB
            +"?a ?p ?w." +LB
            +"?p rdf:type owl:ObjectProperty." +LB
            +"?a rdf:type ?b." +LB
        	+"?p rdfs:subPropertyOf ub:worksFor." +LB
            +"?b rdfs:subClassOf ub:Faculty." +LB
            +"?x ub:advisor ?a." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    
}

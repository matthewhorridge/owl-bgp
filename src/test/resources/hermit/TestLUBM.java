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

public class TestLUBM {
	public static final String LB = System.getProperty("line.separator") ; 

	public static void main(String[] args) throws Exception {
	    long t=System.currentTimeMillis();
	    OWLOntologyDataSet dataset=getLUBMDataSet();
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
        getLUBMQ2(sparqlEngine, dataset);
        getLUBMQ3(sparqlEngine, dataset);
        getLUBMQ4(sparqlEngine, dataset);
        getLUBMQ5(sparqlEngine, dataset);
        getLUBMQ6(sparqlEngine, dataset);
        getLUBMQ7(sparqlEngine, dataset);
        getLUBMQ8(sparqlEngine, dataset);
        getLUBMQ9(sparqlEngine, dataset);
        getLUBMQ10(sparqlEngine, dataset);
        getLUBMQ11(sparqlEngine, dataset);
        getLUBMQ12(sparqlEngine, dataset);
        getLUBMQ13(sparqlEngine, dataset);
        getLUBMQ14(sparqlEngine, dataset);
        //System.out.println("The execution of the 14 queries finished in "+(System.currentTimeMillis()-t) +"  msec");
	}
	public static OWLOntologyDataSet getLUBMDataSet() throws OWLOntologyCreationException {
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
	    
	    //OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies/LUBMwDep.owl"));
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
	        + "PREFIX g: <http://www.co-ode.org/ontologies/galen#>"
	        + "SELECT * WHERE { " +LB;
	    return prefix;
	}
	public static void getLUBMQTest(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q0");
	    String queryString=getLUBMPrefix() 
        //+"?x rdf:type ?a." +LB
       // +"?y rdf:type ?b." +LB
	    +"?x ?z ?y." +LB
	    +"?z rdf:type owl:ObjectProperty." +LB
	    //+"?x rdfs:subClassOf ub:GraduateStudent."
	    + "ub:Student rdfs:subClassOf ["+LB
        + "   a owl:Restriction ; "+LB
        + "   owl:onProperty ?z ; "+LB
        + "   owl:someValuesFrom ub:Course]. " +LB
	    + " } "+LB;
	    long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    //System.out.println("Query: "+(System.currentTimeMillis()-t));
	    //t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    //System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}
	public static void getLUBMQ0(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q0");
	    String queryString=getLUBMPrefix()
	        + " ?o rdfs:subClassOf ub:GraduateStudent."
	        + "   ?o rdfs:subClassOf ["+LB
            + "   a owl:Restriction ; "+LB
            + "   owl:onProperty ub:takesCourse ; "+LB
            + "   owl:someValuesFrom ?y]. " +LB
	        + " } "+LB;
	    long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    //System.out.println("Query: "+(System.currentTimeMillis()-t));
	    //t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    //System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}
	public static void getLUBMQ1(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q1");
       String queryString=getLUBMPrefix()
        + "  ?x rdf:type ub:GraduateStudent. " +LB
        + "  ?x ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>. " +LB
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
        	+ "  ?x rdf:type ub:GraduateStudent. " +LB
            + "  ?y rdf:type ub:University. " +LB
            + "  ?z rdf:type ub:Department. " +LB
            + "  ?x ub:memberOf ?z. " +LB
            + "  ?z ub:subOrganizationOf ?y. " +LB
            + "  ?x ub:undergraduateDegreeFrom ?y." +LB
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
            + "  ?x rdf:type ub:Publication. " +LB
            + "  ?x ub:publicationAuthor <http://www.Department0.University0.edu/AssistantProfessor0>. " +LB
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
            + "?x rdf:type ub:Professor. " +LB
            + "?x ub:worksFor <http://www.Department0.University0.edu>. " +LB
            + "?x ub:name ?y1. " +LB
            + "?x ub:emailAddress ?y2. " +LB
            + "?x ub:telephone ?y3. " +LB
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
            + "?x rdf:type ub:Person. " +LB         
            + "?x ub:memberOf <http://www.Department0.University0.edu>. " +LB
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
            + "  ?x rdf:type ub:Student. " +LB
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
            +"  ?x rdf:type ub:Student. " +LB
            +"  ?y rdf:type ub:Course. " +LB
            +"  <http://www.Department0.University0.edu/AssociateProfessor0> ub:teacherOf ?y. " +LB
            +"  ?x ub:takesCourse ?y." +LB
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
            +"  ?x rdf:type ub:Student. " +LB
            +"  ?y rdf:type ub:Department. " +LB
            +"  ?x ub:memberOf ?y. " +LB
            +"  ?y ub:subOrganizationOf <http://www.University0.edu>."
            +"  ?x ub:emailAddress ?z." +LB
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
            +"  ?x rdf:type ub:Student. " +LB
            +"  ?y rdf:type ub:Faculty. " +LB
            +"  ?z rdf:type ub:Course. " +LB
            +"  ?x ub:advisor ?y. " +LB
            +"  ?y ub:teacherOf ?z. " +LB
            +"  ?x ub:takesCourse ?z. " +LB
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
            + "  ?x rdf:type ub:Student. " +LB
            + "  ?x ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>"
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getLUBMQ11(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q11");
        String queryString=getLUBMPrefix()
            + "  ?x rdf:type ub:ResearchGroup. " +LB   
            + "  ?x ub:subOrganizationOf <http://www.University0.edu>." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getLUBMQ12(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q12");
        String queryString=getLUBMPrefix()
            + "  ?x rdf:type ub:Chair. " +LB
            + "  ?y rdf:type ub:Department. " +LB
            + "  ?x ub:worksFor ?y. "+LB
            + "  ?y ub:subOrganizationOf <http://www.University0.edu>." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getLUBMQ13(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q13");
        String queryString=getLUBMPrefix()
            + "  ?x rdf:type ub:Person. " +LB
            + "  <http://www.University0.edu> ub:hasAlumnus ?x." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getLUBMQ14(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q14");
        String queryString=getLUBMPrefix()
            + "  ?x rdf:type ub:UndergraduateStudent. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
}

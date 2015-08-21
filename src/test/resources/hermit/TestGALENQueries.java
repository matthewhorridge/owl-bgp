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

public class TestGALENQueries {
	public static final String LB = System.getProperty("line.separator") ; 

	public static void main(String[] args) throws Exception {
	    long t=System.currentTimeMillis();
	    OWLOntologyDataSet dataset=getGALENDataSet();
	    System.out.println("OWLOntology loaded in: "+(System.currentTimeMillis()-t));
	    OWLOntologyGraph graph=dataset.getDefaultGraph();
	    t=System.currentTimeMillis();	    
	    graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY/*, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS*/);
	    System.out.println("Precomputation lasted: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine(new MinimalPrintingMonitor());

	    getComplexQ1(sparqlEngine, dataset);
        getComplexQ2(sparqlEngine, dataset);
        getComplexQ3(sparqlEngine, dataset);
        getComplexQ4(sparqlEngine, dataset);
        getComplexQ5(sparqlEngine, dataset);
	}
	public static OWLOntologyDataSet getGALENDataSet() throws OWLOntologyCreationException {
	    OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
	    OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/newOntologies/Galen.owl"));
        return new OWLOntologyDataSet(ont, null);
    }
	public static String getGALENPrefix() {
	   String prefix="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
	        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
	        + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
	        + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
	        + "PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +LB
	        + "PREFIX g: <http://www.co-ode.org/ontologies/galen#>"
	        + "SELECT * WHERE { " +LB;
	    return prefix;
	}
	
	public static void getComplexQ1(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q1");
       String queryString=getGALENPrefix()
    	   + "g:Infection rdfs:subClassOf  _:o. " +LB
    	   + "_:o rdf:type owl:Restriction. " +LB
    	   + "_:o owl:onProperty g:HasCausalLinkTo. "+LB
    	   + "_:o owl:someValuesFrom ?x." +LB
        + " } "+LB;
       Query query=QueryFactory.create(queryString);
       //t=System.currentTimeMillis();
       sparqlEngine.execQuery(query,dataset);
       //System.out.println("Result: "+(System.currentTimeMillis()-t));
	}
	public static void getComplexQ2(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q2");
	    String queryString=getGALENPrefix()
	       + "g:Infection rdfs:subClassOf  _:o. " +LB
	       + "_:o rdf:type owl:Restriction. " +LB
	       + "_:o owl:onProperty ?y. "+LB
	       + "_:o owl:someValuesFrom ?x. " +LB
	       + " ?y rdf:type owl:ObjectProperty." +LB
            + "} "+LB;
        Query query=QueryFactory.create(queryString);
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
	}
	public static void getComplexQ3(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q3");
            String queryString=getGALENPrefix()
            +"?x rdfs:subClassOf ["+LB
            + "   a owl:Class ; "+LB
            + "      owl:intersectionOf ("+LB
            + "         g:Infection "+LB
            + "         ["+LB
            + "            a owl:Restriction ;"+LB
            + "            owl:onProperty g:hasCausalAgent ; "+LB 
            + "            owl:someValuesFrom ?y "+LB
            + "         ]"+LB
            + "      ) "+LB
            + "] ."+LB
            + "} "+LB;
        Query query=QueryFactory.create(queryString);
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getComplexQ4(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q4");
        String queryString=getGALENPrefix()
        	+ "g:NAMEDLigament rdfs:subClassOf ["+LB
            + "   a owl:Class ; "+LB
            + "      owl:intersectionOf ("+LB
            + "         ?x "+LB
            + "         g:NAMEDInternalBodyPart" +LB
            + "      ) "+LB
            + "] . "+LB
                
            /*+ "?x rdfs:subClassOf ["+LB
            + "   a owl:Restriction ; "+LB
            + "   owl:onProperty g:hasShapeAnalagousTo ; "+LB
            + "   owl:someValuesFrom ["+LB
            + "      a owl:Class ;"+LB
            + "      owl:intersectionOf ("+LB
            + "         ?y "+LB
            + "         ["+LB
            + "            a owl:Restriction ;"+LB
            + "            owl:onProperty ?z ; "+LB 
            + "            owl:someValuesFrom g:linear "+LB
            + "         ]"+LB
            + "      ) "+LB
            + "   ]"+LB
            + "] . "+LB
            + "?z rdf:type owl:ObjectProperty." +LB
            */
            +"?x rdfs:subClassOf ["+LB
            + "   a owl:Class ; "+LB
            + "      owl:intersectionOf ("+LB
            + "         ["+LB
            + "            a owl:Restriction ;"+LB
            + "            owl:onProperty g:hasShapeAnalagousTo ; "+LB 
            + "            owl:someValuesFrom ?y "+LB
            + "         ]"+LB
            + "         ["+LB
            + "            a owl:Restriction ;"+LB
            + "            owl:onProperty ?z ; "+LB 
            + "            owl:someValuesFrom g:linear "+LB
            + "         ]"+LB
            + "      ) "+LB
            + "] ."+LB
            //+"?y rdfs:subClassOf g:Shape. "+LB
            + "?z rdf:type owl:ObjectProperty." +LB
            + "} "+LB;
        Query query=QueryFactory.create(queryString);
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getComplexQ5(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q5");
        String queryString=getGALENPrefix()
        	+"?x rdfs:subClassOf g:NonNormalCondition. "+LB
            +"?z rdfs:subPropertyOf g:ModifierAttribute." +LB
                
            +"?x rdfs:subClassOf ["+LB
            + "   a owl:Restriction ; "+LB
            + "   owl:onProperty ?y ; "+LB
            + "   owl:someValuesFrom g:Status]. " +LB
            + "?y rdf:type owl:ObjectProperty." +LB
            
            +"g:Bacterium rdfs:subClassOf ["+LB
            + "   a owl:Restriction ; "+LB
            + "   owl:onProperty ?z ; "+LB
            + "   owl:someValuesFrom ?w]. " +LB
            +"?z rdf:type owl:ObjectProperty ."+LB
                
            +"?w rdfs:subClassOf g:AbstractStatus. "+LB
            +"?y rdfs:subPropertyOf g:StatusAttribute."+LB
            + "} "+LB;
        Query query=QueryFactory.create(queryString);
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
}

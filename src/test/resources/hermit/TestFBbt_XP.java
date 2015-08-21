
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

	public class TestFBbt_XP {
		public static final String LB = System.getProperty("line.separator") ; 

		public static void main(String[] args) throws Exception {
		    long t=System.currentTimeMillis();
		    OWLOntologyDataSet dataset=getFBbt_XPDataSet();
		    System.out.println("OWLOntology loaded in: "+(System.currentTimeMillis()-t));
		    OWLOntologyGraph graph=dataset.getDefaultGraph();
		    t=System.currentTimeMillis();	    
		    graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY/*, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS*/);
		    System.out.println("Precomputation lasted: "+(System.currentTimeMillis()-t));
		    t=System.currentTimeMillis();
		    OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine(new MinimalPrintingMonitor());
		   
		    //getQ1(sparqlEngine, dataset);
	        //getQ2(sparqlEngine, dataset);
	        //getQ3(sparqlEngine, dataset);
	        getQ4(sparqlEngine, dataset);
		    //getQ5(sparqlEngine, dataset);
	        //getQ6(sparqlEngine, dataset);
		}
		public static OWLOntologyDataSet getFBbt_XPDataSet() throws OWLOntologyCreationException {
		    OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
		    OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/newOntologies/FBbt_XP.owl"));
		    return new OWLOntologyDataSet(ont, null);
	    }
		
		public static String getPrefix() {
		   String prefix="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
		        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
		        + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
		        + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
		        + "PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +LB
		        + "SELECT * WHERE { " +LB;
		    return prefix;
		}
		
		public static void getQ1(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
		   System.out.println("Q1");
		   String queryString=getPrefix()
		   + "?x rdfs:subClassOf  _:o. " +LB
		   + "_:o rdf:type owl:Restriction. " +LB
		   + "_:o owl:onProperty <http://purl.org/obo/owl/OBO_REL#part_of>. "+LB
		   + "_:o owl:allValuesFrom ?y." +LB
		   + "?x rdfs:subClassOf <http://purl.org/obo/owl/FBbt#FBbt_00005789>. " +LB
		   + " } "+LB;
	       Query query=QueryFactory.create(queryString);
	       //t=System.currentTimeMillis();
	       sparqlEngine.execQuery(query,dataset);
	       //System.out.println("Result: "+(System.currentTimeMillis()-t));
		}
		public static void getQ2(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
		    System.out.println("Q2");
		    String queryString=getPrefix()
		    + "?x rdfs:subClassOf  _:o. " +LB
		    + "_:o rdf:type owl:Restriction. " +LB
		    + "_:o owl:onProperty ?y. "+LB
		    + "_:o owl:allValuesFrom <http://purl.org/obo/owl/FBbt#FBbt_00001606>." +LB
		    + "?y rdfs:subPropertyOf <http://purl.org/obo/owl/OBO_REL#part_of>. " +LB
		    + "?y rdf:type owl:ObjectProperty." +LB
		    + "} "+LB;
			Query query=QueryFactory.create(queryString);
	        //t=System.currentTimeMillis();
		    sparqlEngine.execQuery(query,dataset);
		    //System.out.println("Result: "+(System.currentTimeMillis()-t));
		}
		public static void getQ3(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
		    System.out.println("Q3");
		    String queryString=getPrefix()
		    + "?x rdfs:subClassOf  _:o. " +LB
		    + "_:o rdf:type owl:Restriction. " +LB
		    + "_:o owl:onProperty ?y. "+LB
		    + "_:o owl:someValuesFrom <http://purl.org/obo/owl/FBbt#FBbt_00025990>." +LB
		    + "?y rdfs:subPropertyOf <http://purl.org/obo/owl/obo#overlaps>. " +LB
		    + "?y rdf:type owl:ObjectProperty." +LB
	        + "} "+LB;
	        Query query=QueryFactory.create(queryString);
	        //t=System.currentTimeMillis();
	        sparqlEngine.execQuery(query,dataset);
	        //System.out.println("Result: "+(System.currentTimeMillis()-t));
		}
		public static void getQ4(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	        System.out.println("Q4");
			String queryString=getPrefix()
			+ "<http://purl.org/obo/owl/FBbt#FBbt_00001606> rdfs:subClassOf  _:o. " +LB
			+ "_:o rdf:type owl:Restriction. " +LB
			+ "_:o owl:onProperty ?y. "+LB
			+ "_:o owl:someValuesFrom ?x." +LB
			+ "?y rdf:type owl:ObjectProperty." +LB			
			+ "} "+LB;
		    Query query=QueryFactory.create(queryString);
		    //t=System.currentTimeMillis();
		    sparqlEngine.execQuery(query,dataset);
		    //System.out.println("Result: "+(System.currentTimeMillis()-t));
	    }    
		public static void getQ5(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
		    System.out.println("Q5");
	        String queryString=getPrefix()
	        + "<http://purl.org/obo/owl/FBbt#FBbt_00001606> rdfs:subClassOf  _:o. " +LB
	        + "_:o rdf:type owl:Restriction. " +LB
	     	+ "_:o owl:onProperty ?y. "+LB
	     	+ "_:o owl:someValuesFrom ?x." +LB
	     	+ "?y rdfs:subPropertyOf <http://purl.org/obo/owl/obo#develops_from>. " +LB
	     	+"?y rdf:type owl:ObjectProperty." +LB
	    	+ "} "+LB;
	        Query query=QueryFactory.create(queryString);
	        //t=System.currentTimeMillis();
	        sparqlEngine.execQuery(query,dataset);
	        //System.out.println("Result: "+(System.currentTimeMillis()-t));
	    }
		public static void getQ6(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
		    System.out.println("Q6");
	        String queryString=getPrefix()
	        +"?x rdfs:subClassOf ["+LB
	    	+ "   a owl:Class ; "+LB
	    	+ "      owl:intersectionOf ("+LB
	    	+ "         ["+LB
	    	+ "            a owl:Restriction ;"+LB
	    	+ "            owl:onProperty ?p ; "+LB 
	    	+ "            owl:someValuesFrom ?y "+LB
	    	+ "         ]"+LB
	    	+ "         ?w "+ LB
	    	+ "      ) "+LB
	    	+ "]."+LB
	    	+ "?p rdf:type owl:ObjectProperty." +LB
	    	+ "?y rdfs:subClassOf <http://purl.org/obo/owl/FBbt#FBbt_00001884>. " +LB
	    	+ "?p rdfs:subPropertyOf <http://purl.org/obo/owl/OBO_REL#part_of>. " +LB
	    	//+ "?z rdf:type owl:ObjectProperty." +LB
	    	//+ "?z rdfs:subPropertyOf <http://purl.org/obo/owl/obo#develops_from>. " +LB
	    	//+ "?w rdfs:subClassOf <http://purl.org/obo/owl/FBbt#FBbt_00000001>." +LB
	        + "} "+LB;
	        Query query=QueryFactory.create(queryString);
	        //t=System.currentTimeMillis();
	        sparqlEngine.execQuery(query,dataset);
	        //System.out.println("Result: "+(System.currentTimeMillis()-t));
		}
}

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

public class TestUOBM {
	public static final String LB = System.getProperty("line.separator") ; 

	public static void main(String[] args) throws Exception {
	    long t=System.currentTimeMillis();
	    OWLOntologyDataSet dataset=getUOBMDataSet();
	    System.out.println("OWLOntology: "+(System.currentTimeMillis()-t));
	    OWLOntologyGraph graph=dataset.getDefaultGraph();
	    t=System.currentTimeMillis();
	    
	    graph.getReasoner().precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY/*, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS*/);
	    
	    System.out.println("Precompute: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    OWLReasonerSPARQLEngine sparqlEngine=new OWLReasonerSPARQLEngine(new MinimalPrintingMonitor());
	    //getUOBMQExample1(sparqlEngine, dataset);  
	    //getUOBMQ1(sparqlEngine, dataset);
	    getUOBMQ01(sparqlEngine, dataset);
        getUOBMQ02(sparqlEngine, dataset);
	    getUOBMQ1(sparqlEngine, dataset);
	    getUOBMQ2(sparqlEngine, dataset);
        getUOBMQ3(sparqlEngine, dataset);
        getUOBMQ4(sparqlEngine, dataset);
        getUOBMQ5(sparqlEngine, dataset);
        getUOBMQ6(sparqlEngine, dataset);
        getUOBMQ7(sparqlEngine, dataset);
        getUOBMQ8(sparqlEngine, dataset);
        getUOBMQ9(sparqlEngine, dataset);
        getUOBMQ10(sparqlEngine, dataset);
        getUOBMQ11(sparqlEngine, dataset);
        getUOBMQ12(sparqlEngine, dataset);
        getUOBMQ13(sparqlEngine, dataset);        
        getUOBMQ14(sparqlEngine, dataset);
        getUOBMQ15(sparqlEngine, dataset);
	}
	public static OWLOntologyDataSet getUOBMDataSet() throws OWLOntologyCreationException {
	    OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
	    OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/univ-bench-dl.owl"));
	    OWLOntology tmp=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBM_owl-dl/1-ub-dl-univ0"+".owl"));
	    //OWLOntology tmp=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/univ0.owl"));
	    manager.addAxioms(ont, tmp.getAxioms());
	    for (int i=0;i<3;i++) {
	    	tmp=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBM_owl-dl/1-ub-dl-univ0-dept"+i+".owl"));
            manager.addAxioms(ont, tmp.getAxioms());
	    }
	    //OWLOntology ont=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBMw3Dep.owl"));
        return new OWLOntologyDataSet(ont, null);
    }
	public static String getUOBMPrefix() {
	   String prefix="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
	        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
	        + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
	        + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#>" +LB
	        + "PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +LB
	        + "PREFIX g: <http://www.co-ode.org/ontologies/galen#>"
//	        + "PREFIX uob: <http://uob.iodt.ibm.com/univ-bench-lite.owl#> " +LB 
	        + "PREFIX uob: <http://uob.iodt.ibm.com/univ-bench-dl.owl#> " +LB 
	        + "SELECT * WHERE { " +LB;
	    return prefix;
	}
	
	/*public static void getUOBMQExample(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("QEx");
	    String queryString=getUOBMPrefix()
	    + "  ?x rdf:type uob:GraduateStudent. " +LB
	    + "  ?x uob:takesCourse ?y. " +LB
	    + "  ?y rdf:type uob:GraduateCourse." +LB
	    + " } "+LB;
	    long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    System.out.println("Query: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}*/
	
	/*public static void getUOBMQExample(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("QEx");
	    String queryString=getUOBMPrefix()
	    + "  ?x rdf:type uob:Person. " +LB
	    + "  ?x uob:like ?y. " +LB
	    + "  ?x rdf:type uob:PeopleWithHobby." +LB
	    + " } "+LB;
	    long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    System.out.println("Query: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}*/
	
	/*public static void getUOBMQExample(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("QEx");
	    String queryString=getUOBMPrefix()
	    + "  ?x rdf:type uob:Person. " +LB
	    + "  ?x uob:isCrazyAbout ?y. " +LB
	    + "  ?x rdf:type uob:Sports." +LB
	    //+ "  ?x rdf:type uob:SportsFan. " +LB
	    + " } "+LB;
	    long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    System.out.println("Query: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}*/
	
	/*public static void getUOBMQExample1(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("QEx");
	    String queryString=getUOBMPrefix()
	    + "  ?x rdf:type uob:Woman. " +LB
	    + "  ?x uob:teachingAssistantOf ?y. " +LB
	    + "  ?y rdf:type uob:Course." +LB
	    //+ "  ?x rdf:type uob:PeopleWithManyHobbies." +LB
	    + " } "+LB;
	    long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    System.out.println("Query: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}*/
	
	/*public static void getUOBMQ01(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q01");
	    String queryString=getUOBMPrefix()
	    + "  ?y rdf:type uob:Woman. " +LB
	    + "  ?x rdf:type uob:GraduateStudent. " +LB
	    //+ "  ?y rdf:type uob:Faculty. " +LB
	    //+ "  ?y rdf:type uob:Employee. " +LB
	    //+ "  ?y rdf:type uob:Professor. " +LB
	   // + "  ?x rdf:type uob:SportsFan. " +LB
	    + "  ?x uob:isAdvisedBy ?y. " +LB
//	    + "  ?x rdf:type uob:PeopleWithManyHobbies. " +LB
	    + " } "+LB;
	    long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    System.out.println("Query: "+(System.currentTimeMillis()-t));
	    t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}*/
	
	public static void getUOBMQ01(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q01");
	    String queryString=getUOBMPrefix()
	    + "  ?y rdf:type uob:Woman. " +LB
	    + "  ?x rdf:type uob:GraduateStudent. " +LB
	    + "  ?x uob:isAdvisedBy ?y. " +LB
	    + " } "+LB;
	    //long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    //System.out.println("Query: "+(System.currentTimeMillis()-t));
	    //t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    //System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}
	
	public static void getUOBMQ02(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q02");
	    String queryString=getUOBMPrefix()	    
	    + "  ?x rdf:type uob:Woman. " +LB
	    + "  ?x rdf:type uob:GraduateStudent. " +LB
	    + "  ?x rdf:type uob:SportsFan. " +LB
        + " } "+LB;
	    //long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    //System.out.println("Query: "+(System.currentTimeMillis()-t));
	    //t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    //System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}
	public static void getUOBMQ1(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q1");
	    String queryString=getUOBMPrefix()
	    + "  ?x rdf:type uob:UndergraduateStudent. " +LB
        + "  ?x uob:takesCourse <http://www.Department0.University0.edu/Course0>. " +LB
	    +" uob:takesCourse rdf:type owl:ObjectProperty."
        + " } "+LB;
	    //long t=System.currentTimeMillis();
	    Query query=QueryFactory.create(queryString);
	    //System.out.println("Query: "+(System.currentTimeMillis()-t));
	    //t=System.currentTimeMillis();
	    sparqlEngine.execQuery(query,dataset);
	    //System.out.println("Result: "+(System.currentTimeMillis()-t));
//	    ResultSetFormatter.asText(result);
	}
	public static void getUOBMQ2(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q2");
//	    OWLOntology: 6102
//	    HermiT: 188608
//	    Query: 727, 604
//	    Result: 289, 382
       String queryString=getUOBMPrefix()
        + "  ?x rdf:type uob:Employee. " +LB
        + " } "+LB;
       //long t=System.currentTimeMillis();
       Query query=QueryFactory.create(queryString);
       //System.out.println("Query: "+(System.currentTimeMillis()-t));
       //t=System.currentTimeMillis();
       sparqlEngine.execQuery(query,dataset);
       //System.out.println("Result: "+(System.currentTimeMillis()-t));
	}
	
	public static void getUOBMQ3(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q3");
//	    OWLOntology: 5949
//	    HermiT: 184355
//	    Query: 867, 2
//	    Result: 323911, 256837
	    String queryString=getUOBMPrefix()
        	+ "  ?x rdf:type uob:Student. " +LB
            + "  ?x uob:isMemberOf <http://www.Department0.University0.edu>. " +LB
            + "} "+LB;
	    //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
	}
	public static void getUOBMQ4(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q4");
//	    Query: 2
//	    Result: 45
        String queryString=getUOBMPrefix()
            + "  ?x rdf:type uob:Publication. " +LB
            + "  ?x uob:publicationAuthor ?y. " +LB
            + "  ?y rdf:type uob:Faculty. " +LB
            + "  ?y uob:isMemberOf <http://www.Department0.University0.edu>. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getUOBMQ5(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
//	    Query: 2
//	    Result: 16
	    System.out.println("Q5");
        String queryString=getUOBMPrefix()
            + "?x rdf:type uob:ResearchGroup. " +LB
            + "?x uob:subOrganizationOf <http://www.University0.edu>. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getUOBMQ6(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
//	    Query: 1
//	    Result: 100
	    System.out.println("Q6");
        String queryString=getUOBMPrefix()
            + "?x rdf:type uob:Person. " +LB         
            + "<http://www.University0.edu> uob:hasAlumnus ?x. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getUOBMQ7(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
//	    Query: 1
//	    Result: 161
	    System.out.println("Q7");
        String queryString=getUOBMPrefix()
            + "  ?x rdf:type uob:Person. " +LB
            + "  ?x uob:hasSameHomeTownWith <http://www.Department0.University0.edu/FullProfessor0>. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
	public static void getUOBMQ8(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
	    System.out.println("Q8");
        String queryString=getUOBMPrefix()
            +"  ?x rdf:type uob:SportsLover. " +LB
            +"  <http://www.Department0.University0.edu> uob:hasMember ?x. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getUOBMQ9(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q9");
        String queryString=getUOBMPrefix()
            +"  ?x rdf:type uob:GraduateCourse. " +LB
            +"  ?x uob:isTaughtBy ?y. " +LB
            +"  ?y uob:isMemberOf ?z. " +LB
            +"  ?z uob:subOrganizationOf <http://www.University0.edu>."
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getUOBMQ10(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q10");
        String queryString=getUOBMPrefix()
            +"  ?x uob:isFriendOf <http://www.Department0.University0.edu/FullProfessor0>. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getUOBMQ11(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q11");
        String queryString=getUOBMPrefix()
            + "  ?x rdf:type uob:Person. " +LB
            + "  ?x uob:like ?y. " +LB
            + "  ?z rdf:type uob:Chair. " +LB
            + "  ?z uob:isHeadOf <http://www.Department0.University0.edu>."
            + "  ?z uob:like ?y. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getUOBMQ12(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q12");
        String queryString=getUOBMPrefix()
            + "  ?x rdf:type uob:Student. " +LB   
            + "  ?x uob:takesCourse ?y. " +LB   
            + "  ?y uob:isTaughtBy <http://www.Department0.University0.edu/FullProfessor0>." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getUOBMQ13(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q13");
        String queryString=getUOBMPrefix()
            + "  ?x rdf:type uob:PeopleWithHobby. " +LB
            + "  ?x uob:isMemberOf <http://www.Department0.University0.edu>. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getUOBMQ14(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q14");
        String queryString=getUOBMPrefix()
            + "  ?x rdf:type uob:Woman. " +LB
            + "  ?x rdf:type uob:Student. " +LB
            + "  ?x uob:isMemberOf ?y. " +LB
//            + "  ?x uob:isHeadOf ?y. " +LB
//            + "  ?x uob:isTaughtBy ?y. " +LB
            + "  ?y uob:subOrganizationOf <http://www.University0.edu>." +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
    public static void getUOBMQ15(OWLReasonerSPARQLEngine sparqlEngine, OWLOntologyDataSet dataset) {
        System.out.println("Q15");
        String queryString=getUOBMPrefix()
            + "  ?x rdf:type uob:PeopleWithManyHobbies. " +LB
            + "  ?x uob:isMemberOf <http://www.Department0.University0.edu>. " +LB
            + "} "+LB;
        //long t=System.currentTimeMillis();
        Query query=QueryFactory.create(queryString);
        //System.out.println("Query: "+(System.currentTimeMillis()-t));
        //t=System.currentTimeMillis();
        sparqlEngine.execQuery(query,dataset);
        //System.out.println("Result: "+(System.currentTimeMillis()-t));
    }
}
//OWLOntology: 13654
//Class classification: 6201
//Realisation: 42420
//Q1
//Query: 243, 253
//Cost estimation time: 0, computation time: 30, results size: 4
//Result: 311, 278
//Q2
//Query: 3, 3
//Cost estimation time: 0, computation time: 32, results size: 0
//Result: 278, 125
//Q3
//Query: 11, 2
//Cost estimation time: 0, computation time: 6, results size: 6
//Result: 29, 12
//Q4
//Query: 4, 2
//Cost estimation time: 0, computation time: 4, results size: 34
//Result: 33, 86
//Q5
//Query: 3, 1
//Cost estimation time: 0, computation time: 259, results size: 719
//Result: 318, 46
//Q6
//Query: 4, 2
//Cost estimation time: 0, computation time: 228, results size: 7790
//Result: 243, 117
//Q7
//Query: 727, 2
//Cost estimation time: 0, computation time: 40, results size: 67
//Result: 1414, 38
//Q8
//Query: 1, 1
//Cost estimation time: 0, computation time: 408, results size: 7790
//Result: 497, 853
//Q9
//Query: 3, 1
//Cost estimation time: 0, computation time: 7164, results size: 208
//Result: 235818, 221652
//Q10
//Query: 2, 1
//Cost estimation time: 0, computation time: 4, results size: 4
//Result: 12, 12
//Q11
//Query: 1, 2
//Cost estimation time: 0, computation time: 4, results size: 224
//Result: 11, 11
//Q12
//Query: 2, 1
//Cost estimation time: 0, computation time: 306, results size: 15
//Result: 2866, 8
//Q13
//Query: 3, 2
//Cost estimation time: 0, computation time: 0, results size: 1
//Result: 14, 5
//Q14
//Query: 2, 0
//Cost estimation time: 0, computation time: 133, results size: 5916
//Result: 149, 53

//OWLOntology: 9248
//Class classification: 3822
//Realisation: 26900
//Q1
//Query: 316
//Cost estimation time: 26, computation time: 58, results size: 4
//Result: 430
//Q2
//Query: 39
//Cost estimation time: 238, computation time: 20, results size: 0
//Result: 345
//Q3
//Query: 3
//Cost estimation time: 2, computation time: 4, results size: 6
//Result: 17
//Q4
//Query: 2
//Cost estimation time: 24, computation time: 51, results size: 34
//Result: 94
//Q5
//Query: 1
//Cost estimation time: 13, computation time: 29, results size: 719
//Result: 48
//Q6
//Query: 1
//Cost estimation time: 0, computation time: 105, results size: 7790
//Result: 110
//Q7
//Query: 1
//Cost estimation time: 13, computation time: 17, results size: 67
//Result: 37
//Q8
//Query: 2
//Cost estimation time: 184, computation time: 664, results size: 7790
//Result: 855
//Q9
//Query: 2
//Cost estimation time: 221190, computation time: 6631, results size: 208
//Result: 227827
//Q10
//Query: 1
//Cost estimation time: 3, computation time: 4, results size: 4
//Result: 14
//Q11
//Query: 1
//Cost estimation time: 0, computation time: 5, results size: 224
//Result: 27
//Q12
//Query: 1
//Cost estimation time: 2496, computation time: 166, results size: 15
//Result: 2669
//Q13
//Query: 1
//Cost estimation time: 1, computation time: 0, results size: 1
//Result: 7
//Q14
//Query: 1
//Cost estimation time: 1, computation time: 37, results size: 5916
//Result: 43
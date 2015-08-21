package org.semanticweb.sparql.hermit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.semanticweb.HermiT.OWLBGPHermiT;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.sparql.OWLReasonerSPARQLEngine;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.MinimalPrintingMonitor;

import com.hp.hpl.jena.query.ARQ;

public class BenchmarkOWLBGP {
    protected BufferedReader m_in;
    public static String LB=System.getProperty("line.separator");
    public static int PRECOMPUTE_TIMEOUT=5*60*60*1000; // 4h
    public static int QUERY_TIMEOUT=5*60*60*1000; // 2h
    
    protected OWLOntology ontology;
    protected OWLReasonerSPARQLEngine sparqlEngine;
    protected OWLOntologyDataSet defaultOntologyDataSet;
    protected Long bgpExecTime=0l;
    protected String shortName;
    protected long overallTime=0;
    protected int overallSatTests=0;
    protected int overallSubsTests=0;
    protected int overallTests=0;
    
    public static void main(String[] args) throws Throwable {
        if (args == null || args.length==0 || args[0].equals("-h") || args[0].equals("--help") || args[0].equals("")) {
            System.out.println("usage BenchmarkSPARQLOWL -LUBM(1,0) -Dordering='Static'/'Dynamic' -Dsampling='Sampling' or   BenchmarkSPARQLOWL -LUBM(2,0) -Dordering='Static'/'Dynamic' Dsampling='Sampling' or BenchmarkSPARQLOWL -UOBM -Dordering='Static'/'Dynamic' Dquery='Query queryNo'" );
            System.out.println("Sampling is only applied with dynamic ordering and queryNo is one of the following {4,9,11,12,14,q1,q2}");
            System.out.println();
            System.out.println("LUBM(1,0)   (performs the LUBM benchmark with 1 university)");
            System.out.println("LUBM(2,0)  (performs the LUBM benchmark with 2 universities)");
            System.out.println("UOBM   (performs the UOBM benchmark (1 university departments 0-2))");
            System.exit(0);
        }
        if ("-LUBM(1,0)".equals(args[0])) {
            testLUBM(1);
        } else if ("-LUBM(2,0)".equals(args[0])) {
            testLUBM(2);
        }
        else if ("-UOBM".equals(args[0])) {
        	testUOBM();
        }
    } 
    
    public static void testLUBM(int univsize) throws Exception { 
        Map<String,String> queryID2query=new HashMap<String,String>();
        String queryString;
               
        //2nd query
        queryString="SELECT ?x ?y ?z WHERE { " +LB
        + "?x rdf:type ub:GraduateStudent. " +LB
        + "?y rdf:type ub:University. " +LB
        + "?z rdf:type ub:Department. " +LB
        + "?x ub:memberOf ?z. " +LB
        + "?z ub:subOrganizationOf ?y. " +LB
        + "?x ub:undergraduateDegreeFrom ?y." +LB
        + "?x rdf:type owl:NamedIndividual. " +LB
        + "?y rdf:type owl:NamedIndividual. " +LB
        + "?z rdf:type owl:NamedIndividual. }" +LB;
        queryID2query.put("Query 02",queryString);
        
        //7th query
        queryString="SELECT ?x ?y WHERE { " +LB
        +"?x rdf:type ub:Student. " +LB
        +"?y rdf:type ub:Course. " +LB
        +"?x ub:takesCourse ?y. " +LB
        +"<http://www.Department0.University0.edu/AssociateProfessor0> ub:teacherOf ?y. }" +LB;
        queryID2query.put("Query 07",queryString);
        
        //8th query
        queryString="SELECT ?x ?y ?z WHERE { " +LB
        +"?x rdf:type ub:Student. " +LB
        +"?y rdf:type ub:Department. " +LB
        +"?x ub:memberOf ?y. " +LB
        +"?y ub:subOrganizationOf <http://www.University0.edu>."
        +"?x ub:emailAddress ?z." +LB
        +"?x rdf:type owl:NamedIndividual. " +LB
        +"?y rdf:type owl:NamedIndividual. }" +LB;
        queryID2query.put("Query 08",queryString);
        
        //9th query
        queryString="SELECT ?x ?y ?z WHERE { " +LB
        +"?x rdf:type ub:Student. " +LB
        +"?y rdf:type ub:Faculty. " +LB
        +"?z rdf:type ub:Course. " +LB
        +"?x ub:advisor ?y. " +LB
        +"?y ub:teacherOf ?z. " +LB
        +"?x ub:takesCourse ?z. " +LB
        + "?x rdf:type owl:NamedIndividual. " +LB
        + "?y rdf:type owl:NamedIndividual. " +LB
        + "?z rdf:type owl:NamedIndividual. }" +LB;
        queryID2query.put("Query 09",queryString);
        
        BenchmarkOWLBGP execution;
        int run=1;
        String key1=System.getProperty("ordering");
        String key2=System.getProperty("sampling");
        if (univsize==1){
          if (key1==null || key1.equals("Static"))	
            System.out.println("Starting LUBM(1,0) performing static ordering...");
          else if (key2!=null && key2.equals("Sampling"))
            System.out.println("Starting LUBM(1,0) performing dynamic ordering with 50% sampling...");
          else 
        	 System.out.println("Starting LUBM(1,0) performing dynamic ordering...");   
          execution=new BenchmarkOWLBGP(1);
        }  
        else { 
          if (key1==null || key1.equals("Static"))	
              System.out.println("Starting LUBM(2,0) performing static ordering...");
          else if (key2!=null && key2.equals("Sampling"))
              System.out.println("Starting LUBM(2,0) performing dynamic ordering with 50% sampling...");
          else 
              System.out.println("Starting LUBM(2,0) performing dynamic ordering..."); 
          execution=new BenchmarkOWLBGP(2);
        }
        System.out.println("Taking measurements 4 times, ignoring the first warm-up for the averages...");
        if (execution!=null) {
            SortedMap<String, Long> results=new TreeMap<String, Long>();
            SortedMap<String, Long> resultsBGPEval=new TreeMap<String, Long>();
            while (run < 5) {
                System.out.println("Round "+run+" is starting...");
                for (String queryID : queryID2query.keySet()) {
                    long[] timings=execution.evaluate(queryID, queryID2query.get(queryID));
                    Long storedTime=results.get(queryID);
                    Long storedBGPTime=resultsBGPEval.get(queryID);
                    if (run > 1) {
                        if (storedTime==null) {
                            storedTime=timings[0];
                            results.put(queryID, storedTime);
                            storedBGPTime=timings[1];
                            resultsBGPEval.put(queryID, storedBGPTime);
                        } else {
                            storedTime+=timings[0];
                            storedBGPTime+=timings[1];
                            results.put(queryID, storedTime);
                            resultsBGPEval.put(queryID, storedBGPTime);
                        }
                    }
                }
                run++;
            }
            for (String queryID : results.keySet()) {
                System.out.println(queryID+" took on average: "+Math.round(((double)results.get(queryID)/(double)3))+" ms, only BGP evaluation: "+Math.round(((double)resultsBGPEval.get(queryID)/(double)3))+" ms. ");
            }
        }
    }
    public static void testUOBM() throws Exception { 
        Map<String,String> queryID2query=new HashMap<String,String>();
        String queryString;
              
        queryString="SELECT * WHERE { " +LB
        + "  ?x rdf:type uob:Publication. " +LB
        + "  ?x uob:publicationAuthor ?y. " +LB
        + "  ?y rdf:type uob:Faculty. " +LB
        + "  ?y uob:isMemberOf <http://www.Department0.University0.edu>. " +LB
        + "} "+LB;
        queryID2query.put("Query 4",queryString);
              
        queryString="SELECT * WHERE { " +LB
        +"  ?x rdf:type uob:GraduateCourse. " +LB
        +"  ?x uob:isTaughtBy ?y. " +LB
        +"  ?y uob:isMemberOf ?z. " +LB
        +"  ?z uob:subOrganizationOf <http://www.University0.edu>."
        + "} "+LB;
        queryID2query.put("Query 9",queryString);
        
        queryString="SELECT * WHERE { " +LB
        + "  ?x rdf:type uob:Person. " +LB
        + "  ?x uob:like ?y. " +LB
        + "  ?z rdf:type uob:Chair. " +LB
        + "  ?z uob:isHeadOf <http://www.Department0.University0.edu>."
        + "  ?z uob:like ?y. " +LB
        + "} "+LB;
        queryID2query.put("Query 11",queryString);
        
        queryString="SELECT * WHERE { " +LB
        + "  ?x rdf:type uob:Student. " +LB   
        + "  ?x uob:takesCourse ?y. " +LB   
        + "  ?y uob:isTaughtBy <http://www.Department0.University0.edu/FullProfessor0>." +LB
        + "} "+LB;
        queryID2query.put("Query 12",queryString);
        
        queryString="SELECT * WHERE { " +LB
        + "  ?x rdf:type uob:Woman. " +LB
        + "  ?x rdf:type uob:Student. " +LB
        + "  ?x uob:isMemberOf ?y. " +LB
        + "  ?y uob:subOrganizationOf <http://www.University0.edu>." +LB
        + "} "+LB;
        queryID2query.put("Query 14",queryString);
       
        queryString="SELECT * WHERE { " +LB
        + "  ?x rdf:type uob:GraduateStudent. " +LB
        + "  ?x uob:isAdvisedBy ?y. " +LB
        + "  ?x rdf:type uob:Woman. " +LB
        + "} "+LB;
        queryID2query.put("Query q1",queryString);
        
        queryString="SELECT * WHERE { " +LB
        + "  ?x rdf:type uob:SportsFan. " +LB
        + "  ?x rdf:type uob:GraduateStudent. " +LB
        + "  ?x rdf:type uob:Woman. " +LB
        + "} "+LB;
        queryID2query.put("Query q2",queryString);
        
        int run=1;
        String key1=System.getProperty("ordering");
        if (key1==null || key1.equals("Static"))	
            System.out.println("Starting UOBM performing static ordering...");
        else 
        	 System.out.println("Starting UOBM performing dynamic ordering..."); 
        BenchmarkOWLBGP execution=new BenchmarkOWLBGP(3);
        
        
//       System.out.println("Taking measurements 2 times, ignoring the first warm-up for the averages...");
        if (execution!=null) {
            SortedMap<String, Long> results=new TreeMap<String, Long>();
            SortedMap<String, Long> resultsBGPEval=new TreeMap<String, Long>();
            while (run < 2) {
//            	BenchmarkOWLBGP execution;
//                for (String queryID : queryID2query.keySet()) {
            	  String queryID=System.getProperty("query");
                	System.out.println(queryID+" started");
                	
//                	 execution=new BenchmarkOWLBGP(3);
                    long[] timings=execution.evaluate(queryID, queryID2query.get(queryID));
                    
                    Long storedTime=results.get(queryID);
                    Long storedBGPTime=resultsBGPEval.get(queryID);
                    if (run == 1) {
//                    	System.out.println("Round "+run+" is starting...");
                        if (storedTime==null) {
                            storedTime=timings[0];
                            results.put(queryID, storedTime);
                            storedBGPTime=timings[1];
                            resultsBGPEval.put(queryID, storedBGPTime);
                        } else {
                            storedTime+=timings[0];
                            storedBGPTime+=timings[1];
                            results.put(queryID, storedTime);
                            resultsBGPEval.put(queryID, storedBGPTime);
                        }
                    }
//                }
                run++;
            }
            for (String queryID : results.keySet()) {
                System.out.println(queryID+" took: "+Math.round((double)results.get(queryID))+" ms, only BGP evaluation took: "+Math.round((double)resultsBGPEval.get(queryID))+" ms. ");
            }
        }
    }
    public BenchmarkOWLBGP(int benchvar) throws URISyntaxException, OWLOntologyCreationException, IOException {
        try {
        	OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
    	    OWLOntology ontology;
    	    long t=System.currentTimeMillis();
    	    if (benchvar==1) {
        	   ontology=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies/univ-bench.owl"));
        	   File dir = new File("evaluation/ontologies/LUBM-1");
               String[] children = dir.list();
               for (int i=0;i<children.length;i++){
                  File file=new File("evaluation/ontologies/LUBM-1/"+children[i]); 
                  if (file.isFile()) {
                	OWLOntology tmp=manager.loadOntologyFromOntologyDocument(file);
        	        manager.addAxioms(ontology, tmp.getAxioms());
                  }
               }   	
            }
            else if (benchvar==2){
               ontology=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies/univ-bench.owl"));
         	   File dir = new File("evaluation/ontologies/LUBM-2");
               String[] children = dir.list();
               for (int i=0;i<children.length;i++){
                  File file=new File("evaluation/ontologies/LUBM-2/"+children[i]); 
                  if (file.isFile()) {
                 	OWLOntology tmp=manager.loadOntologyFromOntologyDocument(file);
         	        manager.addAxioms(ontology, tmp.getAxioms());
                  }
               }   	
            }
            else {
              ontology=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/univ-bench-dl.owl"));
        	  OWLOntology tmp=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBM_owl-dl/1-ub-dl-univ0"+".owl"));
        	  manager.addAxioms(ontology, tmp.getAxioms());
        	  for (int i=0;i<3;i++) {
        	    tmp=manager.loadOntologyFromOntologyDocument(new File("evaluation/ontologies2/UOBM_owl-dl/1-ub-dl-univ0-dept"+i+".owl"));
                manager.addAxioms(ontology, tmp.getAxioms());
        	  }	
            }
            System.out.println("The loading time of the ontology is "+ (System.currentTimeMillis()-t)+" ms.");
            sparqlEngine=new OWLReasonerSPARQLEngine(new MinimalPrintingMonitor());
            defaultOntologyDataSet=new OWLOntologyDataSet(ontology, null);
    	    OWLOntologyGraph graph=defaultOntologyDataSet.getDefaultGraph();
    	    
    	    System.out.println("Precomputing inferences...");
    	    
    	    InterruptTimer timer=new InterruptTimer(PRECOMPUTE_TIMEOUT,graph.getReasoner());
            try {
                timer.start();
                t=System.currentTimeMillis();
                OWLReasoner reasoner=graph.getReasoner();
                reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY/*, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS*/);
                if (reasoner instanceof OWLBGPHermiT) {
                	((OWLBGPHermiT)graph.getReasoner()).getInstanceStatistics();
                }
                System.out.println("The precomputation time (classification and initialization of known and possible instances) is "+(System.currentTimeMillis()-t)+" ms.");
                t=System.currentTimeMillis()-t;
                timer.stopTiming();
            } catch (OutOfMemoryError e) {
            	 sparqlEngine=null;
                Runtime.getRuntime().gc();
                t=System.currentTimeMillis()-t;
                System.out.println("Precomputation ran out of memory.");
            } catch (ReasonerInterruptedException e) {
                t=System.currentTimeMillis()-t;
                System.out.println("Precomputation ran out of time.");
            } finally {
                timer.stopTiming();
                timer.join();
            }
        } catch (Exception e) {
            System.out.println("Exception while loading ontology " +e.getMessage()+LB);
            e.printStackTrace(System.err);
        }        
    }
    public long[] evaluate(String queryID, String query) {
        try {
            long t=System.currentTimeMillis();
            InterruptTimer timer=new InterruptTimer(QUERY_TIMEOUT, defaultOntologyDataSet.getDefaultGraph().getReasoner());
            try {
                query="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+LB
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+LB
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+LB
                + "PREFIX ub: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> " +LB
                + "PREFIX uob: <http://uob.iodt.ibm.com/univ-bench-dl.owl#> " +LB +query;

                timer.start();
                t=System.currentTimeMillis();
                sparqlEngine.execQuery(query, defaultOntologyDataSet);
                t=System.currentTimeMillis()-t;
                timer.stopTiming();
                long[] timings=new long[2];
                timings[0]=t;
                timings[1]=(Long)ARQ.getContext().get(OWLReasonerSPARQLEngine.BGP_EXEC_TIME);
                return timings;
            } catch (OutOfMemoryError e) {
                sparqlEngine=null;
                Runtime.getRuntime().gc();
                t=System.currentTimeMillis()-t;
                System.out.println("Query answering ran out of memory.");
            } catch (ReasonerInterruptedException e) {
                t=System.currentTimeMillis()-t;
                System.out.println("Query answering ran out of time.");
            } catch (Throwable e) {
                System.out.println("Error for classes in "+shortName+":"+e.getMessage()+LB);
                e.printStackTrace(System.out);
            } finally {
                timer.stopTiming();
                timer.join();
            }
        } catch (Throwable e) {
            System.out.println("Error for classes in "+shortName+":"+e.getMessage()+LB);
            e.printStackTrace(System.out);
        }
        return new long[] { 0l, 0l }; // we shouldn't be here
    }
    public static String millisToHoursMinutesSecondsString(long millis) {
        long time=millis/1000;
        long ms=millis%1000;
        String timeStr=String.format(String.format("%%0%dd", 3), ms)+"ms";
        String format=String.format("%%0%dd", 2);
        long secs=time%60;
        if (secs>0) timeStr=String.format(format, secs)+"s"+timeStr;
        long mins=(time%3600)/60;
        if (mins>0) timeStr=String.format(format, mins)+"m"+timeStr;
        long hours=time/3600;  
        if (hours>0) timeStr=String.format(format, hours)+"h"+timeStr;
        return timeStr;  
    }
    protected static class InterruptTimer extends Thread {
        protected final int m_timeout;
        protected final OWLReasoner m_reasoner;
        protected boolean m_timingStopped;

        public InterruptTimer(int timeout,OWLReasoner owlReasoner) {
            super("Query Engine Interrupt Thread");
            setDaemon(true);
            m_timeout=timeout;
            m_reasoner=owlReasoner;
            m_timingStopped=false;
        }
        public synchronized void run() {
            try {
                if (!m_timingStopped) {
                    wait(m_timeout);
                    if (!m_timingStopped)
                        if (m_reasoner!=null) m_reasoner.interrupt();
                }
            }
            catch (InterruptedException stopped) {
            }
        }
        public synchronized void stopTiming() {
            m_timingStopped=true;
            notifyAll();
        }
    }
}
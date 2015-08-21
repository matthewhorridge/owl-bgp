package com.hp.hpl.jena.sparql.junit;

import java.io.IOException;
import java.util.List;

import org.apache.jena.atlas.logging.Log;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileManager;

public class OWLBGPQueryTest extends QueryTest {

    protected final TestItem testItem;
    
    public OWLBGPQueryTest(String testName, EarlReport earl, FileManager fm, TestItem t) {
        super(testName, earl, fm, t);
        testItem=t;
    }
    protected static Dataset createDataset(List<String> defaultGraphURIs, List<String> namedGraphURIs) {
        try {
            return new OWLOntologyDataSet(defaultGraphURIs, namedGraphURIs);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail("Could not parse the dataset into OWL ontologies.");
            return null;
        }
    }
    
    @Override
    protected void runTestForReal() throws Throwable {
        Query query=null;
        try {
            try { 
                query = queryFromTestItem(testItem) ; 
            } catch (QueryException qEx) {
                query = null ;
                qEx.printStackTrace(System.err) ;
                fail("Parse failure: "+qEx.getMessage()) ;
                throw qEx ;
            }
            Dataset dataset=setUpDataset(query);
            if (dataset==null && !doesQueryHaveDataset(query)) 
                fail("No dataset for query") ;
            QueryExecution qe=QueryExecutionFactory.create(query, dataset);
            try {
                if (query.isSelectType())
                    super.runTestSelect(query, qe);
                else if (query.isConstructType())
                    runTestConstruct(query, qe);
                else if (query.isDescribeType())
                    runTestDescribe(query, qe);
                else if (query.isAskType())
                    runTestAsk(query, qe);
            } finally { qe.close() ; }
        }
        catch (IOException ioEx)
        {
            //log.debug("IOException: ",ioEx) ;
            fail("IOException: "+ioEx.getMessage()) ;
            throw ioEx ;
        }
        catch (NullPointerException ex) { throw ex ; }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err) ;
            fail( "Exception: "+ex.getClass().getName()+": "+ex.getMessage()) ;
        }
    }
    
    protected Dataset setUpDataset(Query query) {
       try {
           if (doesQueryHaveDataset(query) && doesTestItemHaveDataset()) {
               // Only warn if there are results to test
               // Syntax tests may have FROM etc and a manifest data file. 
               if (testItem.getResultFile()!=null)
                   Log.warn(this, testItem.getName()+" : query data source and also in test file") ; 
           }
           // In test file?
           if (doesTestItemHaveDataset())
               // Not specified in the query - get from test item and load
               return createDataset(testItem.getDefaultGraphURIs(), testItem.getNamedGraphURIs()) ;
            // Check 3 - were there any at all?
            if (!doesQueryHaveDataset(query)) 
             fail("No dataset") ;
            // Left to query
            return null ;
        } catch (JenaException jEx) {
            fail("JenaException creating data source: "+jEx.getMessage()) ;
            return null ;
        }
    }
    protected boolean doesTestItemHaveDataset() {
        return  
            (testItem.getDefaultGraphURIs()!=null &&  testItem.getDefaultGraphURIs().size()>0)
            ||
            (testItem.getNamedGraphURIs()!=null &&  testItem.getNamedGraphURIs().size()>0);
    }
    protected boolean doesQueryHaveDataset(Query query) {
        return query.hasDatasetDescription() ;
    }
    public TestItem getTestItem() {
        return testItem;
    }
}

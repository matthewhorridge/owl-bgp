package org.semanticweb.sparql.owlbgp.wgtests.profilechecker;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.junit.EarlReport;
import com.hp.hpl.jena.sparql.junit.OWLBGPQueryTest;
import com.hp.hpl.jena.sparql.junit.TestItem;
import com.hp.hpl.jena.util.FileManager;

public class ProfileCheckerQueryTest extends OWLBGPQueryTest {
    
    public ProfileCheckerQueryTest(String testName, EarlReport earl, FileManager fm, TestItem t) {
        super(testName, earl, fm, t);
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
                FileWriter fstream=new FileWriter("profileCheckerResults.txt", true);
                BufferedWriter out=new BufferedWriter(fstream);
                out.write("****"+testItem.getName()+"****\n");
                out.close();
                if (query.isSelectType())
                    qe.execSelect();
                else if (query.isConstructType())
                    qe.execConstruct();
                else if (query.isDescribeType())
                    qe.execDescribe();
                else if (query.isAskType())
                    qe.execAsk();
            } finally { qe.close() ; }
        } catch (NullPointerException ex) { 
            throw ex ; 
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}

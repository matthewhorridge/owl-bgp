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

package org.semanticweb.sparql;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;
import org.semanticweb.sparql.bgpevaluation.OWLReasonerStageGenerator;
import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.monitor.MonitorAdapter;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.ARQConstants;
import com.hp.hpl.jena.sparql.engine.main.StageBuilder;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;
import com.hp.hpl.jena.sparql.util.Symbol;

public class OWLReasonerSPARQLEngine {
    public static Symbol BGP_EXEC_TIME = ARQConstants.allocSymbol("bgpExecTime");

    public OWLReasonerSPARQLEngine() {
        this(null);
    }

    public OWLReasonerSPARQLEngine(Monitor bgpEvaluationMonitor) {

        StageGenerator orig = (StageGenerator) ARQ.getContext().get(ARQ.stageGenerator);
        if (bgpEvaluationMonitor == null)
            bgpEvaluationMonitor = new MonitorAdapter();
        StageGenerator hermiTStageGenerator = new OWLReasonerStageGenerator(orig, bgpEvaluationMonitor);
        StageBuilder.setGenerator(ARQ.getContext(), hermiTStageGenerator);
        // ARQ's optimizations are not good for entailment, they can split BGPs and declarations might end up
        // in the wrong part and they can replace joins with sequences, which our iterators cannot deal with (yet)
        ARQ.getContext().set(ARQ.optimization, false);
    }

    public ResultSet execQuery(String sparqlQueryString, OWLOntologyDataSet dataSet) {
        Query query = QueryFactory.create(sparqlQueryString, Syntax.syntaxSPARQL_11); // create Jena query object
        return execQuery(query, dataSet);
    }

    public ResultSet execQuery(Query query, OWLOntologyDataSet dataSet) {
        List<String> graphURIs = query.getGraphURIs(); // FROM
        if (graphURIs.size() > 0) {
            try {
                dataSet = new OWLOntologyDataSet(graphURIs, dataSet.getNamedGraphURIsToGraphs());
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
                return null;
            }
        }
        QueryExecution engine = QueryExecutionFactory.create(query, dataSet);
        long t = System.currentTimeMillis();
        ResultSet results = engine.execSelect();
        //int resultsSize=0;

        /*FileWriter fstream=null;

        
        FileWriter fstream=null;
        try {
            fstream = new FileWriter("outputAnswers.txt");
        } catch (IOException e) {
            System.err.println("Error: Cannot create file output.txt");
            e.printStackTrace();
        }
        BufferedWriter out = new BufferedWriter(fstream);*/
        /*while (results.hasNext()) {
            QuerySolution rb=results.nextSolution();
			//System.out.println(rb);
			try {
				out.newLine();
				out.write(rb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//resultsSize++;
          //results.next();
        }
        try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        //System.out.println("The result size is "+resultsSize);

        long evalTime = System.currentTimeMillis() - t;
        ARQ.getContext().put(BGP_EXEC_TIME, evalTime);
        return results;
    }
//	public int getOrderingType() {
//		return orderingMode;
//	}
}
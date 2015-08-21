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

package org.semanticweb.sparql.owlbgp.wgtests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;

import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.sparql.core.DataFormat;
import com.hp.hpl.jena.sparql.junit.OWLBGPQueryTest;
import com.hp.hpl.jena.sparql.junit.QueryTestException;
import com.hp.hpl.jena.sparql.junit.ScriptTestSuiteFactory;
import com.hp.hpl.jena.sparql.junit.TestItem;
import com.hp.hpl.jena.sparql.junit.TestQueryUtils;
import com.hp.hpl.jena.sparql.vocabulary.TestManifest;
import com.hp.hpl.jena.sparql.vocabulary.TestManifestUpdate_11;
import com.hp.hpl.jena.sparql.vocabulary.TestManifestX;
import com.hp.hpl.jena.sparql.vocabulary.TestManifest_11;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.junit.TestUtils;
import com.hp.hpl.jena.vocabulary.RDF;

public class OWLBGPScriptTestSuitFactory extends ScriptTestSuiteFactory {
    
    public static final String SD="http://www.w3.org/ns/sparql-service-description#";
    
    protected FileManager fileManager=FileManager.get();
    protected final Set<String> allowedRegimes;
    protected final Set<String> includeOnlyTests;
    
    public OWLBGPScriptTestSuitFactory() {
        allowedRegimes=new HashSet<String>(Arrays.asList(TestRunner.includeOnlySemantics));
        includeOnlyTests=new HashSet<String>(Arrays.asList(TestRunner.includeOnlyTests));
    }
    
    @Override
    public Test makeTest(Resource manifest,Resource entry,String testName,Resource action,Resource result) {
        boolean includeTest=true;
        if (!includeOnlyTests.isEmpty()) {
            includeTest=false;
            for (String allowedTestName : includeOnlyTests)
                if (testName.startsWith(allowedTestName)) {
                    includeTest=true;
                    break;
                }
        }
        if (includeTest) {
            if (action==null) {
                System.out.println("Null action: "+entry);
                return null;
            } 
            // Defaults
            Syntax querySyntax=TestQueryUtils.getQuerySyntax(manifest);
            if (querySyntax!=null && !querySyntax.equals(Syntax.syntaxARQ) && !querySyntax.equals(Syntax.syntaxSPARQL_10) && !querySyntax.equals(Syntax.syntaxSPARQL_11))
                throw new QueryTestException("Unknown syntax: "+querySyntax);
            // May be null
            Resource defaultTestType=TestUtils.getResource(manifest,TestManifestX.defaultTestType);
            // test name,test type,action -> query specific query[+data],results
            Resource testType=defaultTestType;
            if (entry.hasProperty(RDF.type))
                testType=entry.getProperty(RDF.type).getResource();
            TestItem item=null;
            if (testType==null || (!testType.equals(TestManifestUpdate_11.UpdateEvaluationTest) && !testType.equals(TestManifest_11.UpdateEvaluationTest))) 
                item=TestItem.create(entry,defaultTestType,querySyntax,DataFormat.langXML);
            if (testType!=null && (testType.equals(TestManifest.QueryEvaluationTest) || testType.equals(TestManifestX.TestQuery))) {
                Property sd_entailmentRegime=new PropertyImpl(SD, "entailmentRegime");
                boolean isRelevantTest=false;
                if (action.hasProperty(sd_entailmentRegime)) {
                    Resource regime=TestUtils.getResource(action, sd_entailmentRegime);
                    if (regime.isResource() && !regime.isAnon() && allowedRegimes.contains(regime.toString())) {
                        isRelevantTest=true;
                    } else if (regime.isAnon() && regime.getProperty(RDF.first)!=null) {
                        Resource listItem=regime;
                        while (!listItem.equals(RDF.nil)&&!isRelevantTest) {
                            regime=listItem.getProperty(RDF.first).getResource();
                            if (regime.isResource() && allowedRegimes.contains(regime.toString()))
                                isRelevantTest=true;
                            listItem=listItem.getProperty(RDF.rest).getResource();
                        }
                    }
                    if (isRelevantTest)
                        return new OWLBGPQueryTest(testName,results,fileManager,item);
                }
            }
        }
        return null;
    }

}

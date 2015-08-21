/* 
   Adapted from arq.qtest from jena-arq-2.9.3-tests.jar
   
   Run with "--earl <pathToManifest>" as program arguments for an earl report. 
   The SPARQL WG entailment tests are available from:
   http://www.w3.org/2009/sparql/docs/tests/data-sparql11/entailment/manifest.ttl
      
   Copyright 2010-2012 by the developers of the OWL-BGP project. 

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

import junit.framework.TestSuite;

import org.semanticweb.sparql.bgpevaluation.OWLReasonerStageGenerator;
import org.semanticweb.sparql.bgpevaluation.monitor.MonitorAdapter;

import arq.qtest;
import arq.cmd.TerminationException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.main.StageBuilder;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;
import com.hp.hpl.jena.sparql.expr.E_Function;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.junit.EarlReport;
import com.hp.hpl.jena.sparql.junit.ScriptTestSuiteFactory;
import com.hp.hpl.jena.sparql.junit.SimpleTestRunner;
import com.hp.hpl.jena.sparql.util.NodeFactoryExtra;
import com.hp.hpl.jena.sparql.vocabulary.DOAP;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.sparql.vocabulary.TestManifest;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

public class TestRunner extends qtest {
    public static final String ENT="http://www.w3.org/ns/entailment/";
    public static final String OWLDIRECT_NS=ENT+"OWL-Direct";
    public static final String OWLRDFBASED_NS=ENT+"OWL-RDF-Based";
    public static final String D_NS=ENT+"D";
    public static final String RDF_NS=ENT+"RDF";
    public static final String RDFS_NS=ENT+"RDFS";
    public static final String[] semantics=new String[] { RDF_NS, RDFS_NS, D_NS, OWLRDFBASED_NS, OWLDIRECT_NS };
    
    public static final String PROF="http://www.w3.org/ns/owl-profile/";
    public static final String EL=PROF+"EL";
    public static final String QL=PROF+"QL";
    public static final String RL=PROF+"RL";
    public static final String DL=PROF+"DL";
    public static final String FULL=PROF+"Full";
    public static final String[] profiles=new String[] { EL, QL, RL, DL, FULL };
    
    // include only tests that have a name starting with one of the given string
    // leave empty for all tests
    public static final String[] includeOnlyTests=new String[] {  }; //"bind03"
    // generates just the entailment tests with OWL Direct Semantics
    public static final String[] includeOnlySemantics=new String[] { OWLDIRECT_NS };
    
    public TestRunner(String[] argv) {
        super(argv);
    }
    
    public static void main (String... argv) {
        //ARQ.hideNonDistiguishedVariables
        StageGenerator orig=(StageGenerator)ARQ.getContext().get(ARQ.stageGenerator);
        StageGenerator hermiTStageGenerator=new OWLReasonerStageGenerator(orig, new MonitorAdapter());
//        StageGenerator hermiTStageGenerator=new OWLReasonerStageGenerator(orig, new PrintingMonitor());
        StageBuilder.setGenerator(ARQ.getContext(), hermiTStageGenerator);
        ARQ.getContext().set(ARQ.optimization, false);
        ARQ.getContext().set(ARQ.strictGraph, true);
        ARQ.getContext().set(ARQ.strictSPARQL, true);
        ARQ.init();
        try {
            new TestRunner(argv).mainRun();
        } catch (TerminationException ex) { 
            System.exit(ex.getCode()); 
        }
    }
    @Override
    protected void exec() {
        NodeValue.VerboseWarnings=false;
        E_Function.WarnOnUnknownFunction=false;
        if (createEarlReport)
            oneManifestEarl(testfile) ;
        else
            oneManifest(testfile) ;
    }
    static void oneManifest(String testManifest) {
        TestSuite suite=new OWLBGPScriptTestSuitFactory().process(testManifest);// generates just the entailment tests with OWL Direct Semantics
        junit.textui.TestRunner.run(suite) ;
    }
    static void oneManifestEarl(String testManifest) {
        String name="SPARQLing HermiT";
        String releaseName="SPARQLing HermiT";
        String version="0.1";
        String homepage="http://code.google.com/p/owl-bgp/";
        String systemURI="http://code.google.com/p/owl-bgp/" ;
        EarlReport report=new EarlReport(systemURI, name, version, homepage) ;
        ScriptTestSuiteFactory.results=report ;
        Model model=report.getModel() ;
        model.setNsPrefix("dawg", TestManifest.getURI()) ;
        // Birte runs the report
        Resource who=model.createResource(FOAF.Person)
                .addProperty(FOAF.name, "Birte Glimm")
                .addProperty(FOAF.homepage, 
                             model.createResource("http://www.uni-ulm.de/in/ki/glimm")) ;
        Resource reporter=report.getReporter() ;
        reporter.addProperty(DC.creator, who) ;
        model.setNsPrefix("doap", DOAP.getURI()) ; 
        model.setNsPrefix("xsd", XSD.getURI()) ;
        // DAWG specific stuff.
        Resource system=report.getSystem() ;
        system.addProperty(RDF.type, DOAP.Project) ;
        system.addProperty(DOAP.name, name) ;
        system.addProperty(DOAP.homepage, homepage) ;
        system.addProperty(DOAP.maintainer, who) ;
        
        Resource release=model.createResource(DOAP.Version) ;
        system.addProperty(DOAP.release, release) ;
        
        Node today_node=NodeFactoryExtra.todayAsDate() ;
        Literal today=model.createTypedLiteral(today_node.getLiteralLexicalForm(), today_node.getLiteralDatatype()) ;
        release.addProperty(DOAP.created, today) ;
        release.addProperty(DOAP.name, releaseName) ;      // Again
        
        TestSuite suite=new OWLBGPScriptTestSuitFactory().process(testManifest);
        SimpleTestRunner.runSilent(suite) ;
        
        OWLBGPScriptTestSuitFactory.results.getModel().write(System.out, "TTL") ;
    }
}

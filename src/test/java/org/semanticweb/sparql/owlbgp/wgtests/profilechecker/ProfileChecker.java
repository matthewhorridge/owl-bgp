package org.semanticweb.sparql.owlbgp.wgtests.profilechecker;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestSuite;

import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.sparql.owlbgp.wgtests.TestRunner;

import arq.cmd.TerminationException;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.sparql.engine.main.StageBuilder;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;
import com.hp.hpl.jena.sparql.expr.E_Function;
import com.hp.hpl.jena.sparql.expr.NodeValue;

public class ProfileChecker extends TestRunner {
    public ProfileChecker(String[] argv) {
        super(argv);
    }

    public static Set<OWLProfile> owlProfiles=new HashSet<OWLProfile>();

    static {
        owlProfiles.add(new OWL2DLProfile());
        owlProfiles.add(new OWL2ELProfile());
        owlProfiles.add(new OWL2QLProfile());
        owlProfiles.add(new OWL2RLProfile());
    }
    
    public static void main(String... argv) {
        StageGenerator orig=(StageGenerator)ARQ.getContext().get(ARQ.stageGenerator);
        StageGenerator profileCheckerStageGenerator=new ProfileCheckerStageGenerator(orig);
        StageBuilder.setGenerator(ARQ.getContext(), profileCheckerStageGenerator);
        ARQ.getContext().set(ARQ.optimization, false);
        ARQ.getContext().set(ARQ.strictGraph, true);
        ARQ.getContext().set(ARQ.strictSPARQL, true);
        ARQ.init();
        try {
            new ProfileChecker(argv).mainRun();
        } catch (TerminationException ex) { 
            System.exit(ex.getCode()); 
        }
    }
    @Override
    protected void exec() {
        NodeValue.VerboseWarnings=false;
        E_Function.WarnOnUnknownFunction=false;
        TestSuite suite=new ProfileCheckerScriptTestSuitFactory().process(testfile);
        junit.textui.TestRunner.run(suite) ;
    }

}

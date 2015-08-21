package org.semanticweb.sparql.owlbgp.evaluation;


import junit.framework.TestCase;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;

public class TestGalenEntailment extends TestCase{
public static final String LB = System.getProperty("line.separator") ;
    
    protected final OWLOntologyManager manager;
    protected final OWLDataFactory factory;
    protected OWLOntology queriedOntology;
    protected OWLOntologyDataSet dataset;
    
    public TestGalenEntailment() {
        this(null);
    }
    
    public TestGalenEntailment(String name) {
        super(name);
        manager = OWLManager.createOWLOntologyManager();
        factory=manager.getOWLDataFactory();
    }
	
	protected void setUp() throws Exception {
        org.semanticweb.owlapi.model.IRI physicalIRI=org.semanticweb.owlapi.model.IRI.create(getClass().getResource("/ontologies/GalenExplanation.owl").toURI());
        queriedOntology=manager.loadOntologyFromOntologyDocument(physicalIRI);
	}    
	
	public void testIsEntailed() {
	    String galen="http://www.co-ode.org/ontologies/galen#";
        Configuration c=new Configuration();
        c.ignoreUnsupportedDatatypes=true;
        c.throwInconsistentOntologyException=false;
        ReasonerFactory factory = new ReasonerFactory(); 
        OWLReasoner hermit=factory.createReasoner(queriedOntology, c);
        OWLDataFactory df=manager.getOWLDataFactory();
        OWLClass infection=df.getOWLClass(IRI.create(galen+"Infection"));
        OWLClassExpression notDomainCategory=df.getOWLObjectComplementOf(df.getOWLClass(IRI.create(galen+"DomainCategory")));
        
        OWLObjectProperty topOP=df.getOWLTopObjectProperty();
        OWLClassExpression and=df.getOWLObjectIntersectionOf(infection, df.getOWLObjectAllValuesFrom(topOP, notDomainCategory));
        assertFalse(hermit.isSatisfiable(and));

        OWLObjectProperty attribute=df.getOWLObjectProperty(IRI.create(galen+"Attribute"));
        and=df.getOWLObjectIntersectionOf(infection, df.getOWLObjectAllValuesFrom(attribute, notDomainCategory));
        assertFalse(hermit.isSatisfiable(and));
	}
}

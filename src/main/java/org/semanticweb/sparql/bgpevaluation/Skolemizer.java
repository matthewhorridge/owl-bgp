/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of the OWL-BGP project.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP.  If not, see <http://www.gnu.org/licenses/>.
*/


package org.semanticweb.sparql.bgpevaluation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;

/**
 * This class implements the structural transformation from our new tableau paper. This transformation departs in the following way from the paper: it keeps the concepts of the form \exists R.{ a_1, ..., a_n }, \forall R.{ a_1, ..., a_n }, and \forall R.\neg { a } intact. These concepts are then clausified in a more efficient way.
 */
public class Skolemizer {
    public static final String ANONYMOUS_INDIVIDUAL_SKOLEM_PREFIX = "http://ananymous.org/";
    protected final SkolemizationVisitor m_skolemizationVisitor;
    protected final Set<String> m_skolems = new HashSet<String>();
    protected final Set<Literal> m_literals = new HashSet<Literal>();
    protected final Set<ObjectProperty> m_toldFunctionalObjectProperties = new HashSet<ObjectProperty>();
    protected OWLOntology m_ontology;
    protected OWLDataFactory m_factory;
    protected boolean m_modified = false;

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            System.out.println("Please give a path as argument");
            System.exit(0);
        }
        long t = System.currentTimeMillis();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        String fileName = args[0];
        File inputOntologyFile = new File(fileName);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(inputOntologyFile);
        int lastDot = fileName.lastIndexOf(".");
        String newFileName = fileName.substring(0, lastDot) + "-anonymised" + fileName.substring(lastDot);
        File skolemizedOntologyFile = new File(newFileName);
        skolemizedOntologyFile = skolemizedOntologyFile.getAbsoluteFile();
        Skolemizer skolemizer = new Skolemizer();
        OWLOntology skolemizedOntology = skolemizer.skolemize(ontology);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(skolemizedOntologyFile));
        manager.saveOntology(skolemizedOntology, new OWLFunctionalSyntaxOntologyFormat(), outputStream);
//        Configuration config=new Configuration();
//        //config.ignoreUnsupportedDatatypes=true;
//        config.useDisjunctionLearning=true;
//        config.tableauMonitorType=TableauMonitorType.TIMING;
//        Reasoner reasoner=new Reasoner(config,anonymisedOntology);
//        long t=System.currentTimeMillis();
//        reasoner.classify();
        System.out.println("Done in " + millisToHoursMinutesSecondsString(System.currentTimeMillis() - t));
    }

    public Set<String> getSkolems() {
        return m_skolems;
    }

    public Set<Literal> getLiterals() {
        return m_literals;
    }

    public Set<ObjectProperty> getToldFunctionalObjectProperties() {
        return m_toldFunctionalObjectProperties;
    }

    public static String millisToHoursMinutesSecondsString(long millis) {
        long time = millis / 1000;
        long ms = time % 1000;
        String timeStr = String.format(String.format("%%0%dd", 3), ms) + "ms";
        String format = String.format("%%0%dd", 2);
        long secs = time % 60;
        if (secs > 0) timeStr = String.format(format, secs) + "s" + timeStr;
        long mins = (time % 3600) / 60;
        if (mins > 0) timeStr = String.format(format, mins) + "m" + timeStr;
        long hours = time / 3600;
        if (hours > 0) timeStr = String.format(format, hours) + "h" + timeStr;
        return timeStr;
    }

    public Skolemizer() {
        m_skolemizationVisitor = new SkolemizationVisitor();
    }

    public OWLOntology skolemize(OWLOntology ontology) {
        m_ontology = ontology;
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        m_factory = manager.getOWLDataFactory();
        SkolemizationVisitor skolemizationVisitor = new SkolemizationVisitor();
        Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
        for (OWLOntology o : m_ontology.getImportsClosure())
            for (OWLAxiom axiom : o.getAxioms())
                if (axiom.isLogicalAxiom())
                    newAxioms.add((OWLAxiom) axiom.accept(skolemizationVisitor));
                else
                    newAxioms.add(axiom);
        try {
            return OWLManager.createOWLOntologyManager().createOntology(newAxioms);
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException("Error: Could not create the Skolemized ontology. ");
        }
    }


    protected class SkolemizationVisitor implements OWLObjectVisitorEx<OWLObject> {

        public OWLObject visit(OWLOntology ontology) {
            OWLOntologyManager manager = ontology.getOWLOntologyManager();
            try {
                OWLOntology oNew = manager.createOntology(ontology.getOntologyID());
                for (OWLAxiom ax : ontology.getLogicalAxioms()) {
                    manager.addAxiom(oNew, (OWLAxiom) ax.accept(this));
                }
                return oNew;
            } catch (OWLOntologyCreationException e) {
                System.err.println("Could not create the new ontology.");
                return null;
            }
        }

        // Semantic-less axioms
        public OWLDeclarationAxiom visit(OWLDeclarationAxiom axiom) {
            return axiom;
        }

        public OWLAnnotationAssertionAxiom visit(OWLAnnotationAssertionAxiom axiom) {
            return axiom;
        }

        public OWLSubAnnotationPropertyOfAxiom visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            return axiom;
        }

        public OWLAnnotationPropertyDomainAxiom visit(OWLAnnotationPropertyDomainAxiom axiom) {
            return axiom;
        }

        public OWLAnnotationPropertyRangeAxiom visit(OWLAnnotationPropertyRangeAxiom axiom) {
            return axiom;
        }

        // Class axioms
        public OWLSubClassOfAxiom visit(OWLSubClassOfAxiom axiom) {
            OWLClassExpression sub = (OWLClassExpression) axiom.getSubClass().accept(this);
            OWLClassExpression sup = (OWLClassExpression) axiom.getSuperClass().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLSubClassOfAxiom(sub, sup, axiom.getAnnotations());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            OWLIndividual ind1 = (OWLIndividual) axiom.getSubject().accept(this);
            OWLIndividual ind2 = (OWLIndividual) axiom.getObject().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLNegativeObjectPropertyAssertionAxiom(axiom.getProperty(), ind1, ind2, axiom.getAnnotations());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLReflexiveObjectPropertyAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLDisjointClassesAxiom axiom) {
            Set<OWLClassExpression> classes = new HashSet<OWLClassExpression>();
            for (OWLClassExpression ce : axiom.getClassExpressions()) {
                classes.add((OWLClassExpression) ce.accept(m_skolemizationVisitor));
            }
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLDisjointClassesAxiom(classes, axiom.getAnnotations());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLDataPropertyDomainAxiom axiom) {
            OWLClassExpression ce = (OWLClassExpression) axiom.getDomain().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLDataPropertyDomainAxiom(axiom.getProperty(), ce, axiom.getAnnotations());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLObjectPropertyDomainAxiom axiom) {
            OWLClassExpression ce = (OWLClassExpression) axiom.getDomain().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLObjectPropertyDomainAxiom(axiom.getProperty(), ce, axiom.getAnnotations());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            OWLIndividual ind = (OWLIndividual) axiom.getSubject().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLNegativeDataPropertyAssertionAxiom(axiom.getProperty(), ind, axiom.getObject(), axiom.getAnnotations());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLDifferentIndividualsAxiom axiom) {
            return axiom; // no anonymous inds allowd
        }

        public OWLAxiom visit(OWLDisjointDataPropertiesAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLDisjointObjectPropertiesAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLObjectPropertyRangeAxiom axiom) {
            OWLClassExpression ce = (OWLClassExpression) axiom.getRange().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLObjectPropertyRangeAxiom(axiom.getProperty(), ce);
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLObjectPropertyAssertionAxiom axiom) {
            OWLIndividual ind1 = (OWLIndividual) axiom.getSubject().accept(this);
            OWLIndividual ind2 = (OWLIndividual) axiom.getObject().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLObjectPropertyAssertionAxiom(axiom.getProperty(), ind1, ind2);
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLFunctionalObjectPropertyAxiom axiom) {
            OWLObjectPropertyExpression ope = axiom.getProperty().getSimplified();
            if (!ope.isAnonymous())
                m_toldFunctionalObjectProperties.add(ObjectProperty.create(ope.getNamedProperty().getIRI().toString()));
            return axiom;
        }

        public OWLAxiom visit(OWLSubObjectPropertyOfAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLDisjointUnionAxiom axiom) {
            Set<OWLClassExpression> classes = new HashSet<OWLClassExpression>();
            for (OWLClassExpression ce : axiom.getClassExpressions()) {
                classes.add((OWLClassExpression) ce.accept(this));
            }
            OWLClass c = (OWLClass) axiom.getOWLClass().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLDisjointUnionAxiom(c, classes);
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLSymmetricObjectPropertyAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLDataPropertyRangeAxiom axiom) {
            axiom.getRange().accept(this);
            return axiom;
        }

        public OWLAxiom visit(OWLFunctionalDataPropertyAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLEquivalentDataPropertiesAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLClassAssertionAxiom axiom) {
            OWLClassExpression ce = (OWLClassExpression) axiom.getClassExpression().accept(this);
            OWLIndividual ind = (OWLIndividual) axiom.getIndividual().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLClassAssertionAxiom(ce, ind);
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
            Set<OWLClassExpression> classes = new HashSet<OWLClassExpression>();
            for (OWLClassExpression ce : axiom.getClassExpressions()) {
                classes.add((OWLClassExpression) ce.accept(this));
            }
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLEquivalentClassesAxiom(classes);
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLDataPropertyAssertionAxiom axiom) {
            OWLIndividual ind = (OWLIndividual) axiom.getSubject().accept(this);
            axiom.getObject().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLDataPropertyAssertionAxiom(axiom.getProperty(), ind, axiom.getObject());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLTransitiveObjectPropertyAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLSubDataPropertyOfAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLSameIndividualAxiom axiom) {
            return axiom; // no anonymous inds allowed
        }

        public OWLAxiom visit(OWLSubPropertyChainOfAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLInverseObjectPropertiesAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(OWLHasKeyAxiom axiom) {
            OWLClassExpression ce = (OWLClassExpression) axiom.getClassExpression().accept(this);
            if (m_modified) {
                m_modified = false;
                return m_factory.getOWLHasKeyAxiom(ce, axiom.getPropertyExpressions(), axiom.getAnnotations());
            }
            else
                return axiom;
        }

        public OWLAxiom visit(OWLDatatypeDefinitionAxiom axiom) {
            return axiom;
        }

        public OWLAxiom visit(SWRLRule rule) {
            return rule;
        }

        // Class expressions

        public OWLClassExpression visit(OWLClass ce) {
            return ce;
        }

        public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
            Set<OWLClassExpression> conjuncts = new HashSet<OWLClassExpression>();
            for (OWLClassExpression ex : ce.getOperands())
                conjuncts.add((OWLClassExpression) ex.accept(this));
            if (m_modified)
                return m_factory.getOWLObjectIntersectionOf(conjuncts);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectUnionOf ce) {
            Set<OWLClassExpression> disjunts = new HashSet<OWLClassExpression>();
            for (OWLClassExpression ex : ce.getOperands())
                disjunts.add((OWLClassExpression) ex.accept(this));
            if (m_modified)
                return m_factory.getOWLObjectUnionOf(disjunts);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectComplementOf ce) {
            OWLClassExpression cenew = (OWLClassExpression) ce.getOperand().accept(this);
            if (m_modified)
                return m_factory.getOWLObjectComplementOf(cenew);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectSomeValuesFrom ce) {
            OWLClassExpression filler = (OWLClassExpression) ce.getFiller().accept(this);
            if (m_modified)
                return m_factory.getOWLObjectSomeValuesFrom(ce.getProperty(), filler);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectAllValuesFrom ce) {
            OWLClassExpression filler = (OWLClassExpression) ce.getFiller().accept(this);
            if (m_modified)
                return m_factory.getOWLObjectAllValuesFrom(ce.getProperty(), filler);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectHasValue ce) {
            OWLIndividual ind = (OWLIndividual) ce.getValue().accept(this);
            if (m_modified)
                return m_factory.getOWLObjectHasValue(ce.getProperty(), ind);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectMinCardinality ce) {
            OWLClassExpression filler = (OWLClassExpression) ce.getFiller().accept(this);
            if (m_modified)
                return m_factory.getOWLObjectMinCardinality(ce.getCardinality(), ce.getProperty(), filler);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectExactCardinality ce) {
            OWLClassExpression filler = (OWLClassExpression) ce.getFiller().accept(this);
            if (m_modified)
                return m_factory.getOWLObjectExactCardinality(ce.getCardinality(), ce.getProperty(), filler);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectMaxCardinality ce) {
            OWLClassExpression filler = (OWLClassExpression) ce.getFiller().accept(this);
            if (m_modified)
                return m_factory.getOWLObjectMaxCardinality(ce.getCardinality(), ce.getProperty(), filler);
            else
                return ce;
        }

        public OWLClassExpression visit(OWLObjectHasSelf ce) {
            return ce;
        }

        public OWLClassExpression visit(OWLObjectOneOf ce) {
            return ce; // no anonymous ind allowed
        }

        public OWLClassExpression visit(OWLDataSomeValuesFrom ce) {
            return ce;
        }

        public OWLClassExpression visit(OWLDataAllValuesFrom ce) {
            return ce;
        }

        public OWLClassExpression visit(OWLDataHasValue ce) {
            return ce;
        }

        public OWLClassExpression visit(OWLDataMinCardinality ce) {
            return ce;
        }

        public OWLClassExpression visit(OWLDataExactCardinality ce) {
            return ce;
        }

        public OWLClassExpression visit(OWLDataMaxCardinality ce) {
            return ce;
        }

        // object properties

        public OWLObjectPropertyExpression visit(OWLObjectProperty property) {
            return property;
        }

        public OWLObjectPropertyExpression visit(OWLObjectInverseOf property) {
            return property;
        }

        // data properties

        public OWLDataProperty visit(OWLDataProperty property) {
            return property;
        }

        // individuals 

        public OWLIndividual visit(OWLNamedIndividual individual) {
            return individual;
        }

        public OWLIndividual visit(OWLAnonymousIndividual individual) {
            m_modified = true;
//            String newIRI=ANONYMOUS_INDIVIDUAL_SKOLEM_PREFIX+individual.toStringID().substring(2);
//            OWLNamedIndividual skolem=m_factory.getOWLNamedIndividual(IRI.create(newIRI));
            //TODO: check
            OWLNamedIndividual skolem = m_factory.getOWLNamedIndividual(IRI.create(individual.toStringID()));
            m_skolems.add(individual.toStringID());
            return skolem;
        }

        // data ranges

        public OWLObject visit(OWLDatatype node) {
            return node;
        }

        public OWLObject visit(OWLDataComplementOf node) {
            node.getDataRange().accept(this);
            return node;
        }

        public OWLObject visit(OWLDataOneOf node) {
            for (OWLLiteral lit : node.getValues())
                lit.accept(this);
            return node;
        }

        public OWLObject visit(OWLDataIntersectionOf node) {
            for (OWLDataRange dr : node.getOperands())
                dr.accept(this);
            return node;
        }

        public OWLObject visit(OWLDataUnionOf node) {
            for (OWLDataRange dr : node.getOperands())
                dr.accept(this);
            return node;
        }

        public OWLObject visit(OWLDatatypeRestriction node) {
            return node;
        }

        public OWLObject visit(OWLLiteral node) {
            m_literals.add(TypedLiteral.create(node.getLiteral(), node.getLang(), Datatype.create(node.getDatatype().getIRI().toString())));
            return node;
        }

        public OWLObject visit(OWLFacetRestriction node) {
            return node;
        }

        public OWLObject visit(OWLAnnotationProperty property) {
            return property;
        }

        public OWLObject visit(OWLAnnotation node) {
            return node;
        }

        public OWLObject visit(IRI iri) {
            return iri;
        }

        public OWLObject visit(SWRLClassAtom node) {
            return node;
        }

        public OWLObject visit(SWRLDataRangeAtom node) {
            return node;
        }

        public OWLObject visit(SWRLObjectPropertyAtom node) {
            return node;
        }

        public OWLObject visit(SWRLDataPropertyAtom node) {
            return node;
        }

        public OWLObject visit(SWRLBuiltInAtom node) {
            return node;
        }

        public OWLObject visit(SWRLVariable node) {
            return node;
        }

        public OWLObject visit(SWRLIndividualArgument node) {
            return node;
        }

        public OWLObject visit(SWRLLiteralArgument node) {
            return node;
        }

        public OWLObject visit(SWRLSameIndividualAtom node) {
            return node;
        }

        public OWLObject visit(SWRLDifferentIndividualsAtom node) {
            return node;
        }
    }
}

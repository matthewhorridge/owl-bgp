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

package org.semanticweb.sparql.owlbgp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.AsymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.DatatypeDefinition;
import org.semanticweb.sparql.owlbgp.model.axioms.Declaration;
import org.semanticweb.sparql.owlbgp.model.axioms.DifferentIndividuals;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointUnion;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalDataProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.HasKey;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseFunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.IrreflexiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeDataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.ReflexiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.axioms.SubAnnotationPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubDataPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubObjectPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.TransitiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectComplementOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasSelf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectOneOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataComplementOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataOneOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class ToOWLAPIConverter implements ExtendedOWLObjectVisitorEx<OWLObject> {
    protected final OWLDataFactory m_dataFactory;

    public ToOWLAPIConverter(OWLDataFactory dataFactory) {
        m_dataFactory = dataFactory;
    }

    public OWLObject visit(org.semanticweb.sparql.owlbgp.model.IRI iri) {
        return IRI.create(iri.getIRIString());
    }

    public OWLObject visit(Clazz aClass) {
        return m_dataFactory.getOWLClass((IRI) aClass.getIRI().accept(this));
    }

    public OWLObject visit(ClassVariable classVariable) {
        throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + classVariable.toString() + " is unbound. ");
    }

    public OWLObject visit(ObjectIntersectionOf objectIntersectionOf) {
        Set<OWLClassExpression> classExpressions = new HashSet<OWLClassExpression>();
        for (ClassExpression classExpression : objectIntersectionOf.getClassExpressions())
            classExpressions.add((OWLClassExpression) classExpression.accept(this));
        return m_dataFactory.getOWLObjectIntersectionOf(classExpressions);
    }

    public OWLObject visit(ObjectUnionOf objectUnionOf) {
        Set<OWLClassExpression> classExpressions = new HashSet<OWLClassExpression>();
        for (ClassExpression classExpression : objectUnionOf.getClassExpressions())
            classExpressions.add((OWLClassExpression) classExpression.accept(this));
        return m_dataFactory.getOWLObjectUnionOf(classExpressions);
    }

    public OWLObject visit(ObjectComplementOf objectComplementOf) {
        return m_dataFactory.getOWLObjectComplementOf((OWLClassExpression) objectComplementOf.getComplementedClassExpression().accept(this));
    }

    public OWLObject visit(ObjectAllValuesFrom allValuesFrom) {
        return m_dataFactory.getOWLObjectAllValuesFrom((OWLObjectPropertyExpression) allValuesFrom.getObjectPropertyExpression().accept(this), (OWLClassExpression) allValuesFrom.getClassExpression().accept(this));
    }

    public OWLObject visit(ObjectSomeValuesFrom objectSomeValuesFrom) {
        return m_dataFactory.getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) objectSomeValuesFrom.getObjectPropertyExpression().accept(this), (OWLClassExpression) objectSomeValuesFrom.getClassExpression().accept(this));
    }

    public OWLObject visit(ObjectHasValue objectHasValue) {
        return m_dataFactory.getOWLObjectHasValue((OWLObjectPropertyExpression) objectHasValue.getObjectPropertyExpression().accept(this), (OWLNamedIndividual) objectHasValue.getIndividual().accept(this));
    }

    public OWLObject visit(ObjectMinCardinality objectMinCardinality) {
        ClassExpression ce = objectMinCardinality.getClassExpression();
        if (ce != null)
            return m_dataFactory.getOWLObjectMinCardinality(objectMinCardinality.getCardinality(), (OWLObjectPropertyExpression) objectMinCardinality.getObjectPropertyExpression().accept(this), (OWLClassExpression) objectMinCardinality.getClassExpression().accept(this));
        else
            return m_dataFactory.getOWLObjectMinCardinality(objectMinCardinality.getCardinality(), (OWLObjectPropertyExpression) objectMinCardinality.getObjectPropertyExpression().accept(this));
    }

    public OWLObject visit(ObjectExactCardinality objectExactCardinality) {
        ClassExpression ce = objectExactCardinality.getClassExpression();
        if (ce != null)
            return m_dataFactory.getOWLObjectExactCardinality(objectExactCardinality.getCardinality(), (OWLObjectPropertyExpression) objectExactCardinality.getObjectPropertyExpression().accept(this), (OWLClassExpression) objectExactCardinality.getClassExpression().accept(this));
        else
            return m_dataFactory.getOWLObjectExactCardinality(objectExactCardinality.getCardinality(), (OWLObjectPropertyExpression) objectExactCardinality.getObjectPropertyExpression().accept(this));
    }

    public OWLObject visit(ObjectMaxCardinality objectMaxCardinality) {
        ClassExpression ce = objectMaxCardinality.getClassExpression();
        if (ce != null)
            return m_dataFactory.getOWLObjectMaxCardinality(objectMaxCardinality.getCardinality(), (OWLObjectPropertyExpression) objectMaxCardinality.getObjectPropertyExpression().accept(this), (OWLClassExpression) objectMaxCardinality.getClassExpression().accept(this));
        else
            return m_dataFactory.getOWLObjectMaxCardinality(objectMaxCardinality.getCardinality(), (OWLObjectPropertyExpression) objectMaxCardinality.getObjectPropertyExpression().accept(this));
    }

    public OWLObject visit(ObjectHasSelf objectHasSelf) {
        return m_dataFactory.getOWLObjectHasSelf((OWLObjectPropertyExpression) objectHasSelf.getObjectPropertyExpression().accept(this));
    }

    public OWLObject visit(ObjectOneOf objectOneOf) {
        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
        for (Individual individual : objectOneOf.getIndividuals())
            individuals.add((OWLIndividual) individual.accept(this));
        return m_dataFactory.getOWLObjectOneOf(individuals);
    }

    public OWLObject visit(DataSomeValuesFrom dataSomeValuesFrom) {
        return m_dataFactory.getOWLDataSomeValuesFrom((OWLDataPropertyExpression) dataSomeValuesFrom.getDataPropertyExpression().accept(this), (OWLDataRange) dataSomeValuesFrom.getDataRange().accept(this));
    }

    public OWLObject visit(DataAllValuesFrom dataAllValuesFrom) {
        return m_dataFactory.getOWLDataAllValuesFrom((OWLDataPropertyExpression) dataAllValuesFrom.getDataPropertyExpression().accept(this), (OWLDataRange) dataAllValuesFrom.getDataRange().accept(this));
    }

    public OWLObject visit(DataHasValue dataHasValue) {
        return m_dataFactory.getOWLDataHasValue((OWLDataPropertyExpression) dataHasValue.getDataPropertyExpression().accept(this), (OWLLiteral) dataHasValue.getLiteral().accept(this));
    }

    public OWLObject visit(DataMinCardinality dataMinCardinality) {
        DataRange dr = dataMinCardinality.getDataRange();
        if (dr != null)
            return m_dataFactory.getOWLDataMinCardinality(dataMinCardinality.getCardinality(), (OWLDataPropertyExpression) dataMinCardinality.getDataPropertyExpression().accept(this), (OWLDataRange) dataMinCardinality.getDataRange().accept(this));
        else
            return m_dataFactory.getOWLDataMinCardinality(dataMinCardinality.getCardinality(), (OWLDataPropertyExpression) dataMinCardinality.getDataPropertyExpression().accept(this));
    }

    public OWLObject visit(DataExactCardinality dataExactCardinality) {
        DataRange dr = dataExactCardinality.getDataRange();
        if (dr != null)
            return m_dataFactory.getOWLDataMaxCardinality(dataExactCardinality.getCardinality(), (OWLDataPropertyExpression) dataExactCardinality.getDataPropertyExpression().accept(this), (OWLDataRange) dataExactCardinality.getDataRange().accept(this));
        else
            return m_dataFactory.getOWLDataMaxCardinality(dataExactCardinality.getCardinality(), (OWLDataPropertyExpression) dataExactCardinality.getDataPropertyExpression().accept(this));
    }

    public OWLObject visit(DataMaxCardinality dataMaxCardinality) {
        DataRange dr = dataMaxCardinality.getDataRange();
        if (dr != null)
            return m_dataFactory.getOWLDataMaxCardinality(dataMaxCardinality.getCardinality(), (OWLDataPropertyExpression) dataMaxCardinality.getDataPropertyExpression().accept(this), (OWLDataRange) dataMaxCardinality.getDataRange().accept(this));
        else
            return m_dataFactory.getOWLDataMaxCardinality(dataMaxCardinality.getCardinality(), (OWLDataPropertyExpression) dataMaxCardinality.getDataPropertyExpression().accept(this));
    }

    public OWLObject visit(ObjectProperty objectProperty) {
        return m_dataFactory.getOWLObjectProperty((IRI) objectProperty.getIRI().accept(this));
    }

    public OWLObject visit(ObjectInverseOf inverseObjectProperty) {
        return m_dataFactory.getOWLObjectInverseOf((OWLObjectPropertyExpression) inverseObjectProperty.getInvertedObjectProperty().accept(this));
    }

    public OWLObject visit(ObjectPropertyVariable objectPropertyVariable) {
        throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + objectPropertyVariable.toString() + " is unbound. ");
    }

    public OWLObject visit(ObjectPropertyChain objectPropertyChain) {
        throw new RuntimeException("Error: Cannot convert property chains to OWL API objects since chains are just lists in the OWL API. ");
    }

    public OWLObject visit(DataProperty dataProperty) {
        return m_dataFactory.getOWLDataProperty((IRI) dataProperty.getIRI().accept(this));
    }

    public OWLObject visit(DataPropertyVariable dataPropertyVariable) {
        throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + dataPropertyVariable.toString() + " is unbound. ");
    }

    public OWLObject visit(AnnotationProperty annotationProperty) {
        return m_dataFactory.getOWLAnnotationProperty((IRI) annotationProperty.getIRI().accept(this));
    }

    public OWLObject visit(AnnotationPropertyVariable annotationPropertyVariable) {
        throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + annotationPropertyVariable.toString() + " is unbound. ");
    }

    public OWLObject visit(TypedLiteral literal) {
        if (literal.getDatatype() == Datatype.RDF_PLAIN_LITERAL)
            return m_dataFactory.getOWLLiteral(literal.getLexicalForm(), literal.getLangTag());
        else
            return m_dataFactory.getOWLLiteral(literal.getLexicalForm(), (OWLDatatype) literal.getDatatype().accept(this));
    }

    public OWLObject visit(LiteralVariable literalVariable) {
        throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + literalVariable.toString() + " is unbound. ");
    }

    public OWLObject visit(NamedIndividual namedIndividual) {
        return m_dataFactory.getOWLNamedIndividual((IRI) namedIndividual.getIRI().accept(this));
    }

    public OWLObject visit(AnonymousIndividual anonymousIndividual) {
        return m_dataFactory.getOWLAnonymousIndividual(anonymousIndividual.getNodeID());
    }

    public OWLObject visit(IndividualVariable individualVariable) {
        throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + individualVariable.toString() + " is unbound. ");
    }

    public OWLObject visit(Datatype datatype) {
        return m_dataFactory.getOWLDatatype((IRI) datatype.getIRI().accept(this));
    }

    public OWLObject visit(DatatypeVariable datatypeVariable) {
        throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + datatypeVariable.toString() + " is unbound. ");
    }

    public OWLObject visit(DatatypeRestriction datatypeRestriction) {
        Set<OWLFacetRestriction> facetRestrictions = new HashSet<OWLFacetRestriction>();
        for (FacetRestriction facetRestriction : datatypeRestriction.getFacetRestrictions()) {
            facetRestrictions.add((OWLFacetRestriction) facetRestriction.getLiteral().accept(this));
        }
        return m_dataFactory.getOWLDatatypeRestriction((OWLDatatype) datatypeRestriction.getDatatype().accept(this), facetRestrictions);
    }

    public OWLObject visit(FacetRestriction facetRestriction) {
        return m_dataFactory.getOWLFacetRestriction(OWLFacet.getFacet((IRI) facetRestriction.getFacet().getIRI().accept(this)), (OWLLiteral) facetRestriction.getLiteral().accept(this));
    }

    public OWLObject visit(DataComplementOf dataComplementOf) {
        return m_dataFactory.getOWLDataComplementOf((OWLDataRange) dataComplementOf.getDataRange().accept(this));
    }

    public OWLObject visit(DataIntersectionOf dataIntersectionOf) {
        Set<OWLDataRange> dataRanges = new HashSet<OWLDataRange>();
        for (DataRange dataRange : dataIntersectionOf.getDataRanges())
            dataRanges.add((OWLDataRange) dataRange.accept(this));
        return m_dataFactory.getOWLDataIntersectionOf(dataRanges);
    }

    public OWLObject visit(DataUnionOf dataUnionOf) {
        Set<OWLDataRange> dataRanges = new HashSet<OWLDataRange>();
        for (DataRange dataRange : dataUnionOf.getDataRanges())
            dataRanges.add((OWLDataRange) dataRange.accept(this));
        return m_dataFactory.getOWLDataUnionOf(dataRanges);
    }

    public OWLObject visit(DataOneOf dataOneOf) {
        Set<OWLLiteral> literals = new HashSet<OWLLiteral>();
        for (Literal literal : dataOneOf.getLiterals())
            literals.add((OWLLiteral) literal.accept(this));
        return m_dataFactory.getOWLDataOneOf(literals);
    }

    public OWLObject visit(Import imp) {
        return null;
    }

    public OWLObject visit(Annotation annotation) {
        return m_dataFactory.getOWLAnnotation((OWLAnnotationProperty) annotation.m_annotationProperty.accept(this), (OWLAnnotationValue) annotation.m_annotationValue.accept(this));
    }

    public OWLObject visit(AnnotationValue annotationValue) {
        if (annotationValue instanceof Variable)
            throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + annotationValue.toString() + " is unbound. ");
        else
            return annotationValue.accept(this);
    }

    public OWLObject visit(AnnotationSubject annotationSubject) {
        if (annotationSubject instanceof Variable)
            throw new RuntimeException("Error: Can only convert to OWL API objects if all variables are bound, but variable " + annotationSubject + " is unbound. ");
        else
            return annotationSubject.accept(this);
    }

    public OWLObject visit(AnnotationAssertion assertion) {
        return m_dataFactory.getOWLAnnotationAssertionAxiom((OWLAnnotationProperty) assertion.getAnnotationProperty().accept(this), (OWLAnnotationSubject) assertion.getAnnotationSubject().accept(this), (OWLAnnotationValue) assertion.getAnnotationValue().accept(this), getAnnotations(assertion));
    }

    public OWLObject visit(SubAnnotationPropertyOf axiom) {
        return m_dataFactory.getOWLSubAnnotationPropertyOfAxiom((OWLAnnotationProperty) axiom.getSubAnnotationPropertyExpression().accept(this), (OWLAnnotationProperty) axiom.getSuperAnnotationPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(AnnotationPropertyDomain axiom) {
        return m_dataFactory.getOWLAnnotationPropertyDomainAxiom((OWLAnnotationProperty) axiom.getAnnotationPropertyExpression().accept(this), (IRI) axiom.getDomain().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(AnnotationPropertyRange axiom) {
        return m_dataFactory.getOWLAnnotationPropertyRangeAxiom((OWLAnnotationProperty) axiom.getAnnotationPropertyExpression().accept(this), (IRI) axiom.getRange().accept(this), getAnnotations(axiom));
    }

    protected Set<OWLAnnotation> getAnnotations(Axiom axiom) {
        Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
        for (Annotation annotation : axiom.getAnnotations())
            annotations.add((OWLAnnotation) annotation.accept(this));
        return annotations;
    }

    public OWLObject visit(Declaration axiom) {
        return m_dataFactory.getOWLDeclarationAxiom((OWLEntity) axiom.getDeclaredObject().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(SubClassOf axiom) {
        return m_dataFactory.getOWLSubClassOfAxiom((OWLClassExpression) axiom.getSubClassExpression().accept(this), (OWLClassExpression) axiom.getSuperClassExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(EquivalentClasses axiom) {
        Set<OWLClassExpression> classExpressions = new HashSet<OWLClassExpression>();
        for (ClassExpression classExpression : axiom.getClassExpressions())
            classExpressions.add((OWLClassExpression) classExpression.accept(this));
        return m_dataFactory.getOWLEquivalentClassesAxiom(classExpressions, getAnnotations(axiom));
    }

    public OWLObject visit(DisjointClasses axiom) {
        Set<OWLClassExpression> classExpressions = new HashSet<OWLClassExpression>();
        for (ClassExpression classExpression : axiom.getClassExpressions())
            classExpressions.add((OWLClassExpression) classExpression.accept(this));
        return m_dataFactory.getOWLDisjointClassesAxiom(classExpressions, getAnnotations(axiom));
    }

    public OWLObject visit(DisjointUnion axiom) {
        Set<OWLClassExpression> classExpressions = new HashSet<OWLClassExpression>();
        for (ClassExpression classExpression : axiom.getClassExpressions())
            classExpressions.add((OWLClassExpression) classExpression.accept(this));
        return m_dataFactory.getOWLDisjointUnionAxiom((OWLClass) axiom.getClazz().accept(this), classExpressions, getAnnotations(axiom));
    }

    public OWLObject visit(SubObjectPropertyOf axiom) {
        if (axiom.getSubObjectPropertyExpression() instanceof ObjectPropertyChain) {
            List<OWLObjectPropertyExpression> opes = new ArrayList<OWLObjectPropertyExpression>();
            for (ObjectPropertyExpression op : ((ObjectPropertyChain) axiom.getSubObjectPropertyExpression()).getObjectPropertyExpressions()) {
                opes.add((OWLObjectPropertyExpression) op.accept(this));
            }
            return m_dataFactory.getOWLSubPropertyChainOfAxiom(opes, (OWLObjectPropertyExpression) axiom.getSuperObjectPropertyExpression().accept(this), getAnnotations(axiom));
        }
        else {
            return m_dataFactory.getOWLSubObjectPropertyOfAxiom((OWLObjectPropertyExpression) axiom.getSubObjectPropertyExpression().accept(this), (OWLObjectPropertyExpression) axiom.getSuperObjectPropertyExpression().accept(this), getAnnotations(axiom));
        }
    }

    public OWLObject visit(EquivalentObjectProperties axiom) {
        Set<OWLObjectPropertyExpression> opes = new HashSet<OWLObjectPropertyExpression>();
        for (ObjectPropertyExpression ope : axiom.getObjectPropertyExpressions())
            opes.add((OWLObjectPropertyExpression) ope.accept(this));
        return m_dataFactory.getOWLEquivalentObjectPropertiesAxiom(opes, getAnnotations(axiom));
    }

    public OWLObject visit(DisjointObjectProperties axiom) {
        Set<OWLObjectPropertyExpression> opes = new HashSet<OWLObjectPropertyExpression>();
        for (ObjectPropertyExpression ope : axiom.getObjectPropertyExpressions())
            opes.add((OWLObjectPropertyExpression) ope.accept(this));
        return m_dataFactory.getOWLDisjointObjectPropertiesAxiom(opes, getAnnotations(axiom));
    }

    public OWLObject visit(InverseObjectProperties axiom) {
        Set<ObjectPropertyExpression> opes = axiom.getObjectPropertyExpressions();
        assert opes.size() == 2;
        Iterator<ObjectPropertyExpression> it = opes.iterator();
        ObjectPropertyExpression ope1 = it.next();
        ObjectPropertyExpression ope2 = it.next();
        return m_dataFactory.getOWLInverseObjectPropertiesAxiom((OWLObjectPropertyExpression) ope1.accept(this), (OWLObjectPropertyExpression) ope2.accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(ObjectPropertyDomain axiom) {
        return m_dataFactory.getOWLObjectPropertyDomainAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), (OWLClassExpression) axiom.getDomain().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(ObjectPropertyRange axiom) {
        return m_dataFactory.getOWLObjectPropertyDomainAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), (OWLClassExpression) axiom.getRange().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(FunctionalObjectProperty axiom) {
        return m_dataFactory.getOWLFunctionalObjectPropertyAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(InverseFunctionalObjectProperty axiom) {
        return m_dataFactory.getOWLInverseFunctionalObjectPropertyAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(ReflexiveObjectProperty axiom) {
        return m_dataFactory.getOWLReflexiveObjectPropertyAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(IrreflexiveObjectProperty axiom) {
        return m_dataFactory.getOWLIrreflexiveObjectPropertyAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(SymmetricObjectProperty axiom) {
        return m_dataFactory.getOWLSymmetricObjectPropertyAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(AsymmetricObjectProperty axiom) {
        return m_dataFactory.getOWLAsymmetricObjectPropertyAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(TransitiveObjectProperty axiom) {
        return m_dataFactory.getOWLTransitiveObjectPropertyAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(SubDataPropertyOf axiom) {
        return m_dataFactory.getOWLSubDataPropertyOfAxiom((OWLDataPropertyExpression) axiom.getSubDataPropertyExpression().accept(this), (OWLDataPropertyExpression) axiom.getSuperDataPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(EquivalentDataProperties axiom) {
        Set<OWLDataPropertyExpression> dpes = new HashSet<OWLDataPropertyExpression>();
        for (DataPropertyExpression dpe : axiom.getDataPropertyExpressions())
            dpes.add((OWLDataPropertyExpression) dpe.accept(this));
        return m_dataFactory.getOWLEquivalentDataPropertiesAxiom(dpes, getAnnotations(axiom));
    }

    public OWLObject visit(DisjointDataProperties axiom) {
        Set<OWLDataPropertyExpression> dpes = new HashSet<OWLDataPropertyExpression>();
        for (DataPropertyExpression dpe : axiom.getDataPropertyExpressions())
            dpes.add((OWLDataPropertyExpression) dpe.accept(this));
        return m_dataFactory.getOWLDisjointDataPropertiesAxiom(dpes, getAnnotations(axiom));
    }

    public OWLObject visit(DataPropertyDomain axiom) {
        return m_dataFactory.getOWLDataPropertyDomainAxiom((OWLDataPropertyExpression) axiom.getDataPropertyExpression().accept(this), (OWLClassExpression) axiom.getDomain().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(DataPropertyRange axiom) {
        return m_dataFactory.getOWLDataPropertyRangeAxiom((OWLDataPropertyExpression) axiom.getDataPropertyExpression().accept(this), (OWLDataRange) axiom.getRange().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(FunctionalDataProperty axiom) {
        return m_dataFactory.getOWLFunctionalDataPropertyAxiom((OWLDataPropertyExpression) axiom.getDataPropertyExpression().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(DatatypeDefinition axiom) {
        return m_dataFactory.getOWLDatatypeDefinitionAxiom((OWLDatatype) axiom.getDatatype().accept(this), (OWLDataRange) axiom.getDataRange().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(HasKey axiom) {
        Set<OWLPropertyExpression<?, ?>> pes = new HashSet<OWLPropertyExpression<?, ?>>();
        for (ObjectPropertyExpression ope : axiom.getObjectPropertyExpressions()) {
            pes.add((OWLObjectPropertyExpression) ope.accept(this));
        }
        for (DataPropertyExpression dpe : axiom.getDataPropertyExpressions()) {
            pes.add((OWLDataPropertyExpression) dpe.accept(this));
        }
        return m_dataFactory.getOWLHasKeyAxiom((OWLClassExpression) axiom.getClassExpression().accept(this), pes, getAnnotations(axiom));
    }

    public OWLObject visit(SameIndividual axiom) {
        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
        for (Individual individual : axiom.getIndividuals())
            individuals.add((OWLIndividual) individual.accept(this));
        return m_dataFactory.getOWLSameIndividualAxiom(individuals, getAnnotations(axiom));
    }

    public OWLObject visit(DifferentIndividuals axiom) {
        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
        for (Individual individual : axiom.getIndividuals())
            individuals.add((OWLIndividual) individual.accept(this));
        return m_dataFactory.getOWLDifferentIndividualsAxiom(individuals, getAnnotations(axiom));
    }

    public OWLObject visit(ClassAssertion axiom) {
        return m_dataFactory.getOWLClassAssertionAxiom((OWLClassExpression) axiom.getClassExpression().accept(this), (OWLIndividual) axiom.getIndividual().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(ObjectPropertyAssertion axiom) {
        return m_dataFactory.getOWLObjectPropertyAssertionAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), (OWLIndividual) axiom.getIndividual1().accept(this), (OWLIndividual) axiom.getIndividual2().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(NegativeObjectPropertyAssertion axiom) {
        return m_dataFactory.getOWLNegativeObjectPropertyAssertionAxiom((OWLObjectPropertyExpression) axiom.getObjectPropertyExpression().accept(this), (OWLIndividual) axiom.getIndividual1().accept(this), (OWLIndividual) axiom.getIndividual2().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(DataPropertyAssertion axiom) {
        return m_dataFactory.getOWLDataPropertyAssertionAxiom((OWLDataPropertyExpression) axiom.getDataPropertyExpression().accept(this), (OWLIndividual) axiom.getIndividual().accept(this), (OWLLiteral) axiom.getLiteral().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(NegativeDataPropertyAssertion axiom) {
        return m_dataFactory.getOWLNegativeDataPropertyAssertionAxiom((OWLDataPropertyExpression) axiom.getDataPropertyExpression().accept(this), (OWLIndividual) axiom.getIndividual().accept(this), (OWLLiteral) axiom.getLiteral().accept(this), getAnnotations(axiom));
    }

    public OWLObject visit(Ontology ontology) {
        Identifier iri = ontology.getOntologyIRI();
        IRI ontologyIRI = (iri != null ? (IRI) iri.accept(this) : null);
        iri = ontology.getVersionIRI();
        IRI versionIRI = (iri != null ? (IRI) iri.accept(this) : null);
        OWLOntologyID id = new OWLOntologyID(ontologyIRI, versionIRI);
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager(m_dataFactory);
            OWLOntology o = m.createOntology(id);
            Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
            for (Axiom ax : ontology.getAxioms())
                axioms.add((OWLAxiom) ax.accept(this));
            m.addAxioms(o, axioms);
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (Annotation annotation : ontology.getAnnotations())
                changes.add(new AddOntologyAnnotation(o, (OWLAnnotation) annotation.accept(this)));
            for (Import imported : ontology.getDirectImports())
                changes.add(new AddImport(o, m_dataFactory.getOWLImportsDeclaration((IRI) imported.getImport().accept(this))));
            m.applyChanges(changes);
            return o;
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
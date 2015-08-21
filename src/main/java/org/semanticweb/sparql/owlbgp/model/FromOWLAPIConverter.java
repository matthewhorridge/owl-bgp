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
import java.util.List;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
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
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
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
import org.semanticweb.sparql.owlbgp.model.axioms.ReflexiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.axioms.SubAnnotationPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubDataPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubObjectPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.TransitiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
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
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction.OWL2_FACET;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;

public class FromOWLAPIConverter implements OWLObjectVisitorEx<ExtendedOWLObject> {

    protected static FromOWLAPIConverter instance;

    private FromOWLAPIConverter() {
        // empty
    }

    public static FromOWLAPIConverter getInstance() {
        if (instance == null)
            instance = new FromOWLAPIConverter();
        return instance;
    }

    public static ExtendedOWLObject convert(OWLObject object) {
        return object.accept(FromOWLAPIConverter.getInstance());
    }

    public ExtendedOWLObject visit(OWLSubClassOfAxiom axiom) {
        return SubClassOf.create((ClassExpression) axiom.getSubClass().accept(this), (ClassExpression) axiom.getSuperClass().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    protected Set<Annotation> convertAnnotations(Set<OWLAnnotation> annos) {
        Set<Annotation> result = new HashSet<Annotation>();
        for (OWLAnnotation anno : annos)
            result.add((Annotation) anno.accept(this));
        return result;
    }

    public ExtendedOWLObject visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return NegativeObjectPropertyAssertion.create((ObjectPropertyExpression) axiom.getProperty().accept(this), (Individual) axiom.getSubject().accept(this), (Individual) axiom.getObject().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return AsymmetricObjectProperty.create((ObjectPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return ReflexiveObjectProperty.create((ObjectPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDisjointClassesAxiom axiom) {
        Set<ClassExpression> result = new HashSet<ClassExpression>();
        for (OWLClassExpression ce : axiom.getClassExpressions())
            result.add((ClassExpression) ce.accept(this));
        return DisjointClasses.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDataPropertyDomainAxiom axiom) {
        return DataPropertyDomain.create((DataPropertyExpression) axiom.getProperty().accept(this), (ClassExpression) axiom.getDomain().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLObjectPropertyDomainAxiom axiom) {
        return ObjectPropertyDomain.create((ObjectPropertyExpression) axiom.getProperty().accept(this), (ClassExpression) axiom.getDomain().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        Set<ObjectPropertyExpression> result = new HashSet<ObjectPropertyExpression>();
        for (OWLObjectPropertyExpression dpe : axiom.getProperties())
            result.add((ObjectPropertyExpression) dpe.accept(this));
        return EquivalentObjectProperties.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return NegativeDataPropertyAssertion.create((DataPropertyExpression) axiom.getProperty().accept(this), (Individual) axiom.getSubject().accept(this), (Literal) axiom.getObject().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDifferentIndividualsAxiom axiom) {
        Set<Individual> result = new HashSet<Individual>();
        for (OWLIndividual ind : axiom.getIndividuals())
            result.add((Individual) ind.accept(this));
        return DifferentIndividuals.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDisjointDataPropertiesAxiom axiom) {
        Set<DataPropertyExpression> result = new HashSet<DataPropertyExpression>();
        for (OWLDataPropertyExpression dpe : axiom.getProperties())
            result.add((DataPropertyExpression) dpe.accept(this));
        return DisjointDataProperties.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDisjointObjectPropertiesAxiom axiom) {
        Set<ObjectPropertyExpression> result = new HashSet<ObjectPropertyExpression>();
        for (OWLObjectPropertyExpression dpe : axiom.getProperties())
            result.add((ObjectPropertyExpression) dpe.accept(this));
        return DisjointObjectProperties.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLObjectPropertyRangeAxiom axiom) {
        return ObjectPropertyDomain.create((ObjectPropertyExpression) axiom.getProperty().accept(this), (ClassExpression) axiom.getRange(), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLObjectPropertyAssertionAxiom axiom) {
        return ObjectPropertyAssertion.create((ObjectPropertyExpression) axiom.getProperty().accept(this), (Individual) axiom.getSubject().accept(this), (Individual) axiom.getObject().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return FunctionalObjectProperty.create((ObjectPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLSubObjectPropertyOfAxiom axiom) {
        return SubObjectPropertyOf.create((ObjectPropertyExpression) axiom.getSubProperty().accept(this), (ObjectPropertyExpression) axiom.getSuperProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDisjointUnionAxiom axiom) {
        Set<ClassExpression> result = new HashSet<ClassExpression>();
        for (OWLClassExpression ce : axiom.getClassExpressions())
            result.add((ClassExpression) ce.accept(this));
        return DisjointUnion.create((Clazz) axiom.getOWLClass().accept(this), result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDeclarationAxiom axiom) {
        return Declaration.create((Atomic) axiom.getEntity().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLAnnotationAssertionAxiom axiom) {
        return AnnotationAssertion.create((AnnotationPropertyExpression) axiom.getProperty().accept(this), (AnnotationSubject) axiom.getSubject().accept(this), (AnnotationValue) axiom.getValue().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return SymmetricObjectProperty.create((ObjectPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDataPropertyRangeAxiom axiom) {
        return DataPropertyRange.create((DataPropertyExpression) axiom.getProperty().accept(this), (DataRange) axiom.getRange().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLFunctionalDataPropertyAxiom axiom) {
        return FunctionalDataProperty.create((DataPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLEquivalentDataPropertiesAxiom axiom) {
        Set<DataPropertyExpression> result = new HashSet<DataPropertyExpression>();
        for (OWLDataPropertyExpression dpe : axiom.getProperties())
            result.add((DataPropertyExpression) dpe.accept(this));
        return EquivalentDataProperties.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLClassAssertionAxiom axiom) {
        return ClassAssertion.create((ClassExpression) axiom.getClassExpression().accept(this), (Individual) axiom.getIndividual().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLEquivalentClassesAxiom axiom) {
        Set<ClassExpression> result = new HashSet<ClassExpression>();
        for (OWLClassExpression ce : axiom.getClassExpressions())
            result.add((ClassExpression) ce.accept(this));
        return EquivalentClasses.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDataPropertyAssertionAxiom axiom) {
        return DataPropertyAssertion.create((DataPropertyExpression) axiom.getProperty().accept(this), (Individual) axiom.getSubject().accept(this), (Literal) axiom.getObject().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return TransitiveObjectProperty.create((ObjectPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return IrreflexiveObjectProperty.create((ObjectPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLSubDataPropertyOfAxiom axiom) {
        return SubDataPropertyOf.create((DataPropertyExpression) axiom.getSubProperty().accept(this), (DataPropertyExpression) axiom.getSuperProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return InverseFunctionalObjectProperty.create((ObjectPropertyExpression) axiom.getProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLSameIndividualAxiom axiom) {
        Set<Individual> result = new HashSet<Individual>();
        for (OWLIndividual ind : axiom.getIndividuals())
            result.add((Individual) ind.accept(this));
        return SameIndividual.create(result, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLSubPropertyChainOfAxiom axiom) {
        List<ObjectPropertyExpression> result = new ArrayList<ObjectPropertyExpression>();
        for (OWLObjectPropertyExpression ope : axiom.getPropertyChain())
            result.add((ObjectPropertyExpression) ope.accept(this));
        return SubObjectPropertyOf.create(ObjectPropertyChain.create(result), (ObjectPropertyExpression) axiom.getSuperProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLInverseObjectPropertiesAxiom axiom) {
        return InverseObjectProperties.create((ObjectPropertyExpression) axiom.getFirstProperty().accept(this), (ObjectPropertyExpression) axiom.getSecondProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLHasKeyAxiom axiom) {
        Set<PropertyExpression> pes = new HashSet<PropertyExpression>();
        for (OWLObjectPropertyExpression ope : axiom.getObjectPropertyExpressions()) {
            pes.add((ObjectPropertyExpression) ope.accept(this));
        }
        for (OWLDataPropertyExpression dpe : axiom.getDataPropertyExpressions()) {
            pes.add((DataPropertyExpression) dpe.accept(this));
        }
        return HasKey.create((ClassExpression) axiom.getClassExpression().accept(this), pes, convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLDatatypeDefinitionAxiom axiom) {
        return DatatypeDefinition.create((Datatype) axiom.getDatatype().accept(this), (DataRange) axiom.getDataRange().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(SWRLRule rule) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return SubAnnotationPropertyOf.create((AnnotationPropertyExpression) axiom.getSubProperty().accept(this), (AnnotationPropertyExpression) axiom.getSuperProperty().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return AnnotationPropertyDomain.create((AnnotationPropertyExpression) axiom.getProperty().accept(this), (Identifier) axiom.getDomain().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return AnnotationPropertyRange.create((AnnotationPropertyExpression) axiom.getProperty().accept(this), (Identifier) axiom.getRange().accept(this), convertAnnotations(axiom.getAnnotations()));
    }

    public ExtendedOWLObject visit(OWLClass ce) {
        return Clazz.create(ce.getIRI().toString());
    }

    public ExtendedOWLObject visit(OWLObjectIntersectionOf ce) {
        Set<ClassExpression> result = new HashSet<ClassExpression>();
        for (OWLClassExpression c : ce.getOperands())
            result.add((ClassExpression) c.accept(this));
        return ObjectIntersectionOf.create(result);
    }

    public ExtendedOWLObject visit(OWLObjectUnionOf ce) {
        Set<ClassExpression> result = new HashSet<ClassExpression>();
        for (OWLClassExpression c : ce.getOperands())
            result.add((ClassExpression) c.accept(this));
        return ObjectUnionOf.create(result);
    }

    public ExtendedOWLObject visit(OWLObjectComplementOf ce) {
        return ObjectComplementOf.create((ClassExpression) ce.getOperand().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectSomeValuesFrom ce) {
        return ObjectSomeValuesFrom.create((ObjectPropertyExpression) ce.getProperty().accept(this), (ClassExpression) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectAllValuesFrom ce) {
        return ObjectAllValuesFrom.create((ObjectPropertyExpression) ce.getProperty().accept(this), (ClassExpression) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectHasValue ce) {
        return ObjectHasValue.create((ObjectPropertyExpression) ce.getProperty().accept(this), (Individual) ce.getValue().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectMinCardinality ce) {
        return ObjectMinCardinality.create(ce.getCardinality(), (ObjectPropertyExpression) ce.getProperty().accept(this), (ClassExpression) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectExactCardinality ce) {
        return ObjectExactCardinality.create(ce.getCardinality(), (ObjectPropertyExpression) ce.getProperty().accept(this), (ClassExpression) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectMaxCardinality ce) {
        return ObjectMaxCardinality.create(ce.getCardinality(), (ObjectPropertyExpression) ce.getProperty().accept(this), (ClassExpression) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectHasSelf ce) {
        return ObjectHasSelf.create((ObjectPropertyExpression) ce.getProperty().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectOneOf ce) {
        Set<Individual> result = new HashSet<Individual>();
        for (OWLIndividual ind : ce.getIndividuals())
            result.add((Individual) ind.accept(this));
        return ObjectOneOf.create(result);
    }

    public ExtendedOWLObject visit(OWLDataSomeValuesFrom ce) {
        return DataSomeValuesFrom.create((DataPropertyExpression) ce.getProperty().accept(this), (DataRange) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLDataAllValuesFrom ce) {
        return DataAllValuesFrom.create((DataPropertyExpression) ce.getProperty().accept(this), (DataRange) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLDataHasValue ce) {
        return DataHasValue.create((DataPropertyExpression) ce.getProperty().accept(this), (Literal) ce.getValue().accept(this));
    }

    public ExtendedOWLObject visit(OWLDataMinCardinality ce) {
        return DataMinCardinality.create(ce.getCardinality(), (DataPropertyExpression) ce.getProperty().accept(this), (DataRange) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLDataExactCardinality ce) {
        return DataExactCardinality.create(ce.getCardinality(), (DataPropertyExpression) ce.getProperty().accept(this), (DataRange) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLDataMaxCardinality ce) {
        return DataMaxCardinality.create(ce.getCardinality(), (DataPropertyExpression) ce.getProperty().accept(this), (DataRange) ce.getFiller().accept(this));
    }

    public ExtendedOWLObject visit(OWLDatatype node) {
        return Datatype.create(node.getIRI().toString());
    }

    public ExtendedOWLObject visit(OWLDataComplementOf node) {
        return DataComplementOf.create((DataRange) node.getDataRange().accept(this));
    }

    public ExtendedOWLObject visit(OWLDataOneOf node) {
        Set<Literal> result = new HashSet<Literal>();
        for (OWLLiteral lit : node.getValues())
            result.add((Literal) lit.accept(this));
        return DataOneOf.create(result);
    }

    public ExtendedOWLObject visit(OWLDataIntersectionOf node) {
        Set<DataRange> result = new HashSet<DataRange>();
        for (OWLDataRange dr : node.getOperands())
            result.add((DataRange) dr.accept(this));
        return DataIntersectionOf.create(result);
    }

    public ExtendedOWLObject visit(OWLDataUnionOf node) {
        Set<DataRange> result = new HashSet<DataRange>();
        for (OWLDataRange dr : node.getOperands())
            result.add((DataRange) dr.accept(this));
        return DataUnionOf.create(result);
    }

    public ExtendedOWLObject visit(OWLDatatypeRestriction node) {
        Set<FacetRestriction> result = new HashSet<FacetRestriction>();
        for (OWLFacetRestriction facet : node.getFacetRestrictions())
            result.add((FacetRestriction) facet.accept(this));
        return DatatypeRestriction.create((Datatype) node.getDatatype().accept(this), result);
    }

    public ExtendedOWLObject visit(OWLLiteral node) {
        return TypedLiteral.create(node.getLiteral(), node.getLang(), (Datatype) node.getDatatype().accept(this));
    }

    public ExtendedOWLObject visit(OWLFacetRestriction node) {
        return FacetRestriction.create(OWL2_FACET.valueOf(node.getFacet().toString()), (TypedLiteral) node.getFacetValue().accept(this));
    }

    public ExtendedOWLObject visit(OWLObjectProperty property) {
        return ObjectProperty.create(property.getIRI().toString());
    }

    public ExtendedOWLObject visit(OWLObjectInverseOf property) {
        return ObjectInverseOf.create((ObjectPropertyExpression) property.accept(this));
    }

    public ExtendedOWLObject visit(OWLDataProperty property) {
        return DataProperty.create(property.getIRI().toString());
    }

    public ExtendedOWLObject visit(OWLNamedIndividual individual) {
        return NamedIndividual.create(individual.getIRI().toString());
    }

    public ExtendedOWLObject visit(OWLAnnotationProperty property) {
        return AnnotationProperty.create(property.getIRI().toString());
    }

    public ExtendedOWLObject visit(OWLAnnotation node) {
        return Annotation.create((AnnotationPropertyExpression) node.getProperty().accept(this), (AnnotationValue) node.getValue().accept(this), convertAnnotations(node.getAnnotations()));
    }

    public ExtendedOWLObject visit(IRI iri) {
        return org.semanticweb.sparql.owlbgp.model.IRI.create(iri.toString());
    }

    public ExtendedOWLObject visit(OWLAnonymousIndividual individual) {
        return AnonymousIndividual.create(individual.toStringID());
    }

    public ExtendedOWLObject visit(SWRLClassAtom node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLDataRangeAtom node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLObjectPropertyAtom node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLDataPropertyAtom node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLBuiltInAtom node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLVariable node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLIndividualArgument node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLLiteralArgument node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLSameIndividualAtom node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(SWRLDifferentIndividualsAtom node) {
        throw new UnsupportedOperationException("SWRL rules cannot be converted into extened OWL objects.");
    }

    public ExtendedOWLObject visit(OWLOntology ontology) {
        Identifier ontologyIRI = null;
        IRI iri = ontology.getOntologyID().getOntologyIRI();
        if (iri != null)
            ontologyIRI = org.semanticweb.sparql.owlbgp.model.IRI.create(iri.toString());
        Identifier versionIRI = null;
        iri = ontology.getOntologyID().getVersionIRI();
        if (iri != null)
            versionIRI = org.semanticweb.sparql.owlbgp.model.IRI.create(iri.toString());
        Set<Import> imports = new HashSet<Import>();
        for (OWLImportsDeclaration imp : ontology.getImportsDeclarations())
            imports.add(Import.create(org.semanticweb.sparql.owlbgp.model.IRI.create(imp.getIRI().toString())));
        Set<Axiom> axioms = new HashSet<Axiom>();
        for (OWLAxiom ax : ontology.getAxioms())
            axioms.add((Axiom) ax.accept(this));
        return Ontology.create(ontologyIRI, versionIRI, imports, axioms, convertAnnotations(ontology.getAnnotations()));
    }
}

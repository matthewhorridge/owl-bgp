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

import java.util.HashSet;
import java.util.Set;

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

public class SignatureExtractor implements ExtendedOWLObjectVisitor {
    protected final Set<Clazz> m_classesInSignature;
    protected final Set<Datatype> m_datatypesInSignature;
    protected final Set<ObjectProperty> m_objectPropertiesInSignature;
    protected final Set<DataProperty> m_dataPropertiesInSignature;
    protected final Set<AnnotationProperty> m_annotationPropertiesInSignature;
    protected final Set<NamedIndividual> m_individualsInSignature;
    protected final Set<ClassVariable> m_classVariablesInSignature;
    protected final Set<DatatypeVariable> m_datatypeVariablesInSignature;
    protected final Set<ObjectPropertyVariable> m_objectPropertyVariablesInSignature;
    protected final Set<DataPropertyVariable> m_dataPropertyVariablesInSignature;
    protected final Set<AnnotationPropertyVariable> m_annotationPropertyVariablesInSignature;
    protected final Set<IndividualVariable> m_individualVariablesInSignature;
    protected final Set<LiteralVariable> m_literalVariablesInSignature;
    protected final Set<UntypedVariable> m_untypedVariablesInSignature;

    public SignatureExtractor() {
        m_classesInSignature = new HashSet<Clazz>();
        m_datatypesInSignature = new HashSet<Datatype>();
        m_objectPropertiesInSignature = new HashSet<ObjectProperty>();
        m_dataPropertiesInSignature = new HashSet<DataProperty>();
        m_annotationPropertiesInSignature = new HashSet<AnnotationProperty>();
        m_individualsInSignature = new HashSet<NamedIndividual>();
        m_classVariablesInSignature = new HashSet<ClassVariable>();
        m_datatypeVariablesInSignature = new HashSet<DatatypeVariable>();
        m_objectPropertyVariablesInSignature = new HashSet<ObjectPropertyVariable>();
        m_dataPropertyVariablesInSignature = new HashSet<DataPropertyVariable>();
        m_annotationPropertyVariablesInSignature = new HashSet<AnnotationPropertyVariable>();
        m_individualVariablesInSignature = new HashSet<IndividualVariable>();
        m_literalVariablesInSignature = new HashSet<LiteralVariable>();
        m_untypedVariablesInSignature = new HashSet<UntypedVariable>();
    }

    public Set<Clazz> getClassesInSignature() {
        return m_classesInSignature;
    }

    public Set<Datatype> getDatatypesInSignature() {
        return m_datatypesInSignature;
    }

    public Set<ObjectProperty> getObjectPropertiesInSignature() {
        return m_objectPropertiesInSignature;
    }

    public Set<DataProperty> getDataPropertiesInSignature() {
        return m_dataPropertiesInSignature;
    }

    public Set<AnnotationProperty> getAnnotationPropertiesInSignature() {
        return m_annotationPropertiesInSignature;
    }

    public Set<NamedIndividual> getIndividualsInSignature() {
        return m_individualsInSignature;
    }

    public Set<ClassVariable> getClassVariablesInSignature() {
        return m_classVariablesInSignature;
    }

    public Set<DatatypeVariable> getDatatypeVariablesInSignature() {
        return m_datatypeVariablesInSignature;
    }

    public Set<ObjectPropertyVariable> getObjectPropertyVariablesInSignature() {
        return m_objectPropertyVariablesInSignature;
    }

    public Set<DataPropertyVariable> getDataPropertyVariablesInSignature() {
        return m_dataPropertyVariablesInSignature;
    }

    public Set<AnnotationPropertyVariable> getAnnotationPropertyVariablesInSignature() {
        return m_annotationPropertyVariablesInSignature;
    }

    public Set<IndividualVariable> getIndividualVariablesInSignature() {
        return m_individualVariablesInSignature;
    }

    public Set<LiteralVariable> getLiteralVariablesInSignature() {
        return m_literalVariablesInSignature;
    }

    public Set<UntypedVariable> getUntypedVariablesInSignature() {
        return m_untypedVariablesInSignature;
    }

    public void visit(IRI iri) {
    }

    public void visit(Clazz clazz) {
        m_classesInSignature.add(clazz);
    }

    public void visit(ClassVariable classVariable) {
        m_classVariablesInSignature.add(classVariable);
    }

    public void visit(ObjectIntersectionOf objectIntersectionOf) {
        for (ClassExpression classExpression : objectIntersectionOf.getClassExpressions())
            classExpression.accept(this);
    }

    public void visit(ObjectUnionOf objectUnionOf) {
        for (ClassExpression classExpression : objectUnionOf.getClassExpressions())
            classExpression.accept(this);
    }

    public void visit(ObjectComplementOf objectComplementOf) {
        objectComplementOf.getComplementedClassExpression().accept(this);
    }

    public void visit(ObjectAllValuesFrom allValuesFrom) {
        allValuesFrom.getObjectPropertyExpression().accept(this);
        allValuesFrom.getClassExpression().accept(this);
    }

    public void visit(ObjectSomeValuesFrom objectSomeValuesFrom) {
        objectSomeValuesFrom.getObjectPropertyExpression().accept(this);
        objectSomeValuesFrom.getClassExpression().accept(this);
    }

    public void visit(ObjectHasValue objectHasValue) {
        objectHasValue.getObjectPropertyExpression().accept(this);
        objectHasValue.getIndividual().accept(this);
    }

    public void visit(ObjectMinCardinality objectMinCardinality) {
        objectMinCardinality.getObjectPropertyExpression().accept(this);
        ClassExpression ce = objectMinCardinality.getClassExpression();
        if (ce != null)
            ce.accept(this);
    }

    public void visit(ObjectExactCardinality objectExactCardinality) {
        objectExactCardinality.getObjectPropertyExpression().accept(this);
        ClassExpression ce = objectExactCardinality.getClassExpression();
        if (ce != null)
            ce.accept(this);
    }

    public void visit(ObjectMaxCardinality objectMaxCardinality) {
        objectMaxCardinality.getObjectPropertyExpression().accept(this);
        ClassExpression ce = objectMaxCardinality.getClassExpression();
        if (ce != null)
            ce.accept(this);
    }

    public void visit(ObjectHasSelf objectHasSelf) {
        objectHasSelf.getObjectPropertyExpression().accept(this);
    }

    public void visit(ObjectOneOf objectOneOf) {
        for (Individual individual : objectOneOf.getIndividuals())
            individual.accept(this);
    }

    public void visit(DataSomeValuesFrom dataSomeValuesFrom) {
        dataSomeValuesFrom.getDataPropertyExpression().accept(this);
        dataSomeValuesFrom.getDataRange().accept(this);
    }

    public void visit(DataAllValuesFrom dataAllValuesFrom) {
        dataAllValuesFrom.getDataPropertyExpression().accept(this);
        dataAllValuesFrom.getDataRange().accept(this);
    }

    public void visit(DataHasValue dataHasValue) {
        dataHasValue.getDataPropertyExpression().accept(this);
        dataHasValue.getLiteral().accept(this);
    }

    public void visit(DataMinCardinality dataMinCardinality) {
        dataMinCardinality.getDataPropertyExpression().accept(this);
        DataRange dr = dataMinCardinality.getDataRange();
        if (dr != null)
            dr.accept(this);
    }

    public void visit(DataExactCardinality dataExactCardinality) {
        dataExactCardinality.getDataPropertyExpression().accept(this);
        DataRange dr = dataExactCardinality.getDataRange();
        if (dr != null)
            dr.accept(this);
    }

    public void visit(DataMaxCardinality dataMaxCardinality) {
        dataMaxCardinality.getDataPropertyExpression().accept(this);
        DataRange dr = dataMaxCardinality.getDataRange();
        if (dr != null)
            dr.accept(this);
    }

    public void visit(ObjectProperty objectProperty) {
        m_objectPropertiesInSignature.add(objectProperty);
    }

    public void visit(ObjectInverseOf inverseObjectProperty) {
        inverseObjectProperty.getInvertedObjectProperty().accept(this);
    }

    public void visit(ObjectPropertyVariable objectPropertyVariable) {
        m_objectPropertyVariablesInSignature.add(objectPropertyVariable);
    }

    public void visit(ObjectPropertyChain objectPropertyChain) {
        for (ObjectPropertyExpression ope : objectPropertyChain.getObjectPropertyExpressions())
            ope.accept(this);
    }

    public void visit(DataProperty dataProperty) {
        m_dataPropertiesInSignature.add(dataProperty);
    }

    public void visit(DataPropertyVariable dataPropertyVariable) {
        m_dataPropertyVariablesInSignature.add(dataPropertyVariable);
    }

    public void visit(AnnotationProperty annotationProperty) {
        m_annotationPropertiesInSignature.add(annotationProperty);
    }

    public void visit(AnnotationPropertyVariable annotationPropertyVariable) {
        m_annotationPropertyVariablesInSignature.add(annotationPropertyVariable);
    }

    public void visit(TypedLiteral literal) {
    }

    public void visit(LiteralVariable literalVariable) {
        m_literalVariablesInSignature.add(literalVariable);
    }

    public void visit(NamedIndividual namedIndividual) {
        m_individualsInSignature.add(namedIndividual);
    }

    public void visit(AnonymousIndividual anonymousIndividual) {
    }

    public void visit(IndividualVariable individualVariable) {
        m_individualVariablesInSignature.add(individualVariable);
    }

    public void visit(Datatype datatype) {
        m_datatypesInSignature.add(datatype);
    }

    public void visit(DatatypeVariable datatypeVariable) {
        m_datatypeVariablesInSignature.add(datatypeVariable);
    }

    public void visit(DatatypeRestriction datatypeRestriction) {
        datatypeRestriction.getDatatype().accept(this);
    }

    public void visit(FacetRestriction facetRestriction) {
    }

    public void visit(DataComplementOf dataComplementOf) {
        dataComplementOf.getDataRange().accept(this);
    }

    public void visit(DataIntersectionOf dataIntersectionOf) {
        for (DataRange dataRange : dataIntersectionOf.getDataRanges())
            dataRange.accept(this);
    }

    public void visit(DataUnionOf dataUnionOf) {
        for (DataRange dataRange : dataUnionOf.getDataRanges())
            dataRange.accept(this);
    }

    public void visit(DataOneOf dataOneOf) {
        for (Literal literal : dataOneOf.getLiterals())
            literal.accept(this);
    }

    public void visit(Import imp) {
        if (imp.getImport() instanceof UntypedVariable)
            m_untypedVariablesInSignature.add((UntypedVariable) imp.getImport());
    }

    public void visit(Annotation annotation) {
        annotation.m_annotationProperty.accept(this);
        annotation.m_annotationValue.accept(this);
    }

    public void visit(AnnotationValue annotationValue) {
        if (annotationValue instanceof Variable)
            m_untypedVariablesInSignature.add(UntypedVariable.create(annotationValue.toString()));
    }

    public void visit(AnnotationSubject annotationSubject) {
        if (annotationSubject instanceof Variable)
            m_untypedVariablesInSignature.add(UntypedVariable.create(annotationSubject.toString()));
    }

    public void visit(AnnotationAssertion assertion) {
        assertion.getAnnotationProperty().accept(this);
        assertion.getAnnotationSubject().accept(this);
        assertion.getAnnotationValue().accept(this);
        visitAnnotations(assertion);
    }

    public void visit(SubAnnotationPropertyOf axiom) {
        axiom.getSubAnnotationPropertyExpression().accept(this);
        axiom.getSuperAnnotationPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(AnnotationPropertyDomain axiom) {
        axiom.getAnnotationPropertyExpression().accept(this);
        axiom.getDomain().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(AnnotationPropertyRange axiom) {
        axiom.getAnnotationPropertyExpression().accept(this);
        axiom.getRange().accept(this);
        visitAnnotations(axiom);
    }

    protected void visitAnnotations(Axiom axiom) {
        for (Annotation annotation : axiom.getAnnotations())
            annotation.accept(this);
    }

    public void visit(Declaration axiom) {
        axiom.getDeclaredObject().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(SubClassOf axiom) {
        axiom.getSubClassExpression().accept(this);
        axiom.getSuperClassExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(EquivalentClasses axiom) {
        for (ClassExpression classExpression : axiom.getClassExpressions())
            classExpression.accept(this);
    }

    public void visit(DisjointClasses axiom) {
        for (ClassExpression classExpression : axiom.getClassExpressions())
            classExpression.accept(this);
    }

    public void visit(DisjointUnion axiom) {
        for (ClassExpression classExpression : axiom.getClassExpressions())
            classExpression.accept(this);
    }

    public void visit(SubObjectPropertyOf axiom) {
        axiom.getSubObjectPropertyExpression().accept(this);
        axiom.getSubObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(EquivalentObjectProperties axiom) {
        for (ObjectPropertyExpression ope : axiom.getObjectPropertyExpressions())
            ope.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(DisjointObjectProperties axiom) {
        for (ObjectPropertyExpression ope : axiom.getObjectPropertyExpressions())
            ope.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(InverseObjectProperties axiom) {
        for (ObjectPropertyExpression ope : axiom.getObjectPropertyExpressions())
            ope.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(ObjectPropertyDomain axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        axiom.getDomain().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(ObjectPropertyRange axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        axiom.getRange().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(FunctionalObjectProperty axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(InverseFunctionalObjectProperty axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(ReflexiveObjectProperty axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(IrreflexiveObjectProperty axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(SymmetricObjectProperty axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(AsymmetricObjectProperty axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(TransitiveObjectProperty axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(SubDataPropertyOf axiom) {
        axiom.getSubDataPropertyExpression().accept(this);
        axiom.getSuperDataPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(EquivalentDataProperties axiom) {
        for (DataPropertyExpression dpe : axiom.getDataPropertyExpressions())
            dpe.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(DisjointDataProperties axiom) {
        for (DataPropertyExpression dpe : axiom.getDataPropertyExpressions())
            dpe.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(DataPropertyDomain axiom) {
        axiom.getDataPropertyExpression().accept(this);
        axiom.getDomain().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(DataPropertyRange axiom) {
        axiom.getDataPropertyExpression().accept(this);
        axiom.getRange().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(FunctionalDataProperty axiom) {
        axiom.getDataPropertyExpression().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(DatatypeDefinition axiom) {
        axiom.getDatatype().accept(this);
        axiom.getDataRange().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(HasKey axiom) {
        axiom.getClassExpression().accept(this);
        for (ObjectPropertyExpression ope : axiom.getObjectPropertyExpressions())
            ope.accept(this);
        for (DataPropertyExpression dpe : axiom.getDataPropertyExpressions())
            dpe.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(SameIndividual axiom) {
        for (Individual individual : axiom.getIndividuals())
            individual.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(DifferentIndividuals axiom) {
        for (Individual individual : axiom.getIndividuals())
            individual.accept(this);
        visitAnnotations(axiom);
    }

    public void visit(ClassAssertion axiom) {
        axiom.getClassExpression().accept(this);
        axiom.getIndividual().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(ObjectPropertyAssertion axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        axiom.getIndividual1().accept(this);
        axiom.getIndividual2().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(NegativeObjectPropertyAssertion axiom) {
        axiom.getObjectPropertyExpression().accept(this);
        axiom.getIndividual1().accept(this);
        axiom.getIndividual2().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(DataPropertyAssertion axiom) {
        axiom.getDataPropertyExpression().accept(this);
        axiom.getIndividual().accept(this);
        axiom.getLiteral().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(NegativeDataPropertyAssertion axiom) {
        axiom.getDataPropertyExpression().accept(this);
        axiom.getIndividual().accept(this);
        axiom.getLiteral().accept(this);
        visitAnnotations(axiom);
    }

    public void visit(Ontology ontology) {
        for (Axiom ax : ontology.getAxioms())
            ax.accept(this);
        for (Annotation annotation : ontology.getAnnotations())
            annotation.accept(this);
        for (Import directImport : ontology.getDirectImports())
            directImport.accept(this);
    }
}
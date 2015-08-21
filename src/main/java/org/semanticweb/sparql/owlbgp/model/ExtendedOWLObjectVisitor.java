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

import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.AsymmetricObjectProperty;
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
import org.semanticweb.sparql.owlbgp.model.dataranges.DataUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public interface ExtendedOWLObjectVisitor {
    public void visit(IRI iri);

    public void visit(Clazz clazz);

    public void visit(ClassVariable classVariable);

    public void visit(ObjectIntersectionOf objectIntersectionOf);

    public void visit(ObjectUnionOf objectUnionOf);

    public void visit(ObjectComplementOf objectComplementOf);

    public void visit(ObjectSomeValuesFrom objectSomeValuesFrom);

    public void visit(ObjectAllValuesFrom objectAllValuesFrom);

    public void visit(ObjectHasValue objectHasValue);

    public void visit(ObjectMinCardinality objectMinCardinality);

    public void visit(ObjectExactCardinality objectExactCardinality);

    public void visit(ObjectMaxCardinality objectMaxCardinality);

    public void visit(ObjectHasSelf objectHasSelf);

    public void visit(ObjectOneOf objectOneOf);

    public void visit(DataSomeValuesFrom dataSomeValuesFrom);

    public void visit(DataAllValuesFrom dataAllValuesFrom);

    public void visit(DataHasValue dataHasValue);

    public void visit(DataMinCardinality dataMinCardinality);

    public void visit(DataExactCardinality dataExactCardinality);

    public void visit(DataMaxCardinality dataMaxCardinality);

    public void visit(ObjectProperty objectProperty);

    public void visit(ObjectInverseOf inverseObjectProperty);

    public void visit(ObjectPropertyVariable objectPropertyVariable);

    public void visit(ObjectPropertyChain objectPropertyChain);

    public void visit(DataProperty dataProperty);

    public void visit(DataPropertyVariable dataPropertyVariable);

    public void visit(AnnotationProperty annotationProperty);

    public void visit(AnnotationPropertyVariable annotationProperty);

    public void visit(TypedLiteral literal);

    public void visit(LiteralVariable literalVariable);

    public void visit(NamedIndividual namedIndividual);

    public void visit(AnonymousIndividual anonymousIndividual);

    public void visit(IndividualVariable individualVariable);

    public void visit(Datatype datatype);

    public void visit(DatatypeVariable datatypeVariable);

    public void visit(DatatypeRestriction datatypeRestriction);

    public void visit(FacetRestriction facetRestriction);

    public void visit(DataComplementOf dataComplementOf);

    public void visit(DataIntersectionOf dataIntersectionOf);

    public void visit(DataUnionOf dataUnionOf);

    public void visit(DataOneOf dataOneOf);

    public void visit(Import axiom);

    public void visit(Ontology ontology);

    public void visit(Annotation annotation);

    public void visit(AnnotationValue annotationValue);

    public void visit(AnnotationSubject annotationSubject);

    public void visit(AnnotationAssertion assertion);

    public void visit(SubAnnotationPropertyOf axiom);

    public void visit(AnnotationPropertyDomain axiom);

    public void visit(AnnotationPropertyRange axiom);

    public void visit(Declaration axiom);

    public void visit(SubClassOf axiom);

    public void visit(EquivalentClasses axiom);

    public void visit(DisjointClasses axiom);

    public void visit(DisjointUnion axiom);

    public void visit(SubObjectPropertyOf axiom);

    public void visit(EquivalentObjectProperties axiom);

    public void visit(DisjointObjectProperties axiom);

    public void visit(InverseObjectProperties axiom);

    public void visit(ObjectPropertyDomain axiom);

    public void visit(ObjectPropertyRange axiom);

    public void visit(FunctionalObjectProperty axiom);

    public void visit(InverseFunctionalObjectProperty axiom);

    public void visit(ReflexiveObjectProperty axiom);

    public void visit(IrreflexiveObjectProperty axiom);

    public void visit(SymmetricObjectProperty axiom);

    public void visit(AsymmetricObjectProperty axiom);

    public void visit(TransitiveObjectProperty axiom);

    public void visit(SubDataPropertyOf axiom);

    public void visit(EquivalentDataProperties axiom);

    public void visit(DisjointDataProperties axiom);

    public void visit(DataPropertyDomain axiom);

    public void visit(DataPropertyRange axiom);

    public void visit(FunctionalDataProperty axiom);

    public void visit(DatatypeDefinition axiom);

    public void visit(HasKey axiom);

    public void visit(SameIndividual axiom);

    public void visit(DifferentIndividuals axiom);

    public void visit(ClassAssertion axiom);

    public void visit(ObjectPropertyAssertion axiom);

    public void visit(NegativeObjectPropertyAssertion axiom);

    public void visit(DataPropertyAssertion axiom);

    public void visit(NegativeDataPropertyAssertion axiom);
}
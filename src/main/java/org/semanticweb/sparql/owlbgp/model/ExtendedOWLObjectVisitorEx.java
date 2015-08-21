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

public interface ExtendedOWLObjectVisitorEx<O> {
    O visit(IRI iri);

    O visit(Clazz clazz);

    O visit(ClassVariable classVariable);

    O visit(ObjectIntersectionOf objectIntersectionOf);

    O visit(ObjectUnionOf objectUnionOf);

    O visit(ObjectComplementOf objectComplementOf);

    O visit(ObjectSomeValuesFrom objectSomeValuesFrom);

    O visit(ObjectAllValuesFrom objectAllValuesFrom);

    O visit(ObjectHasValue objectHasValue);

    O visit(ObjectMinCardinality objectMinCardinality);

    O visit(ObjectExactCardinality objectExactCardinality);

    O visit(ObjectMaxCardinality objectMaxCardinality);

    O visit(ObjectHasSelf objectHasSelf);

    O visit(ObjectOneOf objectOneOf);

    O visit(DataSomeValuesFrom dataSomeValuesFrom);

    O visit(DataAllValuesFrom dataAllValuesFrom);

    O visit(DataHasValue dataHasValue);

    O visit(DataMinCardinality dataMinCardinality);

    O visit(DataExactCardinality dataExactCardinality);

    O visit(DataMaxCardinality dataMaxCardinality);

    O visit(ObjectProperty objectProperty);

    O visit(ObjectInverseOf inverseObjectProperty);

    O visit(ObjectPropertyVariable objectPropertyVariable);

    O visit(ObjectPropertyChain objectPropertyChain);

    O visit(DataProperty dataProperty);

    O visit(DataPropertyVariable dataPropertyVariable);

    O visit(AnnotationProperty annotationProperty);

    O visit(AnnotationPropertyVariable annotationProperty);

    O visit(TypedLiteral literal);

    O visit(LiteralVariable literalVariable);

    O visit(NamedIndividual namedIndividual);

    O visit(AnonymousIndividual anonymousIndividual);

    O visit(IndividualVariable individualVariable);

    O visit(Datatype datatype);

    O visit(DatatypeVariable datatypeVariable);

    O visit(DatatypeRestriction datatypeRestriction);

    O visit(FacetRestriction facetRestriction);

    O visit(DataComplementOf dataComplementOf);

    O visit(DataIntersectionOf dataIntersectionOf);

    O visit(DataUnionOf dataUnionOf);

    O visit(DataOneOf dataOneOf);

    O visit(Import axiom);

    O visit(Ontology ontology);

    O visit(Annotation annotation);

    O visit(AnnotationValue annotationValue);

    O visit(AnnotationSubject annotationSubject);

    O visit(AnnotationAssertion assertion);

    O visit(SubAnnotationPropertyOf axiom);

    O visit(AnnotationPropertyDomain axiom);

    O visit(AnnotationPropertyRange axiom);

    O visit(Declaration axiom);

    O visit(SubClassOf axiom);

    O visit(EquivalentClasses axiom);

    O visit(DisjointClasses axiom);

    O visit(DisjointUnion axiom);

    O visit(SubObjectPropertyOf axiom);

    O visit(EquivalentObjectProperties axiom);

    O visit(DisjointObjectProperties axiom);

    O visit(InverseObjectProperties axiom);

    O visit(ObjectPropertyDomain axiom);

    O visit(ObjectPropertyRange axiom);

    O visit(FunctionalObjectProperty axiom);

    O visit(InverseFunctionalObjectProperty axiom);

    O visit(ReflexiveObjectProperty axiom);

    O visit(IrreflexiveObjectProperty axiom);

    O visit(SymmetricObjectProperty axiom);

    O visit(AsymmetricObjectProperty axiom);

    O visit(TransitiveObjectProperty axiom);

    O visit(SubDataPropertyOf axiom);

    O visit(EquivalentDataProperties axiom);

    O visit(DisjointDataProperties axiom);

    O visit(DataPropertyDomain axiom);

    O visit(DataPropertyRange axiom);

    O visit(FunctionalDataProperty axiom);

    O visit(DatatypeDefinition axiom);

    O visit(HasKey axiom);

    O visit(SameIndividual axiom);

    O visit(DifferentIndividuals axiom);

    O visit(ClassAssertion axiom);

    O visit(ObjectPropertyAssertion axiom);

    O visit(NegativeObjectPropertyAssertion axiom);

    O visit(DataPropertyAssertion axiom);

    O visit(NegativeDataPropertyAssertion axiom);
}
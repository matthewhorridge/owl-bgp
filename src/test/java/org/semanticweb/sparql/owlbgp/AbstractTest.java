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

package org.semanticweb.sparql.owlbgp;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.AnnotationValue;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.UntypedVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;

public abstract class AbstractTest extends TestCase {
    protected static final IRI ONTOLOGY_IRI=IRI.create("http://example.org/");
    protected static final String NS="http://example.org/";
    public static final String LB=System.getProperty("line.separator");
    
    public AbstractTest() {
        super();
    }
    public AbstractTest(String name) {
        super(name);
    }
    protected void assertNoTriplesLeft(TripleConsumer consumer) {
        assertTrue(consumer.allTriplesConsumed());
    }
    public static UntypedVariable V(String variableName) {
        return UntypedVariable.create(variableName);
    }
    public static IRI IRI(String iri) {
        if (iri.startsWith("xsd:")) iri=Prefixes.s_semanticWebPrefixes.get("xsd")+iri.substring(4);
        else if (iri.startsWith("rdf:")) iri=Prefixes.s_semanticWebPrefixes.get("rdf")+iri.substring(4);
        else if (iri.startsWith("rdfs:")) iri=Prefixes.s_semanticWebPrefixes.get("rdfs")+iri.substring(5);
        else if (iri.startsWith("owl:")) iri=Prefixes.s_semanticWebPrefixes.get("owl")+iri.substring(4);
        else if (!iri.startsWith("<") && !iri.startsWith("http")) iri=NS+iri;
        return IRI.create(iri);
    }
    public static Clazz C(String className) {
        return C(IRI(className));
    }
    public static Clazz C(IRI classIri) {
        return Clazz.create(classIri);
    }
    public static ClassVariable CV(String classVarName) {
        return ClassVariable.create(classVarName);
    }
    public static Datatype DT(String datatypeUri) {
        return DT(IRI(datatypeUri));
    }
    public static Datatype DT(IRI datatypeIri) {
        return Datatype.create(datatypeIri);
    }
    public static DatatypeVariable DTV(String datatypeVarName) {
        return DatatypeVariable.create(datatypeVarName);
    }
    public static DatatypeVariable DTV(UntypedVariable datatypeVar) {
        return DatatypeVariable.create(datatypeVar.toString());
    }
    public static ObjectProperty OP(String objectPropertyName) {
        return OP(IRI(objectPropertyName));
    }
    public static ObjectProperty OP(IRI objectPropertyIri) {
        return ObjectProperty.create(objectPropertyIri);
    }
    public static ObjectInverseOf IOP(String objectPropertyName) {
        return IOP(IRI(objectPropertyName));
    }
    public static ObjectInverseOf IOPV(String objectPropertyVarName) {
        return IOP(OPV(objectPropertyVarName));
    }
    public static ObjectInverseOf IOP(IRI objectPropertyIri) {
        return IOP(OP(objectPropertyIri));
    }
    public static ObjectInverseOf IOP(ObjectPropertyExpression objectProperty) {
        return ObjectInverseOf.create(objectProperty);
    }
    public static ObjectPropertyVariable OPV(String objectPropertyVarName) {
        return ObjectPropertyVariable.create(objectPropertyVarName);
    }
    public static DataProperty DP(String dataPropertyName) {
        return DP(IRI(dataPropertyName));
    }
    public static DataProperty DP(IRI dataPropertyIri) {
        return DataProperty.create(dataPropertyIri);
    }
    public static DataPropertyVariable DPV(String dataPropertyVarName) {
        return DataPropertyVariable.create(dataPropertyVarName);
    }
    public static AnnotationProperty AP(String annotationPropertyName) {
        return AP(IRI(annotationPropertyName));
    }
    public static AnnotationProperty AP(IRI annotationPropertyIri) {
        return AnnotationProperty.create(annotationPropertyIri);
    }
    public static Annotation ANN(AnnotationPropertyExpression ap, AnnotationValue av, Annotation... annotations) {
        return Annotation.create(ap,av,annotations);
    }
    public static AnnotationPropertyVariable APV(String annotationPropertyVarName) {
        return AnnotationPropertyVariable.create(annotationPropertyVarName);
    }
    public static NamedIndividual NI(String individualName) {
        return NI(IRI(individualName));
    }
    public static NamedIndividual NI(IRI individualIri) {
        return NamedIndividual.create(individualIri);
    }
    public static IndividualVariable IV(String individualVarName) {
        return IndividualVariable.create(individualVarName);
    }
    public static AnonymousIndividual AI(String label) {
        return AnonymousIndividual.create(label);
    }
    public static LiteralVariable LV(String varName) {
        return LiteralVariable.create(varName);
    }
    public static TypedLiteral TL(String lexicalForm) {
        return TL(lexicalForm,(String)null,(Datatype)null);
    }
    public static TypedLiteral TL(String lexicalForm, String langTag) {
        return TL(lexicalForm, langTag, (Datatype)null);
    }
    public static TypedLiteral TL(String lexicalForm, String langTag, String datatypeUri) {
        return TL(lexicalForm, langTag, DT(datatypeUri));
    }
    public static TypedLiteral TL(String lexicalForm, String langTag, IRI datatypeIri) {
        return TL(lexicalForm, langTag, DT(datatypeIri));
    }
    public static TypedLiteral TL(String lexicalForm, String langTag, Datatype datatype) {
        return TypedLiteral.create(lexicalForm, langTag, datatype);
    }
//    public static <O> Set<O> SET(O... objects) {
//        Set<O> result=new HashSet<O>();
//        for (O object : objects)
//            result.add(object);
//        return result;
//    }
    public static Set<DataPropertyExpression> SET(DataPropertyExpression... dataProperties) {
        Set<DataPropertyExpression> result=new HashSet<DataPropertyExpression>();
        for (DataPropertyExpression dp : dataProperties)
            result.add(dp);
        return result;
    }
    public static Set<ObjectPropertyExpression> SET(ObjectPropertyExpression... objectProperties) {
        Set<ObjectPropertyExpression> result=new HashSet<ObjectPropertyExpression>();
        for (ObjectPropertyExpression op : objectProperties)
            result.add(op);
        return result;
    }
    public static Set<ClassExpression> SET(ClassExpression... classes) {
        Set<ClassExpression> result=new HashSet<ClassExpression>();
        for (ClassExpression cls : classes)
            result.add(cls);
        return result;
    }
    public static Set<Annotation> SET(Annotation... annotations) {
        Set<Annotation> result=new HashSet<Annotation>();
        for (Annotation annotation : annotations)
            result.add(annotation);
        return result;
    }
}
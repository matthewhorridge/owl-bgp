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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class TestClasses extends TestCase {
    
    protected static char[] chars={'i', 'r', 'i', '1'};
    protected static char[] charsVar={'v', 'a', 'r'};
    
    public TestClasses() {
        super();
    }
    public TestClasses(String name) {
        super(name);
    }
    public void testClassEquality() {
        String iriString1="http://www.example.org/iri1";
        String iriString2="http://www.example.org/"+new String(chars);
        IRI iri1=IRI.create(iriString1);
        IRI iri2=IRI.create(iriString2);
        assertFalse(iriString1==iriString2);
        assertTrue(iri1==iri2);
        Clazz class1=Clazz.create(iri1);
        Clazz class2=Clazz.create(iri2);
        assertTrue(class1==class2);
    }
    public void testClassVarEquality() {
        String iriString1="?var1";
        Integer one=Integer.parseInt("1");
        String iriString2="?"+new String(charsVar)+one.toString();
        assertFalse(iriString1==iriString2);
        ClassVariable classVar1=ClassVariable.create(iriString1);
        ClassVariable classVar2=ClassVariable.create(iriString2);
        assertTrue(classVar1==classVar2);
    }
    public void testIntersection() {
        String iriString1="http://www.example.org/"+new String(chars);
        String iriString2="http://www.example.org/iri2";
        IRI iri1=IRI.create(iriString1);
        IRI iri2=IRI.create(iriString2);
        Clazz class1a=Clazz.create(iri1);
        Clazz class1b=Clazz.create(iri2);
        Clazz class2a=Clazz.create(iri1);
        Clazz class2b=Clazz.create(iri2);
        Set<ClassExpression> inter1=new HashSet<ClassExpression>();
        inter1.add(class1a);
        inter1.add(class1b);
        Set<ClassExpression> inter2=new HashSet<ClassExpression>();
        inter2.add(class2a);
        inter2.add(class2b);
        ObjectIntersectionOf intersection1=ObjectIntersectionOf.create(inter1);
        ObjectIntersectionOf intersection2=ObjectIntersectionOf.create(inter2);
        assertTrue(intersection1==intersection2);
    }
    public void testDataIntersection() {
        String iriString1="http://www.example.org/"+new String(chars);
        String iriString2="http://www.example.org/iri2";
        IRI iri1=IRI.create(iriString1);
        IRI iri2=IRI.create(iriString2);
        DataRange class1a=Datatype.create(iri1);
        DataRange class1b=Datatype.create(iri2);
        DataRange class2a=Datatype.create(iri2);
        DataRange class2b=Datatype.create(iri1);
        DataRange intersection1=DataIntersectionOf.create(class1a, class1b);
        DataRange intersection2=DataIntersectionOf.create(class2a, class2b);
        assertTrue(intersection1==intersection2);
    }
    public void testIntersectionMultipleVars() {
        String classIRIString="http://www.example.org/"+new String(chars);
        String opIRIString="http://www.example.org/iri2";
        IRI classIRI=IRI.create(classIRIString);
        IRI opIRI=IRI.create(opIRIString);
        ClassExpression classVar=ClassVariable.create("?x");
        ObjectPropertyExpression opVar=ObjectPropertyVariable.create("?y");
        ClassExpression clazz=Clazz.create(classIRI);
        ClassExpression or=ObjectUnionOf.create(clazz, classVar);
        ClassExpression some=ObjectSomeValuesFrom.create(opVar, or);
        ClassExpression and=ObjectIntersectionOf.create(clazz, classVar);
        ObjectPropertyExpression op=ObjectProperty.create(opIRI);
        ClassExpression all=ObjectAllValuesFrom.create(op, and);
        ClassExpression ce=ObjectIntersectionOf.create(all, some);
        Set<Variable> vars=ce.getVariablesInSignature();
        Set<Variable> classVars=ce.getVariablesInSignature(VarType.CLASS);
        Set<Variable> opVars=ce.getVariablesInSignature(VarType.OBJECT_PROPERTY);
        assertTrue(vars.size()==2);
        assertTrue(classVars.size()==1);
        assertTrue(opVars.size()==1);
        assertTrue(vars.contains(classVar));
        assertTrue(vars.contains(opVar));
        assertTrue(classVars.contains(classVar));
        assertTrue(opVars.contains(opVar));
    }
    public void testApplyMultipleBindings() {
        ClassVariable classVar1=ClassVariable.create("?x");
        ClassVariable classVar2=ClassVariable.create("?y");
        ClassVariable classVar3=ClassVariable.create("?z");
        ClassExpression or=ObjectUnionOf.create(classVar1, classVar2, classVar3);
        Map<Variable,Set<? extends Atomic>> varToBindingSets=new HashMap<Variable, Set<? extends Atomic>>();
        Set<Atomic> bindingsX=new HashSet<Atomic>();
        bindingsX.add(Clazz.create("http://example.org/a"));
        bindingsX.add(Clazz.create("http://example.org/b"));
        bindingsX.add(Clazz.create("http://example.org/c"));
        varToBindingSets.put(classVar1, bindingsX);
        Set<Atomic> bindingsY=new HashSet<Atomic>();
        bindingsY.add(Clazz.create("http://example.org/k"));
        bindingsY.add(Clazz.create("http://example.org/l"));
        varToBindingSets.put(classVar2, bindingsY);
        Set<Atomic> bindingsZ=new HashSet<Atomic>();
        bindingsZ.add(Clazz.create("http://example.org/r"));
        bindingsZ.add(Clazz.create("http://example.org/q"));
        bindingsZ.add(Clazz.create("http://example.org/s"));
        varToBindingSets.put(classVar3, bindingsZ);
        int i=0;   
        for (Map<Variable,? extends Atomic> binding : new BindingIterator(varToBindingSets)) {
            ExtendedOWLObject boundOr=or.getBoundVersion(binding);
            assertTrue(boundOr.getVariablesInSignature().size()==0);
            i++;
        }
        assertTrue(i==(3*3*2));
    }
    public void testIntersectionApplyBindings() {
        String classIRI="http://www.example.org/"+new String(chars);
        String opIRI="http://www.example.org/iri2";
        ClassVariable classVar=ClassVariable.create("?x");
        ObjectPropertyVariable opVar=ObjectPropertyVariable.create("?y");
        ClassExpression clazz=Clazz.create(classIRI);
        ClassExpression or=ObjectUnionOf.create(clazz, classVar);
        ClassExpression some=ObjectSomeValuesFrom.create(opVar, or);
        ClassExpression and=ObjectIntersectionOf.create(clazz, classVar);
        ObjectPropertyExpression op=ObjectProperty.create(opIRI);
        ClassExpression all=ObjectAllValuesFrom.create(op, and);
        ClassExpression ce=ObjectIntersectionOf.create(all, some);
        Map<Variable,Atomic> varsToBindings=new HashMap<Variable, Atomic>();
        varsToBindings.put(classVar, Clazz.create("http://www.example.org/CBind"));
        varsToBindings.put(opVar, ObjectProperty.create("http://www.example.org/OPBind"));
        ExtendedOWLObject ceBound=ce.getBoundVersion(varsToBindings);
        assertTrue(ceBound.getVariablesInSignature(VarType.CLASS).size()==0);
        assertTrue(ceBound.getVariablesInSignature(VarType.OBJECT_PROPERTY).size()==0);
        assertTrue(ceBound.getVariablesInSignature().size()==0);
        OWLObject owlCe=ceBound.asOWLAPIObject(OWLManager.getOWLDataFactory());
        assertTrue(owlCe instanceof OWLObjectIntersectionOf);
        assertTrue(((OWLObjectIntersectionOf)owlCe).getOperands().size()==2);
    }
}
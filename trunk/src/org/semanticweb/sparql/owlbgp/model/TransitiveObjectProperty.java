/* Copyright 2010 by the Oxford University Computing Laboratory
   
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
   along with OWL-BGP.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.semanticweb.sparql.owlbgp.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;


public class TransitiveObjectProperty extends AbstractAxiom implements ObjectPropertyAxiom {
    private static final long serialVersionUID = -4494626513381281993L;

    protected static InterningManager<TransitiveObjectProperty> s_interningManager=new InterningManager<TransitiveObjectProperty>() {
        protected boolean equal(TransitiveObjectProperty object1,TransitiveObjectProperty object2) {
            if (object1.m_ope==object2.m_ope
                    ||object1.m_annotations.size()!=object2.m_annotations.size())
                return false;
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            } 
            return true;
        }
        protected boolean contains(Annotation annotation,Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno==annotation)
                    return true;
            return false;
        }
        protected int getHashCode(TransitiveObjectProperty object) {
            int hashCode=7*object.m_ope.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode+=anno.hashCode();
            return hashCode; 
        }
    };
    
    protected final ObjectPropertyExpression m_ope;
   
    protected TransitiveObjectProperty(ObjectPropertyExpression objectPropertyExpression,Set<Annotation> annotations) {
        m_ope=objectPropertyExpression;
        m_annotations=annotations;
    }
    public ObjectPropertyExpression getObjectPropertyExpression() {
        return m_ope;
    }
    public String toString(Prefixes prefixes) {
        StringBuffer buffer=new StringBuffer();
        buffer.append("TransitiveObjectProperty(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_ope.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }
    public static TransitiveObjectProperty create(ObjectPropertyExpression objectPropertyExpression) {
        return TransitiveObjectProperty.create(objectPropertyExpression,new HashSet<Annotation>());
    }
    public static TransitiveObjectProperty create(ObjectPropertyExpression objectPropertyExpression,Set<Annotation> annotations) {
        return s_interningManager.intern(new TransitiveObjectProperty(objectPropertyExpression,annotations));
    }
    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }
    protected OWLObject convertToOWLAPIObject(OWLAPIConverter converter) {
        return converter.visit(this);
    }
    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables=new HashSet<Variable>();
        variables.addAll(m_ope.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }
    public Set<Variable> getUnboundVariablesInSignature(VarType varType) {
        Set<Variable> unbound=new HashSet<Variable>();
        unbound.addAll(m_ope.getUnboundVariablesInSignature(varType));
        getUnboundAnnotationVariables(varType, unbound);
        return unbound;
    }
    public void applyBindings(Map<Variable,Atomic> variablesToBindings) {
        m_ope.applyBindings(variablesToBindings);
    }
    public Axiom getAxiomWithoutAnnotations() {
        return TransitiveObjectProperty.create(m_ope);
    }
}

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


public class InverseObjectProperties extends AbstractAxiom implements ObjectPropertyAxiom {
    private static final long serialVersionUID = -4739651270386976693L;

    protected static InterningManager<InverseObjectProperties> s_interningManager=new InterningManager<InverseObjectProperties>() {
        protected boolean equal(InverseObjectProperties object1,InverseObjectProperties object2) {
            if (object1.m_ope!=object2.m_ope
                    ||object1.m_inverseOpe!=object2.m_inverseOpe
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
        protected int getHashCode(InverseObjectProperties object) {
            int hashCode=7*object.m_ope.hashCode()+11*object.m_inverseOpe.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode+=anno.hashCode();
            return hashCode;
        }
    };
    
    protected final ObjectPropertyExpression m_ope;
    protected final ObjectPropertyExpression m_inverseOpe;
    
    protected InverseObjectProperties(ObjectPropertyExpression ope,ObjectPropertyExpression inverseOpe,Set<Annotation> annotations) {
        m_ope=ope;
        m_inverseOpe=inverseOpe;
        m_annotations=annotations;
    }
    public ObjectPropertyExpression getObjectPropertyExpression() {
        return m_ope;
    }
    public ObjectPropertyExpression getInverseObjectPropertyExpression() {
        return m_inverseOpe;
    }
    public String toString(Prefixes prefixes) {
        StringBuffer buffer=new StringBuffer();
        buffer.append("InverseObjectProperties(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_ope.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_inverseOpe.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }
    public static InverseObjectProperties create(ObjectPropertyExpression objectPropertyExpression, ObjectPropertyExpression inverseObjectPropertyExpression) {
        return InverseObjectProperties.create(objectPropertyExpression,inverseObjectPropertyExpression,new HashSet<Annotation>());
    }
    public static InverseObjectProperties create(ObjectPropertyExpression objectPropertyExpression, ObjectPropertyExpression inverseObjectPropertyExpression,Set<Annotation> annotations) {
        return s_interningManager.intern(new InverseObjectProperties(objectPropertyExpression,inverseObjectPropertyExpression,annotations));
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
        variables.addAll(m_inverseOpe.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }
    public Set<Variable> getUnboundVariablesInSignature(VarType varType) {
        Set<Variable> unbound=new HashSet<Variable>();
        unbound.addAll(m_ope.getUnboundVariablesInSignature(varType));
        unbound.addAll(m_inverseOpe.getUnboundVariablesInSignature(varType));
        getUnboundAnnotationVariables(varType, unbound);
        return unbound;
    }
    public void applyBindings(Map<Variable,Atomic> variablesToBindings) {
        m_ope.applyBindings(variablesToBindings);
        m_inverseOpe.applyBindings(variablesToBindings);
    }
    public Axiom getAxiomWithoutAnnotations() {
        return InverseObjectProperties.create(m_ope, m_inverseOpe);
    }
}

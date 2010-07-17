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

public class Annotation extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = -4586686325214553112L;

    protected static InterningManager<Annotation> s_interningManager=new InterningManager<Annotation>() {
        protected boolean equal(Annotation object1,Annotation object2) {
            return object1.m_annotationProperty==object2.m_annotationProperty && object1.m_annotationValue==object2.m_annotationValue;
        }
        protected int getHashCode(Annotation object) {
            return 7*object.m_annotationProperty.hashCode()+13*object.m_annotationValue.hashCode();
        }
    };
    
    protected final AnnotationPropertyExpression m_annotationProperty;
    protected final AnnotationValue m_annotationValue;
    protected final Set<Annotation> m_annotations;
    
    protected Annotation(AnnotationPropertyExpression annotationProperty,AnnotationValue annotationValue,Set<Annotation> annotations) {
        m_annotationProperty=annotationProperty;
        m_annotationValue=annotationValue;
        m_annotations=annotations;
    }
    public AnnotationPropertyExpression getAnnotationProperty() {
        return m_annotationProperty;
    }
    public AnnotationValue getAnnotationValue() {
        return m_annotationValue;
    }
    public Set<Annotation> getAnnotations() {
        return m_annotations;
    }
    public String toString(Prefixes prefixes) {
        StringBuffer sb=new StringBuffer();
        sb.append("Annotation(");
        boolean notFirst=false;
        for (Annotation anno : m_annotations) {
            if (notFirst) sb.append(" ");
            else notFirst=true;
            sb.append(anno.toString(prefixes));
        }
        sb.append(m_annotationProperty.toString(prefixes));
        sb.append(" ");
        sb.append(m_annotationValue.toString(prefixes));
        sb.append(")");
        return sb.toString();
    }
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }
    public static Annotation create(AnnotationPropertyExpression annotationProperty,AnnotationValue annotationValue) {
        return Annotation.create(annotationProperty,annotationValue,new HashSet<Annotation>());
    }
    public static Annotation create(AnnotationPropertyExpression annotationProperty,AnnotationValue annotationValue,Set<Annotation> annotations) {
        return s_interningManager.intern(new Annotation(annotationProperty,annotationValue,annotations));
    }
    public Identifier getIdentifier() {
        return null;
    }
    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }
    protected OWLObject convertToOWLAPIObject(OWLAPIConverter converter) {
        return converter.visit(this);
    }
    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables=new HashSet<Variable>();
        variables.addAll(m_annotationProperty.getVariablesInSignature(varType));
        variables.addAll(m_annotationValue.getVariablesInSignature(varType));
        return variables;
    }
    public Set<Variable> getUnboundVariablesInSignature(VarType varType) {
        Set<Variable> unbound=new HashSet<Variable>();
        unbound.addAll(m_annotationProperty.getUnboundVariablesInSignature(varType));
        unbound.addAll(m_annotationValue.getUnboundVariablesInSignature(varType));
        return unbound;
    }
    public void applyBindings(Map<Variable,Atomic> variablesToBindings) {
        m_annotationProperty.applyBindings(variablesToBindings);
        m_annotationValue.applyBindings(variablesToBindings);
    }
}

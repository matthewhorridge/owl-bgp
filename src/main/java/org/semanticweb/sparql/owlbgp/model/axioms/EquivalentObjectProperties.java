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

package org.semanticweb.sparql.owlbgp.model.axioms;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.AxiomVisitor;
import org.semanticweb.sparql.owlbgp.model.AxiomVisitorEx;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class EquivalentObjectProperties extends AbstractAxiom {
    private static final long serialVersionUID = -5653341898298818446L;

    protected static InterningManager<EquivalentObjectProperties> s_interningManager = new InterningManager<EquivalentObjectProperties>() {
        protected boolean equal(EquivalentObjectProperties object1, EquivalentObjectProperties object2) {
            if (object1.m_objectPropertyExpressions.size() != object2.m_objectPropertyExpressions.size()
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (ObjectPropertyExpression ope : object1.m_objectPropertyExpressions) {
                if (!contains(ope, object2.m_objectPropertyExpressions))
                    return false;
            }
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(ObjectPropertyExpression ope, Set<ObjectPropertyExpression> opes) {
            for (ObjectPropertyExpression equiv : opes)
                if (equiv == ope)
                    return true;
            return false;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(EquivalentObjectProperties object) {
            int hashCode = 0;
            for (ObjectPropertyExpression equiv : object.m_objectPropertyExpressions)
                hashCode += equiv.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final Set<ObjectPropertyExpression> m_objectPropertyExpressions;

    protected EquivalentObjectProperties(Set<ObjectPropertyExpression> objectPropertyExpressions, Set<Annotation> annotations) {
        super(annotations);
        m_objectPropertyExpressions = Collections.unmodifiableSet(objectPropertyExpressions);
    }

    public Set<ObjectPropertyExpression> getObjectPropertyExpressions() {
        return m_objectPropertyExpressions;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("EquivalentObjectProperties(");
        writeAnnoations(buffer, prefixes);
        boolean notFirst = false;
        for (ObjectPropertyExpression equiv : m_objectPropertyExpressions) {
            if (notFirst)
                buffer.append(' ');
            else
                notFirst = true;
            buffer.append(equiv.toString(prefixes));
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        ObjectPropertyExpression[] objectPropertyExpressions = m_objectPropertyExpressions.toArray(new ObjectPropertyExpression[0]);
        Identifier[] objectPropertyIDs = new Identifier[objectPropertyExpressions.length];
        for (int i = 0; i < objectPropertyExpressions.length; i++) {
            if (objectPropertyExpressions[i] instanceof Atomic)
                objectPropertyIDs[i] = (Atomic) objectPropertyExpressions[i];
            else
                objectPropertyIDs[i] = AbstractExtendedOWLObject.getNextBlankNode();
        }
        for (int i = 0; i < objectPropertyIDs.length - 1; i++)
            buffer.append(writeSingleMainTripleAxiom(prefixes, objectPropertyIDs[i], Vocabulary.OWL_EQUIVALENT_PROPERTY, objectPropertyIDs[i + 1], m_annotations));
        for (int i = 0; i < objectPropertyExpressions.length; i++)
            if (!(objectPropertyExpressions[i] instanceof Atomic))
                buffer.append(objectPropertyExpressions[i].toTurtleString(prefixes, objectPropertyIDs[i]));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static EquivalentObjectProperties create(Set<ObjectPropertyExpression> objectPropertyExpressions) {
        return create(objectPropertyExpressions, new HashSet<Annotation>());
    }

    public static EquivalentObjectProperties create(ObjectPropertyExpression objectPropertyExpression1, ObjectPropertyExpression objectPropertyExpression2, Annotation... annotations) {
        return create(objectPropertyExpression1, objectPropertyExpression2, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static EquivalentObjectProperties create(ObjectPropertyExpression objectPropertyExpression1, ObjectPropertyExpression objectPropertyExpression2, Set<Annotation> annotations) {
        Set<ObjectPropertyExpression> equivs = new HashSet<ObjectPropertyExpression>();
        equivs.add(objectPropertyExpression1);
        equivs.add(objectPropertyExpression2);
        return create(equivs, annotations);
    }

    public static EquivalentObjectProperties create(Set<ObjectPropertyExpression> objectPropertyExpressions, Set<Annotation> annotations) {
        return s_interningManager.intern(new EquivalentObjectProperties(objectPropertyExpressions, annotations));
    }

    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(AxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(AxiomVisitor visitor) {
        visitor.visit(this);
    }

    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        for (ObjectPropertyExpression ope : m_objectPropertyExpressions)
            variables.addAll(ope.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<ObjectPropertyExpression> objectPropertyExpression = new HashSet<ObjectPropertyExpression>();
        for (ObjectPropertyExpression ope : m_objectPropertyExpressions) {
            objectPropertyExpression.add((ObjectPropertyExpression) ope.getBoundVersion(variablesToBindings));
        }
        return create(objectPropertyExpression, getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_objectPropertyExpressions);
    }
}

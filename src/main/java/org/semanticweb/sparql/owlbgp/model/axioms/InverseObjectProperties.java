/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of the OWL-BGP project.

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


package org.semanticweb.sparql.owlbgp.model.axioms;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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


public class InverseObjectProperties extends AbstractAxiom {
    private static final long serialVersionUID = -4739651270386976693L;

    protected static InterningManager<InverseObjectProperties> s_interningManager = new InterningManager<InverseObjectProperties>() {
        protected boolean equal(InverseObjectProperties object1, InverseObjectProperties object2) {
            if (object1.m_opes.size() != object2.m_opes.size()
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (ObjectPropertyExpression ope : object1.m_opes) {
                if (!contains(ope, object2.m_opes))
                    return false;
            }
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(ObjectPropertyExpression ope, Set<ObjectPropertyExpression> opes) {
            for (ObjectPropertyExpression op : opes)
                if (op == ope)
                    return true;
            return false;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(InverseObjectProperties object) {
            int hashCode = 97;
            for (ObjectPropertyExpression ope : object.m_opes)
                hashCode += ope.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final Set<ObjectPropertyExpression> m_opes;

    protected InverseObjectProperties(ObjectPropertyExpression ope, ObjectPropertyExpression inverseOpe, Set<Annotation> annotations) {
        super(annotations);
        Set<ObjectPropertyExpression> opes = new HashSet<ObjectPropertyExpression>();
        opes.add(ope);
        opes.add(inverseOpe);
        m_opes = Collections.unmodifiableSet(opes);
    }

    public Set<ObjectPropertyExpression> getObjectPropertyExpressions() {
        return m_opes;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("InverseObjectProperties(");
        writeAnnoations(buffer, prefixes);
        boolean notFirst = false;
        for (ObjectPropertyExpression ope : m_opes) {
            if (notFirst) buffer.append(" ");
            notFirst = true;
            buffer.append(ope.toString(prefixes));
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        assert m_opes.size() == 2;
        Iterator<ObjectPropertyExpression> it = m_opes.iterator();
        ObjectPropertyExpression ope1 = it.next();
        ObjectPropertyExpression ope2 = it.next();
        Identifier subject;
        if (!(ope1 instanceof Atomic)) {
            subject = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(ope1.toTurtleString(prefixes, subject));
        }
        else
            subject = (Atomic) ope1;
        Identifier object;
        if (!(ope2 instanceof Atomic)) {
            object = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(ope2.toTurtleString(prefixes, object));
        }
        else
            object = (Atomic) ope2;
        buffer.append(writeSingleMainTripleAxiom(prefixes, subject, Vocabulary.OWL_INVERSE_OF, object, m_annotations));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static InverseObjectProperties create(ObjectPropertyExpression objectPropertyExpression, ObjectPropertyExpression inverseObjectPropertyExpression) {
        return create(objectPropertyExpression, inverseObjectPropertyExpression, new HashSet<Annotation>());
    }

    public static InverseObjectProperties create(ObjectPropertyExpression objectPropertyExpression, ObjectPropertyExpression inverseObjectPropertyExpression, Annotation... annotations) {
        return create(objectPropertyExpression, inverseObjectPropertyExpression, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static InverseObjectProperties create(ObjectPropertyExpression objectPropertyExpression, ObjectPropertyExpression inverseObjectPropertyExpression, Set<Annotation> annotations) {
        return s_interningManager.intern(new InverseObjectProperties(objectPropertyExpression, inverseObjectPropertyExpression, annotations));
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
        for (ObjectPropertyExpression ope : m_opes)
            variables.addAll(ope.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        assert m_opes.size() == 2;
        Iterator<ObjectPropertyExpression> it = m_opes.iterator();
        ObjectPropertyExpression ope1 = it.next();
        ObjectPropertyExpression ope2 = it.next();
        return create((ObjectPropertyExpression) ope1.getBoundVersion(variablesToBindings), (ObjectPropertyExpression) ope2.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        assert m_opes.size() == 2;
        Iterator<ObjectPropertyExpression> it = m_opes.iterator();
        ObjectPropertyExpression ope1 = it.next();
        ObjectPropertyExpression ope2 = it.next();
        return create(ope1, ope2);
    }
}

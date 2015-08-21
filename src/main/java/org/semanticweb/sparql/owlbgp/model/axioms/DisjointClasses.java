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
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class DisjointClasses extends AbstractAxiom implements ClassAxiom {
    private static final long serialVersionUID = -2257083759606842880L;

    protected static InterningManager<DisjointClasses> s_interningManager = new InterningManager<DisjointClasses>() {
        protected boolean equal(DisjointClasses classes1, DisjointClasses classes2) {
            if (classes1.m_annotations.size() != classes2.m_annotations.size() || classes1.m_classExpressions.size() != classes2.m_classExpressions.size())
                return false;
            for (ClassExpression equiv : classes1.m_classExpressions) {
                if (!contains(equiv, classes2.m_classExpressions))
                    return false;
            }
            for (Annotation anno : classes1.m_annotations) {
                if (!contains(anno, classes2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(ClassExpression classExpression, Set<ClassExpression> classExpressions) {
            for (ClassExpression equiv : classExpressions)
                if (equiv == classExpression)
                    return true;
            return false;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(DisjointClasses classes) {
            int hashCode = 0;
            for (ClassExpression equiv : classes.m_classExpressions)
                hashCode += equiv.hashCode();
            for (Annotation anno : classes.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final Set<ClassExpression> m_classExpressions;

    protected DisjointClasses(Set<ClassExpression> classExpressions, Set<Annotation> annotations) {
        super(annotations);
        m_classExpressions = Collections.unmodifiableSet(classExpressions); //mutable
    }

    public Set<ClassExpression> getClassExpressions() {
        return m_classExpressions;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DisjointClasses(");
        writeAnnoations(buffer, prefixes);
        boolean notFirst = false;
        for (ClassExpression conjunct : m_classExpressions) {
            if (notFirst)
                buffer.append(' ');
            else
                notFirst = true;
            buffer.append(conjunct.toString(prefixes));
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        if (m_classExpressions.size() == 2) {
            Iterator<ClassExpression> it = m_classExpressions.iterator();
            ClassExpression class1 = it.next();
            ClassExpression class2 = it.next();
            Identifier subject;
            if (!(class1 instanceof Atomic)) {
                subject = AbstractExtendedOWLObject.getNextBlankNode();
                class1.toTurtleString(prefixes, subject);
            }
            else
                subject = (Atomic) class1;
            Identifier object;
            if (!(class2 instanceof Atomic)) {
                object = AbstractExtendedOWLObject.getNextBlankNode();
                class2.toTurtleString(prefixes, object);
            }
            else
                object = (Atomic) class2;
            return writeSingleMainTripleAxiom(prefixes, subject, Vocabulary.OWL_DISJOINT_WITH, object, m_annotations);
        }
        else {
            StringBuffer buffer = new StringBuffer();
            Identifier bnode = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(bnode);
            buffer.append(" ");
            buffer.append(Vocabulary.RDF_TYPE.toString(prefixes));
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_ALL_DISJOINT_CLASSES.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            ClassExpression[] classExpressions = m_classExpressions.toArray(new ClassExpression[0]);
            Identifier[] classIDs = new Identifier[classExpressions.length];
            for (int i = 0; i < classExpressions.length; i++) {
                if (classExpressions[i] instanceof Atomic)
                    classIDs[i] = (Atomic) classExpressions[i];
                else
                    classIDs[i] = AbstractExtendedOWLObject.getNextBlankNode();
            }
            buffer.append(bnode);
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_MEMBERS.toString(prefixes));
            printSequence(buffer, prefixes, null, classIDs);
            for (int i = 0; i < classExpressions.length; i++)
                if (!(classExpressions[i] instanceof Atomic))
                    buffer.append(classExpressions[i].toTurtleString(prefixes, classIDs[i]));
            for (Annotation anno : m_annotations)
                buffer.append(anno.toTurtleString(prefixes, bnode));
            return buffer.toString();
        }
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DisjointClasses create(Set<ClassExpression> classExpressions) {
        return create(classExpressions, new HashSet<Annotation>());
    }

    public static DisjointClasses create(ClassExpression... classExpressions) {
        return create(new HashSet<ClassExpression>(Arrays.asList(classExpressions)), new HashSet<Annotation>());
    }

    public static DisjointClasses create(Set<ClassExpression> classExpressions, Annotation... annotations) {
        return create(classExpressions, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static DisjointClasses create(Set<ClassExpression> classExpressions, Set<Annotation> annotations) {
        return s_interningManager.intern(new DisjointClasses(classExpressions, annotations));
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
        for (ClassExpression classExpression : m_classExpressions) {
            variables.addAll(classExpression.getVariablesInSignature(varType));
        }
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<ClassExpression> classExpressions = new HashSet<ClassExpression>();
        for (ClassExpression ce : m_classExpressions) {
            classExpressions.add((ClassExpression) ce.getBoundVersion(variablesToBindings));
        }
        return create(classExpressions, getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_classExpressions);
    }
}

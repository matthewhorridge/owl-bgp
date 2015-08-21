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
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class DisjointUnion extends AbstractAxiom implements ClassAxiom {
    private static final long serialVersionUID = 7822375000293094470L;

    protected static InterningManager<DisjointUnion> s_interningManager = new InterningManager<DisjointUnion>() {
        protected boolean equal(DisjointUnion object1, DisjointUnion object2) {
            if (object1.m_class != object2.m_class
                    || object1.m_classExpressions.size() != object2.m_classExpressions.size()
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (ClassExpression exp : object1.m_classExpressions) {
                if (!contains(exp, object2.m_classExpressions))
                    return false;
            }
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(ClassExpression classExpression, Set<ClassExpression> classExpressions) {
            for (ClassExpression exp : classExpressions)
                if (exp == classExpression)
                    return true;
            return false;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(DisjointUnion object) {
            int hashCode = 1113 + object.m_class.hashCode();
            for (ClassExpression exp : object.m_classExpressions)
                hashCode += exp.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final Atomic m_class;
    protected final Set<ClassExpression> m_classExpressions;

    protected DisjointUnion(Atomic clazz, Set<ClassExpression> classExpressions, Set<Annotation> annotations) {
        super(annotations);
        m_class = clazz;
        m_classExpressions = classExpressions;
    }

    public Atomic getClazz() {
        return m_class;
    }

    public Set<ClassExpression> getClassExpressions() {
        return m_classExpressions;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DisjointUnion(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_class.toString(prefixes));
        buffer.append(' ');
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
        Identifier listMainNode = AbstractExtendedOWLObject.getNextBlankNode();
        StringBuffer buffer = new StringBuffer();
        buffer.append(writeSingleMainTripleAxiom(prefixes, (Atomic) m_class, Vocabulary.OWL_DISJOINT_UNION_OF, listMainNode, m_annotations));
        Identifier[] listNodes = new Identifier[m_classExpressions.size()];
        ClassExpression[] classExpressions = m_classExpressions.toArray(new ClassExpression[0]);
        for (int i = 0; i < classExpressions.length; i++) {
            if (classExpressions[i] instanceof Atomic)
                listNodes[i] = ((Atomic) classExpressions[i]).getIdentifier();
            else
                listNodes[i] = AbstractExtendedOWLObject.getNextBlankNode();
        }
        printSequence(buffer, prefixes, listMainNode, listNodes);
        for (int i = 0; i < classExpressions.length; i++) {
            if (!(classExpressions[i] instanceof Atomic)) {
                buffer.append(classExpressions[i].toTurtleString(prefixes, listNodes[i]));
            }
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DisjointUnion create(Atomic clazz, Set<ClassExpression> classExpressions) {
        return create(clazz, classExpressions, new HashSet<Annotation>());
    }

    public static DisjointUnion create(Atomic clazz, ClassExpression... classExpressions) {
        return create(clazz, new HashSet<ClassExpression>(Arrays.asList(classExpressions)), new HashSet<Annotation>());
    }

    public static DisjointUnion create(Atomic clazz, Set<ClassExpression> classExpressions, Set<Annotation> annotations) {
        if (!(clazz instanceof Atomic))
            throw new IllegalArgumentException("A disjoint union axiom can only have a class or a class variable as first argument, but here we got " + clazz);
        return s_interningManager.intern(new DisjointUnion(clazz, classExpressions, annotations));
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
        variables.addAll(m_class.getVariablesInSignature(varType));
        for (ClassExpression classExpression : m_classExpressions)
            variables.addAll(classExpression.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<ClassExpression> classExpressions = new HashSet<ClassExpression>();
        for (ClassExpression ce : m_classExpressions) {
            classExpressions.add((ClassExpression) ce.getBoundVersion(variablesToBindings));
        }
        return create((Clazz) m_class.getBoundVersion(variablesToBindings), classExpressions, getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_class, m_classExpressions);
    }
}

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
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class SubClassOf extends AbstractAxiom implements ClassAxiom {
    private static final long serialVersionUID = 1535222085351189793L;

    protected static InterningManager<SubClassOf> s_interningManager = new InterningManager<SubClassOf>() {
        protected boolean equal(SubClassOf object1, SubClassOf object2) {
            if (object1.m_subClass != object2.m_subClass
                    || object1.m_superClass != object2.m_superClass
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(SubClassOf object) {
            int hashCode = 7 * object.m_subClass.hashCode() + 13 * object.m_superClass.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final ClassExpression m_subClass;
    protected final ClassExpression m_superClass;

    protected SubClassOf(ClassExpression subClass, ClassExpression superClass, Set<Annotation> annotations) {
        super(annotations);
        m_subClass = subClass;
        m_superClass = superClass;
    }

    public ClassExpression getSubClassExpression() {
        return m_subClass;
    }

    public ClassExpression getSuperClassExpression() {
        return m_superClass;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SubClassOf(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_subClass.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_superClass.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Identifier subject;
        if (!(m_subClass instanceof Atomic)) {
            subject = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(m_subClass.toTurtleString(prefixes, subject));
        }
        else
            subject = (Atomic) m_subClass;
        Identifier object;
        if (!(m_superClass instanceof Atomic)) {
            object = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(m_superClass.toTurtleString(prefixes, object));
        }
        else
            object = (Atomic) m_superClass;
        buffer.append(writeSingleMainTripleAxiom(prefixes, subject, Vocabulary.RDFS_SUBCLASS_OF, object, m_annotations));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static SubClassOf create(ClassExpression subClass, ClassExpression superClass) {
        return create(subClass, superClass, new HashSet<Annotation>());
    }

    public static SubClassOf create(ClassExpression subClass, ClassExpression superClass, Annotation... annotations) {
        return create(subClass, superClass, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static SubClassOf create(ClassExpression subClass, ClassExpression superClass, Set<Annotation> annotations) {
        return s_interningManager.intern(new SubClassOf(subClass, superClass, annotations));
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
        variables.addAll(m_subClass.getVariablesInSignature(varType));
        variables.addAll(m_superClass.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((ClassExpression) m_subClass.getBoundVersion(variablesToBindings), (ClassExpression) m_superClass.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_subClass, m_superClass);
    }
}

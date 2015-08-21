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
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class DataPropertyDomain extends AbstractAxiom {
    private static final long serialVersionUID = 7064189891149424128L;

    protected static InterningManager<DataPropertyDomain> s_interningManager = new InterningManager<DataPropertyDomain>() {
        protected boolean equal(DataPropertyDomain object1, DataPropertyDomain object2) {
            if (object1.m_dpe != object2.m_dpe
                    || object1.m_classExpression != object2.m_classExpression
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

        protected int getHashCode(DataPropertyDomain object) {
            int hashCode = 1017 + 133 * object.m_dpe.hashCode() + 5 * object.m_classExpression.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final DataPropertyExpression m_dpe;
    protected final ClassExpression m_classExpression;

    protected DataPropertyDomain(DataPropertyExpression dpe, ClassExpression classExpression, Set<Annotation> annotations) {
        super(annotations);
        m_dpe = dpe;
        m_classExpression = classExpression;
    }

    public DataPropertyExpression getDataPropertyExpression() {
        return m_dpe;
    }

    public ClassExpression getDomain() {
        return m_classExpression;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataPropertyDomain(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_dpe.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_classExpression.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Identifier object;
        if (!(m_classExpression instanceof Atomic)) {
            object = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(m_classExpression.toTurtleString(prefixes, object));
        }
        else
            object = (Atomic) m_classExpression;
        buffer.append(writeSingleMainTripleAxiom(prefixes, (Atomic) m_dpe, Vocabulary.RDFS_DOMAIN, object, m_annotations));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DataPropertyDomain create(DataPropertyExpression dpe, ClassExpression classExpression, Annotation... annotations) {
        return create(dpe, classExpression, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static DataPropertyDomain create(DataPropertyExpression dpe, ClassExpression classExpression, Set<Annotation> annotations) {
        return s_interningManager.intern(new DataPropertyDomain(dpe, classExpression, annotations));
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
        variables.addAll(m_dpe.getVariablesInSignature(varType));
        variables.addAll(m_classExpression.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((DataPropertyExpression) m_dpe.getBoundVersion(variablesToBindings), (ClassExpression) m_classExpression.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_dpe, m_classExpression);
    }
}

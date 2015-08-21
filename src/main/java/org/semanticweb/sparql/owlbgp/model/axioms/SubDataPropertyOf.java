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
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class SubDataPropertyOf extends AbstractAxiom {
    private static final long serialVersionUID = 7386154464790495292L;

    protected static InterningManager<SubDataPropertyOf> s_interningManager = new InterningManager<SubDataPropertyOf>() {
        protected boolean equal(SubDataPropertyOf object1, SubDataPropertyOf object2) {
            if (object1.m_subdpe != object2.m_subdpe
                    || object1.m_superdpe != object2.m_superdpe
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

        protected int getHashCode(SubDataPropertyOf object) {
            int hashCode = 19 * object.m_subdpe.hashCode() + 7 * object.m_superdpe.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final DataPropertyExpression m_subdpe;
    protected final DataPropertyExpression m_superdpe;

    protected SubDataPropertyOf(DataPropertyExpression subDataPropertyExpression, DataPropertyExpression superDataPropertyExpression, Set<Annotation> annotations) {
        super(annotations);
        m_subdpe = subDataPropertyExpression;
        m_superdpe = superDataPropertyExpression;
    }

    public DataPropertyExpression getSubDataPropertyExpression() {
        return m_subdpe;
    }

    public DataPropertyExpression getSuperDataPropertyExpression() {
        return m_superdpe;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SubDataPropertyOf(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_subdpe.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_superdpe.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return writeSingleMainTripleAxiom(prefixes, (Atomic) m_subdpe, Vocabulary.RDFS_SUB_PROPERTY_OF, (Atomic) m_superdpe, m_annotations);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static SubDataPropertyOf create(DataPropertyExpression subDataPropertyExpression, DataPropertyExpression superDataPropertyExpression) {
        return create(subDataPropertyExpression, superDataPropertyExpression, new HashSet<Annotation>());
    }

    public static SubDataPropertyOf create(DataPropertyExpression subDataPropertyExpression, DataPropertyExpression superDataPropertyExpression, Annotation... annotations) {
        return create(subDataPropertyExpression, superDataPropertyExpression, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static SubDataPropertyOf create(DataPropertyExpression subDataPropertyExpression, DataPropertyExpression superDataPropertyExpression, Set<Annotation> annotations) {
        return s_interningManager.intern(new SubDataPropertyOf(subDataPropertyExpression, superDataPropertyExpression, annotations));
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
        variables.addAll(m_subdpe.getVariablesInSignature(varType));
        variables.addAll(m_superdpe.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((DataProperty) m_subdpe.getBoundVersion(variablesToBindings), (DataProperty) m_superdpe.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_subdpe, m_superdpe);
    }
}

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
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class DatatypeDefinition extends AbstractAxiom {
    private static final long serialVersionUID = -3515748752748732664L;

    protected static InterningManager<DatatypeDefinition> s_interningManager = new InterningManager<DatatypeDefinition>() {
        protected boolean equal(DatatypeDefinition object1, DatatypeDefinition object2) {
            if (object1.m_datatype != object2.m_datatype
                    || object1.m_dataRange != object2.m_dataRange
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

        protected int getHashCode(DatatypeDefinition object) {
            int hashCode = 27 + 11 * object.m_datatype.hashCode() + 37 * object.m_dataRange.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final Atomic m_datatype;
    protected final DataRange m_dataRange;

    protected DatatypeDefinition(Datatype datatype, DataRange dataRange, Set<Annotation> annotations) {
        super(annotations);
        m_datatype = datatype;
        m_dataRange = dataRange;
    }

    protected DatatypeDefinition(DatatypeVariable datatype, DataRange dataRange, Set<Annotation> annotations) {
        super(annotations);
        m_datatype = datatype;
        m_dataRange = dataRange;
    }

    public Atomic getDatatype() {
        return m_datatype;
    }

    public DataRange getDataRange() {
        return m_dataRange;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DatatypeDefinition(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_datatype.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_dataRange.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Identifier object;
        if (m_dataRange instanceof Atomic)
            object = (Atomic) m_dataRange;
        else {
            object = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(m_dataRange.toTurtleString(prefixes, object));
        }
        buffer.append(writeSingleMainTripleAxiom(prefixes, (Atomic) m_datatype, Vocabulary.OWL_EQUIVALENT_CLASS, object, m_annotations));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DatatypeDefinition create(Datatype datatype, DataRange dataRange) {
        return create(datatype, dataRange, new HashSet<Annotation>());
    }

    public static DatatypeDefinition create(DatatypeVariable datatype, DataRange dataRange) {
        return create(datatype, dataRange, new HashSet<Annotation>());
    }

    public static DatatypeDefinition create(Datatype datatype, DataRange dataRange, Annotation... annotations) {
        return create(datatype, dataRange, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static DatatypeDefinition create(DatatypeVariable datatype, DataRange dataRange, Annotation... annotations) {
        return create(datatype, dataRange, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static DatatypeDefinition create(Datatype datatype, DataRange dataRange, Set<Annotation> annotations) {
        return s_interningManager.intern(new DatatypeDefinition(datatype, dataRange, annotations));
    }

    public static DatatypeDefinition create(DatatypeVariable datatype, DataRange dataRange, Set<Annotation> annotations) {
        return s_interningManager.intern(new DatatypeDefinition(datatype, dataRange, annotations));
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
        variables.addAll(m_datatype.getVariablesInSignature(varType));
        variables.addAll(m_dataRange.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((Datatype) m_datatype.getBoundVersion(variablesToBindings), (DataRange) m_dataRange.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        if (m_datatype instanceof Datatype)
            return create((Datatype) m_datatype, m_dataRange);
        else
            return create((DatatypeVariable) m_datatype, m_dataRange);
    }
}

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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public abstract class AbstractAxiom extends AbstractExtendedOWLObject implements Axiom {
    private static final long serialVersionUID = -1131983400634422022L;

    protected final Set<Annotation> m_annotations;

    protected AbstractAxiom(Set<Annotation> annotations) {
        m_annotations = Collections.unmodifiableSet(annotations == null ? new HashSet<Annotation>() : annotations); //mutable
    }

    public Identifier getIdentifier() {
        return null;
    }

    protected Set<Annotation> getBoundAnnotations(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<Annotation> annotations = new HashSet<Annotation>();
        for (Annotation annotation : m_annotations)
            annotations.add((Annotation) annotation.getBoundVersion(variablesToBindings));
        return annotations;
    }

    protected void writeAnnoations(StringBuffer buffer, Prefixes prefixes) {
        for (Annotation annotation : m_annotations) {
            buffer.append(annotation.toString(prefixes));
            buffer.append(" ");
        }
    }

    protected void writeTurtleAnnoations(StringBuffer buffer, Prefixes prefixes, Identifier mainNode) {
        for (Annotation annotation : m_annotations) {
            buffer.append(annotation.toTurtleString(prefixes, mainNode));
//            buffer.append(LB);
        }
    }

    protected String writeSingleMainTripleAxiom(Prefixes prefixes, Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(subject.toString(prefixes));
        buffer.append(" ");
        buffer.append(predicate.toString(prefixes));
        buffer.append(" ");
        buffer.append(object.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        if (!m_annotations.isEmpty()) {
            AnonymousIndividual bnode = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(bnode);
            buffer.append(" ");
            buffer.append(Vocabulary.RDF_TYPE.toString(prefixes));
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_AXIOM.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(bnode);
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_ANNOTATED_SOURCE.toString(prefixes));
            buffer.append(" ");
            buffer.append(subject.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(bnode);
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_ANNOTATED_PROPERTY.toString(prefixes));
            buffer.append(" ");
            buffer.append(predicate.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(bnode);
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_ANNOTATED_TARGET.toString(prefixes));
            buffer.append(" ");
            buffer.append(object.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            writeTurtleAnnoations(buffer, prefixes, bnode);
        }
        return buffer.toString();
    }

    protected Set<Variable> getAnnotationVariables(VarType varType, Set<Variable> variables) {
        for (Annotation annotation : m_annotations)
            variables.addAll(annotation.getVariablesInSignature(varType));
        return variables;
    }

    public Set<Annotation> getAnnotations() {
        return m_annotations;
    }
}

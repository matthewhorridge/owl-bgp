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


package org.semanticweb.sparql.owlbgp.model.individuals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.AnnotationSubject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;


public class AnonymousIndividual extends AbstractExtendedOWLObject implements Identifier, Individual, AnnotationSubject {
    private static final long serialVersionUID = -8797258383209941720L;

    protected static InterningManager<AnonymousIndividual> s_interningManager = new InterningManager<AnonymousIndividual>() {
        protected boolean equal(AnonymousIndividual object1, AnonymousIndividual object2) {
            return object1.m_nodeID == object2.m_nodeID;
        }

        protected int getHashCode(AnonymousIndividual object) {
            return object.m_nodeID.hashCode();
        }
    };

    protected final String m_nodeID;

    protected AnonymousIndividual(String nodeID) {
        m_nodeID = nodeID.intern();
    }

    public String getNodeID() {
        return m_nodeID;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("_:");
        buffer.append(m_nodeID);
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return toString(prefixes);
    }

    public Identifier getIdentifier() {
        return this;
    }

    public String getIdentifierString() {
        return m_nodeID;
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static AnonymousIndividual create(String nodeID) {
        if (nodeID.charAt(0) == '_' && nodeID.charAt(1) == ':') nodeID = nodeID.substring(2);
        return s_interningManager.intern(new AnonymousIndividual(nodeID));
    }

    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    public Set<Variable> getVariablesInSignature(VarType varType) {
        return new HashSet<Variable>();
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return this;
    }
}

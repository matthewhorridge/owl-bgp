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


package org.semanticweb.sparql.owlbgp.model.literals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;

public class TypedLiteral extends AbstractExtendedOWLObject implements Literal {
    private static final long serialVersionUID = -8503015611577734737L;

    protected static InterningManager<TypedLiteral> s_interningManager = new InterningManager<TypedLiteral>() {
        protected boolean equal(TypedLiteral object1, TypedLiteral object2) {
            return object1.m_lexicalForm == object2.m_lexicalForm && object1.m_langTag == object2.m_langTag && object1.m_dataDatatype == object2.m_dataDatatype;
        }

        protected int getHashCode(TypedLiteral object) {
            return object.m_lexicalForm.hashCode() + 1017 * object.m_langTag.hashCode() + 2013 * object.m_dataDatatype.hashCode();
        }
    };

    protected final String m_lexicalForm;
    protected final String m_langTag;
    protected final Datatype m_dataDatatype;

    protected TypedLiteral(String lexicalForm, String langTag, Datatype datatype) {
        m_lexicalForm = lexicalForm != null ? lexicalForm.intern() : "".intern();
        m_langTag = langTag != null ? langTag.intern() : "".intern();
        m_dataDatatype = datatype != null ? datatype : Datatype.RDF_PLAIN_LITERAL;
    }

    public String getLexicalForm() {
        return m_lexicalForm;
    }

    public String getLangTag() {
        return m_langTag;
    }

    public Datatype getDatatype() {
        return m_dataDatatype;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\"");
        buffer.append(m_lexicalForm);
        if (m_dataDatatype == Datatype.RDF_PLAIN_LITERAL) {
            buffer.append("@");
            buffer.append(m_langTag);
        }
        buffer.append("\"");
        buffer.append("^^");
        buffer.append(m_dataDatatype.toString(prefixes));
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return toString(prefixes);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static TypedLiteral create(String lexicalForm, String langTag, Datatype datatype) {
        return s_interningManager.intern(new TypedLiteral(lexicalForm, langTag, datatype));
    }

    public static TypedLiteral create(String literal) {
        String lexicalForm = null;
        String langTag = null;
        Datatype datatype = null;
        String noDatatype = null;
        int lastCircCircIndex = literal.lastIndexOf("^^");
        if (lastCircCircIndex >= 0) {
            datatype = Datatype.create(IRI.create(literal.substring(lastCircCircIndex)));
            noDatatype = literal.substring(1, lastCircCircIndex - 3);
        }
        else {
            datatype = Datatype.RDF_PLAIN_LITERAL;
            noDatatype = literal;
        }
        int lastAtIndex = noDatatype.lastIndexOf("@");
        if (lastAtIndex >= 0) {
            lexicalForm = noDatatype.substring(0, lastAtIndex - 1);
            langTag = noDatatype.substring(lastAtIndex + 1);
        }
        else {
            lexicalForm = noDatatype;
        }
        return create(lexicalForm, langTag, datatype);
    }

    @Override
    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    @Override
    public Set<Variable> getVariablesInSignature(VarType varType) {
        return new HashSet<Variable>();
    }

    @Override
    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return this;
    }

    public Identifier getIdentifier() {
        return this;
    }

    public String getIdentifierString() {
        return toString();
    }
}

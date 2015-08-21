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


package org.semanticweb.sparql.owlbgp.model.dataranges;

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

public class Datatype extends AbstractExtendedOWLObject implements DataRange, Atomic {
    private static final long serialVersionUID = -5589507335866233523L;

    protected static InterningManager<Datatype> s_interningManager = new InterningManager<Datatype>() {
        protected boolean equal(Datatype object1, Datatype object2) {
            return object1.m_iri == object2.m_iri;
        }

        protected int getHashCode(Datatype object) {
            return object.m_iri.hashCode();
        }
    };

    public static final Datatype RDFS_LITERAL = Datatype.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "Literal");
    public static final Datatype RDF_PLAIN_LITERAL = Datatype.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "PlainLiteral");
    public static final Datatype RDF_XML_LITERAL = Datatype.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "XMLLiteral");
    public static final Datatype OWL_REAL = Datatype.create(Prefixes.s_semanticWebPrefixes.get("owl") + "real");
    public static final Datatype OWL_RATIONAL = Datatype.create(Prefixes.s_semanticWebPrefixes.get("owl") + "rational");
    public static final Datatype XSD_STRING = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "string");
    public static final Datatype XSD_INTEGER = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "integer");
    public static final Datatype XSD_LONG = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "long");
    public static final Datatype XSD_INT = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "int");
    public static final Datatype XSD_SHORT = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "short");
    public static final Datatype XSD_BYTE = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "byte");
    public static final Datatype XSD_DECIMAL = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "decimal");
    public static final Datatype XSD_FLOAT = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "float");
    public static final Datatype XSD_BOOLEAN = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "boolean");
    public static final Datatype XSD_DOUBLE = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "double");
    public static final Datatype XSD_NON_POSITIVE_INTEGER = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "nonPositiveInteger");
    public static final Datatype XSD_NEGATIVE_INTEGER = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "negativeInteger");
    public static final Datatype XSD_NON_NEGATIVE_INTEGER = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "nonNegativeInteger");
    public static final Datatype XSD_UNSIGNED_LONG = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "unsignedLong");
    public static final Datatype XSD_UNSIGNED_INT = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "unsignedInt");
    public static final Datatype XSD_POSITIVE_INTEGER = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "positiveInteger");
    public static final Datatype XSD_BASE64_BINARY = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "base64Binary");
    public static final Datatype XSD_HEX_BINARY = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "hexBinary");
    public static final Datatype XSD_ANY_URI = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "anyURI");
    public static final Datatype XSD_QNAME = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "QName");
    public static final Datatype XSD_NORMALIZED_STRING = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "normalizedString");
    public static final Datatype XSD_TOKEN = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "token");
    public static final Datatype XSD_NAME = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "Name");
    public static final Datatype XSD_NCNAME = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "NCName");
    public static final Datatype XSD_NMTOKEN = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "NMToken");
    public static final Datatype XSD_DATETIME = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "dateTime");
    public static final Datatype XSD_DATETIMESTAMP = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "dateTimeStamp");
    public static final Datatype XSD_UNSIGNEDSHORT = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "unsignedShort");
    public static final Datatype XSD_UNSIGNEDBYTE = Datatype.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "unsignedByte");

    public static final Set<Datatype> OWL2_DATATYPES;

    static {
        OWL2_DATATYPES = new HashSet<Datatype>();
        OWL2_DATATYPES.add(RDFS_LITERAL);
        OWL2_DATATYPES.add(RDF_PLAIN_LITERAL);
        OWL2_DATATYPES.add(RDF_XML_LITERAL);
        OWL2_DATATYPES.add(OWL_REAL);
        OWL2_DATATYPES.add(OWL_RATIONAL);
        OWL2_DATATYPES.add(XSD_STRING);
        OWL2_DATATYPES.add(XSD_INTEGER);
        OWL2_DATATYPES.add(XSD_LONG);
        OWL2_DATATYPES.add(XSD_INT);
        OWL2_DATATYPES.add(XSD_SHORT);
        OWL2_DATATYPES.add(XSD_BYTE);
        OWL2_DATATYPES.add(XSD_DECIMAL);
        OWL2_DATATYPES.add(XSD_FLOAT);
        OWL2_DATATYPES.add(XSD_BOOLEAN);
        OWL2_DATATYPES.add(XSD_DOUBLE);
        OWL2_DATATYPES.add(XSD_NON_POSITIVE_INTEGER);
        OWL2_DATATYPES.add(XSD_NEGATIVE_INTEGER);
        OWL2_DATATYPES.add(XSD_NON_NEGATIVE_INTEGER);
        OWL2_DATATYPES.add(XSD_UNSIGNED_LONG);
        OWL2_DATATYPES.add(XSD_UNSIGNED_INT);
        OWL2_DATATYPES.add(XSD_POSITIVE_INTEGER);
        OWL2_DATATYPES.add(XSD_BASE64_BINARY);
        OWL2_DATATYPES.add(XSD_HEX_BINARY);
        OWL2_DATATYPES.add(XSD_ANY_URI);
        OWL2_DATATYPES.add(XSD_QNAME);
        OWL2_DATATYPES.add(XSD_NORMALIZED_STRING);
        OWL2_DATATYPES.add(XSD_TOKEN);
        OWL2_DATATYPES.add(XSD_NAME);
        OWL2_DATATYPES.add(XSD_NCNAME);
        OWL2_DATATYPES.add(XSD_NMTOKEN);
        OWL2_DATATYPES.add(XSD_DATETIME);
        OWL2_DATATYPES.add(XSD_DATETIMESTAMP);
        OWL2_DATATYPES.add(XSD_UNSIGNEDSHORT);
        OWL2_DATATYPES.add(XSD_UNSIGNEDBYTE);
    }

    public boolean isOWL2Datatype() {
        return OWL2_DATATYPES.contains(this);
    }

    protected final IRI m_iri;

    protected Datatype(IRI iri) {
        m_iri = iri;
    }

    public IRI getIRI() {
        return m_iri;
    }

    public String getIRIString() {
        return m_iri.getIRIString();
    }

    @Override
    public String toString(Prefixes prefixes) {
        return m_iri.toString(prefixes);
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return toString(prefixes);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static Datatype create(String iriString) {
        if (iriString.charAt(0) == '<') iriString = iriString.substring(1, iriString.length() - 1);
        return create(IRI.create(iriString));
    }

    public static Datatype create(IRI iri) {
        return s_interningManager.intern(new Datatype(iri));
    }

    public Identifier getIdentifier() {
        return m_iri;
    }

    public String getIdentifierString() {
        return m_iri.getIRIString();
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

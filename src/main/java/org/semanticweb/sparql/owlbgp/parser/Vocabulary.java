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

package org.semanticweb.sparql.owlbgp.parser;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction.OWL2_FACET;

public class Vocabulary {
    public static final IRI OWL_THING = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Thing");
    public static final IRI OWL_NOTHING = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Nothing");
    public static final IRI OWL_CLASS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Class");
    public static final IRI OWL_ONTOLOGY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Ontology");
    public static final IRI OWL_ONTOLOGY_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "OntologyProperty");
    public static final IRI OWL_IMPORTS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "imports");
    public static final IRI OWL_VERSION_IRI = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "versionIRI");
    public static final IRI OWL_VERSION_INFO = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "versionInfo");
    public static final IRI OWL_EQUIVALENT_CLASS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "equivalentClass");
    public static final IRI OWL_OBJECT_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "ObjectProperty");
    public static final IRI OWL_DATA_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "DataProperty");
    public static final IRI OWL_DATATYPE_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "DatatypeProperty");
    public static final IRI OWL_FUNCTIONAL_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "FunctionalProperty");
    public static final IRI OWL_INVERSE_FUNCTIONAL_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "InverseFunctionalProperty");
    public static final IRI OWL_ASYMMETRIC_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "AsymmetricProperty");
    public static final IRI OWL_SYMMETRIC_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "SymmetricProperty");
    public static final IRI OWL_RESTRICTION = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Restriction");
    public static final IRI OWL_ON_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "onProperty");
    public static final IRI OWL_INTERSECTION_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "intersectionOf");
    public static final IRI OWL_UNION_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "unionOf");
    public static final IRI OWL_ALL_VALUES_FROM = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "allValuesFrom");
    public static final IRI OWL_SOME_VALUES_FROM = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "someValuesFrom");
    public static final IRI OWL_HAS_VALUE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "hasValue");
    public static final IRI OWL_DISJOINT_WITH = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "disjointWith");
    public static final IRI OWL_ONE_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "oneOf");
    public static final IRI OWL_HAS_SELF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "hasSelf");
    public static final IRI OWL_DISJOINT_UNION_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "disjointUnionOf");
    public static final IRI OWL_MIN_CARDINALITY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "minCardinality");
    public static final IRI OWL_MIN_QUALIFIED_CARDINALITY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "minQualifiedCardinality");
    public static final IRI OWL_CARDINALITY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "cardinality");
    public static final IRI OWL_QUALIFIED_CARDINALITY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "qualifiedCardinality");
    public static final IRI OWL_ANNOTATION_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "AnnotationProperty");
    public static final IRI OWL_ANNOTATION = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Annotation");
    public static final IRI OWL_INDIVIDUAL = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Individual");
    public static final IRI OWL_LITERAL = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Literal");
    public static final IRI OWL_NAMED_INDIVIDUAL = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "NamedIndividual");
    public static final IRI OWL_DATATYPE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Datatype");
    public static final IRI RDFS_SUBCLASS_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "subClassOf");
    public static final IRI RDFS_SUB_PROPERTY_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "subPropertyOf");
    public static final IRI RDF_TYPE = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "type");
    public static final IRI RDF_NIL = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "nil");
    public static final IRI RDF_REST = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "rest");
    public static final IRI RDF_FIRST = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "first");
    public static final IRI RDF_LIST = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "List");
    public static final IRI OWL_MAX_CARDINALITY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "maxCardinality");
    public static final IRI OWL_MAX_QUALIFIED_CARDINALITY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "maxQualifiedCardinality");
    public static final IRI OWL_NEGATIVE_PROPERTY_ASSERTION = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "NegativePropertyAssertion");
    public static final IRI RDFS_LABEL = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "label");
    public static final IRI RDFS_COMMENT = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "comment");
    public static final IRI RDFS_SEE_ALSO = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "seeAlso");
    public static final IRI RDFS_IS_DEFINED_BY = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "isDefinedBy");
    public static final IRI RDFS_RESOURCE = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "Resource");
    public static final IRI RDFS_DATATYPE = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "Datatype");
    public static final IRI OWL_TRANSITIVE_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "TransitiveProperty");
    public static final IRI OWL_REFLEXIVE_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "ReflexiveProperty");
    public static final IRI OWL_IRREFLEXIVE_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "IrreflexiveProperty");
    public static final IRI OWL_INVERSE_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "inverseOf");
    public static final IRI OWL_COMPLEMENT_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "complementOf");
    public static final IRI OWL_DATATYPE_COMPLEMENT_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "datatypeComplementOf");
    public static final IRI OWL_ALL_DIFFERENT = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "AllDifferent");
    public static final IRI OWL_DISTINCT_MEMBERS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "distinctMembers");
    public static final IRI OWL_SAME_AS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "sameAs");
    public static final IRI OWL_DIFFERENT_FROM = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "differentFrom");
    public static final IRI OWL_DEPRECATED_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "DeprecatedProperty");
    public static final IRI OWL_EQUIVALENT_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "equivalentProperty");
    public static final IRI OWL_DEPRECATED_CLASS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "DeprecatedClass");
    public static final IRI OWL_DATA_RANGE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "DataRange");
    public static final IRI RDFS_DOMAIN = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "domain");
    public static final IRI RDFS_RANGE = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "range");
    public static final IRI RDFS_CLASS = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdfs") + "Class");
    public static final IRI RDF_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "Property");
    public static final IRI RDF_DESCRIPTION = IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "Description");
    public static final IRI OWL_PRIOR_VERSION = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "priorVersion");
    public static final IRI OWL_DEPRECATED = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "deprecated");
    public static final IRI OWL_BACKWARD_COMPATIBLE_WITH = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "backwardCompatibleWith");
    public static final IRI OWL_INCOMPATIBLE_WITH = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "incompatibleWith");
    public static final IRI OWL_OBJECT_PROPERTY_DOMAIN = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "objectPropertyDomain");
    public static final IRI OWL_DATA_PROPERTY_DOMAIN = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "dataPropertyDomain");
    public static final IRI OWL_DATA_PROPERTY_RANGE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "dataPropertyRange");
    public static final IRI OWL_OBJECT_PROPERTY_RANGE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "objectPropertyRange");
    public static final IRI OWL_SUB_OBJECT_PROPERTY_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "subObjectPropertyOf");
    public static final IRI OWL_SUB_DATA_PROPERTY_OF = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "subDataPropertyOf");
    public static final IRI OWL_PROPERTY_DISJOINT_WITH = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "propertyDisjointWith");
    public static final IRI OWL_FUNCTIONAL_DATA_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "FunctionalDataProperty");
    public static final IRI OWL_FUNCTIONAL_OBJECT_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "FunctionalObjectProperty");
    public static final IRI OWL_ON_CLASS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "onClass");
    public static final IRI OWL_ON_DATA_RANGE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "onDataRange");
    public static final IRI OWL_ON_DATA_TYPE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "onDatatype");
    public static final IRI OWL_WITH_RESTRICTIONS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "withRestrictions");
    public static final IRI OWL_INVERSE_OBJECT_PROPERTY_EXPRESSION = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "inverseObjectPropertyExpression");
    public static final IRI OWL_AXIOM = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "Axiom");
    public static final IRI OWL_PROPERTY_CHAIN_AXIOM = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "propertyChainAxiom");
    public static final IRI OWL_ALL_DISJOINT_CLASSES = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "AllDisjointClasses");
    public static final IRI OWL_MEMBERS = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "members");
    public static final IRI OWL_ALL_DISJOINT_PROPERTIES = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "AllDisjointProperties");
    public static final IRI OWL_TOP_OBJECT_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "topObjectProperty");
    public static final IRI OWL_BOTTOM_OBJECT_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "bottomObjectProperty");
    public static final IRI OWL_TOP_DATA_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "topDataProperty");
    public static final IRI OWL_BOTTOM_DATA_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "bottomDataProperty");
    public static final IRI OWL_HAS_KEY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "hasKey");
    public static final IRI OWL_ANNOTATED_SOURCE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "annotatedSource");
    public static final IRI OWL_ANNOTATED_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "annotatedProperty");
    public static final IRI OWL_ANNOTATED_TARGET = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "annotatedTarget");
    public static final IRI OWL_SOURCE_INDIVIDUAL = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "sourceIndividual");
    public static final IRI OWL_ASSERTION_PROPERTY = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "assertionProperty");
    public static final IRI OWL_TARGET_INDIVIDUAL = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "targetIndividual");
    public static final IRI OWL_TARGET_VALUE = IRI.create(Prefixes.s_semanticWebPrefixes.get("owl") + "targetValue");

    public static final Set<IRI> BUILT_IN_ANNOTATION_PROPERTY_IRIS;

    static {
        BUILT_IN_ANNOTATION_PROPERTY_IRIS = new HashSet<IRI>();
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_LABEL);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_COMMENT);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_VERSION_INFO);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_BACKWARD_COMPATIBLE_WITH);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_PRIOR_VERSION);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_SEE_ALSO);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_IS_DEFINED_BY);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_INCOMPATIBLE_WITH);
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_DEPRECATED);
    }

    public static final Set<IRI> BUILT_IN_OBJECT_PROPERTY_IRIS;

    static {
        BUILT_IN_OBJECT_PROPERTY_IRIS = new HashSet<IRI>();
        BUILT_IN_OBJECT_PROPERTY_IRIS.add(OWL_TOP_OBJECT_PROPERTY);
        BUILT_IN_OBJECT_PROPERTY_IRIS.add(OWL_BOTTOM_OBJECT_PROPERTY);
    }

    public static final Set<IRI> BUILT_IN_DATA_PROPERTY_IRIS;

    static {
        BUILT_IN_DATA_PROPERTY_IRIS = new HashSet<IRI>();
        BUILT_IN_DATA_PROPERTY_IRIS.add(OWL_TOP_DATA_PROPERTY);
        BUILT_IN_DATA_PROPERTY_IRIS.add(OWL_BOTTOM_DATA_PROPERTY);
    }

    public static final Set<IRI> BUILT_IN_CLASS_IRIS;

    static {
        BUILT_IN_CLASS_IRIS = new HashSet<IRI>();
        BUILT_IN_CLASS_IRIS.add(OWL_THING);
        BUILT_IN_CLASS_IRIS.add(OWL_NOTHING);
    }

    public static final Set<IRI> BUILT_IN_DATATYPE_IRIS;

    static {
        BUILT_IN_DATATYPE_IRIS = new HashSet<IRI>();
        for (Datatype dt : Datatype.OWL2_DATATYPES)
            BUILT_IN_DATATYPE_IRIS.add(dt.getIRI());
    }

    public static final Set<IRI> BUILT_IN_FACET_IRIS;

    static {
        BUILT_IN_FACET_IRIS = new HashSet<IRI>();
        for (OWL2_FACET facet : OWL2_FACET.values())
            BUILT_IN_FACET_IRIS.add(facet.getIRI());
    }

    public static final Set<IRI> BUILT_IN_PREDICATE_IRIS;

    static {
        BUILT_IN_PREDICATE_IRIS = new HashSet<IRI>();
        BUILT_IN_PREDICATE_IRIS.add(OWL_CLASS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ONTOLOGY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ONTOLOGY_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_IMPORTS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_VERSION_IRI);
        BUILT_IN_PREDICATE_IRIS.add(OWL_EQUIVALENT_CLASS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_OBJECT_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DATA_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DATATYPE_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_FUNCTIONAL_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_INVERSE_FUNCTIONAL_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ASYMMETRIC_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_SYMMETRIC_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_RESTRICTION);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ON_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_INTERSECTION_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_UNION_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ALL_VALUES_FROM);
        BUILT_IN_PREDICATE_IRIS.add(OWL_SOME_VALUES_FROM);
        BUILT_IN_PREDICATE_IRIS.add(OWL_HAS_VALUE);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DISJOINT_WITH);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ONE_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_HAS_SELF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DISJOINT_UNION_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_MIN_CARDINALITY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_MIN_QUALIFIED_CARDINALITY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_CARDINALITY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_QUALIFIED_CARDINALITY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ANNOTATION_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ANNOTATION);
        BUILT_IN_PREDICATE_IRIS.add(OWL_INDIVIDUAL);
        BUILT_IN_PREDICATE_IRIS.add(OWL_NAMED_INDIVIDUAL);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DATATYPE);
        BUILT_IN_PREDICATE_IRIS.add(RDFS_SUBCLASS_OF);
        BUILT_IN_PREDICATE_IRIS.add(RDFS_SUB_PROPERTY_OF);
        BUILT_IN_PREDICATE_IRIS.add(RDF_TYPE);
        BUILT_IN_PREDICATE_IRIS.add(RDF_NIL);
        BUILT_IN_PREDICATE_IRIS.add(RDF_REST);
        BUILT_IN_PREDICATE_IRIS.add(RDF_FIRST);
        BUILT_IN_PREDICATE_IRIS.add(RDF_LIST);
        BUILT_IN_PREDICATE_IRIS.add(OWL_MAX_CARDINALITY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_MAX_QUALIFIED_CARDINALITY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_NEGATIVE_PROPERTY_ASSERTION);
        BUILT_IN_PREDICATE_IRIS.add(RDFS_RESOURCE);
        BUILT_IN_PREDICATE_IRIS.add(RDFS_DATATYPE);
        BUILT_IN_PREDICATE_IRIS.add(OWL_TRANSITIVE_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_REFLEXIVE_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_IRREFLEXIVE_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_INVERSE_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_COMPLEMENT_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DATATYPE_COMPLEMENT_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ALL_DIFFERENT);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DISTINCT_MEMBERS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_SAME_AS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DIFFERENT_FROM);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DEPRECATED_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_EQUIVALENT_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DEPRECATED_CLASS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DATA_RANGE);
        BUILT_IN_PREDICATE_IRIS.add(RDFS_DOMAIN);
        BUILT_IN_PREDICATE_IRIS.add(RDFS_RANGE);
        BUILT_IN_PREDICATE_IRIS.add(RDFS_CLASS);
        BUILT_IN_PREDICATE_IRIS.add(RDF_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(RDF_DESCRIPTION);
        BUILT_IN_PREDICATE_IRIS.add(OWL_OBJECT_PROPERTY_DOMAIN);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DATA_PROPERTY_DOMAIN);
        BUILT_IN_PREDICATE_IRIS.add(OWL_DATA_PROPERTY_RANGE);
        BUILT_IN_PREDICATE_IRIS.add(OWL_OBJECT_PROPERTY_RANGE);
        BUILT_IN_PREDICATE_IRIS.add(OWL_SUB_OBJECT_PROPERTY_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_SUB_DATA_PROPERTY_OF);
        BUILT_IN_PREDICATE_IRIS.add(OWL_PROPERTY_DISJOINT_WITH);
        BUILT_IN_PREDICATE_IRIS.add(OWL_FUNCTIONAL_DATA_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_FUNCTIONAL_OBJECT_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ON_CLASS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ON_DATA_RANGE);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ON_DATA_TYPE);
        BUILT_IN_PREDICATE_IRIS.add(OWL_WITH_RESTRICTIONS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_INVERSE_OBJECT_PROPERTY_EXPRESSION);
        BUILT_IN_PREDICATE_IRIS.add(OWL_AXIOM);
        BUILT_IN_PREDICATE_IRIS.add(OWL_PROPERTY_CHAIN_AXIOM);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ALL_DISJOINT_CLASSES);
        BUILT_IN_PREDICATE_IRIS.add(OWL_MEMBERS);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ALL_DISJOINT_PROPERTIES);
        BUILT_IN_PREDICATE_IRIS.add(OWL_HAS_KEY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ANNOTATED_SOURCE);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ANNOTATED_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ANNOTATED_TARGET);
        BUILT_IN_PREDICATE_IRIS.add(OWL_SOURCE_INDIVIDUAL);
        BUILT_IN_PREDICATE_IRIS.add(OWL_ASSERTION_PROPERTY);
        BUILT_IN_PREDICATE_IRIS.add(OWL_TARGET_INDIVIDUAL);
        BUILT_IN_PREDICATE_IRIS.add(OWL_TARGET_VALUE);
    }

    public static final Set<IRI> BUILT_IN_VOCABULARY_IRIS;

    static {
        BUILT_IN_VOCABULARY_IRIS = new HashSet<IRI>();
        BUILT_IN_VOCABULARY_IRIS.addAll(BUILT_IN_ANNOTATION_PROPERTY_IRIS);
        BUILT_IN_VOCABULARY_IRIS.addAll(BUILT_IN_OBJECT_PROPERTY_IRIS);
        BUILT_IN_VOCABULARY_IRIS.addAll(BUILT_IN_DATA_PROPERTY_IRIS);
        BUILT_IN_VOCABULARY_IRIS.addAll(BUILT_IN_CLASS_IRIS);
        BUILT_IN_VOCABULARY_IRIS.addAll(BUILT_IN_DATATYPE_IRIS);
        BUILT_IN_VOCABULARY_IRIS.addAll(BUILT_IN_FACET_IRIS);
        BUILT_IN_VOCABULARY_IRIS.addAll(BUILT_IN_PREDICATE_IRIS);
    }
}
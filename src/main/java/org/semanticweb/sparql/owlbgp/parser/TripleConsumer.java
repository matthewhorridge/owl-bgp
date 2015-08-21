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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.AnnotationValue;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.Import;
import org.semanticweb.sparql.owlbgp.model.Ontology;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataComplementOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.translators.ClassExpressionListItemTranslator;
import org.semanticweb.sparql.owlbgp.parser.translators.DataRangeListItemTranslator;
import org.semanticweb.sparql.owlbgp.parser.translators.FacetRestrictionListItemTranslator;
import org.semanticweb.sparql.owlbgp.parser.translators.IndividualListItemTranslator;
import org.semanticweb.sparql.owlbgp.parser.translators.OptimisedListTranslator;
import org.semanticweb.sparql.owlbgp.parser.translators.PropertyExpressionListItemTranslator;
import org.semanticweb.sparql.owlbgp.parser.translators.TypedConstantListItemTranslator;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.PropertyAssertionHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPAllValuesFromHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDataComplementOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDataIntersectionOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDataOneOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDataUnionOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDifferentFromHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDisjointUnionHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDisjointWithHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPDomainHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPEquivalentClassHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPEquivalentPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPExactCardinalityHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPExactQualifiedCardinalityHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPHasKeyAxiomHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPHasSelfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPHasValueHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPImportsHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPInverseOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPMaxCardinalityHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPMaxQualifiedCardinalityHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPMinCardinalityHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPMinQualifiedCardinalityHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPObjectComplementOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPObjectIntersectionOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPObjectOneOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPObjectUnionOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPOnDatatypeHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPPropertyChainAxiomHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPPropertyDisjointWithHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPRangeHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPSameAsHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPSomeValuesFromHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPSubClassOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPSubPropertyOfHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.TPTypeHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate.VersionIRIHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.AllDifferentHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.AllDisjointClassesHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.AllDisjointPropertiesHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.AnnotationHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.AnnotationPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.AsymmetricPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.AxiomHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.ClassHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.DatatypeHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.DatatypePropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.DeprecatedClassOrPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.FunctionalPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.InverseFunctionalPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.IrreflexivePropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.NamedIndividualHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.NegativePropertyAssertionHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.ObjectPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.OntologyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.ReflexivePropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.SymmetricPropertyHandler;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype.TransitivePropertyHandler;

public class TripleConsumer {
    public static final String LB = System.getProperty("line.separator");

    protected boolean debug = false;

    protected final Set<Import> imports = new HashSet<Import>();
    protected Identifier ontologyIRI;
    protected final Map<Identifier, Set<Identifier>> ontologyIRIToVersionIRIs = new HashMap<Identifier, Set<Identifier>>();
    protected final Set<Axiom> axioms = new HashSet<Axiom>();

    protected final Set<Identifier> CEExt = new HashSet<Identifier>();
    protected final Set<Identifier> OPEExt = new HashSet<Identifier>();
    protected final Set<Identifier> DPEExt = new HashSet<Identifier>();
    protected final Set<Identifier> APEExt = new HashSet<Identifier>();
    protected final Set<Identifier> INDExt = new HashSet<Identifier>();
    protected final Set<Identifier> DRExt = new HashSet<Identifier>();

    protected final Set<IndividualVariable> variablesForAnonymousIndividuals = new HashSet<IndividualVariable>();
    protected final Set<Identifier> RIND = new HashSet<Identifier>();
    protected final Map<Identifier, ClassExpression> CE = new HashMap<Identifier, ClassExpression>();
    protected final Map<Identifier, DataRange> DR = new HashMap<Identifier, DataRange>();
    protected final Map<Identifier, ObjectPropertyExpression> OPE = new HashMap<Identifier, ObjectPropertyExpression>();
    protected final Map<Identifier, DataPropertyExpression> DPE = new HashMap<Identifier, DataPropertyExpression>();
    protected final Map<Identifier, AnnotationPropertyExpression> APE = new HashMap<Identifier, AnnotationPropertyExpression>();
    protected final Map<Identifier, Individual> IND = new HashMap<Identifier, Individual>();
    protected final Map<Identifier, Set<Annotation>> ANN = new HashMap<Identifier, Set<Annotation>>();
    protected final Map<Identifier, Set<Identifier>> subjOfInversePropertiesAxiomToObjects = new HashMap<Identifier, Set<Identifier>>();

    protected final Map<Identifier, TripleHandler> streamingByPredicateHandlers = new HashMap<Identifier, TripleHandler>();
    protected final Map<Identifier, Map<Identifier, TripleHandler>> streamingByPredicateAndObjectHandlers = new HashMap<Identifier, Map<Identifier, TripleHandler>>();
    protected final Map<Identifier, TripleHandler> dataRangePredicateHandlers = new HashMap<Identifier, TripleHandler>();
    protected final Map<Identifier, TripleHandler> classExpressionPredicateHandlers = new HashMap<Identifier, TripleHandler>();
    protected final Map<Identifier, TripleHandler> byPredicateHandlers = new HashMap<Identifier, TripleHandler>();
    protected final Map<Identifier, Map<Identifier, TripleHandler>> byPredicateAndObjectHandlers = new HashMap<Identifier, Map<Identifier, TripleHandler>>();
    protected final TripleHandler classAssertionHandler;
    protected final TripleHandler propertyAssertionHandler;

    protected final Map<Identifier, Map<Identifier, Set<Identifier>>> subjToPredToObjects = new HashMap<Identifier, Map<Identifier, Set<Identifier>>>(); // subject, predicate, objects
    protected final Map<Identifier, Map<Identifier, Set<Identifier>>> builtInPredToBuiltInObjToSubjects = new HashMap<Identifier, Map<Identifier, Set<Identifier>>>(); // built-in predicate, built-in object, subjects
    protected final Map<Identifier, Map<Identifier, Set<Identifier>>> builtInPredToSubToObjects = new HashMap<Identifier, Map<Identifier, Set<Identifier>>>(); // built-in predicate, subject, object

    protected final OptimisedListTranslator<ClassExpression> classExpressionListTranslator = new OptimisedListTranslator<ClassExpression>(this, new ClassExpressionListItemTranslator(this));
    protected final OptimisedListTranslator<Individual> individualListTranslator = new OptimisedListTranslator<Individual>(this, new IndividualListItemTranslator(this));
    protected final OptimisedListTranslator<PropertyExpression> propertyListTranslator = new OptimisedListTranslator<PropertyExpression>(this, new PropertyExpressionListItemTranslator(this));
    protected final OptimisedListTranslator<Literal> literalListTranslator = new OptimisedListTranslator<Literal>(this, new TypedConstantListItemTranslator(this));
    protected final OptimisedListTranslator<DataRange> dataRangeListTranslator = new OptimisedListTranslator<DataRange>(this, new DataRangeListItemTranslator(this));
    protected final OptimisedListTranslator<FacetRestriction> faceRestrictionListTranslator = new OptimisedListTranslator<FacetRestriction>(this, new FacetRestrictionListItemTranslator(this));

    {
        CEExt.add(Vocabulary.OWL_THING);
        CEExt.add(Vocabulary.OWL_NOTHING);
        OPEExt.add(Vocabulary.OWL_TOP_OBJECT_PROPERTY);
        OPEExt.add(Vocabulary.OWL_BOTTOM_OBJECT_PROPERTY);
        DPEExt.add(Vocabulary.OWL_TOP_DATA_PROPERTY);
        DPEExt.add(Vocabulary.OWL_BOTTOM_DATA_PROPERTY);
        APEExt.add(Vocabulary.OWL_PRIOR_VERSION);
        APEExt.add(Vocabulary.OWL_BACKWARD_COMPATIBLE_WITH);
        APEExt.add(Vocabulary.OWL_INCOMPATIBLE_WITH);
        APEExt.add(Vocabulary.OWL_VERSION_INFO);
        APEExt.add(Vocabulary.RDFS_LABEL);
        APEExt.add(Vocabulary.RDFS_COMMENT);
        APEExt.add(Vocabulary.RDFS_SEE_ALSO);
        APEExt.add(Vocabulary.RDFS_IS_DEFINED_BY);
        for (Datatype dt : Datatype.OWL2_DATATYPES)
            DRExt.add(dt.getIdentifier());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TripleConsumer() {
        streamingByPredicateHandlers.put(Vocabulary.OWL_IMPORTS, new TPImportsHandler(this));
        streamingByPredicateHandlers.put(Vocabulary.OWL_VERSION_IRI, new VersionIRIHandler(this));
        IRI rdftype = Vocabulary.RDF_TYPE;
        Map<Identifier, TripleHandler> typeHandlers = new HashMap<Identifier, TripleHandler>();
        typeHandlers.put(Vocabulary.OWL_CLASS, new ClassHandler(this));
        typeHandlers.put(Vocabulary.RDFS_DATATYPE, new DatatypeHandler(this));
        typeHandlers.put(Vocabulary.OWL_OBJECT_PROPERTY, new ObjectPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_DATATYPE_PROPERTY, new DatatypePropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_ANNOTATION_PROPERTY, new AnnotationPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_NAMED_INDIVIDUAL, new NamedIndividualHandler(this));
        typeHandlers.put(Vocabulary.OWL_ONTOLOGY, new OntologyHandler(this));
        typeHandlers.put(Vocabulary.OWL_AXIOM, new AxiomHandler(this));
        typeHandlers.put(Vocabulary.OWL_ANNOTATION, new AnnotationHandler(this));
        typeHandlers.put(Vocabulary.OWL_ALL_DISJOINT_CLASSES, new AllDisjointClassesHandler(this));
        typeHandlers.put(Vocabulary.OWL_ALL_DISJOINT_PROPERTIES, new AllDisjointPropertiesHandler(this));
        typeHandlers.put(Vocabulary.OWL_ALL_DIFFERENT, new AllDifferentHandler(this));
        typeHandlers.put(Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION, new NegativePropertyAssertionHandler(this));
        streamingByPredicateAndObjectHandlers.put(rdftype, typeHandlers);

        dataRangePredicateHandlers.put(Vocabulary.OWL_INTERSECTION_OF, new TPDataIntersectionOfHandler(this));
        dataRangePredicateHandlers.put(Vocabulary.OWL_UNION_OF, new TPDataUnionOfHandler(this));
        dataRangePredicateHandlers.put(Vocabulary.OWL_DATATYPE_COMPLEMENT_OF, new TPDataComplementOfHandler(this));
        dataRangePredicateHandlers.put(Vocabulary.OWL_ONE_OF, new TPDataOneOfHandler(this));
        dataRangePredicateHandlers.put(Vocabulary.OWL_ON_DATA_TYPE, new TPOnDatatypeHandler(this));

        classExpressionPredicateHandlers.put(Vocabulary.OWL_INTERSECTION_OF, new TPObjectIntersectionOfHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_UNION_OF, new TPObjectUnionOfHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_COMPLEMENT_OF, new TPObjectComplementOfHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_ONE_OF, new TPObjectOneOfHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_SOME_VALUES_FROM, new TPSomeValuesFromHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_ALL_VALUES_FROM, new TPAllValuesFromHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_HAS_VALUE, new TPHasValueHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_HAS_SELF, new TPHasSelfHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_MIN_CARDINALITY, new TPMinCardinalityHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_MAX_CARDINALITY, new TPMaxCardinalityHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_CARDINALITY, new TPExactCardinalityHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_MIN_QUALIFIED_CARDINALITY, new TPMinQualifiedCardinalityHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_MAX_QUALIFIED_CARDINALITY, new TPMaxQualifiedCardinalityHandler(this));
        classExpressionPredicateHandlers.put(Vocabulary.OWL_QUALIFIED_CARDINALITY, new TPExactQualifiedCardinalityHandler(this));

        byPredicateHandlers.put(Vocabulary.RDFS_SUBCLASS_OF, new TPSubClassOfHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_EQUIVALENT_CLASS, new TPEquivalentClassHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_DISJOINT_WITH, new TPDisjointWithHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_DISJOINT_UNION_OF, new TPDisjointUnionHandler(this));
        byPredicateHandlers.put(Vocabulary.RDFS_SUB_PROPERTY_OF, new TPSubPropertyOfHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_INVERSE_OF, new TPInverseOfHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_EQUIVALENT_PROPERTY, new TPEquivalentPropertyHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_PROPERTY_CHAIN_AXIOM, new TPPropertyChainAxiomHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_PROPERTY_DISJOINT_WITH, new TPPropertyDisjointWithHandler(this));
        byPredicateHandlers.put(Vocabulary.RDFS_DOMAIN, new TPDomainHandler(this));
        byPredicateHandlers.put(Vocabulary.RDFS_RANGE, new TPRangeHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_DIFFERENT_FROM, new TPDifferentFromHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_SAME_AS, new TPSameAsHandler(this));
        byPredicateHandlers.put(Vocabulary.OWL_HAS_KEY, new TPHasKeyAxiomHandler(this));

        classAssertionHandler = new TPTypeHandler(this);
        propertyAssertionHandler = new PropertyAssertionHandler(this);

        typeHandlers = new HashMap<Identifier, TripleHandler>();
        typeHandlers.put(Vocabulary.OWL_CLASS, new ClassHandler(this));
        typeHandlers.put(Vocabulary.RDFS_DATATYPE, new DatatypeHandler(this));
        typeHandlers.put(Vocabulary.OWL_OBJECT_PROPERTY, new ObjectPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_INVERSE_FUNCTIONAL_PROPERTY, new InverseFunctionalPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_FUNCTIONAL_PROPERTY, new FunctionalPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_ASYMMETRIC_PROPERTY, new AsymmetricPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_SYMMETRIC_PROPERTY, new SymmetricPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_REFLEXIVE_PROPERTY, new ReflexivePropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_IRREFLEXIVE_PROPERTY, new IrreflexivePropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_TRANSITIVE_PROPERTY, new TransitivePropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_DATATYPE_PROPERTY, new DatatypePropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_ANNOTATION_PROPERTY, new AnnotationPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_NAMED_INDIVIDUAL, new NamedIndividualHandler(this));
        typeHandlers.put(Vocabulary.OWL_ALL_DISJOINT_CLASSES, new AllDisjointClassesHandler(this));
        typeHandlers.put(Vocabulary.OWL_ALL_DISJOINT_PROPERTIES, new AllDisjointPropertiesHandler(this));
        typeHandlers.put(Vocabulary.OWL_ALL_DIFFERENT, new AllDifferentHandler(this));
        typeHandlers.put(Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION, new NegativePropertyAssertionHandler(this));
        typeHandlers.put(Vocabulary.OWL_DEPRECATED_CLASS, new DeprecatedClassOrPropertyHandler(this));
        typeHandlers.put(Vocabulary.OWL_DEPRECATED_PROPERTY, new DeprecatedClassOrPropertyHandler(this));
        byPredicateAndObjectHandlers.put(rdftype, typeHandlers);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        if (debug) {
            Prefixes prefixes = Prefixes.STANDARD_PREFIXES;
            System.out.println(subject.toString(prefixes) + " " + predicate.toString(prefixes) + " " + object.toString(prefixes) + " .");
        }
        // Table 6, add additional declaration triples
        if (!isAnonymous(subject) && (object == Vocabulary.OWL_INVERSE_FUNCTIONAL_PROPERTY || object == Vocabulary.OWL_TRANSITIVE_PROPERTY || object == Vocabulary.OWL_SYMMETRIC_PROPERTY) && predicate == Vocabulary.RDF_TYPE)
            handleStreaming(subject, predicate, Vocabulary.OWL_OBJECT_PROPERTY);
        if (!isAnonymous(subject) && object == Vocabulary.OWL_ONTOLOGY_PROPERTY && predicate == Vocabulary.RDF_TYPE)
            // Table 6, correct declaration triples
            handleStreaming(subject, predicate, Vocabulary.OWL_ANNOTATION_PROPERTY);
        else {
            // Table 7, only one triple declarations, reified ones are dealt with later
            // axioms not yet created and triples not removed
            // Table 8, collect bnode subjects of reified axioms 
            TripleHandler handler = streamingByPredicateHandlers.get(predicate);
            if (handler == null) {
                Map<Identifier, TripleHandler> handlerMap = streamingByPredicateAndObjectHandlers.get(predicate);
                if (handlerMap != null)
                    handler = handlerMap.get(object);
                if (handler == null && predicate.equals(Vocabulary.RDF_TYPE) && isAnonymous(object))
                    handler = classAssertionHandler;
            }
            if (handler != null)
                handler.handleStreaming(subject, predicate, object);
            else
                addTriple(subject, predicate, object);
        }
    }

    public void addTriple(Identifier subject, Identifier predicate, Identifier object) {
        Map<Identifier, Set<Identifier>> map;
        if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(predicate)) {
            if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(object)) {
                map = builtInPredToBuiltInObjToSubjects.get(predicate);
                if (map == null) {
                    map = new HashMap<Identifier, Set<Identifier>>();
                    builtInPredToBuiltInObjToSubjects.put(predicate, map);
                }
                Set<Identifier> subjects = map.get(object);
                if (subjects == null) {
                    subjects = new HashSet<Identifier>();
                    map.put(object, subjects);
                }
                subjects.add(subject);
            }
            else {
                map = builtInPredToSubToObjects.get(predicate);
                if (map == null) {
                    map = new HashMap<Identifier, Set<Identifier>>();
                    builtInPredToSubToObjects.put(predicate, map);
                }
                Set<Identifier> objects = map.get(subject);
                if (objects == null) {
                    objects = new HashSet<Identifier>();
                    map.put(subject, objects);
                }
                objects.add(object);
            }
        }
        else {
            map = subjToPredToObjects.get(subject);
            if (map == null) {
                map = new HashMap<Identifier, Set<Identifier>>();
                subjToPredToObjects.put(subject, map);
            }
            Set<Identifier> objects = map.get(predicate);
            if (objects == null) {
                objects = new HashSet<Identifier>();
                map.put(predicate, objects);
            }
            objects.add(object);
        }
    }

    public void removeTriple(Identifier subject, Identifier predicate, Identifier object) {
        Map<Identifier, Set<Identifier>> map;
        if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(predicate)) {
            if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(object)) {
                map = builtInPredToBuiltInObjToSubjects.get(predicate);
                if (map != null) {
                    Set<Identifier> subjects = map.get(object);
                    if (subjects != null) {
                        subjects.remove(subject);
                        if (subjects.isEmpty()) {
                            map.remove(object);
                            if (map.isEmpty())
                                builtInPredToBuiltInObjToSubjects.remove(predicate);
                        }
                    }
                }
            }
            else {
                map = builtInPredToSubToObjects.get(predicate);
                if (map != null) {
                    Set<Identifier> objects = map.get(subject);
                    if (objects != null) {
                        objects.remove(object);
                        if (objects.isEmpty()) {
                            map.remove(subject);
                            if (map.isEmpty())
                                builtInPredToSubToObjects.remove(predicate);
                        }
                    }
                }
            }
        }
        else {
            map = subjToPredToObjects.get(subject);
            if (map != null) {
                Set<Identifier> objects = map.get(predicate);
                if (objects != null) {
                    objects.remove(object);
                    if (objects.isEmpty()) {
                        map.remove(predicate);
                        if (map.isEmpty())
                            subjToPredToObjects.remove(subject);
                    }
                }
            }
        }
    }

    public void handleEnd() {
        //if (ontologyIRI==null) throw new RuntimeException("The ontology did not have an ontology IRI, i.e., it is missing a triple of the form: ontologyIRI rdf:type owl:Ontology . ");
        checkVersionIRIsForOntologyIRI(); // Table 4
        checkImportsOnlyForOntologyIRI(); // Table 4
        // TODO: Check what happens if two ontology IRIs are given, but the forbidden triples are not fully matched.  
        checkOnytologyIRIIsNeverObject(); // Table 4
        removeOWL1DoubleTypes(); // Table 5
        addReifiedDeclarations(); // Table 7
        parseAnnotations(); // Table 10
        parseObjectProperties(); // Table 11
        parseOWL1DataRanges(); // Table 14
        parseOWL1ClassExpressions(); // Table 15
        parseDataRanges(); // Table 12
        parseClassExpressions(); // Table 13
        parseAxioms();
    }

    protected void parseAxioms() {
        parseAnnotatedAxioms();
        // now all annotated ones are consumed, and we can translate the non-annotated ones
        parseNonAnnotatedAxioms();
    }

    protected void parseAnnotatedAxioms() {
//        printIndexedTriples();
        Map<Identifier, Set<Identifier>> objToSubjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE);
        if (objToSubjects != null) {
            Set<Identifier> subjects = objToSubjects.get(Vocabulary.OWL_AXIOM);
            if (subjects != null) {
                for (Identifier subject : new HashSet<Identifier>(subjects)) {
                    if (isAnonymous(subject)) {
                        Identifier[] triple = getReifiedTriple(subject, Vocabulary.OWL_AXIOM);
                        if (triple != null) {
                            removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_AXIOM);
                            removeTriple(subject, Vocabulary.OWL_ANNOTATED_SOURCE, triple[0]);
                            removeTriple(subject, Vocabulary.OWL_ANNOTATED_PROPERTY, triple[1]);
                            removeTriple(subject, Vocabulary.OWL_ANNOTATED_TARGET, triple[2]);
                            Set<Annotation> axiomAnnotations = ANN.get(subject);
                            parseAxiom(triple[0], triple[1], triple[2], axiomAnnotations);
                        }
                    }
                }
            }
            // Axioms represented by blank nodes
            // _:4 rdf:type owl:AllDisjointClasses . 
            // _:4 owl:members _:5 . ...
            // _:4 rdfs:label "CDE2anno@"^^rdf:PlainLiteral . 
            IRI[] bnodeAxiomObjects = new IRI[]{Vocabulary.OWL_ALL_DISJOINT_CLASSES, Vocabulary.OWL_ALL_DISJOINT_PROPERTIES, Vocabulary.OWL_ALL_DIFFERENT, Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION};
            for (IRI iri : bnodeAxiomObjects) {
                subjects = objToSubjects.get(iri);
                if (subjects != null) {
                    for (Identifier subject : new HashSet<Identifier>(subjects)) {
                        if (isAnonymous(subject)) {
                            Set<Annotation> axiomAnnotations = ANN.get(subject);
                            if (axiomAnnotations == null)
                                axiomAnnotations = new HashSet<Annotation>();
                            parseAxiom(subject, Vocabulary.RDF_TYPE, iri, axiomAnnotations);
                        }
                    }
                }
            }
        }
    }

    protected void parseAxiom(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> axiomAnnotations) {
        TripleHandler handler = byPredicateHandlers.get(predicate);
        if (handler == null) {
            Map<Identifier, TripleHandler> handlerMap = byPredicateAndObjectHandlers.get(predicate);
            if (handlerMap != null)
                handler = handlerMap.get(object);
        }
        if (handler == null && predicate == Vocabulary.RDF_TYPE)
            handler = classAssertionHandler;
        else if (handler == null)
            handler = propertyAssertionHandler;
        handler.handleTriple(subject, predicate, object, axiomAnnotations);
        removeTriple(subject, predicate, object);
    }

    protected void parseNonAnnotatedAxioms() {
        TripleHandler handler = null;
        for (Identifier predicate : new HashSet<Identifier>(builtInPredToBuiltInObjToSubjects.keySet())) {
            Map<Identifier, Set<Identifier>> objToSubjects = builtInPredToBuiltInObjToSubjects.get(predicate);
            for (Identifier object : new HashSet<Identifier>(objToSubjects.keySet())) {
                Map<Identifier, TripleHandler> objToHandler = byPredicateAndObjectHandlers.get(predicate);
                if (objToHandler != null) {
                    handler = objToHandler.get(object);
                    if (handler != null) {
                        for (Identifier subject : new HashSet<Identifier>(objToSubjects.get(object))) {
                            handler.handleTriple(subject, predicate, object);
                            removeTriple(subject, predicate, object);
                        }
                    }
                }
            }
        }
        for (Identifier predicate : new HashSet<Identifier>(builtInPredToSubToObjects.keySet())) {
            Map<Identifier, Set<Identifier>> subjToObjects = builtInPredToSubToObjects.get(predicate);
            handler = byPredicateHandlers.get(predicate);
            if (handler == null && predicate == Vocabulary.RDF_TYPE)
                handler = classAssertionHandler;
            if (handler != null) {
                for (Identifier subject : new HashSet<Identifier>(subjToObjects.keySet())) {
                    for (Identifier object : new HashSet<Identifier>(subjToObjects.get(subject))) {
                        handler.handleTriple(subject, predicate, object);
                        removeTriple(subject, predicate, object);
                    }
                }
            }
        }
        handler = propertyAssertionHandler;
        for (Identifier subject : new HashSet<Identifier>(subjToPredToObjects.keySet())) {
            Map<Identifier, Set<Identifier>> predToObjects = subjToPredToObjects.get(subject);
            for (Identifier predicate : new HashSet<Identifier>(predToObjects.keySet())) {
                for (Identifier object : new HashSet<Identifier>(predToObjects.get(predicate))) {
                    handler.handleTriple(subject, predicate, object);
                    removeTriple(subject, predicate, object);
                }
            }
        }
    }

    protected void parseOWL1DataRanges() {
        // Table 14
        // _:x rdf:type owl:DataRange
        if (builtInPredToBuiltInObjToSubjects.containsKey(Vocabulary.RDF_TYPE)) {
            Set<Identifier> datatypeIdentifiers = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE).get(Vocabulary.OWL_DATA_RANGE);
            if (datatypeIdentifiers != null) {
                for (Identifier subject : new HashSet<Identifier>(datatypeIdentifiers)) {
                    if (isAnonymous(subject) && getDR(subject) == null) {
                        // _:x owl:OneOf rdf:nil or _:x owl:OneOf SEQ
                        // try _:x owl:OneOf rdf:nil -> not rdfs:Literal
                        boolean translated = false;
                        Map<Identifier, Set<Identifier>> objToSubj = builtInPredToBuiltInObjToSubjects.get(Vocabulary.OWL_ONE_OF);
                        if (objToSubj != null) {
                            Set<Identifier> subj = objToSubj.get(Vocabulary.RDF_NIL);
                            if (subj != null && subj.contains(subject)) {
                                mapDataRangeIdentifierToDataRange(subject, DataComplementOf.create(Datatype.RDFS_LITERAL));
                                removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_DATA_RANGE);
                                removeTriple(subject, Vocabulary.OWL_ONE_OF, Vocabulary.RDF_NIL);
                                translated = true;
                            }
                        }
                        if (!translated) {
                            // try _:x owl:OneOf SEQ
                            translateDataRange(subject, Vocabulary.OWL_ONE_OF);
                        }
                    }
                }
            }
        }
    }

    protected void parseOWL1ClassExpressions() {
        // Table 15
        if (builtInPredToBuiltInObjToSubjects.containsKey(Vocabulary.RDF_TYPE)) {
            Set<Identifier> datatypeIdentifiers = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE).get(Vocabulary.OWL_CLASS);
            if (datatypeIdentifiers != null) {
                for (Identifier subject : new HashSet<Identifier>(datatypeIdentifiers)) {
                    // subject rdf:type owl:Class
                    if (isAnonymous(subject) && getCE(subject) == null) {
                        // _:x owl:unionOf rdf:nil . 
                        Map<Identifier, Set<Identifier>> objToSubj = builtInPredToBuiltInObjToSubjects.get(Vocabulary.OWL_UNION_OF);
                        if (objToSubj != null) {
                            Set<Identifier> subj = objToSubj.get(Vocabulary.RDF_NIL);
                            if (subj != null && subj.contains(subject)) {
                                mapClassIdentifierToClassExpression(subject, Clazz.NOTHING);
                                removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_CLASS);
                                removeTriple(subject, Vocabulary.OWL_UNION_OF, Vocabulary.RDF_NIL);
                                continue;
                            }
                        }
                        // _:x owl:unionOf ClassIRI .
                        Map<Identifier, Set<Identifier>> subjToObj = builtInPredToSubToObjects.get(Vocabulary.OWL_UNION_OF);
                        if (subjToObj != null && subjToObj.containsKey(subject)) {
                            Set<Identifier> obj = subjToObj.get(subject);
                            if (obj.size() == 1) {
                                Identifier object = obj.iterator().next();
                                if (!isAnonymous(object) && getCE(object) != null) {
                                    mapClassIdentifierToClassExpression(subject, getCE(object));
                                    removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_CLASS);
                                    removeTriple(subject, Vocabulary.OWL_UNION_OF, object);
                                    continue;
                                }
                            }
                            else {
                                throw new RuntimeException("Error: More than one object for the triple with subject " + subject + " and object " + Vocabulary.OWL_UNION_OF + ". ");
                            }
                        }
                        // _:x owl:intersectionOf rdf:nil . 
                        objToSubj = builtInPredToBuiltInObjToSubjects.get(Vocabulary.OWL_INTERSECTION_OF);
                        if (objToSubj != null) {
                            Set<Identifier> subj = objToSubj.get(Vocabulary.RDF_NIL);
                            if (subj != null && subj.contains(subject)) {
                                mapClassIdentifierToClassExpression(subject, Clazz.THING);
                                removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_CLASS);
                                removeTriple(subject, Vocabulary.OWL_INTERSECTION_OF, Vocabulary.RDF_NIL);
                                continue;
                            }
                        }
                        // _:x owl:inersectionOf ClassIRI .
                        subjToObj = builtInPredToSubToObjects.get(Vocabulary.OWL_INTERSECTION_OF);
                        if (subjToObj != null && subjToObj.containsKey(subject)) {
                            Set<Identifier> obj = subjToObj.get(subject);
                            if (obj.size() == 1) {
                                Identifier object = obj.iterator().next();
                                if (!isAnonymous(object) && getCE(object) != null) {
                                    mapClassIdentifierToClassExpression(subject, getCE(object));
                                    removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_CLASS);
                                    removeTriple(subject, Vocabulary.OWL_INTERSECTION_OF, object);
                                    continue;
                                }
                            }
                            else {
                                throw new RuntimeException("Error: More than one object for the triple with subject " + subject + " and object " + Vocabulary.OWL_UNION_OF + ". ");
                            }
                        }
                        // _:x owl:oneOf rdf:nil . 
                        objToSubj = builtInPredToBuiltInObjToSubjects.get(Vocabulary.OWL_ONE_OF);
                        if (objToSubj != null) {
                            Set<Identifier> subj = objToSubj.get(Vocabulary.RDF_NIL);
                            if (subj != null && subj.contains(subject)) {
                                mapClassIdentifierToClassExpression(subject, Clazz.NOTHING);
                                removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_CLASS);
                                removeTriple(subject, Vocabulary.OWL_ONE_OF, Vocabulary.RDF_NIL);
                            }
                        }
                    }
                }
            }
        }
    }

    protected void parseClassExpressions() {
        Map<Identifier, Set<Identifier>> objToSubjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE);
        if (objToSubjects != null) {
            Set<Identifier> identifiers = objToSubjects.get(Vocabulary.OWL_CLASS);
            if (identifiers != null) {
                for (Identifier subject : new HashSet<Identifier>(identifiers)) {
                    if (isAnonymous(subject)) {
                        translateClassExpression(subject);
                        removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_CLASS);
                    }
                }
            }
            identifiers = objToSubjects.get(Vocabulary.OWL_RESTRICTION);
            if (identifiers != null) {
                for (Identifier subject : new HashSet<Identifier>(identifiers)) {
                    if (isAnonymous(subject)) {
                        translateClassExpression(subject);
                        removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.OWL_RESTRICTION);
                    }
                }
            }
        }
    }

    public void translateClassExpression(Identifier subject) {
        if (getCE(subject) == null) {
            boolean isTranslated = false;
            for (Identifier predicate : classExpressionPredicateHandlers.keySet()) {
                if (builtInPredToSubToObjects.containsKey(predicate) && builtInPredToSubToObjects.get(predicate).containsKey(subject)) {
                    isTranslated = true;
                    translateClassExpression(subject, predicate);
                    break;
                }
            }
            if (!isTranslated)
                throw new RuntimeException("Error: Could not translate class expression: " + subject + ". Maybe a class declaration is missing? ");
        }
    }

    public void translateClassExpression(Identifier subject, Identifier predicate) {
        Identifier object = getObject(subject, predicate, false);
        if (object != null) {
            removeTriple(subject, predicate, object);
            classExpressionPredicateHandlers.get(predicate).handleTriple(subject, predicate, object);
        }
        else {
            // TODO: error handling
            System.err.println("error");
        }
    }

    public Set<PropertyExpression> translateToPropertyExpressionSet(Identifier listMainNode) {
        return propertyListTranslator.translateToSet(listMainNode);
    }

    public List<PropertyExpression> translateToPropertyExpressionList(Identifier listMainNode) {
        return propertyListTranslator.translateToList(listMainNode);
    }

    public Set<ClassExpression> translateToClassExpressionSet(Identifier listMainNode) {
        return classExpressionListTranslator.translateToSet(listMainNode);
    }

    public Set<Individual> translateToIndividualSet(Identifier listMainNode) {
        return individualListTranslator.translateToSet(listMainNode);
    }

    protected void parseDataRanges() {
        if (builtInPredToBuiltInObjToSubjects.containsKey(Vocabulary.RDF_TYPE)) {
            Set<Identifier> datatypeIdentifiers = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE).get(Vocabulary.RDFS_DATATYPE);
            if (datatypeIdentifiers != null) {
                for (Identifier subject : new HashSet<Identifier>(datatypeIdentifiers)) {
                    if (isAnonymous(subject)) {
                        translateDataRange((AnonymousIndividual) subject);
                        removeTriple(subject, Vocabulary.RDF_TYPE, Vocabulary.RDFS_DATATYPE);
                    }
                }
            }
        }
    }

    public void translateDataRange(Identifier subject) {
        if (getDR(subject) == null) {
            boolean isTranslated = false;
            for (Identifier predicate : dataRangePredicateHandlers.keySet()) {
                if (builtInPredToSubToObjects.containsKey(predicate) && builtInPredToSubToObjects.get(predicate).containsKey(subject)) {
                    isTranslated = true;
                    translateDataRange(subject, predicate);
                    break;
                }
            }
            if (!isTranslated)
                throw new RuntimeException("Error: Could not translate data range: " + subject + ". Maybe a declaration for a custom datatype is missing? ");
        }
    }

    public void translateDataRange(Identifier subject, Identifier predicate) {
        if (builtInPredToSubToObjects.get(predicate).get(subject).size() == 1) {
            Identifier object = builtInPredToSubToObjects.get(predicate).get(subject).iterator().next();
            removeTriple(subject, predicate, object);
            dataRangePredicateHandlers.get(predicate).handleTriple(subject, predicate, object);
        }
        else {
            // TODO: error handling
            System.err.println("error");
        }
    }

    public Set<DataRange> translateToDataRangeSet(Identifier listMainNode) {
        return dataRangeListTranslator.translateToSet(listMainNode);
    }

    public Set<Literal> translateToLiteralSet(Identifier listMainNode) {
        return literalListTranslator.translateToSet(listMainNode);
    }

    public Set<FacetRestriction> translateToFacetRestrictionSet(Identifier subject) {
        // subject owl:withRestrictions listMainNode
        Identifier predicate = Vocabulary.OWL_WITH_RESTRICTIONS;
        Set<Identifier> listMainNodes = getObjects(subject, predicate);
        if (listMainNodes != null && listMainNodes.size() == 1) {
            Identifier mainNode = listMainNodes.iterator().next();
            removeTriple(subject, predicate, mainNode);
            return faceRestrictionListTranslator.translateToSet(mainNode);
        }
        else {
            // TODO: error handling
            System.err.println("error");
            return new HashSet<FacetRestriction>();
        }
    }

    protected void parseObjectProperties() {
        // We can have two triples: _:x owl:inverseOf *:y1 . _:x owl:inverseOf *:y2.
        // At most one of *:y1, *:y2 can be a blank node and if it is, then the triple
        // represents an InverseObjectProperties axiom and we should not use it here
        // if both are IRIs, we have to check whether there is an axiom annotation for
        // one of *:y1 or *:y2, if it is, then this triple is part of an
        // InverseObjectProperties axiom and we should not mistake it for ObjectInverseOf.
        // Without an axiom annotation, we can choose any one, the other one then
        // becomes the InverseObjectProperties axiom. This is non-deterministic, but both
        // possible outcomes are equivalent.
        // _:0 owl:inverseOf <http://example.org/op3> .
        // _:0 owl:inverseOf <http://example.org/op2> .
        // _:1 rdf:type owl:Axiom .
        // _:1 owl:annotatedSource _:0 .
        // _:1 owl:annotatedProperty owl:inverseOf .
        // _:1 owl:annotatedTarget <http://example.org/op2> .
        // _:1 rdfs:label "Anno@"^^rdf:PlainLiteral .
        // Here it is clear from the annotation that _:0 owl:inverseOf <http://example.org/op2> .
        // is an InverseObjectProperties axiom, whereas _:0 owl:inverseOf <http://example.org/op3> .
        // says that _:0 is to be parsed as ObjectInverseOf(<http://example.org/op3>)
//        printIndexedTriples();
        Map<Identifier, Set<Identifier>> objToSubj = builtInPredToBuiltInObjToSubjects.get(Vocabulary.OWL_ANNOTATED_PROPERTY);
        if (objToSubj != null) {
            Set<Identifier> reificationSubjects = objToSubj.get(Vocabulary.OWL_INVERSE_OF);
            if (reificationSubjects != null) {
                // _:1, _:100
                for (Identifier reificationSubject : reificationSubjects) {
                    Identifier reifiedSubject = getObject(reificationSubject, Vocabulary.OWL_ANNOTATED_SOURCE, false);
                    if (isAnonymous(reifiedSubject)) {
                        Identifier reifiedObject = getObject(reificationSubject, Vocabulary.OWL_ANNOTATED_TARGET, false);
                        Set<Identifier> axiomObjectsForSubject = subjOfInversePropertiesAxiomToObjects.get(reifiedSubject);
                        if (axiomObjectsForSubject == null) {
                            axiomObjectsForSubject = new HashSet<Identifier>();
                            subjOfInversePropertiesAxiomToObjects.put(reifiedSubject, axiomObjectsForSubject);
                        }
                        axiomObjectsForSubject.add(reifiedObject);
                    }
                }
            }
        }
        // Table 11
        Map<Identifier, Set<Identifier>> inverses = builtInPredToSubToObjects.get(Vocabulary.OWL_INVERSE_OF);
        if (inverses != null) {
            for (Identifier subject : new HashSet<Identifier>(inverses.keySet())) {
                if (isAnonymous(subject)) {
                    if (subjOfInversePropertiesAxiomToObjects.containsKey(subject)) {
                        // take out those that correspond to axioms instead of ObjectInverseOf
                        inverses.get(subject).removeAll(subjOfInversePropertiesAxiomToObjects.get(subject));
                    }
                    translateObjectPropertyExpression(subject, inverses);
                } // else it is an InverseobjectProperties axiom, parsed later 
            }
            if (builtInPredToSubToObjects.get(Vocabulary.OWL_INVERSE_OF) != null && builtInPredToSubToObjects.get(Vocabulary.OWL_INVERSE_OF).isEmpty())
                builtInPredToSubToObjects.remove(Vocabulary.OWL_INVERSE_OF);
        }
    }

    public void translateObjectPropertyExpression(Identifier subject, Map<Identifier, Set<Identifier>> inverses) {
        if (!OPE.containsKey(subject) && !OPEExt.contains(subject)) {
            Set<Identifier> inverseProperties = inverses.get(subject);
            boolean translated = false;
            if (inverseProperties != null) {
//                if (inverseProperties.size()!=1)
//                    throw new RuntimeException("A property represented by blank node "+subject+" is the inverse of more than one property, which is not allowed. ");
//                else {
                for (Identifier object : inverseProperties) {
                    if (!isAnonymous(object)) {
                        translated = true;
                        removeTriple(subject, Vocabulary.OWL_INVERSE_OF, object);
                        translateObjectPropertyExpression(object, inverses);
                        ObjectPropertyExpression inverseOf = getOPE(object);
                        if (inverseOf != null)
                            OPE.put(subject, ObjectInverseOf.create(inverseOf));
                        else
                            //TODO: error handling
                            System.err.println("error");
                        break;
                    }
                }
            }
            if (!translated)
                throw new RuntimeException("Error: An inverse object property represented by node " + subject + " cannot be resolved to an object property expression. ");
        }
    }

    protected void printIndexedTriples() {
        Prefixes prefixes = Prefixes.STANDARD_PREFIXES;
        for (Identifier predicate : builtInPredToBuiltInObjToSubjects.keySet()) {
            Map<Identifier, Set<Identifier>> objToSubj = builtInPredToBuiltInObjToSubjects.get(predicate);
            for (Identifier object : objToSubj.keySet()) {
                for (Identifier subject : objToSubj.get(object)) {
                    System.out.println(subject.toString(prefixes) + " " + predicate.toString(prefixes) + " " + object.toString(prefixes) + " .");
                }
            }
        }
        for (Identifier predicate : builtInPredToSubToObjects.keySet()) {
            Map<Identifier, Set<Identifier>> subjToObj = builtInPredToSubToObjects.get(predicate);
            for (Identifier subject : subjToObj.keySet()) {
                for (Identifier object : subjToObj.get(subject)) {
                    System.out.println(subject.toString(prefixes) + " " + predicate.toString(prefixes) + " " + object.toString(prefixes) + " .");
                }
            }
        }
        for (Identifier subject : subjToPredToObjects.keySet()) {
            Map<Identifier, Set<Identifier>> predToObj = subjToPredToObjects.get(subject);
            for (Identifier predicate : predToObj.keySet()) {
                for (Identifier object : predToObj.get(predicate)) {
                    System.out.println(subject.toString(prefixes) + " " + predicate.toString(prefixes) + " " + object.toString(prefixes) + " .");
                }
            }
        }
    }

    protected void parseAnnotations() {
        // Table 10
        Set<Identifier> annotationProperties = new HashSet<Identifier>(APE.keySet());
        annotationProperties.addAll(APEExt);
        annotationProperties.addAll(APE.keySet());
        for (Identifier annotationProperty : annotationProperties) {
            for (Identifier[] subjObj : getSubjectObjectMap(annotationProperty)) {
                // x *:y xlt <-> subjObj[0] annotationProperty subjObj[1]
                if (subjObj[1] instanceof AnnotationValue) {
                    Set<Annotation> annotationsForX = ANN.get(subjObj[0]);
                    if (annotationsForX == null) {
                        annotationsForX = new HashSet<Annotation>();
                        ANN.put(subjObj[0], annotationsForX);
                    }
                    annotationsForX.add(Annotation.create(getAPE(annotationProperty), (AnnotationValue) subjObj[1], getAnnotationAnnotations(subjObj[0], annotationProperty, subjObj[1])));
                    removeTriple(subjObj[0], (Identifier) annotationProperty, subjObj[1]);
                }
            }
        }
    }

    protected Set<Annotation> getAnnotations(Identifier subject) {
        Set<Annotation> annotations = ANN.get(subject);
        if (annotations == null) {
            annotations = new HashSet<Annotation>();
            Map<Identifier, Set<Identifier>> predToObjects = getPredicateToObjects(subject);
            if (!predToObjects.isEmpty()) {
                for (Identifier predicate : new HashSet<Identifier>(predToObjects.keySet())) {
                    AnnotationPropertyExpression ape = getAPE(predicate);
                    if (ape != null) {
                        for (Identifier object : new HashSet<Identifier>(predToObjects.get(predicate))) {
                            if (object instanceof AnnotationValue) {
                                annotations.add(Annotation.create(ape, (AnnotationValue) object, getAnnotationAnnotations(subject, predicate, object)));
                                ANN.put(subject, annotations);
                                removeTriple(subject, predicate, object);
                            }
                        }
                    }
                    else {
                        // TODO: error handling
                        System.err.println("error");
                    }
                }
            }
        }
        return annotations;
    }

    protected Set<Annotation> getAnnotationAnnotations(Identifier subject, Identifier predicate, Identifier object) {
        Set<Annotation> annotationAnnotations = new HashSet<Annotation>();
        Identifier reificationSubject = getReificationSubjectFor(subject, predicate, object, Vocabulary.OWL_ANNOTATION);
        if (reificationSubject != null) {
            // it has itself annotations
            removeTriple(reificationSubject, Vocabulary.RDF_TYPE, Vocabulary.OWL_ANNOTATION);
            removeTriple(reificationSubject, Vocabulary.OWL_ANNOTATED_SOURCE, subject);
            removeTriple(reificationSubject, Vocabulary.OWL_ANNOTATED_PROPERTY, predicate);
            removeTriple(reificationSubject, Vocabulary.OWL_ANNOTATED_TARGET, object);
            annotationAnnotations.addAll(getAnnotations(reificationSubject));
//            if (containsTripleWithSubjectOrObject(reificationSubject))
//                throw new RuntimeException("The subject of a reified axiom also occures as an object or subject of triples that do not belong to the annotation, which is not allowed: "+reificationSubject);
        }
        return annotationAnnotations;
    }

    public Identifier getFirst(Identifier subject) {
        Identifier object = null;
        Map<Identifier, Set<Identifier>> subjToObjects = builtInPredToSubToObjects.get(Vocabulary.RDF_FIRST);
        if (subjToObjects != null) {
            Set<Identifier> objects = subjToObjects.get(subject);
            if (objects != null) {
                if (objects.size() != 1)
                    throw new RuntimeException("Error: Could not translate list with main node: " + subject + " because there is more than one triple with subject " + subject + " and predicate rdf:first. ");
                object = objects.iterator().next();
            }
        }
        removeTriple(subject, Vocabulary.RDF_FIRST, object);
        return object;
    }

    public Identifier getRest(Identifier subject) {
        Identifier object = null;
        Map<Identifier, Set<Identifier>> subjToObjects = builtInPredToSubToObjects.get(Vocabulary.RDF_REST);
        if (subjToObjects != null) {
            Set<Identifier> objects = subjToObjects.get(subject);
            if (objects != null) {
                if (objects.size() != 1)
                    throw new RuntimeException("Error: Could not translate list with main node: " + subject + " because there is more than one triple with subject " + subject + " and predicate rdf:rest. ");
                object = objects.iterator().next();
            }
        }
        if (object == null) {
            // maybe rdf:nil?
            Map<Identifier, Set<Identifier>> objToSubjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_REST);
            if (objToSubjects != null && objToSubjects.containsKey(Vocabulary.RDF_NIL) && objToSubjects.get(Vocabulary.RDF_NIL).contains(subject))
                object = Vocabulary.RDF_NIL;
            else
                throw new RuntimeException("There is no triple with predicate rdf:rest, but we have to translate a list with main node: " + subject);
        }
        removeTriple(subject, Vocabulary.RDF_REST, object);
        return object;
    }

    protected boolean containsTripleWithSubjectOrObject(Identifier id) {
        if (subjToPredToObjects.keySet().contains(id)) return true;
        for (Identifier pred : builtInPredToSubToObjects.keySet()) {
            Map<Identifier, Set<Identifier>> subj2objects = builtInPredToSubToObjects.get(pred);
            if (subj2objects.keySet().contains(id)) return true;
            for (Identifier subj : subj2objects.keySet())
                if (subj2objects.get(subj).contains(id)) return true;
        }
        for (Identifier pred : builtInPredToBuiltInObjToSubjects.keySet()) {
            Map<Identifier, Set<Identifier>> objToSubj = builtInPredToBuiltInObjToSubjects.get(pred);
            if (objToSubj.keySet().contains(id)) return true;
            for (Identifier obj : objToSubj.keySet())
                if (objToSubj.get(obj).contains(id)) return true;
        }
        for (Identifier subj : subjToPredToObjects.keySet()) {
            Map<Identifier, Set<Identifier>> pred2objects = subjToPredToObjects.get(subj);
            for (Identifier pred : pred2objects.keySet()) {
                if (pred2objects.get(pred).contains(id)) return true;
            }
        }
        return false;
    }

    protected Identifier getReificationSubjectFor(Identifier subject, Identifier predicate, Identifier object, Identifier axiomType) {
        Set<Identifier> reificationSubjects = getSubjects(Vocabulary.RDF_TYPE, axiomType);
        for (Identifier reificationSubject : reificationSubjects) {
            if (containsTriple(reificationSubject, Vocabulary.OWL_ANNOTATED_PROPERTY, predicate)
                    && containsTriple(reificationSubject, Vocabulary.OWL_ANNOTATED_TARGET, object)
                    && containsTriple(reificationSubject, Vocabulary.OWL_ANNOTATED_SOURCE, subject))
                return reificationSubject;
        }
        return null;
    }

    public Identifier[] getReifiedTriple(Identifier subject, Identifier axiomType) {
        if (!isAnonymous(subject))
            throw new RuntimeException("Only blank nodes can be the subject of reified triples, but here we have: " + subject + " rdf:type " + axiomType.toString());
        Identifier reifiedSubject;
        Identifier reifiedPredicate;
        Identifier reifiedObject;
        Map<Identifier, Set<Identifier>> objToSubjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE);
        if (objToSubjects != null && objToSubjects.containsKey(axiomType)) {
            if (objToSubjects.get(axiomType).contains(subject)) {
                Identifier subjectIdentifier = (axiomType == Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION) ? Vocabulary.OWL_SOURCE_INDIVIDUAL : Vocabulary.OWL_ANNOTATED_SOURCE;
                Identifier predicateIdentifier = (axiomType == Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION) ? Vocabulary.OWL_ASSERTION_PROPERTY : Vocabulary.OWL_ANNOTATED_PROPERTY;
                Identifier objectIdentifier = (axiomType == Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION) ? Vocabulary.OWL_TARGET_INDIVIDUAL : Vocabulary.OWL_ANNOTATED_TARGET;
                Set<Identifier> reifiedSubjects = getObjects(subject, subjectIdentifier);
                if (reifiedSubjects == null || reifiedSubjects.size() < 1)
                    throw new RuntimeException("Error: We didn't get a reified subject for a reification axiom: " + subject + " rdf:type " + axiomType);
                else if (reifiedSubjects.size() > 1)
                    throw new RuntimeException("Error: We got more than one reified subject for a reification axiom: " + subject + " rdf:type " + axiomType);
                else reifiedSubject = reifiedSubjects.iterator().next();

                Set<Identifier> reifiedPredicates = getObjects(subject, predicateIdentifier);
                if (reifiedPredicates == null || reifiedPredicates.size() < 1)
                    throw new RuntimeException("Error: We didn't get a reified predicate for a reification axiom: " + subject + " rdf:type " + axiomType);
                else if (reifiedPredicates.size() > 1)
                    throw new RuntimeException("Error: We got more than one reified predicate for a reification axiom: " + subject + " rdf:type " + axiomType);
                else reifiedPredicate = reifiedPredicates.iterator().next();

                Set<Identifier> reifiedObjects = getObjects(subject, objectIdentifier);
                if (reifiedObjects == null || reifiedObjects.size() < 1) {
                    if (objectIdentifier == Vocabulary.OWL_TARGET_INDIVIDUAL) {
                        // try literal
                        objectIdentifier = Vocabulary.OWL_TARGET_VALUE;
                        reifiedObjects = getObjects(subject, objectIdentifier);
                    }
                }
                if (reifiedObjects == null || reifiedObjects.size() < 1)
                    throw new RuntimeException("Error: We didn't get a reified object for a reification axiom: " + subject + " rdf:type " + axiomType);
                else if (reifiedObjects.size() > 1)
                    throw new RuntimeException("Error: We got more than one reified object for a reification axiom: " + subject + " rdf:type " + axiomType);
                else reifiedObject = reifiedObjects.iterator().next();
                removeTriple(subject, subjectIdentifier, reifiedSubject);
                removeTriple(subject, predicateIdentifier, reifiedPredicate);
                removeTriple(subject, objectIdentifier, reifiedObject);
                return new Identifier[]{reifiedSubject, reifiedPredicate, reifiedObject};
            }
        }
        return null;
    }

    protected Map<Identifier, Set<Identifier>> getPredicateToObjects(Identifier subject) {
        Map<Identifier, Set<Identifier>> predToObjects = new HashMap<Identifier, Set<Identifier>>();
        if (subjToPredToObjects.containsKey(subject)) {
            predToObjects = subjToPredToObjects.get(subject);
        }
        for (Identifier predicate : builtInPredToSubToObjects.keySet()) {
            Map<Identifier, Set<Identifier>> subjToObjects = builtInPredToSubToObjects.get(predicate);
            Set<Identifier> objects = subjToObjects.get(subject);
            if (objects != null) {
                Set<Identifier> objs = predToObjects.get(predicate);
                if (objs == null)
                    predToObjects.put(predicate, objects);
                else
                    objs.addAll(objects);
            }
        }
        for (Identifier predicate : builtInPredToBuiltInObjToSubjects.keySet()) {
            Map<Identifier, Set<Identifier>> objToSubjects = builtInPredToBuiltInObjToSubjects.get(predicate);
            for (Identifier obj : objToSubjects.keySet()) {
                Set<Identifier> subjects = objToSubjects.get(subject);
                if (subjects != null && subjects.contains(subject)) {
                    Set<Identifier> objs = predToObjects.get(predicate);
                    if (objs == null) {
                        objs = new HashSet<Identifier>();
                        predToObjects.put(predicate, objs);
                    }
                    objs.add(obj);
                }
            }
        }
        return predToObjects;
    }

    protected Set<Identifier[]> getSubjectObjectMap(Identifier predicate) {
        Set<Identifier[]> map = new HashSet<Identifier[]>();
        Identifier[] subjObj;
        if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(predicate)) {
            if (builtInPredToSubToObjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> subjToObjects = builtInPredToSubToObjects.get(predicate);
                for (Identifier subject : subjToObjects.keySet()) {
                    for (Identifier object : subjToObjects.get(subject)) {
                        subjObj = new Identifier[2];
                        subjObj[0] = subject;
                        subjObj[1] = object;
                        map.add(subjObj);
                    }
                }
            }
            if (builtInPredToBuiltInObjToSubjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> builtInObjectToSubjects = builtInPredToBuiltInObjToSubjects.get(predicate);
                for (Identifier builtInObject : builtInObjectToSubjects.keySet()) {
                    for (Identifier subject : builtInObjectToSubjects.get(builtInObject)) {
                        subjObj = new Identifier[2];
                        subjObj[0] = subject;
                        subjObj[1] = builtInObject;
                        map.add(subjObj);
                    }
                }
            }
        }
        else {
            for (Identifier subject : subjToPredToObjects.keySet()) {
                Map<Identifier, Set<Identifier>> predToObjects = subjToPredToObjects.get(subject);
                if (predToObjects.containsKey(predicate)) {
                    for (Identifier object : predToObjects.get(predicate)) {
                        subjObj = new Identifier[2];
                        subjObj[0] = subject;
                        subjObj[1] = object;
                        map.add(subjObj);
                    }
                }
            }
        }
        return map;
    }

    public Set<Identifier> getObjects(Identifier subject, Identifier predicate) {
        Set<Identifier> objects = new HashSet<Identifier>();
        if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(predicate)) {
            if (builtInPredToSubToObjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> subjToObjects = builtInPredToSubToObjects.get(predicate);
                if (subjToObjects.containsKey(subject)) objects.addAll(subjToObjects.get(subject));
            }
            if (builtInPredToBuiltInObjToSubjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> builtInObjectToSubjects = builtInPredToBuiltInObjToSubjects.get(predicate);
                for (Identifier builtInObject : builtInObjectToSubjects.keySet()) {
                    if (builtInObjectToSubjects.get(builtInObject).contains(subject)) {
                        objects.add(builtInObject);
                    }
                }
            }
            return objects;
        }
        else {
            Map<Identifier, Set<Identifier>> predToObjects = subjToPredToObjects.get(subject);
            if (predToObjects != null && predToObjects.containsKey(predicate))
                return predToObjects.get(predicate);
            else
                return objects;
        }
    }

    public Identifier getObject(Identifier subject, Identifier predicate, boolean consume) {
        Set<Identifier> objects = getObjects(subject, predicate);
        if (objects.size() == 1) {
            Identifier object = objects.iterator().next();
            if (consume) removeTriple(subject, predicate, object);
            return object;
        }
        else
            return null;
    }

    public Literal getLiteralObject(Identifier subject, Identifier predicate) {
        Set<Literal> literals = getLiteralObjects(subject, predicate);
        if (literals.isEmpty())
            return null;
        else if (literals.size() == 1) {
            Literal literal = literals.iterator().next();
            removeTriple(subject, predicate, literal);
            return literal;
        }
        else {
            // TODO: error handling
            throw new RuntimeException("error");
        }
    }

    public Set<Literal> getLiteralObjects(Identifier subject, Identifier predicate) {
        Set<Identifier> objects = getObjects(subject, predicate);
        Set<Literal> literals = new HashSet<Literal>();
        for (Identifier object : objects) {
            if (object instanceof Literal)
                literals.add((Literal) object);
            else {
                // TODO: error handling
                throw new RuntimeException("error");
            }
        }
        return literals;
    }

    protected Set<Identifier> getSubjects(Identifier predicate, Identifier object) {
        Set<Identifier> subjects = new HashSet<Identifier>();
        if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(predicate)) {
            if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(object) && builtInPredToBuiltInObjToSubjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> builtInObjectToSubjects = builtInPredToBuiltInObjToSubjects.get(predicate);
                if (builtInObjectToSubjects.containsKey(object))
                    return builtInObjectToSubjects.get(object);
            }
            else if (builtInPredToSubToObjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> subjectsToObjects = builtInPredToSubToObjects.get(predicate);
                for (Identifier subject : subjectsToObjects.keySet()) {
                    if (subjectsToObjects.get(subject).contains(object))
                        subjects.add(subject);
                }
            }
        }
        else {
            for (Identifier subject : subjToPredToObjects.keySet()) {
                Map<Identifier, Set<Identifier>> predToObjects = subjToPredToObjects.get(subject);
                Set<Identifier> objects = predToObjects.get(predicate);
                if (objects != null && objects.contains(object))
                    subjects.add(subject);
            }
        }
        return subjects;
    }

    protected boolean containsTriple(Identifier subject, Identifier predicate, Identifier object) {
        if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(predicate)) {
            if (Vocabulary.BUILT_IN_PREDICATE_IRIS.contains(object) && builtInPredToBuiltInObjToSubjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> builtInObjectToSubjects = builtInPredToBuiltInObjToSubjects.get(predicate);
                if (builtInObjectToSubjects.containsKey(object))
                    return builtInObjectToSubjects.get(object).contains(subject);
            }
            else if (builtInPredToSubToObjects.containsKey(predicate)) {
                Map<Identifier, Set<Identifier>> subjectsToObjects = builtInPredToSubToObjects.get(predicate);
                if (subjectsToObjects.containsKey(subject))
                    return subjectsToObjects.get(subject).contains(object);
            }
        }
        else if (subjToPredToObjects.containsKey(subject)) {
            Map<Identifier, Set<Identifier>> predicatesToObjects = subjToPredToObjects.get(subject);
            if (predicatesToObjects.containsKey(predicate))
                return predicatesToObjects.get(predicate).contains(object);
        }
        return false;
    }

    protected void addReifiedDeclarations() {
        Map<Identifier, Set<Identifier>> objToSubjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE);
        if (objToSubjects != null) {
            Set<Identifier> subjects = objToSubjects.get(Vocabulary.OWL_AXIOM);
            if (subjects != null) {
                for (Identifier subject : subjects) {
                    if (isAnonymous(subject)) {
                        if (builtInPredToBuiltInObjToSubjects.containsKey(Vocabulary.OWL_ANNOTATED_PROPERTY)) {
                            Map<Identifier, Set<Identifier>> apObjToSubjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.OWL_ANNOTATED_PROPERTY);
                            for (Identifier reifiedPredicate : apObjToSubjects.keySet()) {
                                if (apObjToSubjects.get(reifiedPredicate).contains(subject)) {
                                    Map<Identifier, TripleHandler> handlerMap = streamingByPredicateAndObjectHandlers.get(reifiedPredicate);
                                    if (handlerMap != null && builtInPredToBuiltInObjToSubjects.containsKey(Vocabulary.OWL_ANNOTATED_TARGET)) {
                                        Map<Identifier, Set<Identifier>> annotatedTargetToDecl = builtInPredToBuiltInObjToSubjects.get(Vocabulary.OWL_ANNOTATED_TARGET);
                                        for (Identifier reifiedObject : annotatedTargetToDecl.keySet()) {
                                            TripleHandler handler = handlerMap.get(reifiedObject);
                                            if (handler != null
                                                    && annotatedTargetToDecl.get(reifiedObject).contains(subject)
                                                    && builtInPredToSubToObjects.containsKey(Vocabulary.OWL_ANNOTATED_SOURCE)
                                                    && builtInPredToSubToObjects.get(Vocabulary.OWL_ANNOTATED_SOURCE).containsKey(subject)) {
                                                for (Identifier reifiedSubject : builtInPredToSubToObjects.get(Vocabulary.OWL_ANNOTATED_SOURCE).get(subject))
                                                    handler.handleStreaming(reifiedSubject, reifiedPredicate, reifiedObject);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void checkOnytologyIRIIsNeverObject() {
        for (Identifier pred : builtInPredToSubToObjects.keySet()) {
            Map<Identifier, Set<Identifier>> subj2objects = builtInPredToSubToObjects.get(pred);
            for (Identifier subj : subj2objects.keySet()) {
                for (Identifier obj : subj2objects.get(subj)) {
                    if (obj == ontologyIRI)
                        throw new RuntimeException("The ontology IRI " + ontologyIRI + " cannot be used as an object of a triple, but here we have: " + subj + " " + pred + " " + obj + " . ");
                }
            }
        }
        for (Identifier subj : subjToPredToObjects.keySet()) {
            Map<Identifier, Set<Identifier>> pred2objects = subjToPredToObjects.get(subj);
            for (Identifier pred : pred2objects.keySet()) {
                for (Identifier obj : pred2objects.get(pred)) {
                    if (obj == ontologyIRI)
                        throw new RuntimeException("The ontology IRI " + ontologyIRI + " cannot be used as an object of a triple, but here we have: " + subj + " " + pred + " " + obj + " . ");
                }
            }
        }
    }

    protected void checkImportsOnlyForOntologyIRI() {
        Map<Identifier, Set<Identifier>> map = builtInPredToSubToObjects.get(Vocabulary.OWL_IMPORTS);
        if (map != null && !map.isEmpty()) {
            for (Identifier iri : map.keySet()) {
                if (iri != ontologyIRI)
                    throw new RuntimeException("The ontology has IRI " + ontologyIRI + " but an import triple uses a different subject, which is not allowed: " + iri + " owl:imports " + map.get(iri).iterator().next() + ". and " + ontologyIRI + " rdf:type owl:ontology.");
            }
        }
    }

    protected void checkVersionIRIsForOntologyIRI() {
        if (ontologyIRIToVersionIRIs.keySet().size() > 1) {
            throw new RuntimeException("The parsed ontology has version IRIs for more than one ontology IRI, which is not allowed. ");
        }
        else if (ontologyIRIToVersionIRIs.keySet().size() == 1 && ontologyIRIToVersionIRIs.keySet().iterator().next() != ontologyIRI) {
            throw new RuntimeException("The parsed ontology has version IRIs for an IRI different from the ontology IRI, which is not allowed. ");
        }
    }

    protected void removeOWL1DoubleTypes() {
        // Table 5
        Map<Identifier, Set<Identifier>> obj2subjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_TYPE);
        if (obj2subjects != null) {
            Set<Identifier> subjects = obj2subjects.get(Vocabulary.RDFS_CLASS);
            if (subjects != null) {
                Set<Identifier> subjectsOfRedundantTriples = new HashSet<Identifier>();
                for (Identifier subj : subjects) {
                    Set<Identifier> owl2type = obj2subjects.get(Vocabulary.OWL_CLASS);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.RDFS_DATATYPE);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_DATA_RANGE);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_RESTRICTION);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.RDFS_DATATYPE);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                }
                subjects.removeAll(subjectsOfRedundantTriples);
                if (subjects.isEmpty()) obj2subjects.remove(Vocabulary.RDFS_CLASS);
                if (obj2subjects.isEmpty()) builtInPredToBuiltInObjToSubjects.remove(Vocabulary.RDF_TYPE);
            }
            subjects = obj2subjects.get(Vocabulary.OWL_CLASS);
            if (subjects != null) {
                Set<Identifier> subjectsOfRedundantTriples = new HashSet<Identifier>();
                for (Identifier subj : subjects) {
                    Set<Identifier> owl2type = obj2subjects.get(Vocabulary.OWL_RESTRICTION);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                    }
                }
                subjects.removeAll(subjectsOfRedundantTriples);
                if (subjects.isEmpty()) obj2subjects.remove(Vocabulary.OWL_CLASS);
                if (obj2subjects.isEmpty()) builtInPredToBuiltInObjToSubjects.remove(Vocabulary.RDF_TYPE);
            }
            subjects = obj2subjects.get(Vocabulary.RDF_PROPERTY);
            if (subjects != null) {
                Set<Identifier> subjectsOfRedundantTriples = new HashSet<Identifier>();
                for (Identifier subj : subjects) {
                    Set<Identifier> owl2type = obj2subjects.get(Vocabulary.OWL_OBJECT_PROPERTY);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_FUNCTIONAL_PROPERTY);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_INVERSE_FUNCTIONAL_PROPERTY);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_TRANSITIVE_PROPERTY);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_DATA_PROPERTY);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_ANNOTATION_PROPERTY);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                    owl2type = obj2subjects.get(Vocabulary.OWL_ONTOLOGY_PROPERTY);
                    if (owl2type != null && owl2type.contains(subj)) {
                        subjectsOfRedundantTriples.add(subj);
                        break;
                    }
                }
                subjects.removeAll(subjectsOfRedundantTriples);
                if (subjects.isEmpty()) obj2subjects.remove(Vocabulary.RDF_PROPERTY);
                if (obj2subjects.isEmpty()) builtInPredToBuiltInObjToSubjects.remove(Vocabulary.RDF_TYPE);
            }
            subjects = obj2subjects.get(Vocabulary.RDF_LIST);
            Map<Identifier, Set<Identifier>> firstSubjObjects = builtInPredToSubToObjects.get(Vocabulary.RDF_FIRST);
            Map<Identifier, Set<Identifier>> restSubjObjects = builtInPredToSubToObjects.get(Vocabulary.RDF_REST);
            Set<Identifier> restNilSubjects = new HashSet<Identifier>();
            if (builtInPredToBuiltInObjToSubjects.containsKey(Vocabulary.RDF_REST)) {
                Map<Identifier, Set<Identifier>> restObjSubjects = builtInPredToBuiltInObjToSubjects.get(Vocabulary.RDF_REST);
                if (restObjSubjects != null && restObjSubjects.containsKey(Vocabulary.RDF_NIL))
                    restNilSubjects = restObjSubjects.get(Vocabulary.RDF_NIL);
            }
            if (subjects != null) {
                Set<Identifier> subjectsOfRedundantTriples = new HashSet<Identifier>();
                for (Identifier subj : subjects) {
                    if (firstSubjObjects.containsKey(subj) && (restSubjObjects.containsKey(subj) || restNilSubjects.contains(subj)))
                        subjectsOfRedundantTriples.add(subj);
                }
                subjects.removeAll(subjectsOfRedundantTriples);
                if (subjects.isEmpty()) obj2subjects.remove(Vocabulary.RDF_LIST);
                if (obj2subjects.isEmpty()) builtInPredToBuiltInObjToSubjects.remove(Vocabulary.RDF_TYPE);
            }
        }
    }

    public boolean allTriplesConsumed() {
        return builtInPredToBuiltInObjToSubjects.isEmpty() && builtInPredToSubToObjects.isEmpty() && subjToPredToObjects.isEmpty();
    }

    public ClassExpression getCE(Identifier identifier) {
        if (identifier == null)
            return null;
        ClassExpression ce = CE.get(identifier);
        if (ce == null && identifier instanceof IRI && CEExt.contains(identifier)) {
            ce = Clazz.create((IRI) identifier); // built-in or declared in queried ontology
        }
        else if (ce == null && isVariable(identifier) && !DR.containsKey(identifier) && !DRExt.contains(identifier)) {
            ce = ClassVariable.create(identifier.toString());
            mapClassIdentifierToClassExpression(identifier, ce); // infer type
        }
        return ce;
    }

    public ObjectPropertyExpression getOPE(Identifier identifier) {
        if (identifier == null)
            return null;
        ObjectPropertyExpression ope = OPE.get(identifier);
        if (ope == null && identifier instanceof IRI && OPEExt.contains(identifier)) {
            ope = ObjectProperty.create((IRI) identifier);
        }
        return ope;
    }

    public DataPropertyExpression getDPE(Identifier identifier) {
        if (identifier == null)
            return null;
        DataPropertyExpression dpe = DPE.get(identifier);
        if (dpe == null && identifier instanceof IRI && DPEExt.contains(identifier)) {
            dpe = DataProperty.create((IRI) identifier);
        }
        return dpe;
    }

    public AnnotationPropertyExpression getAPE(Identifier identifier) {
        if (identifier == null)
            return null;
        AnnotationPropertyExpression ape = APE.get(identifier);
        if (ape == null && identifier instanceof IRI && APEExt.contains(identifier)) {
            ape = AnnotationProperty.create((IRI) identifier);
        }
        return ape;
    }

    public Individual getIND(Identifier identifier) {
        if (identifier == null)
            return null;
        Individual ind = IND.get(identifier);
        if (ind == null)
            if (identifier instanceof IRI && INDExt.contains(identifier))
                ind = NamedIndividual.create((IRI) identifier);
            else if (isAnonymous(identifier))
                ind = AnonymousIndividual.create(identifier.toString());
            else if (isVariable(identifier))
                ind = IndividualVariable.create(identifier.toString());
            else
                ind = NamedIndividual.create(identifier.toString());
        return ind;
    }

    public DataRange getDR(Identifier identifier) {
        if (identifier == null)
            return null;
        DataRange dr = DR.get(identifier);
        if (dr == null && identifier instanceof IRI && DRExt.contains(identifier)) {
            dr = Datatype.create((IRI) identifier);
        }
        return dr;
    }

    public void setClassesInOntologySignature(Set<Clazz> classes) {
        for (Clazz cls : classes)
            CEExt.add(cls.getIRI());
    }

    public void setObjectPropertiesInOntologySignature(Set<ObjectProperty> objectProperties) {
        for (ObjectProperty op : objectProperties)
            OPEExt.add(op.getIRI());
    }

    public void setDataPropertiesInOntologySignature(Set<DataProperty> dataProperties) {
        for (DataProperty dp : dataProperties)
            DPEExt.add(dp.getIRI());
    }

    public void setAnnotationPropertiesInOntologySignature(Set<AnnotationProperty> annotationProperties) {
        for (AnnotationProperty ap : annotationProperties)
            APEExt.add(ap.getIRI());
    }

    public void setIndividualsInOntologySignature(Set<NamedIndividual> individuals) {
        for (NamedIndividual ind : individuals)
            INDExt.add(ind.getIRI());
    }

    public void setCustomDatatypesInOntologySignature(Set<Datatype> customDatatypes) {
        for (Datatype dt : customDatatypes)
            DRExt.add(dt.getIRI());
    }

    public void mapClassIdentifierToClassExpression(Identifier id, ClassExpression classExpression) {
        CE.put(id, classExpression);
    }

    public void mapObjectPropertyIdentifierToObjectProperty(Identifier id, ObjectPropertyExpression objectPropertyExpression) {
        OPE.put(id, objectPropertyExpression);
    }

    public void mapDataPropertyIdentifierToDataProperty(Identifier id, DataPropertyExpression dataProperty) {
        DPE.put(id, dataProperty);
    }

    public void mapAnnotationPropertyIdentifierToAnnotationProperty(Identifier id, AnnotationPropertyExpression annotationProperty) {
        APE.put(id, annotationProperty);
    }

    public void mapIndividualIdentifierToindividual(Identifier id, Individual individual) {
        IND.put(id, individual);
    }

    public void mapDataRangeIdentifierToDataRange(Identifier id, DataRange datatype) {
        DR.put(id, datatype);
    }

    public void addVariableForAnonymousIndividual(IndividualVariable variableForAnonymousIndividual) {
        variablesForAnonymousIndividuals.add(variableForAnonymousIndividual);
    }

    public Set<IndividualVariable> getVariablesForAnonymousIndividual() {
        return variablesForAnonymousIndividuals;
    }

    public NamedIndividual getNamedIndividual(Identifier id) {
        if (id instanceof IRI) return NamedIndividual.create((IRI) id);
        if (isVariable(id)) {
            // TODO: error handling
            System.err.println("error");
        }
        else if (isAnonymous(id)) {
            // TODO: error handling
            System.err.println("error");
        }
        return null;
    }

    public void addVersionIRI(Identifier ontologyIRI, Identifier versionIRI) {
        Set<Identifier> versionIRIs = ontologyIRIToVersionIRIs.get(ontologyIRI);
        if (versionIRIs == null) {
            versionIRIs = new HashSet<Identifier>();
            ontologyIRIToVersionIRIs.put(ontologyIRI, versionIRIs);
        }
        versionIRIs.add(versionIRI);
    }

    public boolean isAnonymous(Identifier iri) {
        return (iri instanceof AnonymousIndividual);
    }

    public boolean isVariable(Identifier iri) {
        return (iri instanceof Variable);
    }

    public void addAxiom(Axiom axiom) {
        axioms.add(axiom);
    }

    public Set<Axiom> getAxioms() {
        return axioms;
    }

    public Ontology getOntology() {
        Set<Clazz> classesInSignature = new HashSet<Clazz>();
        Set<ClassVariable> classesVariablesInSignature = new HashSet<ClassVariable>();
        for (Identifier cls : CE.keySet()) {
            if (cls instanceof Clazz)
                classesInSignature.add((Clazz) cls);
            if (cls instanceof ClassVariable)
                classesVariablesInSignature.add((ClassVariable) cls);
        }
        Identifier versionIRI = null;
        if (ontologyIRIToVersionIRIs.containsKey(ontologyIRI)) {
            Set<Identifier> iris = ontologyIRIToVersionIRIs.get(ontologyIRI);
            if (!iris.isEmpty())
                versionIRI = iris.iterator().next();
        }
        return Ontology.create(ontologyIRI, versionIRI, imports, axioms, ANN.get(ontologyIRI));
    }

    public boolean isOntologyIRI(Identifier iri) {
        return ontologyIRI == iri;
    }

    public Identifier getOntologyIRI() {
        return ontologyIRI;
    }

    public void setOntologyIRI(Identifier iri) {
        if (ontologyIRI != null)
            throw new RuntimeException("Error: The ontology has more than one IRI. It already has the IRI " + ontologyIRI + " and now it also got " + iri + ". ");
        else ontologyIRI = iri;
    }

    public void addImport(Import imported) {
        imports.add(imported);
    }

    public void addReifiedSubject(Identifier subject) {
        RIND.add(subject);
    }
}

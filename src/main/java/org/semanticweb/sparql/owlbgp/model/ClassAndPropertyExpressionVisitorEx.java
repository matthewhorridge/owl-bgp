package org.semanticweb.sparql.owlbgp.model;

import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectComplementOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasSelf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectOneOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public interface ClassAndPropertyExpressionVisitorEx<O> {

    O visit(Clazz clazz);

    O visit(ClassVariable classVariable);

    O visit(ObjectProperty objectProperty);

    O visit(ObjectInverseOf objectproperty);

    O visit(ObjectPropertyChain objectProperty);

    O visit(ObjectPropertyVariable objectPropertyVariable);

    O visit(ObjectIntersectionOf objectIntersectionOf);

    O visit(ObjectUnionOf objectUnionOf);

    O visit(ObjectComplementOf objectComplementOf);

    O visit(ObjectSomeValuesFrom objectSomeValuesFrom);

    O visit(ObjectAllValuesFrom objectAllValuesFrom);

    O visit(ObjectHasValue objectHasValue);

    O visit(ObjectMinCardinality objectMinCardinality);

    O visit(ObjectExactCardinality objectExactCardinality);

    O visit(ObjectMaxCardinality objectMaxCardinality);

    O visit(ObjectHasSelf objectHasSelf);

    O visit(ObjectOneOf objectOneOf);

    O visit(DataSomeValuesFrom dataSomeValuesFrom);

    O visit(DataAllValuesFrom dataAllValuesFrom);

    O visit(DataHasValue dataHasValue);

    O visit(DataMinCardinality dataMinCardinality);

    O visit(DataExactCardinality dataExactCardinality);

    O visit(DataMaxCardinality dataMaxCardinality);

}

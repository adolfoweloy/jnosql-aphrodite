/*
 *  Copyright (c) 2018 Otávio Santana and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *  You may elect to redistribute this code under either of these licenses.
 *  Contributors:
 *  Otavio Santana
 */
package org.jnosql.aphrodite.antlr;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jnosql.query.Condition;
import org.jnosql.query.Function;
import org.jnosql.query.FunctionValue;
import org.jnosql.query.InsertQuery;
import org.jnosql.query.InsertSupplier;
import org.jnosql.query.JSONValue;
import org.jnosql.query.NumberValue;
import org.jnosql.query.Operator;
import org.jnosql.query.ParamValue;
import org.jnosql.query.Sort;
import org.jnosql.query.StringValue;
import org.jnosql.query.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.json.JsonObject;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InsertSupplierTest {

    private InsertSupplier insertSupplier = new DefaultInsertSupplier();


    @Test
    public void shouldReturnErrorWhenStringIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> insertSupplier.apply(null));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"insert God (name = \"Diana\")"})
    public void shouldReturnParserQuery(String query) {
        InsertQuery insertQuery = checkInsertFromStart(query);
        List<Condition> conditions = insertQuery.getConditions();
        assertEquals(1, conditions.size());
        Condition condition = conditions.get(0);
        assertEquals("name", condition.getName());
        assertEquals(Operator.EQUALS, condition.getOperator());
        Value<?> value = condition.getValue();
        assertTrue(value instanceof StringValue);
        assertEquals("Diana", StringValue.class.cast(value).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"insert God (age = 30)"})
    public void shouldReturnParserQuery1(String query) {
        InsertQuery insertQuery = checkInsertFromStart(query);
        List<Condition> conditions = insertQuery.getConditions();
        assertEquals(1, conditions.size());
        Condition condition = conditions.get(0);
        assertEquals("age", condition.getName());
        assertEquals(Operator.EQUALS, condition.getOperator());
        Value<?> value = condition.getValue();
        assertTrue(value instanceof NumberValue);
        assertEquals(30L, NumberValue.class.cast(value).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"insert God (stamina = 32.23)"})
    public void shouldReturnParserQuery2(String query) {
        InsertQuery insertQuery = checkInsertFromStart(query);
        List<Condition> conditions = insertQuery.getConditions();
        assertEquals(1, conditions.size());
        Condition condition = conditions.get(0);
        assertEquals("stamina", condition.getName());
        assertEquals(Operator.EQUALS, condition.getOperator());
        Value<?> value = condition.getValue();
        assertTrue(value instanceof NumberValue);
        assertEquals(32.23, NumberValue.class.cast(value).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"insert God (siblings = {\"Apollo\": \"Brother\", \"Zeus\": \"Father\"})"})
    public void shouldReturnParserQuery3(String query) {
        InsertQuery insertQuery = checkInsertFromStart(query);
        List<Condition> conditions = insertQuery.getConditions();
        assertEquals(1, conditions.size());
        Condition condition = conditions.get(0);
        assertEquals("siblings", condition.getName());
        assertEquals(Operator.EQUALS, condition.getOperator());
        Value<?> value = condition.getValue();
        assertTrue(value instanceof JSONValue);
        JsonObject jsonObject = JSONValue.class.cast(value).get();
        assertEquals("Brother", jsonObject.getString("Apollo"));
        assertEquals("Father", jsonObject.getString("Zeus"));
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"insert God (age = @age)"})
    public void shouldReturnParserQuery4(String query) {
        InsertQuery insertQuery = checkInsertFromStart(query);
        List<Condition> conditions = insertQuery.getConditions();
        assertEquals(1, conditions.size());
        Condition condition = conditions.get(0);
        assertEquals("age", condition.getName());
        assertEquals(Operator.EQUALS, condition.getOperator());
        Value<?> value = condition.getValue();
        assertTrue(value instanceof ParamValue);
        assertEquals("age", ParamValue.class.cast(value).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"insert God (birthday = convert(\"1988-01-01\", java.time.LocalDate))"})
    public void shouldReturnParserQuery5(String query) {
        InsertQuery insertQuery = checkInsertFromStart(query);
        List<Condition> conditions = insertQuery.getConditions();
        assertEquals(1, conditions.size());
        Condition condition = conditions.get(0);
        assertEquals("birthday", condition.getName());
        assertEquals(Operator.EQUALS, condition.getOperator());
        Value<?> value = condition.getValue();
        assertTrue(value instanceof FunctionValue);
        Function function = FunctionValue.class.cast(value).get();
        assertEquals("convert", function.getName());
        Object[] params = function.getParams();
        assertEquals(2, params.length);
        assertEquals("1988-01-01", StringValue.class.cast(params[0]).get());
        assertEquals(LocalDate.class, params[1]);
    }


    private InsertQuery checkInsertFromStart(String query) {
        InsertQuery insertQuery = insertSupplier.apply(query);
        assertEquals("God", insertQuery.getEntity());
        return insertQuery;
    }

}

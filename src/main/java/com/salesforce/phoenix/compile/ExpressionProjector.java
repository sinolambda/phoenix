/*******************************************************************************
 * Copyright (c) 2013, Salesforce.com, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     Neither the name of Salesforce.com nor the names of its contributors may 
 *     be used to endorse or promote products derived from this software without 
 *     specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.salesforce.phoenix.compile;


import java.sql.SQLException;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import com.salesforce.phoenix.expression.Expression;
import com.salesforce.phoenix.schema.PDataType;
import com.salesforce.phoenix.schema.tuple.Tuple;



/**
 * 
 * Projector for getting value from a select statement for an expression
 *
 * @author jtaylor
 * @since 0.1
 */
public class ExpressionProjector implements ColumnProjector {
    private final String name;
    private final Expression expression;
    private final String tableName;
    private final boolean isCaseSensitive;
    
    public ExpressionProjector(String name, String tableName, Expression expression, boolean isCaseSensitive) {
        this.name = name;
        this.expression = expression;
        this.tableName = tableName;
        this.isCaseSensitive = isCaseSensitive;
    }
    
    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final Object getValue(Tuple tuple, PDataType type, ImmutableBytesWritable ptr) throws SQLException {
        Expression expression = getExpression();
        if (!expression.evaluate(tuple, ptr)) {
            return null;
        }
        if (ptr.getLength() == 0) {
            return null;
        }        
        return type.toObject(ptr, expression.getDataType(), expression.getColumnModifier());
    }

    @Override
    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }
}

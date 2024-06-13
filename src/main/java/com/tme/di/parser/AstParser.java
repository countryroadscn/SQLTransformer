package com.tme.di.parser;

import com.tme.di.parser.ast.DescribeQuery;
import com.tme.di.parser.ast.SelectUnionQuery;
import lombok.extern.slf4j.Slf4j;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class AstParser {

    private boolean fillDefaultDatabase = false;

    public AstParser() {
    }

    public AstParser(boolean fillDefaultDatabase) {
        this.fillDefaultDatabase = fillDefaultDatabase;
    }

    public Object parse(String sql, String defaultDatabase) {
        try {
            long start = System.currentTimeMillis();
            // try parsing a SQL
            InputStream inputStream = new ByteArrayInputStream(sql.getBytes());
            CharStream antlrInputStream = CharStreams.fromStream(inputStream);
            ClickHouseLexer chLexer = new ClickHouseLexer(antlrInputStream);
            TokenStream tokens = new CommonTokenStream(chLexer);
            ClickHouseParser chParser = new ClickHouseParser(tokens);
            // Notice: chParser.queryStmt() can only be called once as it reads data from stream
            ClickHouseParser.QueryStmtContext tree = chParser.queryStmt();
            // System.out.println(tree.toStringTree(chParser));
            CstVisitor visitor = new CstVisitor(defaultDatabase, fillDefaultDatabase);
            Object ast = visitor.visit(tree);
            log.debug("It takes " + (System.currentTimeMillis() - start) + "ms to parse the sql.");
            if (null != ast) {
                if(ast instanceof SelectUnionQuery){
                    // log.debug("This is a SELECT statement.");
                } else if(ast instanceof DescribeQuery) {
                    log.debug("This is a DESCRIBE statement");
                } else{
                    log.debug("This is NOT a SELECT or Describe statement.");
                }
            }
            return ast;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object parse(String sql) {
        return parse(sql, "default");
    }
}

package com.tme.di.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    // StarRocks保留关键字
    private static final Set<String> BUILT_IN_IDENTIFIER_IN_UPPER_CASE = new HashSet<>(Arrays.asList(
        "ADD", "ALL", "ALTER", "ANALYZE", "AND", "ARRAY", "AS", "ASC", "BETWEEN", "BIGINT", "BITMAP", "BOTH", "BY", "CASE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "COMPACTION", "CONVERT", "CREATE", "CROSS", "CUBE", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DATABASE", "DATABASES", "DECIMAL", "DECIMALV2", "DECIMAL32", "DECIMAL64", "DECIMAL128", "DEFAULT", "DEFERRED", "DELETE", "DENSE_RANK", "DESC", "DESCRIBE", "DISTINCT", "DOUBLE", "DROP", "DUAL", "ELSE", "EXCEPT", "EXISTS", "EXPLAIN", "FALSE", "FIRST_VALUE", "FLOAT", "FOR", "FORCE", "FROM", "FULL", "FUNCTION", "GRANT", "GROUP", "GROUPS", "GROUPING", "GROUPING_ID", "HAVING", "HLL", "HOST", "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX", "INFILE", "INNER", "INSERT", "INT", "INTEGER", "INTERSECT", "INTO", "IS", "JOIN", "JSON", "KEY", "KEYS", "KILL", "LAG", "LARGEINT", "LAST_VALUE", "LATERAL", "LEAD", "LEFT", "LIKE", "LIMIT", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "MAXVALUE", "MINUS", "MOD", "NTILE", "NOT", "NULL", "ON", "OR", "ORDER", "OUTER", "OUTFILE", "OVER", "PARTITION", "PERCENTILE", "PRIMARY", "PROCEDURE", "QUALIFY", "RANGE", "RANK", "READ", "REGEXP", "RELEASE", "RENAME", "REPLACE", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "ROW_NUMBER", "SCHEMA", "SCHEMAS", "SELECT", "SET", "SET_VAR", "SHOW", "SMALLINT", "SYSTEM", "TABLE", "TERMINATED", "TEXT", "THEN", "TINYINT", "TO", "TRUE", "UNION", "UNIQUE", "UNSIGNED", "UPDATE", "USE", "USING", "VALUES", "VARCHAR", "WHEN", "WHERE", "WITH"
    ));

    // private static final Set<String> BUILT_IN_IDENTIFIER_IN_UPPER_CASE = new HashSet<>(Arrays.asList(
    //         "COLUMN", "FORCE", "ARRAY", "FROM"
    // ));

    public static String escapeReservedKeyword(String name) {
        // 处理用到了保留字的情况
        if (BUILT_IN_IDENTIFIER_IN_UPPER_CASE.contains(name.toUpperCase())) {
            return '`' + name + '`';
        }
        return name;
    }

    public static void main(String[] args) {
        String clickhouseExpression1 = "toStartOfInterval(my_timestamp, INTERVAL 1 HOUR, 'UTC')";
        String clickhouseExpression2 = "toStartOfInterval(my_timestamp, INTERVAL 60 SECOND)";

        // Convert to date_trunc expression
        String dateTruncExpression1 = convertFuncToStartOfInterval(clickhouseExpression1);
        String dateTruncExpression2 = convertFuncToStartOfInterval(clickhouseExpression2);

        System.out.println("Converted Expression 1: " + dateTruncExpression1);
        System.out.println("Converted Expression 2: " + dateTruncExpression2);
    }

    public static String convertFuncToStartOfInterval(String clickhouseExpression) {
        // Pattern to match toStartOfInterval(date_or_date_with_time, INTERVAL x unit [, time_zone])
        Pattern pattern = Pattern.compile(
            "toStartOfInterval\\s*\\(([^,]+),\\s*INTERVAL\\s+([0-9]+)\\s+([A-Z]+)(?:,\\s*'([^']*)')?\\)"
        );
        Matcher matcher = pattern.matcher(clickhouseExpression);
        
        if (matcher.find()) {
            String timestamp = matcher.group(1).trim();
            String intervalValue = matcher.group(2).trim();
            String intervalUnit = matcher.group(3).trim().toLowerCase();
            String timeZone = matcher.group(4) != null ? matcher.group(4).trim() : null;
            
            // Validate and convert interval unit
            switch (intervalUnit) {
                case "second":
                    if (intervalValue.equals("60")) 
                        intervalUnit = "minute";
                    break;
                case "minute":
                case "hour":
                case "day":
                case "week":
                case "month":
                case "quarter":
                case "year":
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported interval unit: " + intervalUnit);
            }
            
            // Construct date_trunc expression
            StringBuilder dateTruncExpression = new StringBuilder("date_trunc('");
            dateTruncExpression.append(intervalUnit).append("', ").append(timestamp);
            
            if (timeZone != null && !timeZone.isEmpty()) {
                dateTruncExpression.append(", '").append(timeZone).append("'");
            }
            
            dateTruncExpression.append(")");
            return dateTruncExpression.toString();
        } else {
            throw new IllegalArgumentException("Invalid ClickHouse expression: " + clickhouseExpression);
        }
    }
}

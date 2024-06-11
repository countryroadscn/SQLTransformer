package com.tme.di.visitor;

public  class Transform {
    public static void main(String[] args) {
        if (args.length > 0) {
            String clickhouseSql = "SELECT sumIf(1,a=0)";
            System.out.println("clickhouse sql: " + clickhouseSql);
            StarRocksSqlBuilder sr = new StarRocksSqlBuilder();
            try{
                String starRocksSql = sr.getStarRocksSql(clickhouseSql);
                System.out.println("starrocks sql: " + starRocksSql);
            } catch (SqlRewriteException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No input provided");
        }
        // String clickhouseSql = "SELECT toDateTime(date_time) FROM db.table";
        // StarRocksSqlBuilder sr = new StarRocksSqlBuilder();
        // try{
        //     String starRocksSql = sr.getStarRocksSql(clickhouseSql);
        //     System.out.println(starRocksSql);
        // } catch (SqlRewriteException e) {
        //     e.printStackTrace();
        // }
    }
}

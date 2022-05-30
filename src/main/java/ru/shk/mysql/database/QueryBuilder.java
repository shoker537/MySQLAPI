package ru.shk.mysql.database;

import java.sql.ResultSet;

public class QueryBuilder {
    private final StringBuilder query = new StringBuilder();
    private final MySQL sql;
    private boolean hasWhere = false;

    public QueryBuilder(MySQL sql){
        this.sql = sql;
    }
    public QueryBuilder SELECT(String what){
        query.append("SELECT ").append(what);
        return this;
    }
    public QueryBuilder FROM(String where){
        query.append(" FROM ").append(where);
        return this;
    }
    public QueryBuilder WHERE(String what){
        if(!hasWhere){
            hasWhere = true;
            query.append(" WHERE ").append(what);
        } else {
            query.append(" AND ").append(what);
        }
        return this;
    }

    public QueryBuilder LIMIT(int limit){
        query.append(" LIMIT "+limit);
        return this;
    }

    public QueryBuilder LIMIT(int limit1, int limit2){
        query.append(" LIMIT "+limit1+", "+limit2);
        return this;
    }

    public ResultSet execute(){
        return sql.Query(query.toString());
    }

}

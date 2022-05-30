package ru.shk.mysql.database;

import java.sql.SQLException;

public class UpdateBuilder {
    private final StringBuilder query = new StringBuilder();
    private final MySQL sql;
    private boolean hasWhere = false;

    public UpdateBuilder(MySQL sql, String start){
        this.sql = sql;
        query.append(start).append(" ");
    }

    public UpdateBuilder SET(String... values){
        query.append(" SET ").append(String.join(", ", values));
        return this;
    }

    public UpdateBuilder WHERE(String what){
        if(!hasWhere){
            hasWhere = true;
            query.append(" WHERE ").append(what);
        } else {
            query.append(" AND ").append(what);
        }
        return this;
    }

    public UpdateBuilder TABLE(String what){
        query.append(what);
        return this;
    }

    public void executeSync(){
        sql.UpdateSync(query.toString());
    }
    public void execute(){
        sql.Update(query.toString());
    }
    public void executeExpected() throws SQLException {
        sql.UpdateExcepted(query.toString());
    }

}

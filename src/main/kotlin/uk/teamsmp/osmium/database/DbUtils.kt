package uk.teamsmp.osmium.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

fun Connection.executeQuery(query: String, vararg params: Any): ResultSet {
    val statement: PreparedStatement = this.prepareStatement(query)
    params.forEachIndexed { index, param ->
        statement.setObject(index + 1, param)
    }
    return statement.executeQuery()
}

fun Connection.executeUpdate(query: String, vararg params: Any): Int {
    val statement: PreparedStatement = this.prepareStatement(query)
    params.forEachIndexed { index, param ->
        statement.setObject(index + 1, param)
    }
    return statement.executeUpdate()
}

package com.loicfrance.library.database;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

/**
 * Created by RichardFrance on 28/02/2018.
 */

public abstract class DatabaseHandler extends SQLiteOpenHelper {

//##################################################################################################
//#                                          Constructors                                          #
//##################################################################################################

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

//##################################################################################################
//#                                     database modifications                                     #
//##################################################################################################
    /**
     * Gives you a simple SQL command that creates your table. You can use the result as the first
     * argument of the function {@link #SQLCreateTableCommand(String tableName, String... data)}
     * <pre>Example :
     *{@code
     *getSQLCreateTableCommand("TABLE_NAME",new String[]{
     *          KEY, "INTEGER PRIMARY KEY AUTOINCREMENT",
     *          DATA1, "TEXT",
     *          DATA2, "INTEGER"}}
     *</pre>
     * @param tableName the name of the table you want to create
     * @param data with the form :
     *              {
     *              dataName, type,
     *              dataName, type,
     *              dataName, type,
     *              ...
     *              }.
     *              indicate the primary key with its type + " PRIMARY KEY" +
     *              (optionally) " AUTOINCREMENT".
     * @return the SQL command you can use to create the TABLE.
     */
    public static String SQLCreateTableCommand(String tableName, String[] data) {
        if(data.length%2 ==1) throw new IllegalArgumentException("second argument (String[] data)" +
                " must be of size 2. see javadoc for more information");
        StringBuilder result = new StringBuilder("CREATE TABLE " + tableName + '(');
        for(int i=0; i< data.length/2; i++) {
            if(i>0) result.append(", ");
            result.append(data[2 * i]).append(' ').append(data[2 * i + 1]);
        }

        return result.append(");").toString();
    }

    public static String SQLDropTablesCommand(@NonNull @Size(min = 1) String[] tableNames) {
        StringBuilder result = new StringBuilder("DROP TABLE IF EXISTS ");
        for(int i=0; i< tableNames.length; i++) {
            if(i>0) result.append(", ");
            result.append(tableNames[i]);
        }
        return  result.append(" CASCADE;").toString();
    }

    abstract public String getTablesSQLSchema();
    abstract public String getDropTablesCommand();

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.getWritableDatabase().execSQL(getTablesSQLSchema());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(getDropTablesCommand());
        onCreate(db);
    }
    public SQLiteDatabase open() {
        return this.getWritableDatabase();
    }
    public void close() {
        this.getWritableDatabase().close();
    }

    /**
     * execute a SQL statement that is neither SELECT, INSERT, UPDATE or DELETE
     * @see SQLiteDatabase#execSQL(String) SQLiteDatabase.execSQL(String)
     * @param sqlCommand the command to execute
     */
    protected void execSQL(String sqlCommand) {
        this.getWritableDatabase().execSQL(sqlCommand);
    }
    protected void execSQL(String sqlCommand, String[] bindArgs){
        this.getWritableDatabase().execSQL(sqlCommand, bindArgs);
    }

//##################################################################################################
//#                                     database modifications                                     #
//##################################################################################################

    protected void insertValues(String tableName, ContentValues values) {
        this.open().insert(tableName, null, values);
    }

    protected int delete(String tableName, String whereClause, String[] whereArgs) {
        return this.open().delete(tableName, whereClause, whereArgs);
    }

    protected int clearTable(String tableName) {
        return this.delete(tableName,"1 = ?", new String[]{"1"});
    }

//##################################################################################################
//#                                        database queries                                        #
//##################################################################################################

    protected Cursor rawGet(String sqlCommand, String[] selectionArgs) {
        return open().rawQuery(sqlCommand, selectionArgs);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected Cursor rawGet(String sqlCommand, String[] selectionArgs,
                            CancellationSignal cancellationSignal) {
        return open().rawQuery(sqlCommand, selectionArgs, cancellationSignal);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected Cursor get(String table, boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy, String limit,
                         CancellationSignal cancellationSignal) {
        return open().query(distinct, table, columns, whereClause, whereArgs, groupBy,
                having, orderBy, limit, cancellationSignal);
    }
    protected Cursor get(String table, boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy, String limit) {
        return open().query(distinct, table, columns, whereClause, whereArgs, groupBy,
                having, orderBy, limit);
    }
    protected Cursor get(String table, boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy) {
        return get(table, distinct, columns, whereClause, whereArgs, groupBy, having, orderBy, null);
    }
    protected Cursor get(String table, boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having) {
        return get(table, distinct, columns, whereClause, whereArgs, groupBy, having, null, null);
    }
    protected Cursor get(String table, boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy) {
        return get(table, distinct, columns, whereClause, whereArgs, groupBy, null, null, null);
    }
    protected Cursor get(String table, boolean distinct, String[] columns, String whereClause,
                         String[] whereArgs) {
        return get(table, distinct, columns, whereClause, whereArgs, null, null, null, null);
    }
    protected Cursor get(String table, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy, String limit) {
        return get(table, false, columns, whereClause, whereArgs, groupBy, having,
                orderBy, limit);
    }
    protected Cursor get(String table, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy) {
        return get(table, false, columns, whereClause, whereArgs, groupBy, having, orderBy, null);
    }
    protected Cursor get(String table, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having) {
        return get(table, false, columns, whereClause, whereArgs, groupBy, having, null, null);
    }
    protected Cursor get(String table, String[] columns, String whereClause, String[] whereArgs, String groupBy) {
        return get(table, false, columns, whereClause, whereArgs, groupBy, null, null, null);
    }
    protected Cursor get(String table, String[] columns, String whereClause, String[] whereArgs) {
        return get(table, false, columns, whereClause, whereArgs, null, null, null, null);
    }
}

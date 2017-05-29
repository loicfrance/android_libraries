package com.loicfrance.library.database;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.CancellationSignal;

/**
 * Created by Loic France on 01/10/2016.
 */

public abstract class AbstractDatabase extends SQLiteOpenHelper {
    private String tableName, fileName;
    private SQLiteDatabase mDb = null;
    public AbstractDatabase(Context context, String tableName, String fileName,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, fileName, factory, version);
        this.tableName = tableName;
        this.fileName = fileName;
    }
//##################################################################################################
//###################################### table initialization ######################################
//##################################################################################################
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableCommand());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(getDropTableCommand());
        onCreate(db);
    }
    public final String getTableName() {
        return tableName;
    }
    public final String getFileName() {
        return fileName;
    }

    /**
     * must return the list of columns with the format (elements are here separated by comas) :
     *              {
     *              dataName, type,
     *              dataName, type,
     *              dataName, type,
     *              ...
     *              }.
     * you can indicate the primary key with its type + " PRIMARY KEY" +
     * (optionally) " AUTOINCREMENT".
     * Example : {@code
     * public String[] getTableColumnsData() {
     *     return new String[]{
     *         KEY, "INTEGER PRIMARY KEY AUTOINCREMENT",
     *         DATA1, "TEXT",
     *         DATA2, "INTEGER"};
     * }
     * the function result is only used to create the table, so it's not necessary
     * if you completely override the {@link# getCreateTableCommand()} method.
     * }
     */
    protected abstract String[] getTableColumnsData();
    protected String getCreateTableCommand() {
        return SQLCreateTableCommand(tableName, this.getTableColumnsData());
    }
    protected String getDropTableCommand() {
        return "DROP TABLE IF EXISTS " + tableName + ";";
    }

    /**
     * Gives you a simple SQL command that creates your table. You can use the result as the first
     * argument of the function {@link #SQLCreateTableCommand(String tableName, String... data)}
     * Example :
     * {@code getSQLCreateTableCommand("TABLE_NAME",new String[]{
     *                  KEY, "INTEGER PRIMARY KEY AUTOINCREMENT",
     *                  DATA1, "TEXT",
     *                  DATA2, "INTEGER"}}
     *
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
        String result = "CREATE TABLE " + tableName + '(';
        for(int i=0; i< data.length/2; i++) {
            if(i>0) result += ", ";
            result += data[2*i] + ' ' + data[2*i+1];
        }

        return result + ");";
    }

//##################################################################################################
//#################################### table getters and setters ###################################
//##################################################################################################

//______________________________________ open / close / getDb ______________________________________
//**************************************************************************************************

    public SQLiteDatabase open(){
        mDb = getWritableDatabase();
        return mDb;
    }
    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb(){
        return mDb;
    }

//_________________________________________ insert / delete ________________________________________
//**************************************************************************************************
    protected void insertValues(ContentValues values) {
        open().insert(tableName, null, values);
        close();
    }
    protected int delete(String whereClause, String[] whereArgs) {
        int deleted = open().delete(tableName, whereClause, whereArgs);
        close();
        return deleted;
    }
    protected int clearTable() {
        int deleted = delete("1 = ?", new String[]{"1"});
        if(deleted == 0) deleted = delete(null, null);
        return deleted;
    }
//_______________________________________________ get ______________________________________________
//**************************************************************************************************

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected Cursor get(boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy, String limit,
                         CancellationSignal cancellationSignal) {
        return open().query(distinct, tableName, columns, whereClause, whereArgs, groupBy,
                                having, orderBy, limit, cancellationSignal);
    }
    protected Cursor get(boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy, String limit) {
        return open().query(distinct, tableName, columns, whereClause, whereArgs, groupBy,
                                having, orderBy, limit);
    }
    protected Cursor get(boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy) {
        return get(distinct, columns, whereClause, whereArgs, groupBy, having, orderBy, null);
    }
    protected Cursor get(boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having) {
        return get(distinct, columns, whereClause, whereArgs, groupBy, having, null, null);
    }
    protected Cursor get(boolean distinct, String[] columns, String whereClause, String[] whereArgs,
                         String groupBy) {
        return get(distinct, columns, whereClause, whereArgs, groupBy, null, null, null);
    }
    protected Cursor get(boolean distinct, String[] columns, String whereClause,
                         String[] whereArgs) {
        return get(distinct, columns, whereClause, whereArgs, null, null, null, null);
    }
    protected Cursor get(String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy, String limit) {
        return get(false, columns, whereClause, whereArgs, groupBy, having,
                                orderBy, limit);
    }
    protected Cursor get(String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having, String orderBy) {
        return get(false, columns, whereClause, whereArgs, groupBy, having, orderBy, null);
    }
    protected Cursor get(String[] columns, String whereClause, String[] whereArgs,
                         String groupBy, String having) {
        return get(false, columns, whereClause, whereArgs, groupBy, having, null, null);
    }
    protected Cursor get(String[] columns, String whereClause, String[] whereArgs, String groupBy) {
        return get(false, columns, whereClause, whereArgs, groupBy, null, null, null);
    }
    protected Cursor get(String[] columns, String whereClause, String[] whereArgs) {
        return get(false, columns, whereClause, whereArgs, null, null, null, null);
    }
    protected Cursor rawGet(String sqlCommand, String[] selectionArgs) {
        return open().rawQuery(sqlCommand, selectionArgs);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected Cursor rawGet(String sqlCommand, String[] selectionArgs,
                            CancellationSignal cancellationSignal) {
        return open().rawQuery(sqlCommand, selectionArgs, cancellationSignal);
    }
    protected Cursor getAll(String[] columns) {
        return rawGet("SELECT * FROM " + tableName, null);
        //return get(false, columns, "1 = ?", new String[]{"1"}, null, null, null, null);
    }


}

package mtsealove.com.github.BuslinkerDrivers.Accounts;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import mtsealove.com.github.BuslinkerDrivers.Restful.LoginData;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="create table Account(" +
                "ID text, " +
                "password text) ";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void Write(String ID, String password) {
        //
        SQLiteDatabase readDB=getReadableDatabase();
        String readQuery="select count(*) as count from Account";
        Cursor cursor=readDB.rawQuery(readQuery, null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
        readDB.close();

        SQLiteDatabase db=getWritableDatabase();
        String query="insert into Account values('" +
                ID+"', '" +
                password+"')";;
        if(count!=0)    //데이터가 이미 존재하면 데이터 삭제
            db.execSQL("delete from Account");

        db.execSQL(query);
        db.close();
    }

    public LoginData Read() {
        SQLiteDatabase db=getReadableDatabase();
        String query="select * from Account";
        Cursor cursor=db.rawQuery(query, null);
        try{
            cursor.moveToNext();
            LoginData loginData=new LoginData(cursor.getString(0), cursor.getString(1));
            return loginData;
        }catch (Exception e){
            return  null;
        } finally {
            db.close();
        }
    }
    public void Delete() {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("delete from Account");
        db.close();
    }
}

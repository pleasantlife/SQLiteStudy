package com.kimjinhwan.android.sqlitestudy;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by XPS on 2017-06-09.
 */

public class MySecondSQLiteHelper extends SQLiteOpenHelper {
    private Context context;
    private final String DB_NAME;
    private final int DB_VERSION;

    public MySecondSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, Context context1, String DB_NAME, int DB_VERSION) {
        this(context, name, factory, version, null);

    }

    public MySecondSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        DB_NAME = name;
        DB_VERSION = version;
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        setWriteAheadLoggingEnabled(true);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(int v = 1; v <=DB_VERSION; v++){
            applySqlFile(db,v);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i= (oldVersion+1); i <= newVersion; i++){
            applySqlFile(db,i);
        }
    }

    private void applySqlFile(SQLiteDatabase db, int version) {
        BufferedReader reader = null;                                                           //스트림을 감싸는 스트림은 그 안에 실제 스트림을 넣어주어야 한다. 사용 후 리더를 닫기 위해 try문 밖에 위치시킴.
        try {
        String fileName = String.format(Locale.getDefault(), "%s.%d.sql",DB_NAME,version);      //불러올 파일의 이름 정의
             final InputStream inputStream = context.getAssets().open(fileName);                //InputStream 만들기. final은 안바뀔걸 알기 때문에 써넣었으나 굳이 써넣지는 않음.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();                                        //문자열이 계속 들어올 떄 합치는 역할을 해줌. (+와 같음.)
            for(String line ; (line = reader.readLine()) != null;) {                               // String line = null;
                                                                                                    //  While((line = reader.readLine())) != null){}   과 같음.
            if(!TextUtils.isEmpty(line) && !line.startsWith("--")){
                stringBuilder.append(line.trim());                                                  //
            }
            if(line.endsWith(";")){
                db.execSQL(stringBuilder.toString());
                stringBuilder.setLength(0);                                                         // 지워짐.
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

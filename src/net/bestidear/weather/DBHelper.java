package net.bestidear.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String dataname){
		super(context, dataname, null, 2);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public String[] getAllProvinces() {
		String[] columns={"name"};
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("provinces", columns, null, null, null, null, null);
		columns = null;
		int count= cursor.getCount();
		String[] provinces = new String[count];
		count=0;
		while(!cursor.isLast()) {
			cursor.moveToNext();
			provinces[count] = cursor.getString(0);
			count=count+1;
		}
		cursor.close();
		db.close();
		return provinces;
	}
	
	public List<String[][]> getAllCityAndCode(String[] provinces) {
		int length= provinces.length;
		String[][] city = new String[length][];
		String[][] code = new String[length][];
		int count = 0;
		SQLiteDatabase db = getReadableDatabase();
		for(int i=0; i<length; i++) {
			Cursor cursor = db.query("citys", new String[]{"name", "city_num"},
					"province_id = ? ", new String[]{String.valueOf(i)}, null, null, null);
			count = cursor.getCount();
			city[i] = new String[count];
			code[i] = new String[count];
			count = 0;
			while(!cursor.isLast()) {
				cursor.moveToNext();
				city[i][count] = cursor.getString(0);
				code[i][count] = cursor.getString(1);
				count = count + 1;
			}
		    cursor.close();
		}
		db.close();
		List<String[][]> result = new ArrayList<String[][]>();
		result.add(city);
		result.add(code);
		return result;
	}
	
	
	public List<String[]> getCityAndCodeByProvince(String provinces) {
        String[] city ;
        String[] code ;
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("citys", new String[]{"name", "city_num"},
                    "province_id = ? ", new String[]{String.valueOf(provinces)}, null, null, null);
        count = cursor.getCount();
        city = new String[count];
        code = new String[count];
        count = 0;
        while(!cursor.isLast()) {
            cursor.moveToNext();
            city[count] = cursor.getString(0);
            code[count] = cursor.getString(1);
            count = count + 1;
        }
        cursor.close();
        db.close();
        List<String[]> result = new ArrayList<String[]>();
        result.add(city);
        result.add(code);
        return result;
    }
	
	public String getCityCodeByName(String cityName) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("citys", new String[]{"city_num"},
				"name = ? ", new String[]{cityName}, null, null, null);
		String cityCode = null;
		if(!cursor.isLast()){
			cursor.moveToNext();
			cityCode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return cityCode;
	}
	
	public String getProvinceCodeByCityNum(String cityNum) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("citys", new String[]{"province_id"},
                "city_num = ? ", new String[]{cityNum}, null, null, null);
        String ProvinceCode = null;
        if(!cursor.isLast()){
            cursor.moveToNext();
            ProvinceCode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return ProvinceCode;
    }
	
}

/**
 * Created May 26, 2012
 */
package edu.droidshark.tcpdump;

import java.util.Scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.droidshark.R;

/**
 * A class for creating a database to store tcpdump filters
 * 
 * @author Sam SmithReams
 * 
 */
public class FilterDatabase extends SQLiteOpenHelper
{
	private final static String DB_NAME = "filters_db";
	private Context context;

	public FilterDatabase(Context context)
	{
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE filters (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, filter TEXT);");
		
		//Insert filters included with app into database
		Scanner scanner = new Scanner(context.getResources().openRawResource(R.raw.filters));
		scanner.useDelimiter("\n");
		ContentValues cv = new ContentValues();
		while(scanner.hasNext())
		{
			String temp = scanner.next();
			String[] filter = temp.split(",");
			cv.put("name", filter[0]);
			if(filter.length > 1)
				cv.put("filter", filter[1]);
			else
				cv.put("filter", "");
			db.insert("filters", "name", cv);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//Do nothing
	}
}

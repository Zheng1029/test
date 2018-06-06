//************************************************************************************
//*  本類別實作管理消費種類之各個方法
//*  by Min-Hsiung Hung 2014-04-19
//************************************************************************************
package com.example.user.myapplication_2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ManageExpenseTypeActivity extends AppCompatActivity
{
	// 與建立資料庫有關之變數宣告
	private static String DATABASE_TABLE2 = "expenseTypesTable"; //宣告"消費種類資料表"常數
	private SQLiteDatabase db;    //宣告資料庫變數
	private MyDBHelper dbHelper;  //宣告資料庫幫助器變數

	// 與圖形介面元件相關之變數宣告
	Button btnCreateNewExpenseType, btnDeleteExpenseType; //分別為"建立新消費種類"與"刪除舊消費種類"之按鈕變數
	Button btnDispalyExpenseTypes, btnReturnToMainActivity;    //分別為"顯示消費種類"與"回到主操作介面"之按鈕變數
	EditText etxtNewExpenseType, etxtOldExpenseType;  //分別為"輸入新消費種類"與"輸入舊消費種類"之可編輯文字方塊變數
	EditText etxtUserID; // 顯示使用者帳號之可編輯文字方塊變數
	TextView tvOutput; //"顯示消費種類"之文字盒

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manageexpensetypeactivity); //顯示操作界面
		Log.d("test2", "M program begin!");

		try
		{
			// 取得使用者介面上之所有元件
			btnCreateNewExpenseType = (Button) findViewById(R.id.btnCreateNewExpenseType);
			btnDeleteExpenseType = (Button) findViewById(R.id.btnDeleteExpenseType);
			btnDispalyExpenseTypes = (Button) findViewById(R.id.btnDispalyExpenseTypes);
			btnReturnToMainActivity = (Button) findViewById(R.id.btnReturnToMainActivity);
			etxtNewExpenseType = (EditText) findViewById(R.id.etxtNewExpenseType);
			etxtOldExpenseType = (EditText) findViewById(R.id.etxtOldExpenseType);
			tvOutput = (TextView) findViewById(R.id.tvOutput);

			//
			// 建立一個SQL資料庫 (MyTallyBook)
			dbHelper = new MyDBHelper(this); //建立資料庫輔助類別物件
			db = dbHelper.getWritableDatabase(); // 透過輔助類別物件建立一個可以讀寫的資料庫

			// 以下為讀取並顯示消費資料表中之所有消費種類
			// 以下為SQL資料讀取指令字串：從消費資料表expenseTypes中讀取所有紀錄(包含所有欄位)
			Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2, null);
			if (cursor != null)
			{  // 若有傳回消費種類，則進行以下處理
				int n = cursor.getCount(); //取得消費種類紀錄筆數
				String str="目前已建立" + n + "個運動種類:\n";
				cursor.moveToFirst();  // 將指標移到第1筆紀錄
				for (int i = 0; i < n; i++) //利用迴圈讀取每一筆紀錄
				{
					str += (i+1) +": " + cursor.getString(1) + "\n"; //讀取第1個欄位值(消費種類expenseType)，然後串接到顯示字串(str)中
					cursor.moveToNext();  // 將指標移到下一筆紀錄
				}
				tvOutput.setText(str); //將顯示字串的內容顯示在"顯示消費種類"之文字盒上
			}
			else // 若沒有傳回消費種類，則在"顯示消費種類"之文字盒上顯示提醒訊息
			{
				tvOutput.setText("目前並沒有建立任額運動種類!\n");
			}

		}
		catch(Exception ex)
		{
			tvOutput.setText("取得GUI元件及目前使用者帳號密碼時發生例外，原因如下： " + ex.getMessage());
		}
	}

	// 當Activity停止時關閉database
	@Override
	protected void onStop()
	{
		super.onStop();
		db.close(); // 關閉資料庫
	}

	// 建立新消費種類按鈕事件處理程式
	public void btnCreateNewExpenseType_Click(View v)
	{
		//取得新消費種類之字串
		String newExpenseTypeString =etxtNewExpenseType.getText().toString().trim(); //.trim()去掉字串兩端之空白
		// 以下確保使用者有輸入新的消費種類
		int empty=0;
		if(newExpenseTypeString.length()==0) // 若沒有輸入新的消費種類，則利用吐司訊息提醒使用者
		{
			Toast.makeText(this, "沒有填寫新運動種類!", Toast.LENGTH_LONG).show();
			empty++;
		}
		if (empty==0) // 若使用者有輸入新的消費種類，則進行以下處理
		{
			try //利用try{}包住可能會產生例外的處理程式碼(如存取資料庫及檔案)，讓程式即便有例外，也不會當掉，以增加app之強韌性
			{
				// 以下為SQL條件式資料讀取指令字串：從消費種類資料表expenseTypes中讀取消費資料種類等於所輸入的新消費種類之紀錄(包含所有欄位)
				Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2 + " WHERE expenseType = '" +
						newExpenseTypeString + "'", null);
				int count = cursor.getCount(); // 取出傳回的紀錄筆數
				if(count==0) // 若傳回的資料筆數為0，表示新消費種類並不存在於目前資料表中，則可以將新的消費種類存入資料表中
				{
					// 以下為SQL資料插入指令字串： 將(新消費種類之字串newExpenseTypeString)存入資料表2(expenseTypes資料表)中，
					// 要存入expeneType欄位中
					db.execSQL("INSERT INTO " + DATABASE_TABLE2 + " (expenseType) VALUES ('" +
							newExpenseTypeString +"')");
					// 利用吐司訊息告知使用者，已成功存入新的消費種類
					Toast.makeText(this, "已新增【" + newExpenseTypeString + "】運動種類!", Toast.LENGTH_LONG).show();
					etxtNewExpenseType.setText(""); //清除"新消費種類輸入文字盒"之內容

					// 以下為讀取並顯示消費資料表中之所有消費種類(當新增1筆消費種類，就重新顯示所有的消費種類給使用者看)
					// 以下為SQL資料讀取指令字串：從消費資料表expenseTypes中讀取所有紀錄(包含所有欄位)
					cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2, null); //執行資料表查詢命令
					int n = cursor.getCount(); //取得資料筆數
					String str="目前已建立之運動種類:\n";
					cursor.moveToFirst();  // 將指標移到第1筆紀錄
					for (int i = 0; i < n; i++) //利用迴圈讀取每一筆紀錄
					{
						str += (i+1) +": " + cursor.getString(1) + "\n"; //讀取第1個欄位值(消費種類expenseType)，然後串接到顯示字串(str)中
						cursor.moveToNext();  // 將指標移到下一筆紀錄
					}
					tvOutput.setText(str); //將顯示字串的內容顯示在"顯示消費種類"之文字盒上
				}
				else // 若傳回的資料筆數不為0，表示新消費種類已存在於目前資料表中，則利用吐司訊息告知使用者
				{
					Toast.makeText(this, "該運動種類已經存在!", Toast.LENGTH_LONG).show();
				}
			}
			catch(Exception ex) // try{}中的程式碼執行時發生例外，則利用catch去捕捉，並利用吐司訊息提示給使用者知道
			{
				Toast.makeText(this, "程式出現例外: " + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	// 刪除消費種類按鈕事件處理程式
	public void btnDeleteExpenseType_Click(View v)
	{
		String oldExpenseTypeIDString =etxtOldExpenseType.getText().toString().trim();
		int empty=0;
		if(oldExpenseTypeIDString.length()==0)
		{
			Toast.makeText(this, "沒有填寫運動種類編號!", Toast.LENGTH_LONG).show();
			empty++;
		}
		if (empty==0) // 使用者有輸入消費種類標號
		{
			try  //利用try{}包住可能會產生例外的處理程式碼(如存取資料庫及檔案)，讓程式即便有例外，也不會當掉，以增加app之強韌性
			{
				int oldExpenseTypeID = Integer.parseInt(oldExpenseTypeIDString); //將所輸入舊消費種類的編號字串轉換成整數
				// 以下為SQL資料讀取指令字串：從消費資料表expenseTypes中讀取所有紀錄(包含所有欄位)
				Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2 , null);
				int count = cursor.getCount(); //取的紀錄筆數
				if((oldExpenseTypeID > count) || (oldExpenseTypeID < 1)) // 若消費種類編號超過範圍(小於1或大於紀錄筆數)，則利用吐司訊息提醒使用者
				{
					Toast.makeText(this, "運動種類編號超過範圍!", Toast.LENGTH_LONG).show();
				}
				else // 若消費種類編號在範圍內(大於等於1且小於等於紀錄筆數)
				{
					// 以下兩行指令為透過所輸入的編號，取出相對應的消費種類 (注意:顯示的編號從1開始，但記錄的編號從0開始；也就是記錄的編號比顯示的編號少1)
					cursor.moveToPosition(oldExpenseTypeID-1); // 將資料表指標移到所選的舊消費種類紀錄上
					String str2=cursor.getString(1); // 讀取第1個欄位值(即消費種類expenseType)
					// 以下為SQL條件式資料刪除指令字串：從消費種類資料表expenseTypes中刪除消費資料種類等於所指定的舊消費種類之紀錄
					db.execSQL("DELETE FROM " + DATABASE_TABLE2 + " WHERE expenseType='" + str2 + "'");
					// 利用吐司訊息告知使用者，已成功刪除所指定的舊消費種類
					Toast.makeText(this, "已刪除【" + str2 + "運動種類!", Toast.LENGTH_LONG).show();
					etxtOldExpenseType.setText(""); //清除"舊消費種類編號輸入文字盒"之內容

					// 以下為讀取並顯示消費資料表中之所有消費種類(當刪減1筆消費種類，就重新顯示所有的消費種類給使用者看)
					// 以下為SQL資料讀取指令字串：從消費資料表expenseTypes中讀取所有紀錄(包含所有欄位)
					cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2, null); //執行資料表查詢命令
					if(cursor!=null)  // 若有傳回消費種類，則進行以下處理
					{
						int n =cursor.getCount(); //取得資料筆數
						String str="目前已建立之運動種類:\n";
						cursor.moveToFirst();  // 將指標移到第1筆紀錄
						for (int i = 0; i < n; i++) //利用迴圈讀取每一筆紀錄
						{
							str += (i+1) +": " + cursor.getString(1) + "\n"; //讀取第1個欄位值(消費種類expenseType)，然後串接到顯示字串(str)中
							cursor.moveToNext();  // 將指標移到下一筆紀錄
						}
						tvOutput.setText(str); //將顯示字串的內容顯示在"顯示消費種類"之文字盒上
					}
					else  // 若沒有傳回消費種類，則在"顯示消費種類"之文字盒上顯示提醒訊息
					{
						tvOutput.setText("目前並沒有任何運動種類!\n");
					}

				}
			}
			catch(Exception ex) // try{}中的程式碼執行時發生例外，則利用catch去捕捉，並利用吐司訊息提示給使用者知道
			{
				Toast.makeText(this, "程式出現例外: " + ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	// 重新顯示消費種類按鈕事件處理程式
	public void btnDispalyExpenseTypes_Click(View v)
	{
		try
		{
			// 以下為讀取並顯示消費資料表中之所有消費種類
			// 以下為SQL資料讀取指令字串：從消費資料表expenseTypes中讀取所有紀錄(包含所有欄位)
			Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2, null); //執行資料表查詢命令
			if (cursor != null) // 若有傳回消費種類，則進行以下處理
			{
				int n = cursor.getCount(); //取得資料筆數
				String str="目前已建立之運動種類:\n";
				cursor.moveToFirst();  // 將指標移到第1筆紀錄
				for (int i = 0; i < n; i++) //利用迴圈讀取每一筆紀錄
				{
					str += (i+1) +": " + cursor.getString(1) + "\n"; //讀取第1個欄位值(消費種類expenseType)，然後串接到顯示字串(str)中
					cursor.moveToNext();  // 將指標移到下一筆紀錄
				}
				tvOutput.setText(str); //將顯示字串的內容顯示在"顯示運動種類"之文字盒上
			}
			else // 若沒有傳回消費種類，則在"顯示運動種類"之文字盒上顯示提醒訊息
			{
				tvOutput.setText("目前並沒有建立任何運動種類!\n");
			}
		}
		catch(Exception ex)
		{
			tvOutput.setText("顯示所有消費種類時發生例外，原因如下： " + ex.getMessage());
		}
	}

	// 關閉對話方塊按鈕按鈕事件處理程式
	public void btnReturnToMainActivity_Click(View v)
	{
		try
		{
			//建立查詢歷史消費紀錄之對話方塊
			Intent intent = new Intent(this, MainActivity.class);
			//啟動查詢歷史消費紀錄活動
			startActivity(intent);
			finish(); //關閉本活動
		}
		catch(Exception ex)
		{
			tvOutput.setText("關閉對話方塊時發生例外，原因如下： " + ex.getMessage());
		}
	}
}

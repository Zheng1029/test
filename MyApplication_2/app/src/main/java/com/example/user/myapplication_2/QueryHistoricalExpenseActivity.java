//************************************************************************************
//*  本類別實作查詢歷史消費紀錄之各個方法
//*  by Min-Hsiung Hung 2014-05-26
//************************************************************************************
package com.example.user.myapplication_2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class QueryHistoricalExpenseActivity extends AppCompatActivity
{
	// 與建立資料庫有關之變數宣告
	private static String DATABASE_TABLE1 = "tallybookTable"; //宣告"記帳簿資料表"常數
	private SQLiteDatabase db; //宣告資料庫變數
	private MyDBHelper dbHelper; //宣告資料庫幫助器變數

	// 與圖形介面元件相關之變數宣告
	Button btnChoseStartDate, btnChoseEndDate; //分別為"選擇起始日期"與"選擇結束日期"之按鈕變數
	Button btnQuery, btnDelete; //分別為"查詢消費紀錄"與"刪除消費紀錄"之按鈕變數
	Button btnClearOutput, btnReturnToMainActivity; //分別為"清除主顯示區"與"返回主操作介面"之按鈕變數
	RadioButton radioButtonByDate, radioButtonByType; //分別為"選擇依照日期先後顯示"與"選擇依照消費種類顯示"之按鈕變數
	TextView tvStartDate, tvEndDate, tvOutput; //分別為"建立新消費種類"與"刪除舊消費種類"之選鈕變數
	EditText etxtUserID; // 顯示使用者帳號之可編輯文字方塊變數

	// 其他變數
	String StartDateString="", EndDateString=""; //分別為紀錄起始日期及結束日期之字串變數
	String str ="";
	boolean deleteFlag=false; //判斷是否要刪除

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.queryhistoricalexpenseactivity); //顯示操作界面
		Log.d("test2", "Q program begin!");

		try
		{
			// 取得使用者介面上之所有元件
			btnChoseStartDate = (Button) findViewById(R.id.btnChoseStartDate);
			btnChoseEndDate = (Button) findViewById(R.id.btnChoseEndDate);
			btnQuery = (Button) findViewById(R.id.btnQuery);
			btnDelete = (Button) findViewById(R.id.btnDelete);
			btnClearOutput = (Button) findViewById(R.id.btnClearOutput);
			btnReturnToMainActivity = (Button) findViewById(R.id.btnReturnToMainActivity);
			radioButtonByDate = (RadioButton) findViewById(R.id.radioButtonByDate);
			radioButtonByType = (RadioButton) findViewById(R.id.radioButtonByType);
			tvStartDate = (TextView) findViewById(R.id.tvStartDate);
			tvEndDate = (TextView) findViewById(R.id.tvEndDate);
			tvOutput = (TextView) findViewById(R.id.tvOutput);

			// 建立一個SQL資料庫 (MyTallyBook)
			dbHelper = new MyDBHelper(this); //建立資料庫輔助類別物件
			db = dbHelper.getWritableDatabase(); // 透過輔助類別物件建立一個可以讀寫的資料庫

			// 清除主顯示區域
			tvOutput.setText("");

			// 將目前日期設定為預設選擇日期，並顯示在付款日文字方塊上
			displayPayDate();
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

	// 將目前日期設定為預設選擇日期，並顯示在付款日文字方塊上
	public void displayPayDate()
	{
		Calendar dt = Calendar.getInstance(); //取得一個日曆物件
		int year = dt.get(Calendar.YEAR);     //取得目前日期的年份
		int monthOfYear = dt.get(Calendar.MONTH);   //取得目前日期的月份
		int dayOfMonth = dt.get(Calendar.DAY_OF_MONTH); //取得目前日期的每月的第幾天

		StartDateString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的消費日期字串
		tvStartDate.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日"); //將所選日期顯示在付款日文字方塊上
		EndDateString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的消費日期字串
		tvEndDate.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日"); //將所選日期顯示在付款日文字方塊上
	}

	// 選擇起始日期按鈕事件處理方法
	public void btnChoseStartDate_Click(View v)
	{
		try
		{
			tvOutput.setText(""); //清除主顯示區
			Calendar dt = Calendar.getInstance(); //取得一個日曆物件
			DatePickerDialog dDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				// 覆寫選擇日期後的事件處理方法onDateSet()，所傳入的是所選擇的年、月、日，
				// 注意:月份的索引值從0開始，因此正確的月份為(monthOfYear+1)
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth)
				{
					StartDateString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的起始日期字串
					tvStartDate.setText(year+ "年" + (monthOfYear+1) +"月"+ dayOfMonth +"日"); //將所選日期顯示在標籤上
				}}, dt.get(Calendar.YEAR), dt.get(Calendar.MONTH),dt.get(Calendar.DAY_OF_MONTH)); //以目前日期為預設日期
			dDialog.show(); //顯示日期選擇器對話方塊
		}
		catch(Exception ex)
		{
			tvOutput.setText("選擇起始日期時發生例外，原因如下： " + ex.getMessage());
		}
	}

	// 選擇結束日期按按鈕事件處理方法
	public void btnChoseEndDate_Click(View v)
	{
		try
		{
			tvOutput.setText(""); //清除主顯示區
			Calendar dt = Calendar.getInstance(); //取得一個日曆物件
			DatePickerDialog dDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				// 覆寫選擇日期後的事件處理方法onDateSet()，所傳入的是所選擇的年、月、日
				// 注意:月份的索引值從0開始，因此正確的月份為(monthOfYear+1)
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth)
				{
					EndDateString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的結束日期字串
					tvEndDate.setText(year+ "年" + (monthOfYear+1) +"月"+ dayOfMonth +"日"); //將所選日期顯示在標籤上
				}}, dt.get(Calendar.YEAR), dt.get(Calendar.MONTH),dt.get(Calendar.DAY_OF_MONTH)); //以目前日期為預設日期
			dDialog.show(); //顯示日期選擇器對話方塊
		}
		catch(Exception ex)
		{
			tvOutput.setText("選擇結束日期時發生例外，原因如下： " + ex.getMessage());
		}
	}

	// 查詢消費紀錄按鈕事件處理方法
	public void btnQuery_Click(View v)
	{
		tvOutput.setText(""); //清除主顯示區
		// 以下確保使用者有選擇起始日期及結束日期
		int empty=0;
		if(StartDateString.length()==0) // 若沒有選擇起始日期，則利用吐司訊息提醒使用者
		{
			Toast.makeText(this, "沒有選擇起始日期!", Toast.LENGTH_SHORT).show();
			empty++;
		}
		if(EndDateString.length()==0)   // 若沒有選擇結束日期，則利用吐司訊息提醒使用者
		{
			Toast.makeText(this, "沒有選擇結束日期!", Toast.LENGTH_SHORT).show();
			empty++;
		}
		if (empty==0) // 若使用者有選擇起始日期及結束日期，則進行以下處理
		{
			try //利用try{}包住可能會產生例外的處理程式碼(如存取資料庫及檔案)，讓程式即便有例外，也不會當掉，以增加app之強韌性
			{
				if(radioButtonByDate.isChecked()) // 若選擇依照日期先後顯示
				{
					// 以下為SQL條件式資料讀取指令字串：從記帳簿資料表tallybook中讀取介於起始日期與結束日期間的所有消費紀錄，且依照日期先後排列(包含所有欄位)
					String commandString = "SELECT * FROM " + DATABASE_TABLE1 + " WHERE payDate between '" +
							StartDateString + "' and '" + EndDateString +"' ORDER BY payDate" ;
					Cursor cursor = db.rawQuery(commandString, null); //執行資料表查詢命令
					if(cursor != null) // 若有回傳運動紀錄，則進行以下處理
					{
						int n = cursor.getCount(); //取得資料筆數
						String str = "在" + StartDateString + "到" + EndDateString + "共有"+ n + "筆運動紀錄:\n";
						int totalAmount = 0; // 用於紀錄運動總次數
						cursor.moveToFirst();  // 將指標移到第1筆紀錄
						for (int i = 0; i < n; i++) // 利用迴圈逐一讀取每一筆紀錄
						{
							totalAmount += Integer.parseInt(cursor.getString(1)); //讀取第1個欄位(即消費金額price)，並加總到運動次數中
							cursor.moveToNext();  // 移動到下一筆
						}
						str += "共計活動 " + totalAmount +" 次\n"; // 將運動類型串接到顯示字串(str)中
						// 顯示運動紀錄之每一個欄位之抬頭
						String[] colNames = {"編號", "價錢", "消費類別", "說明", "消費日期"};
						for (int i = 0; i < colNames.length; i++)
							str += String.format("%5s\t", colNames[i]); // 將每一個欄位的抬頭串接到顯示字串(str)中
						str += "\n";

						cursor.moveToFirst();  // 將指標移到第1筆紀錄
						// 顯示欄位值
						for (int i = 0; i < n; i++) //利用迴圈讀取每一筆紀錄之各個欄位
						{
							str += String.format("%6s\t", (i+1)); // 串接記錄編號(索引值+1)
							str += String.format("%8s\t", cursor.getString(1)); // 串接第1個欄位值(即運動名稱price)
							str += String.format("%6s\t", cursor.getString(2)); // 串接第2個欄位值(即運動類型expenseType)
							str += String.format("%8s\t", cursor.getString(3)); // 串接第3個欄位值(即運動類型說明comment)
							str += String.format("%14s\t", cursor.getString(4));// 串接第4個欄位值(即運動日期payDate)
							str += "\n";
							cursor.moveToNext();  // 移動到下一筆
						}
						tvOutput.setText(str); //將顯示字串的內容顯示在主顯示區文字盒上
					}
					else // 若沒有回傳消費紀錄，則顯示並沒有消費紀錄
					{
						tvOutput.setText("在" + StartDateString + "到" + EndDateString + "並沒有運動紀錄!\n");
					}
				}
				if(radioButtonByType.isChecked()) // 若選擇依消費種類顯示
				{
					// 以下為SQL條件式資料讀取指令字串：從記帳簿資料表tallybook中讀取介於起始日期與結束日期間的所有消費紀錄中的消費種類(利用DISTINCT排除重複的值)
					Cursor cursor = db.rawQuery("SELECT DISTINCT expenseType FROM " + DATABASE_TABLE1 +
							" WHERE payDate between '" + StartDateString + "' and '" +
							EndDateString +"'", null); //執行資料表查詢命令
					if(cursor != null) // 若有傳回消費紀錄，則進行以下處理
					{
						String str1 = "在" + StartDateString + "到" + EndDateString + "之運動統計如下:\n";
						String str="";
						int n = cursor.getCount(); // 讀取傳回的紀錄筆數
						int totalAmount=0, num1; // 用於紀錄
						String [] types = new String[n]; //宣告一個長度等於傳回的紀錄筆數之字串陣列，用於存儲所傳回的消費種類
						cursor.moveToFirst(); // 移到第1筆紀錄
						for(int i=0; i<n; i++) // 利用迴圈讀取每一筆消費種類，並計算該消費種類的消費總額
						{
							types[i]=cursor.getString(0); // 讀取每一紀錄之消費種類 (目前記錄中只有消費種類，因此索引值為0)
							// 以下為SQL條件式資料讀取指令字串：從記帳簿資料表tallybook中讀取介於起始日期與結束日期間且消費種類等於所讀到的消費種類的所有消費紀錄，
							//並計算該消費種類的消費總金額(利用SUM(price)來計算總金額)
							String commandString = "SELECT SUM(price) FROM " + DATABASE_TABLE1 +
									" WHERE (payDate between '" + StartDateString + "' AND '" +
									EndDateString +"') AND (expenseType = '" + types[i]+ "')";
							Cursor cursor1 = db.rawQuery(commandString, null);
							cursor1.moveToFirst(); //移到所傳回的第1筆紀錄
							if(cursor1 != null) // 若有傳回值，則取出該消費種類的總金額(目前記錄中只有消費總金額，因此索引值為0)
								num1 = cursor1.getInt(0);
							else // 若沒有傳回值，則該消費種類之總金額為0
								num1=0;
							totalAmount += num1; // 加總該消費種類之總金額
							str += String.format("%5s: " + "%8s \n", types[i],  num1); //格式化串接該消費種類之總金額
							cursor.moveToNext(); // 移到下一筆紀錄
						}
						str1 += "運動次數總計" + totalAmount + "\n";
						str1 += str;
						tvOutput.setText(str1);	//將顯示字串str1的內容顯示在主顯示區文字盒上
					}
					else // 若有傳回消費紀錄，則顯示沒有消費紀錄
					{
						tvOutput.setText("在" + StartDateString + "到" + EndDateString + "並沒有運動紀錄!\n");
					}
				}
			}
			catch(Exception ex) // try{}中的程式碼執行時發生例外，則利用catch去捕捉，並利用吐司訊息提示給使用者知道
			{
				Toast.makeText(this, "程式出現例外: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	// 刪除消費紀錄按鈕事件處理方法
	public void btnDelete_Click(View v)
	{
		tvOutput.setText(""); //清除主顯示區
		// 以下確保使用者有選擇起始日期及結束日期
		int empty=0;
		if(StartDateString.length()==0)  // 若沒有選擇起始日期，則利用吐司訊息提醒使用者
		{
			Toast.makeText(this, "沒有選擇起始日期!", Toast.LENGTH_SHORT).show();
			empty++;
		}
		if(EndDateString.length()==0)  // 若沒有選擇結束日期，則利用吐司訊息提醒使用者
		{
			Toast.makeText(this, "沒有選擇結束日期!", Toast.LENGTH_SHORT).show();
			empty++;
		}
		if (empty==0) // 若使用者有選擇起始日期及結束日期，則進行以下處理
		{
			try //利用try{}包住可能會產生例外的處理程式碼(如存取資料庫及檔案)，讓程式即便有例外，也不會當掉，以增加app之強韌性
			{
				// 以下為建立警示對話方塊，以讓使用者確認是否刪除消費紀錄
				AlertDialog.Builder builder = new AlertDialog.Builder(this); //建立警示對話方塊物件
				str = "確定刪除" + StartDateString + "到" + EndDateString + "間之運動紀錄?";
				builder.setTitle("確認對話方塊") //設定警示對話方塊之標題
						.setMessage(str) // //設定警示對話方塊之顯示訊息
						.setPositiveButton("確定", new DialogInterface.OnClickListener() { // 建立"確定按鈕"
							public void onClick(DialogInterface dialoginterface, int i) // 點選"確認按鈕"之事件處理方法
							{
								// 查詢刪除前之消費紀錄筆數
								Cursor cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE1, null);
								int n1 = cursor.getCount(); //取得刪除前之消費紀錄筆數

								// 刪除選定日期範圍內之消費紀錄
								// 以下為SQL條件式資料刪除指令字串：從記帳簿資料表tallybook中刪除在所選起始日期與結束日期間的所有消費紀錄
								String commandString = "DELETE FROM " + DATABASE_TABLE1 +
										" WHERE payDate between '" + StartDateString +
										"' and '" + EndDateString +"'";
								db.execSQL(commandString);

								// 查詢刪除後之消費紀錄筆數
								cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE1, null);
								int n2 = cursor.getCount(); //取得刪除後之消費紀錄筆數

								// 在主顯示區顯示已成功刪除的消費紀錄筆數
								tvOutput.setText("已成功刪除" + (n1-n2) + "筆消費紀錄!");
							}
						})
						.setNegativeButton("取消", null) // 建立"取消按鈕"，若點選擇取消警示對話方塊
						.show(); // 顯示所建立之警示對話方塊
			}
			catch(Exception ex) // try{}中的程式碼執行時發生例外，則利用catch去捕捉，並利用吐司訊息提示給使用者知道
			{
				Toast.makeText(this, "程式出現例外: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	// 清除主顯示區按鈕事件處理方法
	public void btnClearMainOutput_Click(View v)
	{
		try
		{
			tvOutput.setText(""); // 清除主顯示區
		}
		catch(Exception ex)
		{
			tvOutput.setText("清除主顯示區時發生例外，原因如下： " + ex.getMessage());
		}
	}

	// 跳回主活動按鈕事件處理方法
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
			tvOutput.setText("跳回主活動時發生例外，原因如下： " + ex.getMessage());
		}
	}
}

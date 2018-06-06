package com.example.user.myapplication_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.Dialog;

import java.util.Calendar;
import java.util.Date;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private static String DATABASE_TABLE1 = "tallybookTable";     //宣告"記帳簿資料表"常數
    private static String DATABASE_TABLE2 = "expenseTypesTable";  //宣告"消費種類資料表"常數
    private SQLiteDatabase db;  //宣告資料庫變數
    private MyDBHelper dbHelper; //宣告資料庫幫助器變數



    // 與圖形介面元件相關之變數宣告
    private EditText etxtPrice, etxtComment; //分別為"輸入消費金額"與"輸入消費說明"之可編輯文字方塊變數
    private Spinner spExpenseTypeList;  //選擇消費種類之下拉式清單
    private Button btnDate; //"選擇消費日期"之按鈕變數
    private TextView tvDate;  //"顯示日期"之文字盒
    private Button btnSave, btnQuery, btnManageExpenseType; //分別為"儲存"，"查詢"與"選擇消費種類"之按鈕變數
    private TextView tvMainOutput;  //"主顯示區"之文字盒
    private Button btnClearMainOutput; //分別為"清除主顯示區"之按鈕變數
    private Button btnCloseApp; // "關閉簡易記帳簿"之按鈕變數


    // 其他變數宣告
    String [] initialExpenseTypes = { "慢跑","桌球","壘球", "打籃球", "游泳", "腳踏車", "足球", "棒球", "爬山", "排球", "健身"}; //初始消費種類陣列
    String [] expenseTypes; //用來運動種類之陣列(用於建立警示對話方塊之單選單)
    Cursor cursor;
    int n;
    ArrayAdapter<String> arrayAdaptor; //字串震裂配接器物件變數

    // 用於儲存記帳簿資料表每筆紀錄中的欄位值之變數宣告
    int Price;            // 儲存運動之變數
    String ExpenseType="";   // 儲存運動種類之變數
    String Comment="";       // 儲存運動說明之變數
    String PayDateString=""; // 儲存運動日期之變數

    int Year, Month, Day; //分別用於儲存所選日期之年、月與日

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //顯示操作界面
        Log.d("test2","create begin");
        try
        {
            // 取得使用者介面上之所有元件
            etxtPrice=(EditText) findViewById(R.id.etxtPrice);
            etxtComment=(EditText) findViewById(R.id.etxtComment);
            spExpenseTypeList = (Spinner) findViewById(R.id.spnExpenseTypeList);
            btnDate = (Button) findViewById(R.id.btnDate);
            tvDate=(TextView) findViewById(R.id.tvDate);
            btnSave = (Button) findViewById(R.id.btnSave);
            btnQuery = (Button) findViewById(R.id.btnQuery);
            btnManageExpenseType = (Button) findViewById(R.id.btnManageExpenseType);
            tvMainOutput=(TextView) findViewById(R.id.tvMainOutput);
            btnClearMainOutput = (Button) findViewById(R.id.btnClearMainOutput);
            btnCloseApp = (Button) findViewById(R.id.btnCloseApp);

            Log.d("test2","get GUIs");

            // 建立一個SQL資料庫 (MyTallyBook1)
            dbHelper = new MyDBHelper(this); //建立資料庫輔助類別物件
            db = dbHelper.getWritableDatabase(); // 透過輔助類別物件建立一個可以讀寫的資料庫
            Log.d("test2","db created");

            // 檢查資料表DATABASE_TABLE1是否已經存在，如果不存在，就建立一個。
            cursor = db.rawQuery(
                    "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                            DATABASE_TABLE1 + "'", null);
            if(cursor != null)
            {
                if(cursor.getCount() == 0)	// 沒有資料表，要建立一個資料表。
                {
                    // 執行SQL建立資料表指令， 建立記帳簿資料表(tallybookTable)，此資料表有以下5個欄位:
                    // _id(編號): 整數，為主鍵，數值會自動增加
                    // price(消費金額): 整數，不可為null(空白)
                    // expenseType(消費種類):可變長度Unicode字串(長度最大15)，不可為null(空白)
                    // comment(消費說明):可變長度Unicode字串(長度最大30)，可為null(空白)
                    // payDate(消費日期):date型別(只包含年月日資訊)，不可為null(空白)
                    db.execSQL("CREATE TABLE " + DATABASE_TABLE1 + " (_id integer primary key autoincrement, "
                            + "price int not null, expenseType nvarchar(15) not null, comment nvarchar(30) null, "
                            + "payDate date not null)");
                }

            }

            // 檢查資料表DATABASE_TABLE2是否已經存在，如果不存在，就建立一個。
            cursor = db.rawQuery(
                    "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                            DATABASE_TABLE2 + "'", null);
            Log.d("test2","get cursor!");
            if(cursor != null)
            {
                if(cursor.getCount() == 0)	// 沒有資料表，要建立一個資料表。
                {
                    // 執行SQL建立資料表指令， 建立消費種類資料表(expenseTypes)，此資料表有以下2個欄位:
                    // _id(編號): 整數，為主鍵，數值會自動增加
                    // expenseType(消費種類):可變長度Unicode字串(長度最大15)，不可為null(空白)
                    db.execSQL("CREATE TABLE " + DATABASE_TABLE2 + " (_id integer primary key autoincrement, "
                            + "expenseType nvarch(15) not null)");
                }
            }

            // 若是資料庫中沒有任何消費種類，則在資料庫中建立預設的消費種類
            cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2, null);
            if(cursor!=null)
            {
                n = cursor.getCount();//取得消費種類記錄筆數
                if(n==0) //若沒有消費種類，則將初始消費種類存入資料庫中
                {
                    for (int i=0; i<initialExpenseTypes.length; i++)
                        db.execSQL("INSERT INTO " + DATABASE_TABLE2 + " (expenseType) VALUES ('" +
                                initialExpenseTypes[i] +  "')");
                }
            }

            //
            // 若是資料庫中沒有任何消費種類，則在資料庫中建立預設的消費種類
            cursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE2, null);
            if(cursor!=null)
            {

                n = cursor.getCount(); //取得消費種類記錄筆數
                expenseTypes = new String[n]; //動態建立消費種類字串震裂
                cursor.moveToFirst();  // 將指標移到第1筆紀錄
                for (int i=0; i<n; i++)
                {
                    expenseTypes[i]= cursor.getString(1); //讀取第1個欄位值(消費種類expenseType)
                    cursor.moveToNext();  // 將指標移到下一筆紀錄
                }

            }

            //讀取消費種類資料表，並將運動種類填充Spineer
            fillSpinner();
            // 將目前日期設定為預設選擇日期，並顯示在付款日文字方塊上
            displayPayDate();
        }
        catch(Exception ex)
        {
            tvMainOutput.setText("載入GUI元件發生例外，原因如下： " + ex.getMessage());
            Log.d("test",ex.getMessage());
        }
    }

    //讀取運動種類資料表，並將運動種類填充Spineer
    public void fillSpinner()
    {
        try
        {
            Log.d("test2","spinner begin!");
            // 建立ArrayAdapter接合器物件，並建立Spinner項目清單
            arrayAdaptor = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, expenseTypes);

            spExpenseTypeList.setAdapter(arrayAdaptor);  // 指定接合器物件
            Log.d("test2","adapter set!");
            // 指定事件處理物件
            spExpenseTypeList.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    int index = arg0.getSelectedItemPosition();
                    ExpenseType = expenseTypes[index];
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {}
            });
        }
        catch(Exception ex)
        {
            tvMainOutput.setText("填充Spinner時發生例外，原因如下： " + ex.getMessage());
            Log.d("test",ex.getMessage());
        }
    }

    // 顯示目前的日期於EditText中
    public void displayPayDate()
    {
        Calendar dt = Calendar.getInstance(); //取得一個日曆物件
        int year = dt.get(Calendar.YEAR);     //取得目前日期的年份
        int monthOfYear = dt.get(Calendar.MONTH);   //取得目前日期的月份
        int dayOfMonth = dt.get(Calendar.DAY_OF_MONTH); //取得目前日期的每月的第幾天

        PayDateString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的運動日期字串
        tvDate.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日"); //將所選日期顯示在付款日文字方塊上
    }

    // 當Activity停止時關閉database
    @Override
    protected void onStop()
    {
        super.onStop();
        db.close(); // 關閉資料庫
    }



    // 選擇運動日期按鈕之點擊事件處理程式
    public void btnDate_Click(View view)
    {
        tvMainOutput.setText(""); //清除主顯示區
        Calendar dt = Calendar.getInstance(); //取得一個日曆物件
        DatePickerDialog dDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            // 覆寫選擇日期後的事件處理方法onDateSet()，所傳入的是所選擇的年、月、日
            // 注意:月份的索引值從0開始，因此正確的月份為(monthOfYear+1)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,	int dayOfMonth) //傳入使用者所選的時間資訊(年、月、日)
            {
                PayDateString = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //以所選日期建立一個格式化的運動日期字串
                tvDate.setText(year+"年"+ (monthOfYear+1) + "月" + dayOfMonth+ "日"); //將所選日期顯示在標籤上
            }}, dt.get(Calendar.YEAR), dt.get(Calendar.MONTH),dt.get(Calendar.DAY_OF_MONTH)); //以目前日期為預設日期
        dDialog.show(); //顯示日期選擇器對話方塊
    }

    // 儲存消費紀錄按鈕之點擊事件處理程式
    public void btnSave_Click(View view)
    {
        tvMainOutput.setText(""); //清除主顯示區
        String priceString =etxtPrice.getText().toString(); // 取得消費金額之字串
        Comment=etxtComment.getText().toString(); // 取得消費說明之字串
        // 以下確保使用者有輸入消費金額、選擇消費種類及選擇消費日期 (消費說明可以不用填寫)
        int empty=0;
        if(priceString.length()==0) // 若沒有輸入消費金額，則利用吐司訊息提醒使用者
        {
            Toast.makeText(this, "沒有填寫運動金額!", Toast.LENGTH_LONG).show();
            empty++;
        }
        if(ExpenseType.length()==0) // 若沒有選擇消費種類，則利用吐司訊息提醒使用者
        {
            Toast.makeText(this, "消費選擇運動種類!", Toast.LENGTH_LONG).show();
            empty++;
        }
        if (PayDateString.length()==0) // 若沒有選擇消費日期，則利用吐司訊息提醒使用者
        {
            Toast.makeText(this, "沒有選擇運動日期!", Toast.LENGTH_LONG).show();
            empty++;
        }
        if (empty==0) // 若使用者有輸入消費金額、選擇消費種類及選擇消費日期，則進行以下處理
        {
            try //利用try{}包住可能會產生例外的處理程式碼(如存取資料庫及檔案)，讓程式即便有例外，也不會當掉，以增加app之強韌性
            {
                Price=Integer.parseInt(priceString); // 將消費金額字串轉換成整數存入Price變數中
                // 以下為SQL資料插入指令字串： 將(Price, ExpenseType, Comment, PayDateString)四個值存入資料表1中，
                // 分別要存入到price, expeneType, comment,payDate四個欄位中
                String commandString="INSERT INTO " + DATABASE_TABLE1 + " (price, expenseType, comment, payDate) VALUES " +
                        "(" + Price + ", '" + ExpenseType + "', '" + Comment + "', '" + PayDateString + "')";
                db.execSQL(commandString); //執行SQL資料插入指令
                Toast.makeText(this, "成功儲存新的運動紀錄!", Toast.LENGTH_LONG).show(); //提示使用者已成功儲存新的消費紀錄
                // 以下為清除GUI上所輸入或所選擇的字，使恢復原狀，以方便使用者輸入下一筆消費資料
                etxtPrice.setText("");
                etxtComment.setText("");
                displayPayDate(); //顯示目前日期於TextView
                fillSpinner();

            }
            catch(Exception ex) // try{}中的程式碼執行時發生例外，則利用catch去捕捉，並利用吐司訊息提示給使用者知道
            {
                Toast.makeText(this, "程式出現例外: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }



    // 查詢歷史消費紀錄按鈕之點擊事件處理程式，啟動消費資料查詢Activity，並關閉本Activity
    public void btnQuery_Click(View view)
    {
        try
        {
            //建立查詢歷史消費紀錄之對話方塊
            Intent intent = new Intent(this, QueryHistoricalExpenseActivity.class);

            //啟動查詢歷史消費紀錄活動
            startActivity(intent);
            finish(); //關閉本活動
        }
        catch(Exception ex)
        {
            tvMainOutput.setText("查詢歷史運動紀錄時發生例外，原因如下： " + ex.getMessage());
        }
    }

    // 管理消費種類按鈕之點擊事件處理程式，啟動管理消費種類Activity，，並關閉本Activity
    public void btnManageExpenseType_Click(View view)
    {
        try
        {
            //建立消費種類管理對話方塊
            Intent intent = new Intent(this, ManageExpenseTypeActivity.class);

            //啟動管理消費種類活動
            startActivity(intent);
            finish();  //關閉本活動
        }
        catch(Exception ex)
        {
            tvMainOutput.setText("跳至管理運動種類活動發生例外，原因如下： " + ex.getMessage());
        }
    }


    // 清除顯示區按鈕之點擊事件處理程式
    public void btnClearMainOutput_Click(View view)
    {
        tvMainOutput.setText(""); //清除主顯示區
    }

    // "關閉簡易記帳簿按鈕"之點擊事件處理程式
    public void btnCloseApp_Click(View view)
    {
        finish(); //關閉簡易記帳簿
    }


}

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String databse = "190646T.db";
    public static final String useraccounts = "useraccounts";
    public static final String transactions = "transactions";

    public DBHelper(Context context){
        super(context,databse,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //first time create useraccounts table
        String newAccountSQL = "CREATE TABLE IF NOT EXISTS " + useraccounts + "( account_no TEXT PRIMARY KEY, bank TEXT NOT NULL, account_holder Text NOT NULL, balance TEXT NOT NULL );";
        database.execSQL(newAccountSQL);

        //first time create useraccounts table
        String newTransactionsSQL = "CREATE TABLE IF NOT EXISTS " + transactions + "( tans_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, account_no TEXT NOT NULL, type TEXT NOT NULL CHECK (type == \"INCOME\" OR type == \"EXPENSE\"), amount REAL NOT NULL, FOREIGN KEY(account_no) REFERENCES "+useraccounts+"(account_no) ON DELETE CASCADE );";
        database.execSQL(newTransactionsSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // no need
    }

    //all user account related functions that are use to access database.

    //get list of account numbers in the database
    public Cursor getAccountNumbers(){
        SQLiteDatabase database = this.getReadableDatabase();
        String allacountNos = "SELECT account_no FROM "+ useraccounts+";";
        Cursor account_nos = database.rawQuery(allacountNos,null);

        return account_nos;
    }

    //get all accounts in the database
    public Cursor getAllAccounts(){
        SQLiteDatabase database = this.getReadableDatabase();
        String selAllAccounts = "SELECT * FROM "+ useraccounts + ";";
        Cursor accounts = database.rawQuery(selAllAccounts,null);

        return accounts;
    }

    //get account details when provide a specific account number
    public Cursor getAccount(String accountNo){
        SQLiteDatabase database = this.getReadableDatabase();
        String selAccount = "SELECT * FROM "+ useraccounts + " WHERE account_no ='"+ accountNo+ "';";
        Cursor account = database.rawQuery(selAccount,null);

        return account;
    }

    //add new account to the database
    public boolean addAccount(String account_no, String bank, String account_holder, double initial_balance){
        SQLiteDatabase databse = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("account_no",account_no);
        cv.put("bank",bank);
        cv.put("account_holder",account_holder);
        cv.put("balance",initial_balance);

        long result = databse.insert(useraccounts,null, cv);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }


    }

    // remove specific account from database
    public boolean removeAccount(String account_no) {
        SQLiteDatabase databse = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("account_no",account_no);

        int result = databse.delete(useraccounts,"account_no=?",new String[]{account_no});

        if(result == -1){
            return false;
        }
        else{
            return true;
        }

    }

    //update blance of a account
    public boolean updateBalance(String account_no, double amount ){
        SQLiteDatabase databse = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("balance",amount);

        int result = databse.update(useraccounts,cv,"account_no=?",new String[]{account_no});

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    //add new transaction to the database
    public boolean addtransaction(String date, String account_no, String type, double amount) {
        SQLiteDatabase databse = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("date", date);
        cv.put("type", type);
        cv.put("account_no", account_no);
        cv.put("amount", amount);

        long result = databse.insert(transactions, null, cv);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //take all transactions logs in database
    public Cursor getAlltransactios(){
        SQLiteDatabase database = this.getReadableDatabase();
        String selAllTrans = "SELECT * FROM "+ transactions + ";";
        Cursor transactions = database.rawQuery(selAllTrans,null);

        return transactions;
    }
}

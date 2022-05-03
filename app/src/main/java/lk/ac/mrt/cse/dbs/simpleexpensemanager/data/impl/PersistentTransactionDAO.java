package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private DBHelper dbhelperObj;
    private Context context;


    public PersistentTransactionDAO(DBHelper dbhelperObj,Context context) {
        this.dbhelperObj = dbhelperObj;
        this.context = context;
    }

    public PersistentTransactionDAO(DBHelper dbHelper) {
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Cursor account = dbhelperObj.getAccount(accountNo);
        if (!account.moveToFirst()){
            Toast.makeText(context,"Account Number does not found!",Toast.LENGTH_SHORT).show();
            return;
        }

        double CurrAmount = account.getDouble(3);
        if(expenseType.EXPENSE == expenseType && (CurrAmount-amount)<0){
            Toast.makeText(context,"Cannot do this transaction due to insufficient balance.",Toast.LENGTH_SHORT).show();
            return;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String transactionDate = format.format(date);

        Boolean result = dbhelperObj.addtransaction(transactionDate, accountNo, expenseType.name(), amount);

        if(!result){
            Toast.makeText(context,"Cannot add transaction retry.",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionLis = new ArrayList<Transaction>();

        Cursor allTransactions = dbhelperObj.getAlltransactios();

        if (!allTransactions.moveToFirst()){
            return transactionLis;
        }

        do {
            Date transactionDate = null;
            String stringDate = allTransactions.getString(1);
            try{
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                transactionDate = format.parse(stringDate);
            }
            catch (ParseException e){
                e.printStackTrace();
            }

            ExpenseType expenseType = ExpenseType.valueOf(allTransactions.getString(3));
            String account_no = allTransactions.getString(2);
            double amount = allTransactions.getDouble(4);
            Transaction transaction = new Transaction(transactionDate, account_no, expenseType, amount);
            transactionLis.add(transaction);
        }
        while(allTransactions.moveToNext());

        //Toast.makeText(context, transactionLis.toString(), Toast.LENGTH_SHORT).show();
        return transactionLis;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionLis = getAllTransactionLogs();

        if (limit >= transactionLis.size()) {
            return transactionLis;
        }
        return transactionLis.subList((limit-transactionLis.size()), transactionLis.size());
    }
}

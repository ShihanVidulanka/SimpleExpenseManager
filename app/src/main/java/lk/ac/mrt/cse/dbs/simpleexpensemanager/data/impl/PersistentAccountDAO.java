package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private DBHelper dbhelperobj;
    private Context context;

    public PersistentAccountDAO(DBHelper dbhelperobj, Context context) {
        this.dbhelperobj = dbhelperobj;
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> acNoLis = new ArrayList<String>();
        Cursor results = dbhelperobj.getAccountNumbers();

        //check result is emty or not
        if (!results.moveToFirst()){
            return acNoLis;
        }

        do {
            acNoLis.add(results.getString(0));
        }
        while(results.moveToNext());

        return acNoLis;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> allAccounts = new ArrayList<Account>();
        Cursor results = dbhelperobj.getAllAccounts();

        //check result is emty or not
        if (!results.moveToFirst()){
            return allAccounts;
        }

        do {
            String account_no = results.getString(0);
            String bank = results.getString(1);
            String account_holder = results.getString(2);
            double balance = results.getDouble(3);

            Account account = new Account(account_no,bank,account_holder,balance);
            allAccounts.add(account);
        }
        while(results.moveToNext());

        return allAccounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor results = dbhelperobj.getAllAccounts();

        if (results.moveToFirst()) {
            String account_no = results.getString(0);
            String bank = results.getString(1);
            String account_holder = results.getString(2);
            double balance = results.getDouble(3);

            Account account = new Account(account_no, bank, account_holder, balance);
            return account;
        }
        String error = "Account " + accountNo +" does not found.";
        throw new InvalidAccountException(error);
    }

    @Override
    public void addAccount(Account account) {
        Cursor uniqueTest = dbhelperobj.getAccount(account.getAccountNo());

        if (uniqueTest.moveToFirst()){
            Toast.makeText(context,"Account already exist!",Toast.LENGTH_SHORT).show();
            return;
        }

        String account_no = account.getAccountNo();
        String bank = account.getBankName();
        String account_holder = account.getAccountHolderName();
        double initial_balance = account.getBalance();

        Boolean result = dbhelperobj.addAccount(account_no,bank,account_holder,initial_balance);

        if(!result){
            Toast.makeText(context,"Cannot add the account please retry.",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Cursor availability = dbhelperobj.getAccount(accountNo);

        if (!availability.moveToFirst()) {
            String error = "Account " + accountNo + " does not found.";
            throw new InvalidAccountException(error);
        }
        Boolean result = dbhelperobj.removeAccount(accountNo);

        if(!result){
            Toast.makeText(context,"Cannot remove account please retry.",Toast.LENGTH_SHORT).show();
            return;
        }
    }


    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Cursor account = dbhelperobj.getAccount(accountNo);
        if (!account.moveToFirst()){
            String error = "Account " + accountNo +" does not found.";
            throw new InvalidAccountException(error);
        }

        double currBalance = account.getDouble(3);
        double newbalance = 0;

        if (ExpenseType.INCOME == expenseType){
            newbalance = currBalance + amount;
        }
        else if(ExpenseType.EXPENSE == expenseType){
            newbalance = currBalance - amount;
            if (newbalance < 0){
                String error = "Cannot do this transaction due to insufficient balance.";
                throw new InvalidAccountException(error);
            }
        }

        Boolean result = dbhelperobj.updateBalance(accountNo,newbalance);
        if(!result){
            Toast.makeText(context,"Cannot remove account please retry.",Toast.LENGTH_SHORT).show();
            return;
        }

    }
}

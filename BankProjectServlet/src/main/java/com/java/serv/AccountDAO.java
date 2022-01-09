package com.java.serv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {


		Connection connection;
		PreparedStatement pst;
		
		public String withdrawAccount(int accountNo, int withdrawAmount) throws ClassNotFoundException, SQLException {
			Account accountFound = searchAccount(accountNo);
			if (accountFound!=null) {
				int balanceAmount = accountFound.getAmount();
				if (balanceAmount-withdrawAmount >= 1000) {
					connection = ConnectionHelper.getConnection();
					String cmd = "Update Accounts set Amount=Amount-? Where AccountNo=?";
					pst = connection.prepareStatement(cmd);
					pst.setInt(1, withdrawAmount);
					pst.setInt(2, accountNo);
					pst.executeUpdate();
					cmd = "insert into trans(AccountNo,TranAmount,TranType) values(?,?,?)";
					pst = connection.prepareStatement(cmd);
					pst.setInt(1, accountNo);
					pst.setInt(2, withdrawAmount);
					pst.setString(3, "D");
					pst.executeUpdate();
					return "Amount Withdrawn from Your Account...";
				} else {
					return "Insufficient Funds...";
				}
			}
			return "Invalid AccountNo...";
		}
		
		public String depositAccount(int accountNo, int depositAmount) throws ClassNotFoundException, SQLException {
			Account accountFound = searchAccount(accountNo);
			if (accountFound!=null) {
				connection = ConnectionHelper.getConnection();
				String cmd = "Update Accounts set Amount=Amount+? Where AccountNo=?";
				pst = connection.prepareStatement(cmd);
				pst.setInt(1, depositAmount);
				pst.setInt(2, accountNo);
				pst.executeUpdate();
				cmd = "insert into trans(AccountNo,TranAmount,TranType) values(?,?,?)";
				pst = connection.prepareStatement(cmd);
				pst.setInt(1, accountNo);
				pst.setInt(2, depositAmount);
				pst.setString(3, "C");
				pst.executeUpdate();
				return "Amount Credited to your Account...";
			}
			return "Account No Not Found...";
		}
		public String closeAccount(int accountNo) throws ClassNotFoundException, SQLException {
			Account accountFound = searchAccount(accountNo);
			if (accountFound!=null) {
				connection = ConnectionHelper.getConnection();
				String cmd = "update Accounts set status='inactive' where accountNo=?";
				pst = connection.prepareStatement(cmd);
				pst.setInt(1, accountNo);
				pst.executeUpdate();
				return "Account Closed...";			
			}
			return "Account no Not Found...";
		}
		
		public Account searchAccount(int accountNo) throws ClassNotFoundException, SQLException {
			connection = ConnectionHelper.getConnection();
			String cmd = "select * from Accounts where AccountNo=?";
			pst = connection.prepareStatement(cmd);
			pst.setInt(1, accountNo);
			Account account = null;
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				account = new Account();
				account.setAccountNo(rs.getInt("accountNo"));
				account.setFirstName(rs.getString("FirstName"));
				account.setLastName(rs.getString("LastName"));
				account.setCity(rs.getString("city"));
				account.setState(rs.getString("state"));
				account.setAmount(rs.getInt("amount"));
				account.setCheqFacil(rs.getString("cheqFacil"));
				account.setAccountType(rs.getString("accountType"));
			}
			return account;
		}
		
		public String createAccount(Account account) throws ClassNotFoundException, SQLException {
			connection = ConnectionHelper.getConnection();
			int accno = generateAccountNo();
			account.setAccountNo(accno);
			String cmd = "Insert into Accounts(AccountNo,FirstName,LastName,City,State,"
					+ "Amount,CheqFacil,AccountType) values(?,?,?,?,?,?,?,?)";
			pst = connection.prepareStatement(cmd);
			pst.setInt(1, accno);
			pst.setString(2, account.getFirstName());
			pst.setString(3, account.getLastName());
			pst.setString(4, account.getCity());
			pst.setString(5, account.getState());
			pst.setInt(6, account.getAmount());
			pst.setString(7, account.getCheqFacil());
			pst.setString(8, account.getAccountType());
			pst.executeUpdate();
			return "Account Created...";
		}
		
		public int generateAccountNo() throws ClassNotFoundException, SQLException {
			connection = ConnectionHelper.getConnection();
			String cmd = "select case when max(accountNo) IS NULL then 1 "
					+ " else Max(AccountNo)+1 end accno from Accounts";
			pst = connection.prepareStatement(cmd);
			ResultSet rs = pst.executeQuery();
			rs.next();
			int accno = rs.getInt("accno");
			return accno;
		}
	}


package cz.newbank.boarding;

import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NewBank implements IBank {

    private final Object monitor = new Object();

    private final ConcurrentMap<String, IAccount> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> transactions = new ConcurrentHashMap<>();

    @Override
    public void setAccounts(Map<String, Long> initAccs) {
        for (Map.Entry<String, Long> entry: initAccs.entrySet()){
            accounts.put(entry.getKey(), new AccountImpl(entry.getKey(),entry.getValue()));
        }
    }

    @Override
    public void setTransactions(Set<Object[]> transactions) {
        for (Object[] transaction : transactions){
            try {
                processTransaction((String)transaction[0], (String)transaction[1], (Long)transaction[2]);
            } catch (AccountNotFoundException | InsufficientFunds e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processTransaction(String source, String target, Long amount) throws AccountNotFoundException, InsufficientFunds {
        IAccount sourceAcc = getAccount(source);
        IAccount targetAcc = getAccount(target);

        synchronized (monitor){
            if (sourceAcc.getBalance() - amount < 0L ){
                throw new InsufficientFunds();
            }
            sourceAcc.setBalance(sourceAcc.getBalance() - amount);
            targetAcc.setBalance(targetAcc.getBalance() + amount);
            transactions.put(source + target, amount);

        }

    }

    @Override
    public IAccount getAccount(String name) throws AccountNotFoundException {
        IAccount result = accounts.get(name);
        if (result == null)
            throw new AccountNotFoundException();
        return result;
    }

    @Override
    public Long getSumAmounts() {
        Long sum = 0L;
        for (Map.Entry<String, Long> entry : transactions.entrySet()){
            sum +=entry.getValue();
        }
        return sum;
    }

    @Override
    public Long getBalance(String account) {
        try {
            return getAccount(account).getBalance();
        } catch (AccountNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

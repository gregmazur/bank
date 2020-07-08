package cz.newbank.boarding;

public class AccountImpl implements IAccount{

    private volatile String name;
    private volatile Long balance;

    public AccountImpl(String name, Long balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public Long getBalance() {
        return balance;
    }

    @Override
    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @Override
    public String getName() {
        return name;
    }
}

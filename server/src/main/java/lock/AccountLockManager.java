package lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AccountLockManager {

    private static final AccountLockManager INSTANCE = new AccountLockManager();

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private AccountLockManager() {}

    public static AccountLockManager getInstance() {
        return INSTANCE;
    }



    public ReentrantLock getLock(String accountId) {
        return lockMap.computeIfAbsent(accountId, k -> new ReentrantLock(true));
    }



    public void lockAccounts(String account1, String account2) {
        if (account1 == null || account2 == null) return;

        String first = account1.compareTo(account2) < 0 ? account1 : account2;
        String second = account1.compareTo(account2) < 0 ? account2 : account1;

        getLock(first).lock();
        if (!first.equals(second)) {
            getLock(second).lock();
        }
    }



    public void unlockAccounts(String account1, String account2) {
        if (account1 == null || account2 == null) return;

        String first = account1.compareTo(account2) < 0 ? account1 : account2;
        String second = account1.compareTo(account2) < 0 ? account2 : account1;

        if (!first.equals(second)) {
            ReentrantLock secondLock = lockMap.get(second);
            if (secondLock != null && secondLock.isHeldByCurrentThread()) {
                secondLock.unlock();
            }
        }

        ReentrantLock firstLock = lockMap.get(first);
        if (firstLock != null && firstLock.isHeldByCurrentThread()) {
            firstLock.unlock();
        }
    }
}

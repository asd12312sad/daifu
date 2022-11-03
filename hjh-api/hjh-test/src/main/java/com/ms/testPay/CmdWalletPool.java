package com.ms.testPay;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保持最高100个命令行钱包连接
 */
@Service
public class CmdWalletPool {
    private static Map<String, CmdWallet> pool = new ConcurrentHashMap<>();
    private static int maxSize = 100;

    public static CmdWallet getCmdWallet(String name) {
        if (pool.containsKey(name)) {
            return pool.get(name);
        }
        CmdWallet cmdWallet = new CmdWallet();
        pool.put(name, cmdWallet);
        return cmdWallet;
    }

    public static void removeCmdWallet(String name) {
        pool.remove(name);
    }



}

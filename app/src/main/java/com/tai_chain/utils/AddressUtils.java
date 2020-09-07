package com.tai_chain.utils;

public class AddressUtils {

    public static String addr0X2TIT(String addr) {
        String address=addr.toLowerCase();
        if (address.startsWith("0x")) {
//            address.replaceFirst("0x", "tit");
            address=address.replace("0x", "tit");
        }
        return address;
    }
    public static String addrTit20x(String addr) {
        String address=addr.toLowerCase();
        if (address.startsWith("tit")) {
            address=address.replace("tit", "0x");
        }
        return address;
    }
}

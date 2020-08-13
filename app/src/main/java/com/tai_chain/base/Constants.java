package com.tai_chain.base;

import android.os.Environment;
import android.text.format.DateUtils;

import com.tai_chain.app.MyApp;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.store.SPVBlockStore;

import java.io.File;
import java.math.RoundingMode;

public class Constants {

    public static final boolean WRITE_AHEAD_LOGGING = true;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final String ACTION_WALLET_REFERENCE_CHANGED = MyApp.class.getPackage().getName()
            + ".wallet_reference_changed";

    public static final int MAX_DECIMAL_PLACES = 8;
    //Show this number of decimal places in transaction info.
    public static final int MAX_DECIMAL_PLACES_FOR_UI = 5;
    public static final String WALLET_TYPE = "walletType";

    public static final String METHOD = "method";
    public static final String JSONRPC = "jsonrpc";
    public static final String ETH_BALANCE = "eth_getBalance";
    public static final String LATEST = "latest";
    public static final String PARAMS = "params";
    public static final String ID = "id";
    public static final String ETH_GAS_PRICE = "eth_gasPrice";
    public static final String ETH_ESTIMATE_GAS = "eth_estimateGas";
    public static final String ETH_BLOCK_NUMBER= "eth_blockNumber";
    public static final int SCANNER_REQUEST = 201;

    public static final int CONTACTS_REQUEST = 216;
    public static final int REQUEST_IMAGE_CAPTURE = 203;

    public static final String CANARY_STRING = "canary";
    public static final int CAMERA_REQUEST_ID = 34;
    public static final int GEO_REQUEST_ID = 35;

    /**
     * Currency units
     */
    public static final int CURRENT_UNIT_BITS = 0;
    public static final int CURRENT_UNIT_MBITS = 1;
    public static final int CURRENT_UNIT_BITCOINS = 2;



    //BTC

    public static final char CHAR_THIN_SPACE = '\u2009';
    public static final int PEER_DISCOVERY_TIMEOUT_MS = 10 * (int) DateUtils.SECOND_IN_MILLIS;
    public static final int PEER_TIMEOUT_MS = 15 * (int) DateUtils.SECOND_IN_MILLIS;

    public static final long LAST_USAGE_THRESHOLD_JUST_MS = DateUtils.HOUR_IN_MILLIS;
    public static final long LAST_USAGE_THRESHOLD_RECENTLY_MS = 2 * DateUtils.DAY_IN_MILLIS;
    public static final long LAST_USAGE_THRESHOLD_INACTIVE_MS = 4 * DateUtils.WEEK_IN_MILLIS;

    public static final long DELAYED_TRANSACTION_THRESHOLD_MS = 2 * DateUtils.HOUR_IN_MILLIS;
    public static final int NOTIFICATION_ID_CONNECTED = 1;
    public static final int NOTIFICATION_ID_COINS_RECEIVED = 2;


    public static final String USER_AGENT = "Bitcoin Wallet";
    public static final boolean ENABLE_EXCHANGE_RATES = true;
    /** Enable switch for synching of the blockchain */
    public static final boolean ENABLE_BLOCKCHAIN_SYNC = true;



    public interface ErrorCode {

        int UNKNOWN = 1;
        int CANT_GET_STORE_PASSWORD = 2;
    }

    public static final String DEFAULT_GAS_PRICE = "18000000000";
    public static final String DEFAULT_GAS_PRICE_GWEI= "18";
    public static final String DEFAULT_GAS_LIMIT_FOR_ETH = "21000";
    public static final String DEFAULT_GAS_LIMIT = "90000";
    public static final String DEFAULT_GAS_LIMIT_FOR_TOKENS = "144000";
    public static final String DEFAULT_GAS_LIMIT_FOR_NONFUNGIBLE_TOKENS = "432000";

    public static final long GAS_LIMIT_MIN = 21000L;
    public static final long GAS_PER_BYTE = 300;
}

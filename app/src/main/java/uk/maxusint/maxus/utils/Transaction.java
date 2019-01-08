package uk.maxusint.maxus.utils;

public class Transaction {
    public class Type {
        public static final int DEPOSIT_REQUEST = 101;
        public static final int DEPOSIT_HISTORY = 102;
        public static final int WITHDRAW_REQUEST = 103;
        public static final int WITHDRAW_HISTORY = 104;
        public static final int BALANCE_TRANSFER_REQUEST = 105;
        public static final int BALANCE_TRANSFER_HISTORY = 106;

        public class TypeString {
            public static final String DEPOSIT = "Deposit";
            public static final String WITHDRAW = "Withdraw";
            public static final String BALANCE_TRANSFER = "Balance Transfer";
        }
    }

    public class Charge {
        public static final double DEPOSIT = 0.0;
        public static final double WITHDRAW = 0.0;
        public static final double BALANCE_TRANSFER = 0.1;
    }

    public class Status {
        public static final String REQUEST_SEND = "Request Send";
        public static final String PENDING = "Pending";
        public static final String SUCCESS = "Success";
    }

}
